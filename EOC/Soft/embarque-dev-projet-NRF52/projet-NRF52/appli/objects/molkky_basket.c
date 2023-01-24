/*
 * molkky_basket.c
 *
 *  Created on: 28 déc. 2022
 *      Author: morgan
 */



#include "../config.h"
#include "molkky.h"
#include "bsp/mpu6050.h"
#include "appli/common/systick.h"
#include "appli/common/buttons.h"
#include "appli/common/leds.h"

#include <stdbool.h>
#include <stdint.h>
#include <string.h>
#include "sdk_common.h"
#include "nrf.h"
#include "appli/common/nrf_esb.h"
#include "nrf_error.h"
#include "nrf_esb_error_codes.h"
#include "nrf_delay.h"
#include "nrf_gpio.h"
#include "boards.h"
#include "nrf_delay.h"
#include "app_util.h"

#include "nrf_log.h"
#include "nrf_log_ctrl.h"
#include "nrf_log_default_backends.h"

#include "nrf_esb.h"

#include <stdbool.h>
#include <stdint.h>
#include "sdk_common.h"
#include "nrf.h"
#include "nrf_esb_error_codes.h"
#include "nrf_delay.h"
#include "nrf_gpio.h"
#include "nrf_error.h"
#include "boards.h"

#include "../config.h"
#include "nrf52.h"
#include "components/libraries/util/app_util_platform.h"
#include "components/libraries/util/sdk_common.h"
#include "../common/serial_dialog.h"

//#include "components/libraries/uart/app_uart.h"
//#include "nrf_uart.h"
//#include "nrf_uarte.h"
//#include <stdarg.h>

//#include "nrf_uart.h"
//#include "app_uart.h"

#include "../main.h"


#include <math.h>

#if OBJECT_ID == MOLKKY_BASKET

#define BASKET_ID 0
#define ESB_PAYLOAD_LENGTH 5
#define PIN_QT 12 //nombre de quilles dans un jeu
#define MSG_INTERVAL 1000000 //temps en us séparant l'envoi de 2 messages d'une même quille (ici 1seconde)

#define FREQUENCY 2450 // fréquence de l'esb en MHz
#define PATH_LOSS 2 // perte de chemin

//#define RX_PIN 6
//#define TX_PIN 8

#define BOARD_LED 12
/*
 * L'objectif de ce script est de recevoir les messages envoyés en ESB par les quilles, de séparer les messages venant des quilles liées au panier des quilles qui sont en mode init
 *  puis de transmettre leur message en UART en ajoutant la distance mesurée à partir du RSSI
 *  message reçu : 		Id	|	Type msg	|	BatLvl	|	Accel	|	Angle
 *  message envoyé : 	Id	|	Type msg	|	BatLvl	|	Accel	|	Angle	| Distance (RSSI)
 */
typedef struct{
	uint8_t id;
	uint32_t time_since_last_msg;

}pin_informations_t;

pin_informations_t pin_tab[PIN_QT];

typedef struct{
	uint8_t pin_id;
	uint8_t pin_msg_type;
	uint8_t pin_batt_lvl;
	uint8_t pin_accel;
	uint8_t pin_angle;

}pin_t;


uint8_t uart_tx_data[ESB_PAYLOAD_LENGTH + 2]; //+2 car on ajoute la distance calculée par le RSSI et le caractère de fin \n

enum{
	PL_PIN_ID = 0,
	PL_MSG_TYPE,
	PL_BATT_LVL,
	PL_ACCEL,
	PL_ANGLE,
	PL_DISTANCE,
	PL_LENGTH
};

enum{
	PIN_INIT = 0,
	PIN_FALLEN,
	PIN_DEATH,
	PIN_BATT
};

typedef enum{
	INIT,
	RECEIVE_DATA,
	WAIT,
	STOP
}state_e;

int8_t pin_identify(pin_t pin);
uint8_t get_distance(nrf_esb_payload_t rx_payload);

static volatile uint32_t t = 0;
void process_ms(void)
{
	if(t)
		t--;
}

static state_e state = INIT;
//
//static uint32_t sending_period;

uint8_t led_nr;

nrf_esb_payload_t rx_payload;

//uint8_t tx_data[8] = {12,1,10,0,0,42,(uint8_t)'\n'};
void nrf_esb_event_handler(nrf_esb_evt_t const * p_event)
{
    switch (p_event->evt_id)
    {
        case NRF_ESB_EVENT_TX_SUCCESS:
            NRF_LOG_DEBUG("TX SUCCESS EVENT");
            break;
        case NRF_ESB_EVENT_TX_FAILED:
            NRF_LOG_DEBUG("TX FAILED EVENT");
            break;
        case NRF_ESB_EVENT_RX_RECEIVED:
            NRF_LOG_DEBUG("RX RECEIVED EVENT");
//            debug_printf("Something received\n");
            if (nrf_esb_read_rx_payload(&rx_payload) == NRF_SUCCESS)
            {

                pin_t pin_temp;

                if(rx_payload.length == ESB_PAYLOAD_LENGTH){
                	pin_temp.pin_id = rx_payload.data[PL_PIN_ID];
                	pin_temp.pin_msg_type = rx_payload.data[PL_MSG_TYPE];
                	pin_temp.pin_batt_lvl = rx_payload.data[PL_BATT_LVL];
                	pin_temp.pin_accel = rx_payload.data[PL_ACCEL];
                	pin_temp.pin_angle = rx_payload.data[PL_ANGLE];
                }
                int8_t pin_num = pin_identify(pin_temp);
                if(pin_num >=0 && pin_num < PIN_QT && ((SYSTICK_get_time_us()-(pin_tab[pin_num].time_since_last_msg)) > MSG_INTERVAL)){
                	uart_tx_data[PL_PIN_ID] = pin_num +1; //le numéro réel de la pin est son num + 1
                	uart_tx_data[PL_MSG_TYPE]= pin_temp.pin_msg_type;
                	uart_tx_data[PL_BATT_LVL] = pin_temp.pin_batt_lvl;
                	uart_tx_data[PL_ACCEL] = pin_temp.pin_accel;
                	uart_tx_data[PL_ANGLE] = pin_temp.pin_angle;
                	uart_tx_data[PL_DISTANCE] = get_distance(rx_payload);
                	uart_tx_data[PL_LENGTH] = '\n';

                	for (uint8_t i = 0; i < (ESB_PAYLOAD_LENGTH + 2); i++){
                		while (SERIAL_DIALOG_put(uart_tx_data[i]) != NRF_SUCCESS);
					 }
                	pin_tab[pin_num].time_since_last_msg = SYSTICK_get_time_us();
#if(ENABLE_LED)
                	nrf_gpio_pin_toggle(BOARD_LED);
#endif
                }


            }
            break;
    }
}



void gpio_init( void )
{
    bsp_board_init(BSP_INIT_LEDS);
}


uint32_t esb_init( void )
{
    uint32_t err_code;
    uint8_t base_addr_0[4] = {0xE7, 0xE7, 0xE7, 0xE7};
    uint8_t base_addr_1[4] = {0xC2, 0xC2, 0xC2, 0xC2};
    uint8_t addr_prefix[8] = {0xE7, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8 };
    nrf_esb_config_t nrf_esb_config         = NRF_ESB_DEFAULT_CONFIG;
    nrf_esb_config.payload_length           = 8;
    nrf_esb_config.protocol                 = NRF_ESB_PROTOCOL_ESB_DPL;
    nrf_esb_config.bitrate                  = NRF_ESB_BITRATE_250KBPS;
    nrf_esb_config.tx_output_power			= NRF_ESB_TX_POWER_4DBM;
    nrf_esb_config.mode                     = NRF_ESB_MODE_PRX;
    nrf_esb_config.event_handler            = nrf_esb_event_handler;
    nrf_esb_config.selective_auto_ack       = false;

    err_code = nrf_esb_init(&nrf_esb_config);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_base_address_0(base_addr_0);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_base_address_1(base_addr_1);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_prefixes(addr_prefix, 8);
    VERIFY_SUCCESS(err_code);

    return err_code;
}

//uint8_t tx_data[8] = {12,1,10,0,0,42,(uint8_t)'\n'};
void molkky_basket_state_machine(void){



	switch(state)
	{
		case INIT:{
			LEDS_init(I_HAVE_LED_BATTERY);
#if(ENABLE_LED)
			LED_set(LED_ID_NETWORK, LED_MODE_OFF);
			LED_set(LED_ID_BATTERY, LED_MODE_OFF);
#endif
			debug_printf("On passe dans l'init\n");

			Systick_add_callback_function(&process_ms);
			SERIAL_DIALOG_init();

			uint32_t err_code;

			gpio_init();
			err_code = NRF_LOG_INIT(NULL);
			APP_ERROR_CHECK(err_code);

			NRF_LOG_DEFAULT_BACKENDS_INIT();
#if(ENABLE_LED)
			nrf_gpio_cfg_output(BOARD_LED);
#endif
//			nrf_gpio_pin_write(RX_PIN, 0);
//			nrf_gpio_pin_write(TX_PIN, 0);
			err_code = esb_init();
			APP_ERROR_CHECK(err_code);

			NRF_LOG_DEBUG("Enhanced ShockBurst Receiver Example started.");

			err_code = nrf_esb_start_rx();
			APP_ERROR_CHECK(err_code);
			debug_printf("init done\n");

			// définition à la mano des id des quilles, à terme, il faudra les stocker et les lire en mémoire
			for(uint8_t i = 0; i<12; i++){
				pin_tab[i].id = i+1;
				pin_tab[i].time_since_last_msg = 0;
			}
			// -- TO DO --


			    //	Id			|	Type msg	|	BatLvl		|	Accel		|	Angle

			    state = RECEIVE_DATA;
			break;}

		case RECEIVE_DATA:{
			while(1){
//				for (uint32_t i = 0; i < 8; i++)
//				 				    {
//				while (SERIAL_DIALOG_put(tx_data[i]) != NRF_SUCCESS);
//				 				    }
//				nrf_gpio_pin_toggle(19);
//				nrf_delay_ms(1000);
			}


			break;}

		case WAIT:
			debug_printf("on attend avant d'envoyer à nouveau\n");
//			nrf_delay_ms(5000);
//			tx_payload.data[1]=0;
//			state = SEND_DATA;

			break;

		case STOP:
			//Mode OFF à coder
			break;
		default:
			break;
	}

}

int8_t pin_identify(pin_t pin){
	uint8_t id = pin.pin_id;
	int8_t num = -1;
	for(int8_t i=0; i<PIN_QT; i++){
		if(id == pin_tab[i].id){
			num = i;
		}
	}

	return num;
}



uint8_t get_distance(nrf_esb_payload_t rx_payload){
    uint8_t distance = pow(10, (27.55 - (20 * log10(FREQUENCY)) + log10(PATH_LOSS) + rx_payload.rssi) / 20); //source : chatGPT

	return distance;
}

#endif
