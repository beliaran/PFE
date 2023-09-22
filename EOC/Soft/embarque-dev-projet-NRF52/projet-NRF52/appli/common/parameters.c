/*
 * parameter.c
 *
 *  Created on: 15 f�vr. 2021
 *      Author: meven
 */

#include "appli/config.h"
#include "parameters.h"
#include "flash.h"

typedef struct
{
	bool_e enable;
	bool_e updated;
	bool_e value_saved_in_flash;	//TODO g�rer cette fonctionnalit�
	uint32_t value;
	callback_fun_i32_t callback_after_set_from_RF;	//Cette callback sera appel�e si un message demande le changement d'un param�tre
	callback_i32_fun_t callback_if_get_from_RF;		//Cette fonction sera appel�e si un message demande � lire un param�tre
}params_t;

static params_t params[PARAM_NB];


/*
 * Explications :
 * 	Il existe deux cat�gories de param�tres.
 * 		1- Les param�tres qui sont pilot�s depuis la station de base (donc depuis la liaison RF) :
 	 	 	 Ils n'ont pas besoin de la callback_if_get_from_RF
 	 	 	 Ils peuvent �tre associ�s � une  callback_after_set_from_RF.
 	 	 	 	 Ainsi, lorsque la station provoquera une �criture de ce param�tre, la fonction de callback associ�e sera appel�e pour agir en cons�quence.
 	 	2- Les param�tres qui sont fournis par l'objet et lu par la station de base (typiquement, la valeur d'un capteur).
 	 		  Ils n'ont pas besoin d'�tre associ�s � une callback_after_set_from_RF, puisque la station n'a pas de raison d'�crire dans cette valeur !
 	 		  Ils peuvent �tre associ�s � une callback_if_get_from_RF.
 	 		  	  Cette fonction sera appel�e pour mettre � jour ce param�tre lorsque la station en demandera la valeur.
 	 		  	  	  Cela permet de ne pas avoir � mettre � jour de fa�on r�guli�re la valeur du param�tre � chaque mesure capteur !
 */


//Cette fonction doit �tre appel�e lors de l'init, avant l'init des objets.
void PARAMETERS_init(void)
{
	for(uint8_t i = 0; i<PARAM_32_BITS_NB; i++)
	{
		params[i] = (params_t){
			.enable = FALSE,
			.updated = FALSE,
			.value = 0,
			.callback_after_set_from_RF = NULL,
			.callback_if_get_from_RF = NULL
			};
	}
	FLASHWRITER_init();
}

//chaque objet doit appeler cette fonction pour chacun de ses param�tres
void PARAMETERS_enable(param_id_e param_id, int32_t default_value, bool_e value_saved_in_flash, callback_fun_i32_t callback_after_set_from_RF, callback_i32_fun_t callback_if_get_from_RF)
{
	params[param_id].enable = TRUE;
	params[param_id].value_saved_in_flash = value_saved_in_flash;
	params[param_id].callback_after_set_from_RF = callback_after_set_from_RF;
	params[param_id].callback_if_get_from_RF = callback_if_get_from_RF;
	params[param_id].updated = FALSE;
	params[param_id].value = default_value;
	if(value_saved_in_flash)
	{
		PARAMETERS_read_from_flash(param_id);	//la default value sera �cras�e si ce param�tre dispose d'une valeur en flash !
	}
}

void PARAMETERS_update(param_id_e param_id, int32_t new_value)
{
	if(param_id < PARAM_32_BITS_NB && params[param_id].enable)
	{
		params[param_id].value = new_value;
		params[param_id].updated = TRUE;
		if(params[param_id].callback_after_set_from_RF != NULL)
			params[param_id].callback_after_set_from_RF(new_value);

		if(params[param_id].value_saved_in_flash)
		{
			//sauvegarder le param�tre en flash...
			uint32_t address = (uint32_t)param_id * 4;
			FLASHWRITER_write(address, params[param_id].value_saved_in_flash);
		}
	}
}

//cette fonction se destine aux param�tres sp�cifiques dont la valeur ne peut se contenter de 32 bits.
//dans ce cas, on confie � une callback le traitement des donn�es... exprim�es sous forme d'un paquet d'octet.
void PARAMETERS_update_custom(param_id_e param_id, uint8_t * datas)
{
	if(param_id > PARAM_32_BITS_NB && param_id < PARAM_NB && params[param_id].enable)
	{
		params[param_id].updated = TRUE;
		if(params[param_id].callback_after_set_from_RF != NULL)
			params[param_id].callback_after_set_from_RF((uint32_t)datas);	//on transmets l'adresse des donn�es � traiter.... de fa�on un peu violente.
	}
}


void PARAMETERS_read_from_flash(param_id_e param_id)
{
	//TODO si le param�tre est activ�, et que son value_saved_in_flash est vrai, on va chercher sa valeur en flash.
	//Etape1 : on lit la premi�re adresse de la flash pour savoir si des donn�es s'y trouvent
	//Etape2 : Si des donn�es s'y trouvent, on va chercher ce param�tre
		//pour toute donn�e lue � 0xFFFFFFFF -> on pr�f�re la valeur par d�faut (=on ne mets pas � jour le param�tre)

	if(params[param_id].enable && params[param_id].value_saved_in_flash){
		uint32_t address = (uint32_t)param_id * 4;
		uint32_t flash_value = FLASHWRITER_read(address);
		if(flash_value != 0xFFFFFFFF)
			params[param_id].value = flash_value;
	}

}


//Cette fonction sauvegarde en flash tout les param�tres dont la value_saved_in_flash est vrai, et dont la valeur est diff�rente de celle pr�sente en flash !
void PARAMETERS_save_to_flash(void)
{
	PARAMETERS_init();
	for(uint8_t i = 0; i<PARAM_32_BITS_NB; i++){
		uint32_t address = i * 4;
		if(params[i].value_saved_in_flash && params[i].value != FLASHWRITER_read(address)){
			FLASHWRITER_write(address, params[i].value);
		}
	}
}

int32_t PARAMETERS_get(param_id_e param_id)
{
	if(params[param_id].callback_if_get_from_RF != NULL)
		params[param_id].value = params[param_id].callback_if_get_from_RF();
	return params[param_id].value;
}
