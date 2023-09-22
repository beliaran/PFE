/*
 * object_fall_sensor.h
 *
 *  Created on: 3 févr. 2021
 *      Author: norab
 */

#ifndef APPLI_OBJECTS_MOLKKY_H_
#define APPLI_OBJECTS_MOLKKY_H_

void molkky_mpu_state_machine(void);

void molkky_esb_tx_state_machine(void);
void molkky_esb_rx_state_machine(void);
void molkky_blinky_state_machine(void);
void molkky_pin_state_machine(void);
void molkky_basket_state_machine(void);
//void afficher_offset(void);
//void calculateAngles(void);
//void readSensor(void);
//void calculateGyroAngles(void);
//void calculateAccelerometerAngles(void);
//


#endif /* APPLI_OBJECTS_OBJECT_FALL_SENSOR_H_ */

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
//#define GET_OFFSET 0
//
//#define X 0
//#define Y 1
//#define Z 2
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
//static MPU6050_Gyroscope_t gyro_sens = MPU6050_Gyroscope_500s;
//static MPU6050_Accelerometer_t acc_sens = MPU6050_Accelerometer_8G;
//
//#define FS_GYRO 65.5
//#define FS_ACC 4096
//
//static MPU6050_t mpu_datas;
//static int16_t acc_raw[3] = {0,0,0};
//static int16_t gyro_raw[3] = {0,0,0};
//static int16_t gyro_offset[3] = {85,8,-8};
//static float accX, accY, accZ,gyroX,gyroY,gyroZ;
//
//
//
//typedef enum{
//	INIT,
//	GET_DATA,
//	FALLEN,
//	STOP
//}state_e;
//
//static volatile uint32_t t = 0;
//void process_ms(void)
//{
//	if(t)
//		t--;
//}
//
//void molkky_mpu_state_machine(void){
//
//	static state_e state = INIT;
//    LEDS_init(I_HAVE_LED_BATTERY);
//	switch(state)
//	{
//		case INIT:{
//
//			debug_printf("On passe dans l'init\n");
//			MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
//			Systick_add_callback_function(&process_ms);
//			state = GET_DATA;
//			break;}
//
//		case GET_DATA:{
//
//
//			break;}
//
//		case FALLEN:{
//			state = GET_DATA;
//
//			break;}
//
//		case STOP:
//			//Mode OFF à coder
//			break;
//		default:
//			break;
//	}
//#if(GET_OFFSET)
//	debug_printf("On passe dans l'init\n");
//	Systick_add_callback_function(&process_ms);
//	afficher_offset();
//#endif
//}
//
//
//void get_MPU_values(void){
//	//On récupère les données brutes auxquelles on soustrait l'offset et on divise le tout par le facteur de sensibilité
//	MPU6050_ReadAllType1(&mpu_datas);
//	gyro_raw[X] = (mpu_datas.Gyroscope_X - gyro_offset[X]) / FS_GYRO;
//	gyro_raw[Y] = (mpu_datas.Gyroscope_Y - gyro_offset[Y]) / FS_GYRO;
//	gyro_raw[Z] = (mpu_datas.Gyroscope_Z - gyro_offset[Z]) / FS_GYRO;
//
//	acc_raw[X] = mpu_datas.Accelerometer_X;
//	acc_raw[Y] = mpu_datas.Accelerometer_Y;
//	acc_raw[Z] = mpu_datas.Accelerometer_Z;
//
//
//
//}
//
//
//
//void afficher_offset(void){
//	int32_t gyr_x = 0;
//	int32_t gyr_y = 0;
//	int32_t gyr_z = 0;
//	debug_printf("coucou\n");
//	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
//
//	int max_samples = 2000;
//
//	for (int i = 0; i < max_samples; i++) {
//		MPU6050_ReadAllType1(&mpu_datas);
//
//		gyr_x += mpu_datas.Gyroscope_X;
//		gyr_y += mpu_datas.Gyroscope_Y;
//		gyr_z += mpu_datas.Gyroscope_Z;
//
//	}
//
//	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);
//
//
//	// Calculate average offsets
//	gyr_x /= max_samples;
//	gyr_y /= max_samples;
//	gyr_z /= max_samples;
//
//	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);
//
//	gyr_x = 0;
//	gyr_y = 0;
//	gyr_z = 0;
//}
//
//
//
//#endif









//
//
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
//static int16_t gyro_raw[3] = {0,0,0};
//static float gyro_angle[3] = {0,0,0};
//
//static int16_t gyro_offset[3] = {85,8,-8};
//
//static int32_t acc_raw[3] = {0,0,0};
//static float acc_angle[3] = {0,0,0};
//
//static int16_t acc_total_vector;
//static int32_t test[3] = {0,0,0};
//static float measures[3] = {0,0,0};
//static uint16_t  period; // Sampling period
//static uint16_t loop_timer;
//static uint16_t print_timer;
//
//
//static uint8_t init = 1;
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
//
//}
//
//void readSensor(void){
//	MPU6050_ReadAllType1(&mpu_datas);
//
//	gyro_raw[X] = mpu_datas.Gyroscope_X;
//	gyro_raw[Y] = mpu_datas.Gyroscope_Y;
//	gyro_raw[Z] = mpu_datas.Gyroscope_Z;
//
//	acc_raw[X] = mpu_datas.Accelerometer_X;
//	acc_raw[Y] = mpu_datas.Accelerometer_Y;
//	acc_raw[Z] = mpu_datas.Accelerometer_Z;
//}
//
//void calculateAngles(){
//	calculateGyroAngles();
//	calculateAccelerometerAngles();
//
//	measures[ROLL]  = measures[ROLL]  * 0.9 + gyro_angle[X] * 0.1;
//	measures[PITCH] = measures[PITCH] * 0.9 + gyro_angle[Y] * 0.1;
//	measures[YAW]   = -gyro_raw[Z] / SSF_GYRO; // Store the angular motion for this axis
//
//}
//
//void calculateGyroAngles()
//{
//  // Subtract offsets
//  gyro_raw[X] -= gyro_offset[X];
//  gyro_raw[Y] -= gyro_offset[Y];
//  gyro_raw[Z] -= gyro_offset[Z];
//
//  // Angle calculation using integration
//  gyro_angle[X] += (gyro_raw[X] / (FREQ * SSF_GYRO));
//  gyro_angle[Y] += (-gyro_raw[Y] / (FREQ * SSF_GYRO)); // Change sign to match the accelerometer's one
//
//  // Transfer roll to pitch if IMU has yawed
//  gyro_angle[Y] += gyro_angle[X] * sin(gyro_raw[Z] * (PI / (FREQ * SSF_GYRO * 180)));
//  gyro_angle[X] -= gyro_angle[Y] * sin(gyro_raw[Z] * (PI / (FREQ * SSF_GYRO * 180)));
//}
//
//void calculateAccelerometerAngles()
//{
//  // Calculate total 3D acceleration vector : sqrt(X + Y + Z)
//  acc_total_vector = sqrt(pow(acc_raw[X], 2) + pow(acc_raw[Y], 2) + pow(acc_raw[Z], 2));
//
//  // To prevent asin to produce a NaN, make sure the input value is within [-1;+1]
//  if (fabs(acc_raw[X]) < acc_total_vector) {
//    acc_angle[X] = asin((float)acc_raw[Y] / acc_total_vector) * (180 / PI); // asin gives angle in radian. Convert to degree multiplying by 180/pi
//  }
//
//  if (fabs(acc_raw[Y]) < acc_total_vector) {
//    acc_angle[Y] = asin((float)acc_raw[X] / acc_total_vector) * (180 / PI);
//  }
//}
//
//
//
//
//void afficher_offset(void){
//	int32_t gyr_x = 0;
//	int32_t gyr_y = 0;
//	int32_t gyr_z = 0;
//	debug_printf("coucou\n");
//	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
//
//	int max_samples = 2000;
//
//	for (int i = 0; i < max_samples; i++) {
//		MPU6050_ReadAllType1(&mpu_datas);
//
//		gyr_x += mpu_datas.Gyroscope_X;
//		gyr_y += mpu_datas.Gyroscope_Y;
//		gyr_z += mpu_datas.Gyroscope_Z;
//
//	}
//
//	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);
//
//
//	// Calculate average offsets
//	gyr_x /= max_samples;
//	gyr_y /= max_samples;
//	gyr_z /= max_samples;
//
//	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);
//
//	gyr_x = 0;
//	gyr_y = 0;
//	gyr_z = 0;
//}
//
//
//#endif
//





