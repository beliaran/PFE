/**
 * Copyright (c) 2014 - 2020, Nordic Semiconductor ASA
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form, except as embedded into a Nordic
 *    Semiconductor ASA integrated circuit in a product or a software update for
 *    such product, must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. Neither the name of Nordic Semiconductor ASA nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * 4. This software, with or without modification, must only be used with a
 *    Nordic Semiconductor ASA integrated circuit.
 *
 * 5. Any software provided in binary form under this license must not be reverse
 *    engineered, decompiled, modified and/or disassembled.
 *
 * THIS SOFTWARE IS PROVIDED BY NORDIC SEMICONDUCTOR ASA "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY, NONINFRINGEMENT, AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL NORDIC SEMICONDUCTOR ASA OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
/** @file
 * @defgroup uart_example_main main.c
 * @{
 * @ingroup uart_example
 * @brief UART Example Application main file.
 *
 * This file contains the source code for a sample application using UART.
 *
 */

#define UART_PRESENT
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include "app_uart.h"
#include "app_error.h"
#include "nrf_delay.h"
#include "nrf_gpio.h"
//#include "boards.h"
#include "nrf.h"
#include "bsp.h"
#include "nrf_uart.h"

#include <stdarg.h>


#define RX_SIMPLE 0
#define TX_SIMPLE 0
#define TX_MSG 1

#define RX_ADV 0
#define TX_ADV 0

#define bool_e int
#define TRUE 1
#define FALSE 0
#define SOH 0xBA
#define EOT 0xDA
typedef void(*callback_fun_c_t)(char c);	//Type pointeur sur fonction qui admet un caractère en paramètre



#define MAX_TEST_DATA_BYTES     (15U)                /**< max number of test bytes to be used for tx and rx. */
#define UART_TX_BUF_SIZE 256                         /**< UART TX buffer size. */
#define UART_RX_BUF_SIZE 256                         /**< UART RX buffer size. */



#if(RX_SIMPLE)

void uart_error_handle(app_uart_evt_t * p_event)
{
    if (p_event->evt_type == APP_UART_COMMUNICATION_ERROR)
    {
        APP_ERROR_HANDLER(p_event->data.error_communication);
    }
    else if (p_event->evt_type == APP_UART_FIFO_ERROR)
    {
        APP_ERROR_HANDLER(p_event->data.error_code);
    }
}



/* When UART is used for communication with the host do not use flow control.*/
#define UART_HWFC APP_UART_FLOW_CONTROL_DISABLED


/**
 * @brief Function for main application entry.
 */
int main(void)
{
    uint32_t err_code;

    bsp_board_init(BSP_INIT_LEDS);

    const app_uart_comm_params_t comm_params =
      {
          27,//RX
          30,//TX
          7,//RTS
          9,//CTS
		  APP_UART_FLOW_CONTROL_DISABLED,
          false,
          NRF_UART_BAUDRATE_115200

      };

    APP_UART_FIFO_INIT(&comm_params,
                         UART_RX_BUF_SIZE,
                         UART_TX_BUF_SIZE,
                         uart_error_handle,
                         APP_IRQ_PRIORITY_LOWEST,
                         err_code);

    APP_ERROR_CHECK(err_code);

    printf("\r\nUART example started.\r\n");

	nrf_gpio_cfg_output(17); //led rouge
	nrf_gpio_pin_clear(17);
	nrf_gpio_cfg_output(19); //led bleue
	nrf_gpio_pin_clear(19);

    while (true)
          {
              uint8_t cr;
              while (app_uart_get(&cr) != NRF_SUCCESS);
              nrf_gpio_pin_set(17);

              if (cr == 'a' || cr == 'A')
              {
           	    nrf_gpio_pin_set(17);

              }
              else if(cr == 'e' || cr == 'E'){
           	   nrf_gpio_pin_clear(17);
              }
          }
}


/** @} */
#endif

#if(TX_SIMPLE)
void uart_error_handle(app_uart_evt_t * p_event)
{
    if (p_event->evt_type == APP_UART_COMMUNICATION_ERROR)
    {
        APP_ERROR_HANDLER(p_event->data.error_communication);
    }
    else if (p_event->evt_type == APP_UART_FIFO_ERROR)
    {
        APP_ERROR_HANDLER(p_event->data.error_code);
    }
}



/* When UART is used for communication with the host do not use flow control.*/
#define UART_HWFC APP_UART_FLOW_CONTROL_DISABLED


/**
 * @brief Function for main application entry.
 */
int main(void)
{
    uint32_t err_code;

    bsp_board_init(BSP_INIT_LEDS);

    const app_uart_comm_params_t comm_params =
      {
          27,//RX
          30,//TX
          7,//RTS
          9,//CTS
		  APP_UART_FLOW_CONTROL_DISABLED,
          false,
          NRF_UART_BAUDRATE_115200

      };

    APP_UART_FIFO_INIT(&comm_params,
                         UART_RX_BUF_SIZE,
                         UART_TX_BUF_SIZE,
                         uart_error_handle,
                         APP_IRQ_PRIORITY_LOWEST,
                         err_code);

    APP_ERROR_CHECK(err_code);

    printf("\r\nUART example started.\r\n");

	nrf_gpio_cfg_output(17); //led rouge
	nrf_gpio_pin_clear(17);
	nrf_gpio_cfg_output(19); //led bleue
	nrf_gpio_pin_clear(19);

       uint8_t cr;
       while (true)
             {
                 cr = (uint8_t)'a';
                 nrf_gpio_pin_set(19);
                 while (app_uart_put(cr) != NRF_SUCCESS);
                 nrf_delay_ms(2000);

                cr = (uint8_t)'e';
 				nrf_gpio_pin_clear(19);
 				while (app_uart_put(cr) != NRF_SUCCESS);
 				nrf_delay_ms(2000);
             }
}
#endif

#if(TX_MSG)
void uart_error_handle(app_uart_evt_t * p_event)
{
    if (p_event->evt_type == APP_UART_COMMUNICATION_ERROR)
    {
        APP_ERROR_HANDLER(p_event->data.error_communication);
    }
    else if (p_event->evt_type == APP_UART_FIFO_ERROR)
    {
        APP_ERROR_HANDLER(p_event->data.error_code);
    }
}



/* When UART is used for communication with the host do not use flow control.*/
#define UART_HWFC APP_UART_FLOW_CONTROL_DISABLED


/**
 * @brief Function for main application entry.
 */
int main(void)
{
    uint32_t err_code;

    bsp_board_init(BSP_INIT_LEDS);

    const app_uart_comm_params_t comm_params =
      {
          27,//RX
          30,//TX
          7,//RTS
          9,//CTS
		  APP_UART_FLOW_CONTROL_DISABLED,
          false,
          NRF_UART_BAUDRATE_115200

      };

    APP_UART_FIFO_INIT(&comm_params,
                         UART_RX_BUF_SIZE,
                         UART_TX_BUF_SIZE,
                         uart_error_handle,
                         APP_IRQ_PRIORITY_LOWEST,
                         err_code);

    APP_ERROR_CHECK(err_code);

    printf("\r\nUART example started.\r\n");

	nrf_gpio_cfg_output(17); //led rouge
	nrf_gpio_pin_clear(17);
	nrf_gpio_cfg_output(19); //led bleue
	nrf_gpio_pin_clear(19);

//	uint8_t * tx_data = (uint8_t *)("\rUART_TEST\n");
	uint8_t tx_data[8] = {12,1,10,0,0,42,(uint8_t)'\n'};

//       uint8_t cr;
       while (true)
             {
//                 cr = (uint8_t)'a';
//                 nrf_gpio_pin_set(19);
//                 while (app_uart_put(cr) != NRF_SUCCESS);
//                 nrf_delay_ms(2000);
//
//                cr = (uint8_t)'e';
// 				nrf_gpio_pin_clear(19);
// 				while (app_uart_put(cr) != NRF_SUCCESS);
// 				nrf_delay_ms(2000);



// 				 for (uint32_t i = 0; i < MAX_TEST_DATA_BYTES; i++)
    	   	   	   for (uint32_t i = 0; i < 8; i++)
 				    {
// 				        uint32_t err_code;
 				        while (app_uart_put(tx_data[i]) != NRF_SUCCESS);
             }
 				 nrf_gpio_pin_toggle(19);
				nrf_delay_ms(1000);
             }
}
#endif

#if(RX_ADV)

/*
Norme des messages transmis :

SOH 	SIZE	DATA(s)	EOT
BA		01		HH		DA


Exemples de messages :


BA 00 DA

BA 01 01 DA

BA 01 00 DA

BA 04 D1 D2 D3 D4 DA

*/
static volatile bool_e initialized = FALSE;

void SERIAL_DIALOG_display_msg(uint8_t size, uint8_t * datas);
static void SERIAL_DIALOG_process_msg(uint8_t size, uint8_t * datas);
static void SERIAL_DIALOG_uart_event_handler(app_uart_evt_t * p_event);
static void SERIAL_DIALOG_parse_rx(uint8_t c);
void SERIAL_DIALOG_puts(char* s);

#define RX_BUF_SIZE		128
#define TX_BUF_SIZE		128

static app_uart_buffers_t buffers;
static uint8_t     rx_buf[RX_BUF_SIZE];
static uint8_t     tx_buf[TX_BUF_SIZE];

#define DEBUG_PRINTF_BUFFER_SIZE	128
uint32_t debug_printf(char * format, ...)
{
	va_list args_list;
	va_start(args_list, format);

	static char buf[DEBUG_PRINTF_BUFFER_SIZE];
	int ret;
	ret = (uint32_t)vsnprintf((char*)buf, DEBUG_PRINTF_BUFFER_SIZE, format, args_list);	//Prépare la chaine à envoyer.

	SERIAL_DIALOG_puts(buf);

	va_end(args_list);
	return ret;
}

void SERIAL_DIALOG_init(void)
{
	const app_uart_comm_params_t comm_params =
	  {
			  27,//RX
			           30,//TX
			           7,//RTS
			           9,//CTS
					   APP_UART_FLOW_CONTROL_DISABLED,
			           false,
			           NRF_UART_BAUDRATE_115200
	  };

	buffers.rx_buf      = rx_buf;
	buffers.rx_buf_size = sizeof (rx_buf);
	buffers.tx_buf      = tx_buf;
	buffers.tx_buf_size = sizeof (tx_buf);

	app_uart_init(&comm_params, &buffers, &SERIAL_DIALOG_uart_event_handler, APP_IRQ_PRIORITY_LOWEST);
	initialized = TRUE;

	SERIAL_DIALOG_puts("uart initialized\n");
}

static callback_fun_c_t rx_callback = NULL;

void SERIAL_DIALOG_set_rx_callback(callback_fun_c_t new_callback)
{
	rx_callback = new_callback;
}


static void SERIAL_DIALOG_uart_event_handler(app_uart_evt_t * p_event)
{
	uint8_t c;
	switch(p_event->evt_type)
	{
		case APP_UART_DATA_READY:
			while(app_uart_get(&c) == NRF_SUCCESS)
			{
				//SERIAL_DIALOG_putc(c);
				SERIAL_DIALOG_parse_rx(c);
			}
			break;
		case APP_UART_COMMUNICATION_ERROR:
			if(p_event->data.error_communication == 0x01)
				SERIAL_DIALOG_puts("overrun\n");
			/*else
				SERIAL_DIALOG_puts("unknow com error\n");
*/
			break;
		case APP_UART_FIFO_ERROR:
			SERIAL_DIALOG_puts("fifo error\n");
			break;
		case APP_UART_TX_EMPTY:
			break;
		case APP_UART_DATA:
			SERIAL_DIALOG_puts("data error\n");
			//never happen if fifo is enabled.
			break;
		default:
			break;
	}
}


void SERIAL_DIALOG_putc(char c)
{
	app_uart_put(c);
}

void SERIAL_DIALOG_puts(char* s)
{
	static int reentrance_detection = false;
	if(!reentrance_detection)
	{
		reentrance_detection = TRUE;
		if(!initialized)
			SERIAL_DIALOG_init();
		for(uint16_t i = 0; s[i]; i++)
			while(app_uart_put(s[i])!=NRF_SUCCESS);
		reentrance_detection = FALSE;
	}
}



void SERIAL_DIALOG_process_main()
{


}



/**
 * @brief	Cette fonction assure le traitement des caractères reçus sur l'UART. Les ocets sont rangés dans les variables size et datas, avant d'être traité lorsque le message complet et valide est reçu.
 * @post	La fonction SERIAL_DIALOG_process_msg() sera appelé si un message au format valide est reçu
 * @pre		Cette fonction doit être appelée pour chaque caractère reçu
 */
static void SERIAL_DIALOG_parse_rx(uint8_t c)
{
//	static uint8_t datas[255];
//	static bool_e flag_FA = FALSE;
//	static uint16_t index = 0;
//	static uint8_t size;
//	switch(index)
//	{
//		case 0:
//			if(c == SOH)
//				index++;
//			else
//			{
//				if(rx_callback != NULL)
//					rx_callback(c);
//			}
//			break;
//		case 1:
//			size = c;
//			index++;
//			break;
//		default:
//			if(index-2 == size)
//			{
//				if(c == EOT)//ok, fin du message !
//					SERIAL_DIALOG_process_msg(size, datas);
//				index = 0;
//			}
//			else if(flag_FA)
//			{
//				datas[index-2] = c&0xFA;
//				flag_FA = FALSE;
//				index++;
//			}
//			else if(c == 0xFA)
//				flag_FA = TRUE;		//on ne touche pas à l'index, le prochain caractère sera compté et pris en compte avec le masque &0xFA
//			else if(c == SOH)
//				index = 1;
//			else if(c == EOT)
//				index = 0;
//			else if(index-2 < size)
//			{
//				datas[index-2] = c;
//				index++;
//			}
//			else
//				index = 0;
//			break;
//	}

}


/**
 * @brief	Cette fonction permet l'envoi d'un message sur la liaison série.
 * @pre		Le tableau datas doit contenir au moins 'size' octet. Sinon, le pointeur 'datas' peut être NULL.
 */
void SERIAL_DIALOG_send_msg(uint8_t size, uint8_t * datas)
{
	uint8_t i;
	char c;
	if(size > 0 && datas == NULL)
		return;

	SERIAL_DIALOG_putc(SOH);
	SERIAL_DIALOG_putc(size);

	for(i=0; i<size; i++)
	{
		c = datas[i];
		if(c == 0xBA || c == 0xDA || c == 0xFA)		//si l'octet à transmettre est 0xBA, 0xDA, 0xFA
		{
			SERIAL_DIALOG_putc(0xFA);				//on transmet 0xFA...
			SERIAL_DIALOG_putc(c|0x0F);				//suivi de 0xBF, 0xDF ou 0xFF
		}
		else
			SERIAL_DIALOG_putc(c);					//sinon, on transmets c directement
	}
	SERIAL_DIALOG_putc(EOT);	//EOT
}

/**
 * @brief	Cette fonction traite le message reçu et agit en conséquence.
 */
static void SERIAL_DIALOG_process_msg(uint8_t size, uint8_t * datas)
{
//	SECRETARY_process_msg_from_uart(size, datas);
	nrf_delay_ms(1);
}

/**
 * @brief Cette fonction privée affiche le contenu d'un message donné en paramètre
 */
void SERIAL_DIALOG_display_msg(uint8_t size, uint8_t * datas)
{
	debug_printf("[%d data%s ", size, (size>1)?"s]":"]");
	for(uint8_t i = 0 ; i<size ; i++)
		debug_printf("%02x ", datas[i]);
	debug_printf("\n");
}

int main(void)
{
	SERIAL_DIALOG_init();
    printf("\r\nUART example started.\r\n");

	nrf_gpio_cfg_output(17); //led rouge
	nrf_gpio_pin_clear(17);
	nrf_gpio_cfg_output(19); //led bleue
	nrf_gpio_pin_clear(19);

    while (true)
          {
    	//nothing to do
          }
}
#endif

#if(TX_ADV)

#endif



#if(0)
#define UART_PRESENT
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include "app_uart.h"
#include "app_error.h"
#include "nrf_delay.h"
#include "nrf_gpio.h"
//#include "boards.h"
#include "nrf.h"
#include "bsp.h"
#if defined (UART_PRESENT)
#include "nrf_uart.h"
#endif
#if defined (UARTE_PRESENT)
#include "nrf_uarte.h"
#endif

#define RECEIVER 0
#define TEST1 1
#define SOH		0xBA		//Start Of Header
#define EOT		0xDA

//#define ENABLE_LOOPBACK_TEST  /**< if defined, then this example will be a loopback test, which means that TX should be connected to RX to get data loopback. */

#define MAX_TEST_DATA_BYTES     (15U)                /**< max number of test bytes to be used for tx and rx. */
#define UART_TX_BUF_SIZE 256                         /**< UART TX buffer size. */
#define UART_RX_BUF_SIZE 256                         /**< UART RX buffer size. */

#define DEBUG_PRINTF_BUFFER_SIZE	128

#define bool_e int
#define FALSE 0
#define TRUE 1

static volatile bool_e initialized = FALSE;

void SERIAL_DIALOG_display_msg(uint8_t size, uint8_t * datas);
//static void SERIAL_DIALOG_process_msg(uint8_t size, uint8_t * datas);
static void SERIAL_DIALOG_uart_event_handler(app_uart_evt_t * p_event);
static void SERIAL_DIALOG_parse_rx(uint8_t c);

#define RX_BUF_SIZE		128
#define TX_BUF_SIZE		128

static app_uart_buffers_t buffers;
static uint8_t     rx_buf[RX_BUF_SIZE];
static uint8_t     tx_buf[TX_BUF_SIZE];

void SERIAL_DIALOG_init(void)
{
	const app_uart_comm_params_t comm_params =
	  {
		27,
		30,
		7,//RTS_PIN_NUMBER,
		9,//CTS_PIN_NUMBER,
		APP_UART_FLOW_CONTROL_DISABLED,
		false,	//use parity
#ifdef UART_AT_BAUDRATE_9600
         NRF_UARTE_BAUDRATE_9600
#else
		 NRF_UARTE_BAUDRATE_115200
#endif
	  };

	buffers.rx_buf      = rx_buf;
	buffers.rx_buf_size = sizeof (rx_buf);
	buffers.tx_buf      = tx_buf;
	buffers.tx_buf_size = sizeof (tx_buf);

	app_uart_init(&comm_params, &buffers, &SERIAL_DIALOG_uart_event_handler, APP_IRQ_PRIORITY_LOWEST);
	initialized = TRUE;

//	SERIAL_DIALOG_puts("uart initialized\n");
}

//static callback_fun_c_t rx_callback = NULL;

//void SERIAL_DIALOG_set_rx_callback(callback_fun_c_t new_callback)
//{
//	rx_callback = new_callback;
//}

void SERIAL_DIALOG_uart_event_handler(app_uart_evt_t * p_event)
{
	uint8_t c;
	switch(p_event->evt_type){
	case APP_UART_DATA_READY:
		while(app_uart_get(&c) == NRF_SUCCESS)
		{
			//SERIAL_DIALOG_putc(c);
			SERIAL_DIALOG_parse_rx(c);
		}
		break;
	case APP_UART_COMMUNICATION_ERROR:
		APP_ERROR_HANDLER(p_event->data.error_communication);
		break;
	case APP_UART_FIFO_ERROR:
		 APP_ERROR_HANDLER(p_event->data.error_code);
		break;
	default:
		break;
	}
}



void uart_error_handle(app_uart_evt_t * p_event)
{
#if(RECEIVER)
	uint8_t c;
#endif
	switch(p_event->evt_type){
#if(RECEIVER)
	case APP_UART_DATA_READY:
		while(app_uart_get(&c) == NRF_SUCCESS)
		{
			//SERIAL_DIALOG_putc(c);
			SERIAL_DIALOG_parse_rx(c);
		}
		break;
#endif
	case APP_UART_COMMUNICATION_ERROR:
		APP_ERROR_HANDLER(p_event->data.error_communication);
		break;
	case APP_UART_FIFO_ERROR:
		 APP_ERROR_HANDLER(p_event->data.error_code);
		break;
	default:
		break;
	}
}

void SERIAL_DIALOG_putc(char c)
{
	app_uart_put(c);
}

void SERIAL_DIALOG_puts(char * s)
{
	static bool_e reentrance_detection = FALSE;
	if(!reentrance_detection)
	{
		reentrance_detection = TRUE;
		if(!initialized)
			SERIAL_DIALOG_init();
		for(uint16_t i = 0; s[i]; i++)
			while(app_uart_put(s[i])!=NRF_SUCCESS);
		reentrance_detection = FALSE;
	}
}

static void SERIAL_DIALOG_parse_rx(uint8_t c)
{
#if(RECEIVER)
	static uint8_t datas[255];

//	static bool_e flag_FA = FALSE;
	static uint16_t index = 0;
//	static uint8_t size;
	if(index<255){
		if(c == '\r'){
			index = 0;

			nrf_gpio_pin_write(19, datas[0]);
			nrf_gpio_pin_write(17, datas[1]);

		}
		else{
			datas[index]=c;
		}
	}
#endif



//	switch(index)
//	{
//		case 0:
//			if(c == SOH)
//				index++;
//			else
//			{
//				if(rx_callback != NULL)
//					rx_callback(c);
//			}
//			break;
//		case 1:
//			size = c;
//			index++;
//			break;
//		default:
//			if(index-2 == size)
//			{
//				if(c == EOT)//ok, fin du message !
//					SERIAL_DIALOG_process_msg(size, datas);
//				index = 0;
//			}
//			else if(flag_FA)
//			{
//				datas[index-2] = c&0xFA;
//				flag_FA = FALSE;
//				index++;
//			}
//			else if(c == 0xFA)
//				flag_FA = TRUE;		//on ne touche pas à l'index, le prochain caractère sera compté et pris en compte avec le masque &0xFA
//			else if(c == SOH)
//				index = 1;
//			else if(c == EOT)
//				index = 0;
//			else if(index-2 < size)
//			{
//				datas[index-2] = c;
//				index++;
//			}
//			else
//				index = 0;
//			break;
//	}
}

//static void SERIAL_DIALOG_process_msg(uint8_t size, uint8_t * datas)
//{
//	//c'est à ce moment qu'on envoie la tram en bluetooth
//}


#ifdef ENABLE_LOOPBACK_TEST
/* Use flow control in loopback test. */
#define UART_HWFC APP_UART_FLOW_CONTROL_ENABLED

/** @brief Function for setting the @ref ERROR_PIN high, and then enter an infinite loop.
 */
static void show_error(void)
{

    bsp_board_leds_on();
    while (true)
    {
        // Do nothing.
    }
}


/** @brief Function for testing UART loop back.
 *  @details Transmitts one character at a time to check if the data received from the loopback is same as the transmitted data.
 *  @note  @ref TX_PIN_NUMBER must be connected to @ref RX_PIN_NUMBER)
 */
static void uart_loopback_test()
{
    uint8_t * tx_data = (uint8_t *)("\r\nLOOPBACK_TEST\r\n");
    uint8_t   rx_data;

    // Start sending one byte and see if you get the same
    for (uint32_t i = 0; i < MAX_TEST_DATA_BYTES; i++)
    {
        uint32_t err_code;
        while (app_uart_put(tx_data[i]) != NRF_SUCCESS);

        nrf_delay_ms(10);
        err_code = app_uart_get(&rx_data);

        if ((rx_data != tx_data[i]) || (err_code != NRF_SUCCESS))
        {
            show_error();
        }
    }
    return;
}
#else
/* When UART is used for communication with the host do not use flow control.*/
#define UART_HWFC APP_UART_FLOW_CONTROL_DISABLED
#endif


/**
 * @brief Function for main application entry.
 */
int main(void)
{


//    bsp_board_init(BSP_INIT_LEDS);
#if(TEST1)
	    uint32_t err_code;
    const app_uart_comm_params_t comm_params =
      {
//          RX_PIN_NUMBER,
//          TX_PIN_NUMBER,
//          RTS_PIN_NUMBER,
//          CTS_PIN_NUMBER,
//          UART_HWFC,
//          false,


#if(RECEIVER)
          27,
          30,
#else
		  30,
		  27,
#endif
		  0,
		  0,
		  APP_UART_FLOW_CONTROL_DISABLED,
          false,

          NRF_UART_BAUDRATE_115200
      };

    APP_UART_FIFO_INIT(&comm_params,
                         UART_RX_BUF_SIZE,
                         UART_TX_BUF_SIZE,
                         uart_error_handle,
                         APP_IRQ_PRIORITY_LOWEST,
                         err_code);

    APP_ERROR_CHECK(err_code);


#else
    SERIAL_DIALOG_init();
#endif
//    while (true)
//    {
//        uint8_t cr;
//        while (app_uart_get(&cr) != NRF_SUCCESS);
//        while (app_uart_put(cr) != NRF_SUCCESS);
//
//        if (cr == 'q' || cr == 'Q')
//        {
//            printf(" \r\nExit!\r\n");
//
//            while (true)
//            {
//                // Do nothing.
//            }
//        }
//    }


#if(RECEIVER)
    nrf_gpio_cfg_output(17); //led port 0
    nrf_gpio_pin_dir_set(17, NRF_GPIO_PIN_DIR_OUTPUT);// LED
//    nrf_gpio_pin_set(17);
//    nrf_delay_ms(5000);
    nrf_gpio_pin_clear(17);

    while (true)
       {
//           uint8_t cr;
//           while (app_uart_get(&cr) != NRF_SUCCESS);
////           while (app_uart_put(cr) != NRF_SUCCESS);
//           nrf_gpio_pin_set(17);
//
//           if (cr == 'a' || cr == 'A')
//           {
//        	    nrf_gpio_pin_set(17);
//
//           }
//           else if(cr == 'e' || cr == 'E'){
//        	   nrf_gpio_pin_clear(17);
//           }
       }
#endif


#if(!RECEIVER)
    nrf_gpio_cfg_output(19); //led port 0
      nrf_gpio_pin_dir_set(19, NRF_GPIO_PIN_DIR_OUTPUT);// LED
      nrf_gpio_pin_clear(19);

      nrf_gpio_cfg_output(17); //led port 0
      nrf_gpio_pin_dir_set(17, NRF_GPIO_PIN_DIR_OUTPUT);// LED
  //    nrf_gpio_pin_set(17);
  //    nrf_delay_ms(5000);
      nrf_gpio_pin_clear(17);

#if(TEST1)
      uint8_t cr;
      while (true)
            {
                cr = (uint8_t)'a';
                nrf_gpio_pin_set(19);
                while (app_uart_put(cr) != NRF_SUCCESS);
//                while (app_uart_get(&cr) != NRF_SUCCESS);
//	//           while (app_uart_put(cr) != NRF_SUCCESS);
//                nrf_gpio_pin_set(17);
//
//			   if (cr == 'a' || cr == 'A')
//			   {
//					nrf_gpio_pin_set(17);
//
//			   }
//			   else if(cr == 'e' || cr == 'E'){
//				   nrf_gpio_pin_clear(17);
//			   }

                nrf_delay_ms(2000);

                uint8_t cr = (uint8_t)'e';
				nrf_gpio_pin_clear(19);
				while (app_uart_put(cr) != NRF_SUCCESS);
//				while (app_uart_get(&cr) != NRF_SUCCESS);
//				//           while (app_uart_put(cr) != NRF_SUCCESS);
//			   nrf_gpio_pin_set(17);
//
//			   if (cr == 'a' || cr == 'A')
//			   {
//					nrf_gpio_pin_set(17);
//			   }
//			   else if(cr == 'e' || cr == 'E'){
//				   nrf_gpio_pin_clear(17);
//			   }
			   nrf_delay_ms(2000);
            }
#else

      char* tx_data = ("11\r");

         // Start sending one byte and see if you get the same
	while(true){

//		tx_data = "10\r";
//		for(uint16_t i = 0; i<3; i++)
		tx_data = "a";
		for(uint16_t i = 0; i<1; i++)
		while(app_uart_put(tx_data[i])!=NRF_SUCCESS);
//		nrf_gpio_pin_write(19, 1);
		nrf_gpio_pin_set(19);
//		nrf_gpio_pin_write(17, 0);
		nrf_gpio_pin_clear(17);

		nrf_delay_ms(1000);


//		tx_data = "00\r";
//		for(uint16_t i = 0; i<3; i++)
		tx_data = "e";
		for(uint16_t i = 0; i<1; i++)
		while(app_uart_put(tx_data[i])!=NRF_SUCCESS);
//		nrf_gpio_pin_write(19, 0);
		nrf_gpio_pin_clear(19);
//		nrf_gpio_pin_write(17, 0);
		nrf_gpio_pin_clear(17);
		nrf_delay_ms(1000);



//		tx_data = "01\r";
//		for(uint16_t i = 0; i<3; i++)
		tx_data = "a";
		for(uint16_t i = 0; i<1; i++)
		while(app_uart_put(tx_data[i])!=NRF_SUCCESS);
//		nrf_gpio_pin_write(19, 0);
		nrf_gpio_pin_clear(19);
//		nrf_gpio_pin_write(17, 1);
		nrf_gpio_pin_set(17);

		nrf_delay_ms(1000);

//		tx_data = "11\r";
//		for(uint16_t i = 0; i<3; i++)
		tx_data = "e";
		for(uint16_t i = 0; i<1; i++)
		while(app_uart_put(tx_data[i])!=NRF_SUCCESS);
//		nrf_gpio_pin_write(19, 1);
		nrf_gpio_pin_set(19);
//		nrf_gpio_pin_write(17, 1);
		nrf_gpio_pin_set(17);
		nrf_delay_ms(1000);

	}
#endif
#endif
//#else
//
//
//    // This part of the example is just for testing the loopback .
//    while (true)
//    {
//        uart_loopback_test();
//    }
//#endif
}


/** @} */
#endif
