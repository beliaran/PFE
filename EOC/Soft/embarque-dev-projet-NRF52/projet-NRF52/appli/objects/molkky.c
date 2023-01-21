/*
 * molkky.c
 *
 *  Created on: 2 févr. 2021
 *      Author: mleroux
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


#include <math.h>


#if OBJECT_ID == MOLKKY

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
//		gpio_init();
		LED_set(LED_ID_NETWORK, LED_MODE_OFF);
		LED_set(LED_ID_BATTERY, LED_MODE_OFF);
		debug_printf("On passe dans l'init\n");
		nrf_gpio_cfg_output(12);

//		nrf_gpio_cfg_output(26);

		nrf_gpio_pin_write(12, 1);
//		nrf_gpio_pin_write(26, 1);


//		molkky_init_mpu();

		ret_code_t err_code;

		err_code = NRF_LOG_INIT(NULL);
		APP_ERROR_CHECK(err_code);
		NRF_LOG_DEFAULT_BACKENDS_INIT();
		err_code = esb_init();
		APP_ERROR_CHECK(err_code);


		Systick_add_callback_function(&process_ms);
		timer = SYSTICK_get_time_us();
		sampling_period = (1000000 / FREQ);

//		state_pin = IDLE;
		break;

	case IDLE:
		if(SYSTICK_get_time_us() - timer > sampling_period){
			molkky_get_angle(&angleX, &angleY, &angleZ);
//			debug_printf("X: %ld | Y: %ld | Z: %ld \n", angleX, angleY, angleZ);
			fallenX =(fabs(angleX))<(fabs(fabs(INITIAL_X)-80));
			fallenY =(fabs(angleY))>(fabs(fabs(INITIAL_Y)-80));
			if(fallenX | fallenY){
				state_pin = FALLEN;
//					debug_printf("Quille Tombée \n");
			}
			timer = SYSTICK_get_time_us();
		}
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

//#if OBJECT_ID == MOLKKY_BASKET
//
//typedef enum{
//	INIT,
//	RECEIVE_DATA,
//	WAIT,
//	STOP
//}state_e;
//
//
//static volatile uint32_t t = 0;
//void process_ms(void)
//{
//	if(t)
//		t--;
//}
//
//static state_e state = INIT;
////
////static uint32_t sending_period;
//
//uint8_t led_nr;
//
//nrf_esb_payload_t rx_payload;
//
///*lint -save -esym(40, BUTTON_1) -esym(40, BUTTON_2) -esym(40, BUTTON_3) -esym(40, BUTTON_4) -esym(40, LED_1) -esym(40, LED_2) -esym(40, LED_3) -esym(40, LED_4) */
//
//void nrf_esb_event_handler(nrf_esb_evt_t const * p_event)
//{
//    switch (p_event->evt_id)
//    {
//        case NRF_ESB_EVENT_TX_SUCCESS:
//            NRF_LOG_DEBUG("TX SUCCESS EVENT");
//            break;
//        case NRF_ESB_EVENT_TX_FAILED:
//            NRF_LOG_DEBUG("TX FAILED EVENT");
//            break;
//        case NRF_ESB_EVENT_RX_RECEIVED:
//            NRF_LOG_DEBUG("RX RECEIVED EVENT");
//            debug_printf("Something received\n");
//            if (nrf_esb_read_rx_payload(&rx_payload) == NRF_SUCCESS)
//            {
//                printf("trame reçue ! \n");
//
//                LED_set(LED_ID_NETWORK, rx_payload.data[1]);
//
//                debug_printf("Payload values : \n");
//				debug_printf("Id : %d\n", rx_payload.data[0]);
//				debug_printf("Type : %d\n", rx_payload.data[1]);
//				debug_printf("BatLvl : %d\n", rx_payload.data[2]);
//				debug_printf("Accel : %d\n", rx_payload.data[3]);
//				debug_printf("Angle : %d\n", rx_payload.data[4]);
//				debug_printf("data 5 : %d\n", rx_payload.data[5]);
//				debug_printf("data 6 : %d\n", rx_payload.data[6]);
//				debug_printf("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\n");
//
//            }
//            break;
//    }
//}
//
//
//
//void gpio_init( void )
//{
//    bsp_board_init(BSP_INIT_LEDS);
//}
//
//
//uint32_t esb_init( void )
//{
//    uint32_t err_code;
//    uint8_t base_addr_0[4] = {0xE7, 0xE7, 0xE7, 0xE7};
//    uint8_t base_addr_1[4] = {0xC2, 0xC2, 0xC2, 0xC2};
//    uint8_t addr_prefix[8] = {0xE7, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8 };
//    nrf_esb_config_t nrf_esb_config         = NRF_ESB_DEFAULT_CONFIG;
//    nrf_esb_config.payload_length           = 8;
//    nrf_esb_config.protocol                 = NRF_ESB_PROTOCOL_ESB_DPL;
//    nrf_esb_config.bitrate                  = NRF_ESB_BITRATE_250KBPS;
//    nrf_esb_config.tx_output_power			= NRF_ESB_TX_POWER_4DBM;
//    nrf_esb_config.mode                     = NRF_ESB_MODE_PRX;
//    nrf_esb_config.event_handler            = nrf_esb_event_handler;
//    nrf_esb_config.selective_auto_ack       = false;
//
//    err_code = nrf_esb_init(&nrf_esb_config);
//    VERIFY_SUCCESS(err_code);
//
//    err_code = nrf_esb_set_base_address_0(base_addr_0);
//    VERIFY_SUCCESS(err_code);
//
//    err_code = nrf_esb_set_base_address_1(base_addr_1);
//    VERIFY_SUCCESS(err_code);
//
//    err_code = nrf_esb_set_prefixes(addr_prefix, 8);
//    VERIFY_SUCCESS(err_code);
//
//    return err_code;
//}
//
//void molkky_basket_state_machine(void){
//
//
//
//	switch(state)
//	{
//		case INIT:{
//			LEDS_init(I_HAVE_LED_BATTERY);
//			LED_set(LED_ID_NETWORK, LED_MODE_OFF);
//			LED_set(LED_ID_BATTERY, LED_MODE_OFF);
//			debug_printf("On passe dans l'init\n");
//
//			Systick_add_callback_function(&process_ms);
//
//			uint32_t err_code;
//
//
//			err_code = NRF_LOG_INIT(NULL);
//			APP_ERROR_CHECK(err_code);
//
//			NRF_LOG_DEFAULT_BACKENDS_INIT();
//
//			err_code = esb_init();
//			APP_ERROR_CHECK(err_code);
//
//			NRF_LOG_DEBUG("Enhanced ShockBurst Receiver Example started.");
//
//			err_code = nrf_esb_start_rx();
//			APP_ERROR_CHECK(err_code);
//			debug_printf("init done\n");
//
//
//			    //	Id			|	Type msg	|	BatLvl		|	Accel		|	Angle
//
//			    state = RECEIVE_DATA;
//			break;}
//
//		case RECEIVE_DATA:{
//			while(1){
//			}
//
//
//			break;}
//
//		case WAIT:
//			debug_printf("on attend avant d'envoyer à nouveau\n");
////			nrf_delay_ms(5000);
////			tx_payload.data[1]=0;
////			state = SEND_DATA;
//
//			break;
//
//		case STOP:
//			//Mode OFF à coder
//			break;
//		default:
//			break;
//	}
//
//}
//
//#endif

#if OBJECT_ID == MOLKKY_MPU
#define GET_OFFSET 0

#define X 0
#define Y 1
#define Z 2

#define FREQ 5   // Sampling frequency

#define INITIAL_X -90
#define INITIAL_Y 0
#define INITIAL Z 0



typedef enum{
	INIT,
	GET_DATA,
	FALLEN,
	UP_AGAIN,
	STOP
}state_e;
static uint32_t timer = 0;
static uint32_t  sampling_period;
static int32_t angleX, angleY, angleZ;
static bool_e fallenX = 0;
static bool_e fallenY = 0;
//static bool_e fallenZ = 0;

static volatile uint32_t t = 0;
void process_ms(void)
{
	if(t)
		t--;
}

static state_e state = INIT;

void molkky_mpu_state_machine(void){



	switch(state)
	{
		case INIT:{
			LEDS_init(I_HAVE_LED_BATTERY);
			LED_set(LED_ID_NETWORK, LED_MODE_OFF);
			debug_printf("On passe dans l'init\n");
			molkky_init_mpu();
			Systick_add_callback_function(&process_ms);
			timer = SYSTICK_get_time_us();
			sampling_period = (1000000 / FREQ);
			state = GET_DATA;
			break;}

		case GET_DATA:{
			if(SYSTICK_get_time_us() - timer > sampling_period){
				molkky_get_angle(&angleX, &angleY, &angleZ);
				debug_printf("X: %ld | Y: %ld | Z: %ld \n", angleX, angleY, angleZ);
				fallenX =(fabs(angleX))<(fabs(fabs(INITIAL_X)-80));
				fallenY =(fabs(angleY))>(fabs(fabs(INITIAL_Y)-80));
				if(fallenX | fallenY){
					state = FALLEN;
//					debug_printf("Quille Tombée \n");
				}
				timer = SYSTICK_get_time_us();
			}



			break;}

		case FALLEN:{
			LED_set(LED_ID_NETWORK, LED_MODE_ON);
			state = UP_AGAIN;

			break;}

		case UP_AGAIN:{
			if(SYSTICK_get_time_us() - timer > sampling_period){
				molkky_get_angle(&angleX, &angleY, &angleZ);
				fallenX =(fabs(angleX))<(fabs(fabs(INITIAL_X)-80));
				fallenY =(fabs(angleY))>(fabs(fabs(INITIAL_Y)-80));
				if(!(fallenX | fallenY)){
					state = GET_DATA;
					LED_set(LED_ID_NETWORK, LED_MODE_OFF);
//					debug_printf("Quille Relevée \n");
				}
				timer = SYSTICK_get_time_us();
			}
			break;}

		case STOP:
			//Mode OFF à coder
			break;
		default:
			break;
	}
#if(GET_OFFSET)
	debug_printf("On passe dans l'init\n");
	Systick_add_callback_function(&process_ms);
	afficher_offset();
#endif
}


#endif


#if OBJECT_ID == MOLKKY_ESB_TX


//static nrf_esb_payload_t        tx_payload = NRF_ESB_CREATE_PAYLOAD(0, 0x01, 0x00, 0x00, 0x00, 0x11, 0x00, 0x00, 0x00);
static nrf_esb_payload_t        tx_payload = NRF_ESB_CREATE_PAYLOAD(0, 0x01,0x1C, 75, 2, 15, 3, 15);


static nrf_esb_payload_t        rx_payload;

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
    nrf_esb_config.bitrate                  = NRF_ESB_BITRATE_2MBPS;
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

typedef enum{
	INIT,
	SEND_DATA,
	WAIT,
	STOP
}state_e;


static volatile uint32_t t = 0;
void process_ms(void)
{
	if(t)
		t--;
}

static state_e state = INIT;
void molkky_esb_tx_state_machine(void){



	switch(state)
	{
		case INIT:{
			    //	Id			|	Type msg	|	BatLvl		|	Accel		|	Angle


			ret_code_t err_code;

			gpio_init();

			err_code = NRF_LOG_INIT(NULL);
			APP_ERROR_CHECK(err_code);

			NRF_LOG_DEFAULT_BACKENDS_INIT();


			err_code = esb_init();
			APP_ERROR_CHECK(err_code);

			NRF_LOG_DEBUG("Enhanced ShockBurst Transmitter Example started.");
			state = SEND_DATA;
			break;}

		case SEND_DATA:{
//			while(1){
				NRF_LOG_DEBUG("Transmitting packet %02x", tx_payload.data[1]);

							tx_payload.noack = false;
							if (nrf_esb_write_payload(&tx_payload) == NRF_SUCCESS)
							{
								// Toggle one of the LEDs.
								nrf_gpio_pin_write(LED_1, !(tx_payload.data[1]%8>0 && tx_payload.data[1]%8<=4));
								nrf_gpio_pin_write(LED_2, !(tx_payload.data[1]%8>1 && tx_payload.data[1]%8<=5));
								nrf_gpio_pin_write(LED_3, !(tx_payload.data[1]%8>2 && tx_payload.data[1]%8<=6));
								nrf_gpio_pin_write(LED_4, !(tx_payload.data[1]%8>3));
								tx_payload.data[1]++;
							}
							else
							{
								NRF_LOG_WARNING("Sending packet failed");
							}

//							nrf_delay_us(50000);
							state = WAIT;
//			}


			break;}

		case WAIT:

			debug_printf("on attend avant d'envoyer à nouveau\n");
			nrf_delay_us(50000);
			state = SEND_DATA;
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





#endif


#if OBJECT_ID == MOLKKY_ESB_RX

typedef enum{
	INIT,
	RECEIVE_DATA,
	WAIT,
	STOP
}state_e;


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

/*lint -save -esym(40, BUTTON_1) -esym(40, BUTTON_2) -esym(40, BUTTON_3) -esym(40, BUTTON_4) -esym(40, LED_1) -esym(40, LED_2) -esym(40, LED_3) -esym(40, LED_4) */

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
            debug_printf("Something received\n");
            if (nrf_esb_read_rx_payload(&rx_payload) == NRF_SUCCESS)
            {
                printf("trame reçue ! \n");

                // Set LEDs identical to the ones on the PTX.
                nrf_gpio_pin_write(LED_1, !(rx_payload.data[1]%8>0 && rx_payload.data[1]%8<=4));
                nrf_gpio_pin_write(LED_2, !(rx_payload.data[1]%8>1 && rx_payload.data[1]%8<=5));
                nrf_gpio_pin_write(LED_3, !(rx_payload.data[1]%8>2 && rx_payload.data[1]%8<=6));
                nrf_gpio_pin_write(LED_4, !(rx_payload.data[1]%8>3));

                debug_printf("Payload values : \n");
				debug_printf("Id : %d\n", rx_payload.data[0]);
				debug_printf("Type : %d\n", rx_payload.data[1]);
				debug_printf("BatLvl : %d\n", rx_payload.data[2]);
				debug_printf("Accel : %d\n", rx_payload.data[3]);
				debug_printf("Angle : %d\n", rx_payload.data[4]);
				debug_printf("data 5 : %d\n", rx_payload.data[5]);
				debug_printf("data 6 : %d\n", rx_payload.data[6]);
				debug_printf("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\n");

                NRF_LOG_DEBUG("Receiving packet: %02x", rx_payload.data[1]);
            }
            break;
    }
}


//void clocks_start( void )
//{
//    NRF_CLOCK->EVENTS_HFCLKSTARTED = 0;
//    NRF_CLOCK->TASKS_HFCLKSTART = 1;
//
//    while (NRF_CLOCK->EVENTS_HFCLKSTARTED == 0);
//}


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
    nrf_esb_config.bitrate                  = NRF_ESB_BITRATE_2MBPS;
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

void molkky_esb_rx_state_machine(void){



	switch(state)
	{
		case INIT:{
//			debug_printf("init started\n");
//			LEDS_init(I_HAVE_LED_BATTERY);
//			LED_set(LED_ID_NETWORK, LED_MODE_OFF);
//			debug_printf("On passe dans l'init\n");
//			Systick_add_callback_function(&process_ms);
//			sending_period = (1000000 / FREQ);

			uint32_t err_code;

			gpio_init();

			err_code = NRF_LOG_INIT(NULL);
			APP_ERROR_CHECK(err_code);

			NRF_LOG_DEFAULT_BACKENDS_INIT();

//			clocks_start();

			err_code = esb_init();
			APP_ERROR_CHECK(err_code);

			NRF_LOG_DEBUG("Enhanced ShockBurst Receiver Example started.");

			err_code = nrf_esb_start_rx();
			APP_ERROR_CHECK(err_code);
			debug_printf("init done\n");


			    //	Id			|	Type msg	|	BatLvl		|	Accel		|	Angle

			    state = RECEIVE_DATA;
			break;}

		case RECEIVE_DATA:{
			while(1){
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

#endif
















































//#include "../config.h"
//#include "molkky.h"
//#include "bsp/mpu6050.h"
//#include "appli/common/systick.h"
//#include "appli/common/buttons.h"
//#include "appli/common/leds.h"
//#include <math.h>
//
//#if OBJECT_ID == MOLKKY_MPU
//
///*
// * Rappel : le MPU6050 comporte un accéléromètre et un gyroscope
// * L'accéléromètre donne une accélération et le gyroscope une vitesse angulaire
// *
// * Gyroscope :
// * 				Sensibilité (°/s)	|	Facteur de sensibilité	|
// * 					+- 250						131
// * 					+- 500						65.5
// * 					+- 1000						32.8
// * 					+- 2000						16.4
// *
//  * Accéléromètre :
// * 				Sensibilité (g)	|	Facteur de sensibilité	|
// * 					+- 2						16,384
// * 					+- 4						8,192
// * 					+- 8						4,096
// * 					+- 16						2,048
// *
// */
//#define PI		3.14159265358979323846
//
//#define SSF_GYRO 65.5
//#define SSF_ACC 4096
//
//#define X 0
//#define Y 1
//#define Z 2
//
//#define YAW   0
//#define PITCH 1
//#define ROLL  2
//
//#define FREQ        250   // Sampling frequency
//
//
//
//static MPU6050_t mpu_datas;
//static MPU6050_Gyroscope_t gyro_sens = MPU6050_Gyroscope_500s;
//static MPU6050_Accelerometer_t acc_sens = MPU6050_Accelerometer_8G;
//
//
////static int16_t gyro_raw[3] = {0,0,0};
////static float gyro_angle[3] = {0,0,0};
////
////static int16_t gyro_offset[3] = {85,8,-8};
////
////static int32_t acc_raw[3] = {0,0,0};
//static float acc_angle[3] = {0,0,0};
//
////static int16_t acc_total_vector;
//static int32_t test[3] = {0,0,0};
////static float measures[3] = {0,0,0};
////static uint16_t  period; // Sampling period
////static uint16_t loop_timer;
////static uint16_t print_timer;
//
//
////static uint8_t init = 1;
//
//static volatile uint32_t t = 0;
//
//void process_ms(void)
//{
//	if(t)
//		t--;
//}
//
////void molkky_mpu_state_machine(void){
////	if(init){
////		Systick_add_callback_function(&process_ms);
////		MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
////		loop_timer = SYSTICK_get_time_us();
////		print_timer = SYSTICK_get_time_us();
////		period = (1000000 / FREQ) ;
////
////		init = 0;
////	}
////
//////	while((SYSTICK_get_time_us() - loop_timer) < period);
////
//////	if(SYSTICK_get_time_us() - loop_timer > period){
//////	readSensor();
//////	calculateAngles();
//////	loop_timer = SYSTICK_get_time_us();
//////
//////	test[X] = (int32_t)(acc_angle[X]);
//////	test[Y] = (int32_t)(acc_angle[Y]);
//////	test[Z] = (int32_t)(acc_angle[Z]);
//////	debug_printf("X: %ld | Y: %ld | Z: %ld \n", test[X], test[Y], test[Z]);
////
//////	}
//////	if(SYSTICK_get_time_us() - print_timer > 1000000){
//////		debug_printf("X: %lf | Y: %lf | Z: %lf\n", measures[X], measures[Y], measures[Z]);
//////		print_timer = SYSTICK_get_time_us();
//////	}
////
////if((SYSTICK_get_time_us() - print_timer)>1000000){
////			print_timer = SYSTICK_get_time_us();
////			readSensor();
////			calculateAngles();
////			test[X] = (int32_t)(acc_angle[X]);
////			test[Y] = (int32_t)(acc_angle[Y]);
////			test[Z] = (int32_t)(acc_angle[Z]);
////			debug_printf("X: %ld | Y: %ld | Z: %ld \n", test[X], test[Y], test[Z]);
////		}
////
////
////}
//
//uint32_t time_start = 0;
//
//void molkky_mpu_state_machine(void){
//	mpu6050_test2();
//	/*
//	Systick_add_callback_function(&process_ms);
//
//	time_start = SYSTICK_get_time_us();
//	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
//
//
//	while(1){
//		if((SYSTICK_get_time_us() - time_start)>1000000){
//			readSensor();
//			calculateAccelerometerAngles();
//			test[X] = (int32_t)(acc_angle[X]);
//			test[Y] = (int32_t)(acc_angle[Y]);
//			test[Z] = (int32_t)(acc_angle[Z]);
//			debug_printf("X: %ld | Y: %ld | Z: %ld \n", test[X], test[Y], test[Z]);
//			time_start = SYSTICK_get_time_us();
//		}
//	}
//	*/
//
//}
//
////void readSensor(void){
////	MPU6050_ReadAllType1(&mpu_datas);
////
////	gyro_raw[X] = mpu_datas.Gyroscope_X;
////	gyro_raw[Y] = mpu_datas.Gyroscope_Y;
////	gyro_raw[Z] = mpu_datas.Gyroscope_Z;
////
////	acc_raw[X] = mpu_datas.Accelerometer_X;
////	acc_raw[Y] = mpu_datas.Accelerometer_Y;
////	acc_raw[Z] = mpu_datas.Accelerometer_Z;
////}
////
////void calculateAngles(){
////	calculateGyroAngles();
////	calculateAccelerometerAngles();
////
////	measures[ROLL]  = measures[ROLL]  * 0.9 + gyro_angle[X] * 0.1;
////	measures[PITCH] = measures[PITCH] * 0.9 + gyro_angle[Y] * 0.1;
////	measures[YAW]   = -gyro_raw[Z] / SSF_GYRO; // Store the angular motion for this axis
////
////}
////
////void calculateGyroAngles()
////{
////  // Subtract offsets
////  gyro_raw[X] -= gyro_offset[X];
////  gyro_raw[Y] -= gyro_offset[Y];
////  gyro_raw[Z] -= gyro_offset[Z];
////
////  // Angle calculation using integration
////  gyro_angle[X] += (gyro_raw[X] / (FREQ * SSF_GYRO));
////  gyro_angle[Y] += (-gyro_raw[Y] / (FREQ * SSF_GYRO)); // Change sign to match the accelerometer's one
////
////  // Transfer roll to pitch if IMU has yawed
////  gyro_angle[Y] += gyro_angle[X] * sin(gyro_raw[Z] * (PI / (FREQ * SSF_GYRO * 180)));
////  gyro_angle[X] -= gyro_angle[Y] * sin(gyro_raw[Z] * (PI / (FREQ * SSF_GYRO * 180)));
////}
////
////void calculateAccelerometerAngles()
////{
////  // Calculate total 3D acceleration vector : sqrt(X + Y + Z)
////  acc_total_vector = sqrt(pow(acc_raw[X], 2) + pow(acc_raw[Y], 2) + pow(acc_raw[Z], 2));
////
////  // To prevent asin to produce a NaN, make sure the input value is within [-1;+1]
////  if (fabs(acc_raw[X]) < acc_total_vector) {
////    acc_angle[X] = asin((float)acc_raw[Y] / acc_total_vector) * (180 / PI); // asin gives angle in radian. Convert to degree multiplying by 180/pi
////  }
////
////  if (fabs(acc_raw[Y]) < acc_total_vector) {
////    acc_angle[Y] = asin((float)acc_raw[X] / acc_total_vector) * (180 / PI);
////  }
////}
////
////
////
////
////void afficher_offset(void){
////	int32_t gyr_x = 0;
////	int32_t gyr_y = 0;
////	int32_t gyr_z = 0;
////	debug_printf("coucou\n");
////	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
////
////	int max_samples = 2000;
////
////	for (int i = 0; i < max_samples; i++) {
////		MPU6050_ReadAllType1(&mpu_datas);
////
////		gyr_x += mpu_datas.Gyroscope_X;
////		gyr_y += mpu_datas.Gyroscope_Y;
////		gyr_z += mpu_datas.Gyroscope_Z;
////
////	}
////
////	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);
////
////
////	// Calculate average offsets
////	gyr_x /= max_samples;
////	gyr_y /= max_samples;
////	gyr_z /= max_samples;
////
////	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);
////
////	gyr_x = 0;
////	gyr_y = 0;
////	gyr_z = 0;
////}
////
////
//#endif
////
////
////
//
//
//
//
//
//
//
//
//
//











/*
 * Code test fréquence acquisition données mpu
 * freq dans un while(1) : 2369Hz
 * freq sans while(1) : 2364Hz
 */

//
///*
// * object_fall_sensor.c
// *
// *  Created on: 2 févr. 2021
// *      Author: norab
// */
//#include "../config.h"
//#include "molkky.h"
//#include "bsp/mpu6050.h"
//#include "appli/common/systick.h"
//#include "appli/common/buttons.h"
//#include "appli/common/leds.h"
//
//#if OBJECT_ID == MOLKKY_MPU
//
//
//static volatile uint32_t t = 0;
//void process_ms(void)
//{
//	if(t)
//		t--;
//}
//
//static int32_t time_start = 0;
//uint32_t cpt = 0;
//uint32_t cpt2 = 0;
//
//static MPU6050_t mpu_datas;
//static MPU6050_Gyroscope_t gyro_sens = MPU6050_Gyroscope_500s;
//static MPU6050_Accelerometer_t acc_sens = MPU6050_Accelerometer_8G;
//
//static int16_t acc_raw[3] = {0,0,0};
//static int16_t gyro_raw[3] = {0,0,0};
//static int16_t gyro_offset[3] = {85,8,-8};
//
//#define FS_GYRO 65.5
//#define FS_ACC 4096
//#define X 0
//#define Y 1
//#define Z 2
//
//void molkky_mpu_state_machine(void){
//	Systick_add_callback_function(&process_ms);
//
//	time_start = SYSTICK_get_time_us();
//	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
//
//
//	while(1){
//		cpt++;
//		cpt2++;
//		MPU6050_ReadAllType1(&mpu_datas);
//		gyro_raw[X] = (mpu_datas.Gyroscope_X - gyro_offset[X]) / FS_GYRO;
//		gyro_raw[Y] = (mpu_datas.Gyroscope_Y - gyro_offset[Y]) / FS_GYRO;
//		gyro_raw[Z] = (mpu_datas.Gyroscope_Z - gyro_offset[Z]) / FS_GYRO;
//
//		acc_raw[X] = mpu_datas.Accelerometer_X;
//		acc_raw[Y] = mpu_datas.Accelerometer_Y;
//		acc_raw[Z] = mpu_datas.Accelerometer_Z;
//		if((SYSTICK_get_time_us() - time_start)>1000000){
//			debug_printf("%ld Hz\n", cpt);
//			cpt = 0;
//			cpt2 = 0;
//			time_start = SYSTICK_get_time_us();
//		}
//	}
//
//}
//
//#endif
