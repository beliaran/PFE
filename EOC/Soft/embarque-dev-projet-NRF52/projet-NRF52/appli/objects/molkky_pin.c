/*
 * molkky_pin.c
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

#include "../main.h"

#include "../common/battery.h"



#include <math.h>

#if OBJECT_ID == MOLKKY_PIN_BOARD
/*
 * Code de test
 * Objectif : 	Etape 1 : La quille est en idle tant qu'elle est droite
 * 				Etape 2 : La quille détecte qu'elle est tombée
 * 				Etape 3 : La quille envoie un message en esb
 * 				Etape 4 : La quille attend d'être à nouveau droite
 * 				Etape 5 : La quille revient à son état initial
 */

#define X 0
#define Y 1
#define Z 2

#define PIN_ID 8 //identifiant de la quille

#define BOARD_LED 12 //pin de la led

#define FREQ 5   // Fréquence d'echantillonnage en Hz

#define INITIAL_X 90
#define INITIAL_Y 0
#define INITIAL Z 0

#define MAX_IDLE_TIME 3600000000 //tps en us avant mise en veille en cas d'immobilité (ici 1h)
#define MPU_INIT_DELAY 25 //tps nécessaire à l'init du mpu en ms (besoin de le faire régulièrement car mpu défectueux)
#define NB_CHECK_FALL 1 //nombre de mesures pour confirmer une chute
#define NB_MSG_MAX 10
#define POWER_PIN 17
#define MSG_MAX_INTERVAL 600000000 //tps max en us entre 2 messages (ici 10min) -> envoi d'un msg batterie "alive"

#define ANGLE_FALLEN_X 20
#define ANGLE_FALLEN_Y 70
#define ANGLE_UP_X 70
#define ANGLE_UP_Y 20

void gpio_init(void);
uint32_t esb_init(void);
void nrf_esb_event_handler(nrf_esb_evt_t const * p_event);
void process_ms(void);
uint8_t get_batt_lvl();
uint8_t get_accel();
uint8_t get_angle();

bool_e check_fallen();
bool_e check_up();
void send_init_signal();
void send_batt_signal();
void send_death_signal();

static uint32_t timer = 0;
static uint32_t last_msg_time = 0;
static uint32_t last_move_time = 0;
static uint32_t  sampling_period = 0;

static nrf_esb_payload_t        tx_payload = NRF_ESB_CREATE_PAYLOAD(0, 1,2, 3, 4, 5);
static nrf_esb_payload_t        rx_payload;

static uint8_t check_fall_cpt = 0;
static uint8_t check_up_cpt = 0;

static uint8_t delay_msg = 0;

static int32_t angleX = 0;
static int32_t angleY = 0;
static int32_t angleZ = 0;
//static int32_t prev_angleX = 0;
//static int32_t prev_angleY = 0;
//static int32_t prev_angleZ = 0;

//static int32_t accX = 0;
//static int32_t accY = 0;
//static int32_t accZ = 0;
//static int32_t prev_accX = 0;
//static int32_t prev_accY = 0;
//static int32_t prev_accZ = 0;

static bool_e fallenX = 0;
static bool_e fallenY = 0;
//static bool_e upX = 0;
//static bool_e upY = 0;
static uint32_t time_init_mpu = 0;


typedef enum{
	START,
	IDLE,
	CHECK_FALL,
	FALLEN,
	WAIT,
	CHECK_UP,
	ERROR,
	STOP,
//	SEND_MSG,
}pin_state_e;

//		Id	|	Type msg	|	BatLvl		|	Accel		|	Angle
enum{
	PL_PIN_ID,
	PL_MSG_TYPE,
	PL_BATT_LVL,
	PL_ACCEL,
	PL_ANGLE,
	PL_LENGTH
};

enum{
	PIN_INIT = 0,
	PIN_FALLEN,
	PIN_DEATH,
	PIN_BATT
};

pin_state_e state_pin = START;

void molkky_pin_board_state_machine(void){
	switch(state_pin){
	case START:

		gpio_init();
		nrf_gpio_pin_write(BOARD_LED, 1);
//		time_init_mpu = SYSTICK_get_time_us();
		molkky_init_mpu();
//		time_init_mpu = SYSTICK_get_time_us() - time_init_mpu;
		MEASURE_VBAT_init();
		ret_code_t err_code;
		err_code = NRF_LOG_INIT(NULL);
		APP_ERROR_CHECK(err_code);

		NRF_LOG_DEFAULT_BACKENDS_INIT();
		err_code = esb_init();
		APP_ERROR_CHECK(err_code);

		Systick_add_callback_function(&process_ms);
		timer = SYSTICK_get_time_us();
//		sampling_period = ((1000000/FREQ)-MPU_INIT_DELAY);
		sampling_period = 200000;

		delay_msg = 100+(50*(PIN_ID%2))+(10*(PIN_ID%10)); //on cherche à avoir un delai différent pour le plus grand nombre de quilles

		tx_payload.noack = true;
		tx_payload.data[PL_PIN_ID] = 0;
		tx_payload.data[PL_MSG_TYPE] = 0;
		tx_payload.data[PL_BATT_LVL] = 0;
		tx_payload.data[PL_ACCEL] = 0;
		tx_payload.data[PL_ANGLE] = 0;
		nrf_gpio_pin_write(BOARD_LED, 0);
		state_pin = IDLE;
//		state_pin = SEND_MSG;
		break;


	case IDLE:
		if(SYSTICK_get_time_us() - timer > sampling_period){
//			molkky_init_mpu();
//			SYSTICK_delay_ms(MPU_INIT_DELAY);
			if(check_fallen()){
				state_pin = CHECK_FALL;
			}
			else{
				state_pin = IDLE;
			}
			timer = SYSTICK_get_time_us();
		}
		if(SYSTICK_get_time_us() - last_msg_time > MSG_MAX_INTERVAL){
			send_batt_signal();
		}
		if(SYSTICK_get_time_us() - last_move_time > MAX_IDLE_TIME){
			send_death_signal();
		}
		break;

	case CHECK_FALL:
		//on a détecté une potentielle chute, on va refaire NB_CHECK_FALL mesures pour être sûr
		//on pourrait aussi utiliser ce case pour mesurer la durée du mouvement de la quille ainsi que son accélération max
		state_pin = FALLEN;
		while(check_fall_cpt<NB_CHECK_FALL){
			SYSTICK_delay_us(sampling_period);
//			if(SYSTICK_get_time_us() - timer > sampling_period){
				check_fall_cpt++;
//				molkky_init_mpu();
//				SYSTICK_delay_ms(MPU_INIT_DELAY);
				if(!check_fallen()){
					state_pin = IDLE;
					break;
				}

//				timer = SYSTICK_get_time_us();
//			}
		}
		check_fall_cpt = 0;
		if(state_pin == FALLEN){
			last_move_time = SYSTICK_get_time_us();
			nrf_gpio_pin_write(BOARD_LED, 1);
		}

	break;

	case FALLEN:
		/*
		 * 	PL_PIN_ID = 0,
			PL_MSG_TYPE,
			PL_BATT_LVL,
			PL_ACCEL,
			PL_ANGLE,
			PL_LENGTH
		 */
		tx_payload.noack = true;
//		tx_payload.data[1] = 1;
		tx_payload.data[PL_PIN_ID] = PIN_ID;
		tx_payload.data[PL_MSG_TYPE] = PIN_FALLEN;
		tx_payload.data[PL_BATT_LVL] = get_batt_lvl();
		tx_payload.data[PL_ACCEL] = get_accel();
		tx_payload.data[PL_ANGLE] = get_angle();

		state_pin = WAIT;

		for(uint8_t i=0; i<NB_MSG_MAX; i++){
			if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS){
					debug_printf("Envoi réussi\n");
					}
					else
					{
						debug_printf("Sending packet failed\n");
						state_pin = ERROR;
					}
			SYSTICK_delay_ms(delay_msg);
		}
		last_msg_time = SYSTICK_get_time_us();

		break;

	case WAIT:
//		if(SYSTICK_get_time_us() - timer > sampling_period){
		SYSTICK_delay_us(sampling_period);
//			molkky_init_mpu();
//			SYSTICK_delay_ms(MPU_INIT_DELAY);
//			if(check_up()){
			if(!check_fallen()){
				state_pin = CHECK_UP;
			}
			else{
				state_pin = WAIT;
			}
//			timer = SYSTICK_get_time_us();
//		}
		break;

	case CHECK_UP:
		state_pin = IDLE;
		while(check_up_cpt<NB_CHECK_FALL){
//			if(SYSTICK_get_time_us() - timer > sampling_period){
			SYSTICK_delay_us(sampling_period);
				check_up_cpt++;
//				molkky_init_mpu();
//				SYSTICK_delay_ms(MPU_INIT_DELAY);
//				if(!check_up()){
				if(check_fallen()){
					state_pin = WAIT;
					break;
				}

//				timer = SYSTICK_get_time_us();
//			}
		}
		check_up_cpt = 0;
		if(state_pin == IDLE){
			last_move_time = SYSTICK_get_time_us();
			nrf_gpio_pin_write(BOARD_LED, 0);
		}
		break;

//	case SEND_MSG:
//		molkky_init_mpu();
//		SYSTICK_delay_ms(25);
//		molkky_get_angle(&angleX, &angleY, &angleZ);
//		tx_payload.noack = true;
//		tx_payload.data[0] = 0;
//		tx_payload.data[1] = (uint8_t)angleX;
//		tx_payload.data[2] = (uint8_t)angleY;
//		tx_payload.data[3] = (uint8_t)angleZ;
//
//			if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS)
//			{
//				debug_printf("Envoi réussi\n");
//				nrf_gpio_pin_toggle(BOARD_LED);
//
//			}
//			else
//			{
//				debug_printf("Sending packet failed\n");
//			}
//
//			SYSTICK_delay_ms(975);
//
//		break;

	case ERROR:
		nrf_gpio_pin_toggle(BOARD_LED);
		nrf_delay_ms(500);
		break;

	case STOP:
		break;

	default:
		break;
	}

}


void nrf_esb_event_handler(nrf_esb_evt_t const * p_event)
{
    switch (p_event->evt_id)
    {
        case NRF_ESB_EVENT_TX_SUCCESS:
            NRF_LOG_DEBUG("TX SUCCESS EVENT");
            break;
        case NRF_ESB_EVENT_TX_FAILED:
            NRF_LOG_DEBUG("TX FAILED EVENT");
            (void) nrf_esb_flush_tx();
            (void) nrf_esb_start_tx();
            break;
        case NRF_ESB_EVENT_RX_RECEIVED:
            NRF_LOG_DEBUG("RX RECEIVED EVENT");
            while (nrf_esb_read_rx_payload(&rx_payload) == NRF_SUCCESS)
            {
                if (rx_payload.length > 0)
                {
                    NRF_LOG_DEBUG("RX RECEIVED PAYLOAD");
                }
            }
            break;
    }
}



void gpio_init( void )
{
    nrf_gpio_range_cfg_output(8, 15);
    bsp_board_init(BSP_INIT_LEDS);
}

uint32_t esb_init( void )
{
    uint32_t err_code;
    uint8_t base_addr_0[4] = {0xE7, 0xE7, 0xE7, 0xE7};
    uint8_t base_addr_1[4] = {0xC2, 0xC2, 0xC2, 0xC2};
    uint8_t addr_prefix[8] = {0xE7, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8 };

    nrf_esb_config_t nrf_esb_config         = NRF_ESB_DEFAULT_CONFIG;
    nrf_esb_config.protocol                 = NRF_ESB_PROTOCOL_ESB_DPL;
    nrf_esb_config.retransmit_delay         = 600; //A modifier pour chaque quille pour qu'elles ne renvoient pas le message en même temps
    nrf_esb_config.retransmit_count			= 15;

    nrf_esb_config.bitrate                  = NRF_ESB_BITRATE_250KBPS;
    nrf_esb_config.tx_output_power			= NRF_ESB_TX_POWER_4DBM,
    nrf_esb_config.event_handler            = nrf_esb_event_handler;
    nrf_esb_config.mode                     = NRF_ESB_MODE_PTX;
    nrf_esb_config.selective_auto_ack       = false;

    err_code = nrf_esb_init(&nrf_esb_config);

    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_base_address_0(base_addr_0);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_base_address_1(base_addr_1);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_prefixes(addr_prefix, NRF_ESB_PIPE_COUNT);
    VERIFY_SUCCESS(err_code);

    return err_code;
}

static volatile uint32_t t = 0;


void process_ms(void)
{
	if(t)
		t--;
}

uint8_t get_batt_lvl(){
	return MEASURE_VBAT_get_level();
}

uint8_t get_accel(){
	return 0;
}
uint8_t get_angle(){
	return 0;
}

bool_e check_fallen(){

		molkky_init_mpu();
		SYSTICK_delay_ms(MPU_INIT_DELAY);
		molkky_get_angle(&angleX, &angleY, &angleZ);
		fallenX =(fabs(angleX))<(ANGLE_FALLEN_X);
		fallenY =(fabs(angleY))>(ANGLE_FALLEN_Y);

		if(fallenX || fallenY){
			return TRUE;
		}
		else{
			return FALSE;
		}
}

bool_e check_up(){

		molkky_init_mpu();
		SYSTICK_delay_ms(MPU_INIT_DELAY);
		molkky_get_angle(&angleX, &angleY, &angleZ);
		fallenX =(fabs(angleX))<(INITIAL_X - 20);
		fallenY =(fabs(angleY))>(INITIAL_X + 20);
				if(!(fallenX || fallenY)){
					return TRUE;
				}
				else{
					return FALSE;
				}
}

void send_batt_signal(){
		uint8_t batt_lvl = get_batt_lvl();
		tx_payload.noack = true;
//		tx_payload.data[1] = 1;
		tx_payload.data[PL_PIN_ID] = PIN_ID;
		tx_payload.data[PL_MSG_TYPE] = PIN_BATT;
		tx_payload.data[PL_BATT_LVL] = batt_lvl;
		tx_payload.data[PL_ACCEL] = 0;
		tx_payload.data[PL_ANGLE] = 0;

		for(uint8_t i=0; i<NB_MSG_MAX; i++){
			if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS){
					debug_printf("Envoi réussi\n");
					}
					else
					{
						debug_printf("Sending packet failed\n");
					}
			SYSTICK_delay_ms(delay_msg);
		}
		if(batt_lvl<=10){
			send_death_signal();
		}
		last_msg_time = SYSTICK_get_time_us();
}

void send_death_signal(){
		tx_payload.noack = true;
//		tx_payload.data[1] = 1;
		tx_payload.data[PL_PIN_ID] = PIN_ID;
		tx_payload.data[PL_MSG_TYPE] = PIN_DEATH;
		tx_payload.data[PL_BATT_LVL] = get_batt_lvl();
		tx_payload.data[PL_ACCEL] = 0;
		tx_payload.data[PL_ANGLE] = 0;

		for(uint8_t i=0; i<NB_MSG_MAX; i++){
			if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS){
					debug_printf("Envoi réussi\n");
					}
					else
					{
						debug_printf("Sending packet failed\n");
					}
			SYSTICK_delay_ms(delay_msg);
		}
		nrf_gpio_pin_write(POWER_PIN, 0);
}
#endif

#if OBJECT_ID == MOLKKY_PIN
/*
 * Code de test
 * Objectif : 	Etape 1 : La quille est en idle tant qu'elle est droite
 * 				Etape 2 : La quille détecte qu'elle est tombée
 * 				Etape 3 : La quille envoie un message en esb
 * 				Etape 4 : La quille attend d'être à nouveau droite
 * 				Etape 5 : La quille revient à son état initial
 */

#define X 0
#define Y 1
#define Z 2

#define FREQ 5   // Sampling frequency

#define INITIAL_X -90
#define INITIAL_Y 0
#define INITIAL Z 0

void gpio_init(void);
uint32_t esb_init(void);
void nrf_esb_event_handler(nrf_esb_evt_t const * p_event);
void process_ms(void);







static uint32_t timer = 0;
static uint32_t  sampling_period = 0;

static nrf_esb_payload_t        tx_payload = NRF_ESB_CREATE_PAYLOAD(0, 0x01,0x1C, 75, 2, 15, 3, 15);
static nrf_esb_payload_t        rx_payload;


static int32_t angleX, angleY, angleZ;
static bool_e fallenX = 0;
static bool_e fallenY = 0;




typedef enum{
	INIT,
	IDLE,
	FALLEN,
	WAIT,
	STOP
}pin_state_e;

pin_state_e state_pin = INIT;

void molkky_pin_state_machine(void){
	switch(state_pin){
	case INIT:

		LEDS_init(I_HAVE_LED_BATTERY);
		gpio_init();
		LED_set(LED_ID_NETWORK, LED_MODE_OFF);
		LED_set(LED_ID_BATTERY, LED_MODE_OFF);
		debug_printf("On passe dans l'init\n");

		molkky_init_mpu();

		ret_code_t err_code;

		err_code = NRF_LOG_INIT(NULL);
		APP_ERROR_CHECK(err_code);
		NRF_LOG_DEFAULT_BACKENDS_INIT();
		err_code = esb_init();
		APP_ERROR_CHECK(err_code);


		Systick_add_callback_function(&process_ms);
		timer = SYSTICK_get_time_us();
		sampling_period = (1000000 / FREQ);
		//test
		tx_payload.noack = true;
		//					tx_payload.data[0] = 0;
							tx_payload.data[1] = 0;
							tx_payload.data[2] = 0;
							tx_payload.data[3] = 0;
							if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS){
								debug_printf("yes");
							}

		state_pin = IDLE;
		break;

	case IDLE:
//		if(SYSTICK_get_time_us() - timer > sampling_period){
//			molkky_get_angle(&angleX, &angleY, &angleZ);
////			debug_printf("X: %ld | Y: %ld | Z: %ld \n", angleX, angleY, angleZ);
//			fallenX =(fabs(angleX))<(fabs(fabs(INITIAL_X)-80));
//			fallenY =(fabs(angleY))>(fabs(fabs(INITIAL_Y)-80));
//			if(fallenX | fallenY){
//				state_pin = FALLEN;
////					debug_printf("Quille Tombée \n");
//			}
//			timer = SYSTICK_get_time_us();
//		}
		molkky_get_angle(&angleX, &angleY, &angleZ);
					debug_printf("X: %ld | Y: %ld | Z: %ld \n", angleX, angleY, angleZ);
					tx_payload.noack = true;
					tx_payload.data[1] = (int8_t)abs(angleX);
					tx_payload.data[2] = (int8_t)abs(angleY);
					tx_payload.data[3] = (int8_t)abs(angleZ);
					if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS){
						debug_printf("yes");
					}
		//			fallenX =(fabs(angleX))<(fabs(fabs(INITIAL_X)-80));
		//			fallenY =(fabs(angleY))>(fabs(fabs(INITIAL_Y)-80));
		//			if(fallenX | fallenY){
		////				state_pin = FALLEN;
		////					debug_printf("Quille Tombée \n");
		//			}
		//			timer = SYSTICK_get_time_us();
					nrf_delay_ms(1000);
		break;

	case FALLEN:
		tx_payload.noack = true;
		tx_payload.data[1] = 1;
		if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS)
		{
			debug_printf("Envoi réussi\n");
			LED_set(LED_ID_NETWORK, tx_payload.data[1]);

			state_pin = WAIT;
			// Toggle one of the LEDs.
		}
		else
		{
			debug_printf("Sending packet failed\n");
		}
		break;

	case WAIT:
		if(SYSTICK_get_time_us() - timer > sampling_period){
			molkky_get_angle(&angleX, &angleY, &angleZ);
			fallenX =(fabs(angleX))<(fabs(fabs(INITIAL_X)-80));
			fallenY =(fabs(angleY))>(fabs(fabs(INITIAL_Y)-80));
			if(!(fallenX | fallenY)){
//					debug_printf("Quille Relevée \n");
				tx_payload.noack = true;
				tx_payload.data[1] = 0;
				if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS)
				{
					debug_printf("Envoi réussi\n");
					LED_set(LED_ID_NETWORK, tx_payload.data[1]);
					state_pin = IDLE;
					// Toggle one of the LEDs.
				}
				else
				{
					debug_printf("Sending packet failed\n");
				}
			}
			timer = SYSTICK_get_time_us();
		}
		break;

	case STOP:
		break;

	default:
		break;
	}

}


void nrf_esb_event_handler(nrf_esb_evt_t const * p_event)
{
    switch (p_event->evt_id)
    {
        case NRF_ESB_EVENT_TX_SUCCESS:
            NRF_LOG_DEBUG("TX SUCCESS EVENT");
            break;
        case NRF_ESB_EVENT_TX_FAILED:
            NRF_LOG_DEBUG("TX FAILED EVENT");
            (void) nrf_esb_flush_tx();
            (void) nrf_esb_start_tx();
            break;
        case NRF_ESB_EVENT_RX_RECEIVED:
            NRF_LOG_DEBUG("RX RECEIVED EVENT");
            while (nrf_esb_read_rx_payload(&rx_payload) == NRF_SUCCESS)
            {
                if (rx_payload.length > 0)
                {
                    NRF_LOG_DEBUG("RX RECEIVED PAYLOAD");
                }
            }
            break;
    }
}



void gpio_init( void )
{
    nrf_gpio_range_cfg_output(8, 15);
    bsp_board_init(BSP_INIT_LEDS);
}

uint32_t esb_init( void )
{
    uint32_t err_code;
    uint8_t base_addr_0[4] = {0xE7, 0xE7, 0xE7, 0xE7};
    uint8_t base_addr_1[4] = {0xC2, 0xC2, 0xC2, 0xC2};
    uint8_t addr_prefix[8] = {0xE7, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8 };

    nrf_esb_config_t nrf_esb_config         = NRF_ESB_DEFAULT_CONFIG;
    nrf_esb_config.protocol                 = NRF_ESB_PROTOCOL_ESB_DPL;
    nrf_esb_config.retransmit_delay         = 600; //A modifier pour chaque quille pour qu'elles ne renvoient pas le message en même temps
    nrf_esb_config.retransmit_count			= 15;

    nrf_esb_config.bitrate                  = NRF_ESB_BITRATE_250KBPS;
    nrf_esb_config.tx_output_power			= NRF_ESB_TX_POWER_4DBM,
    nrf_esb_config.event_handler            = nrf_esb_event_handler;
    nrf_esb_config.mode                     = NRF_ESB_MODE_PTX;
    nrf_esb_config.selective_auto_ack       = false;

    err_code = nrf_esb_init(&nrf_esb_config);

    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_base_address_0(base_addr_0);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_base_address_1(base_addr_1);
    VERIFY_SUCCESS(err_code);

    err_code = nrf_esb_set_prefixes(addr_prefix, NRF_ESB_PIPE_COUNT);
    VERIFY_SUCCESS(err_code);

    return err_code;
}

static volatile uint32_t t = 0;


void process_ms(void)
{
	if(t)
		t--;
}

#endif

