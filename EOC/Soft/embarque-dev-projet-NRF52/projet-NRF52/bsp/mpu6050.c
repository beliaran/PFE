/* Copyright (c) 2009 Nordic Semiconductor. All Rights Reserved.
 *
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC
 * SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 *
 * Licensees are granted free, non-transferable use of the information. NO
 * WARRANTY of ANY KIND is provided. This heading must NOT be removed from
 * the file.
 *
 */

/*
 * Rappel : le MPU6050 comporte un accéléromètre et un gyroscope
 * L'accéléromètre donne une accélération et le gyroscope une vitesse angulaire
 *
 * Gyroscope :
 * 				Sensibilité (°/s)	|	Facteur de sensibilité	|
 * 					+- 250						131
 * 					+- 500						65.5
 * 					+- 1000						32.8
 * 					+- 2000						16.4
 *
  * Accéléromètre :
 * 				Sensibilité (g)	|	Facteur de sensibilité	|
 * 					+- 2						16,384
 * 					+- 4						8,192
 * 					+- 8						4,096
 * 					+- 16						2,048
 *
 */




#include "../appli/config.h"
#if USE_MPU6050
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>

#include "appli/common/systick.h"
#include "nrf52_i2c.h"
#include "mpu6050.h"
#include "appli/common/gpio.h"

// ///////////////////////////////////////////////////////////////////////////


/*
 * Include et Define perso :
 * Redondance avec certaines choses déjà déclarée dans le .h, à nettoyer plus tard
 */

#include <math.h>

#define PI		3.14159265358979323846

#define SSF_GYRO 65.5
#define SSF_ACC 4096

#define X 0
#define Y 1
#define Z 2

#define YAW   0
#define PITCH 1
#define ROLL  2

#define FREQ        250   // Sampling frequency

/* Fin des Include / Define persos */

/*
 * Definition des variables perso
 */

static MPU6050_t mpu_datas;
static MPU6050_Gyroscope_t gyro_sens = MPU6050_Gyroscope_500s;
static MPU6050_Accelerometer_t acc_sens = MPU6050_Accelerometer_8G;


static int16_t gyro_raw[3] = {0,0,0};
static float gyro_angle[3] = {0,0,0};

static int16_t gyro_offset[3] = {85,8,-8}; // offset dépendant du capteur, valeur obtenues grâce à la fonction "afficher_offset()"

static int32_t acc_raw[3] = {0,0,0};
static float acc_angle[3] = {0,0,0};

static int16_t acc_total_vector;
static int32_t test[3] = {0,0,0};
static float measures[3] = {0,0,0};
static uint16_t  period; // Sampling period
static uint16_t loop_timer;

static uint32_t time_start = 0;


/* Fin des definitions perso */
// ///////////////////////////////////////////////////////////////////////////

/*lint ++flb "Enter library region" */

#define M_PI 1

//static const uint8_t expected_who_am_i_9250 = 0x71; // !< Expected value to get from WHO_AM_I register.
static const uint8_t expected_who_am_i = 0x68U; // !< Expected value to get from WHO_AM_I register.

static bool_e initialized = FALSE;

void mpu6050_i2c_init(uint8_t device_address)
{
	running_e sub;
	I2C_init(device_address);

	SYSTICK_delay_ms(500);

    // Do a reset on signal paths
    uint8_t reset_value = 0x04U | 0x02U | 0x01U; // Resets gyro, accelerometer and temperature sensor signal paths.
    sub = mpu6050_register_write(ADDRESS_SIGNAL_PATH_RESET, reset_value);

    // Read and verify product ID
    if(sub == END_OK)
    	initialized = mpu6050_verify_product_id();


    while(!initialized)
    {
    	initialized = mpu6050_verify_product_id();
    	SYSTICK_delay_ms(100);
    }
}
/*
 * @brief	Initialise le module MPU6050 en activant son alimentation, puis en configurant les registres internes du MPU6050.
 * @param	GPIOx et GPIO_PIN_x indiquent la broche oÃƒÂ¹ l'on a reliÃƒÂ© l'alimentation Vcc du MPU6050.
 * 			Indiquer NULL dans GPIOx s'il est alimentÃƒÂ© en direct.
 * 			Cette possibilitÃƒÂ© d'alimentation par la broche permet le reset du module par le microcontrÃƒÂ´leur.
 * @param	DataStruct : fournir le pointeur vers une structure qui sera ÃƒÂ  conserver pour les autres appels des fonctions de ce module logiciel.
 * @param 	DeviceNumber : 					voir MPU6050_Device_t
 * @param	AccelerometerSensitivity : 		voir MPU6050_Accelerometer_t
 * @param	GyroscopeSensitivity :			voir MPU6050_Gyroscope_t
 */
bool MPU6050_Init(MPU6050_t* DataStruct, MPU6050_Accelerometer_t AccelerometerSensitivity, MPU6050_Gyroscope_t GyroscopeSensitivity)
{
	uint8_t temp;

	// Initialize Power !
	#ifdef MPU6050_VCC_PIN
		GPIO_configure(MPU6050_VCC_PIN, GPIO_PIN_CNF_PULL_Disabled, TRUE);
		GPIO_write(MPU6050_VCC_PIN, FALSE);
		SYSTICK_delay_ms(500);
		GPIO_write(MPU6050_VCC_PIN, TRUE);
	#endif

	/* Initialize I2C */
	mpu6050_i2c_init(MPU_6050_I2C_ADDRESS);

	/* Wakeup MPU6050 */
	mpu6050_register_write(MPU6050_PWR_MGMT_1, 0x00);

	/* Config accelerometer */
	//temp = I2C_Read(MPU6050_I2C, DataStruct->Address, MPU6050_ACCEL_CONFIG);
	mpu6050_register_read(MPU6050_ACCEL_CONFIG, &temp, 1);
	temp = (temp & 0xE7) | (uint8_t)AccelerometerSensitivity << 3;
	//I2C_Write(MPU6050_I2C, DataStruct->Address, MPU6050_ACCEL_CONFIG, temp);
	mpu6050_register_write(MPU6050_ACCEL_CONFIG, temp);

	/* Config gyroscope */
	//temp = I2C_Read(MPU6050_I2C, DataStruct->Address, MPU6050_GYRO_CONFIG);
	mpu6050_register_read(MPU6050_GYRO_CONFIG, &temp, 1);
	temp = (temp & 0xE7) | (uint8_t)GyroscopeSensitivity << 3;
	//I2C_Write(MPU6050_I2C, DataStruct->Address, MPU6050_GYRO_CONFIG, temp);
	mpu6050_register_write(MPU6050_GYRO_CONFIG, temp);

	/* Set sensitivities for multiplying gyro and accelerometer data */
	switch (AccelerometerSensitivity) {
		case MPU6050_Accelerometer_2G:
			DataStruct->Acce_Mult = (float)1 / MPU6050_ACCE_SENS_2;
			break;
		case MPU6050_Accelerometer_4G:
			DataStruct->Acce_Mult = (float)1 / MPU6050_ACCE_SENS_4;
			break;
		case MPU6050_Accelerometer_8G:
			DataStruct->Acce_Mult = (float)1 / MPU6050_ACCE_SENS_8;
			break;
		case MPU6050_Accelerometer_16G:
			DataStruct->Acce_Mult = (float)1 / MPU6050_ACCE_SENS_16;
			//no break
		default:
			break;
	}

	switch (GyroscopeSensitivity) {
		case MPU6050_Gyroscope_250s:
			DataStruct->Gyro_Mult = (float)1 / MPU6050_GYRO_SENS_250;
			break;
		case MPU6050_Gyroscope_500s:
			DataStruct->Gyro_Mult = (float)1 / MPU6050_GYRO_SENS_500;
			break;
		case MPU6050_Gyroscope_1000s:
			DataStruct->Gyro_Mult = (float)1 / MPU6050_GYRO_SENS_1000;
			break;
		case MPU6050_Gyroscope_2000s:
			DataStruct->Gyro_Mult = (float)1 / MPU6050_GYRO_SENS_2000;
			// no break
		default:
			break;
	}

	/* Return OK */
	return MPU6050_Result_Ok;
}
bool mpu6050_verify_product_id(void)
{
    uint8_t who_am_i = 0x55;
//	uint8_t who_am_i = 0x68;


    if (mpu6050_register_read(ADDRESS_WHO_AM_I, &who_am_i, 1) == END_OK)
    {
        if (who_am_i != expected_who_am_i)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    else
    {
        return false;
    }
}

bool mpu6050_register_write(uint8_t register_address, uint8_t value)
{
	running_e sub;
	do
	{
		sub = I2C_register_write(register_address, value);
	}while(sub == IN_PROGRESS);
	return sub;
}



running_e mpu6050_register_read(uint8_t register_address, uint8_t * destination, uint8_t number_of_bytes)
{
	running_e sub;
	do
	{
		sub = I2C_register_read(register_address, destination, number_of_bytes);
	}while(sub == IN_PROGRESS);
    return sub;
}


bool MPU6050_ReadAllType2(TYPE_2_MPU6050_t* DataStruct) {
	uint8_t data[14];
	int16_t temp;

	/* Read full raw data, 14bytes */
//	I2C_ReadMulti(MPU6050_I2C, DataStruct->Address, MPU6050_ACCEL_XOUT_H, data, 14);
//MPU6050_Init(&MPU6050_Data, GPIOA, GPIO_PIN_0, MPU6050_Device_0, MPU6050_Accelerometer_8G, MPU6050_Gyroscope_2000s) != MPU6050_Result_Ok

	mpu6050_register_read(MPU6050_ACCEL_XOUT_H, data, 14);
	/* Format accelerometer data */
	DataStruct->Accelerometer_x_h = data[0];
	DataStruct->Accelerometer_x_l = data[1];
	DataStruct->Accelerometer_y_h = data[2];
	DataStruct->Accelerometer_y_l = data[3];
	DataStruct->Accelerometer_z_h = data[4];
	DataStruct->Accelerometer_z_l = data[5];
	DataStruct->Gyroscope_x_h= data[8];
	DataStruct->Gyroscope_x_l= data[9];
	DataStruct->Gyroscope_y_h= data[10];
	DataStruct->Gyroscope_y_l= data[11];
	DataStruct->Gyroscope_z_h= data[12];
	DataStruct->Gyroscope_z_l= data[13];

	/* Format temperature */
	temp = (data[6] << 8 | data[7]);
	DataStruct->Temperature = (float)((float)((int16_t)temp) / (float)340.0 + (float)36.53);


	/* Return OK */
	return MPU6050_Result_Ok;

}
 bool MPU6050_ReadAllType1(MPU6050_t* DataStruct) {
	uint8_t data[14];
	int16_t temp;
	for(uint8_t i = 0; i<14 ; i++)
		data[i] = 0x55;
	/* Read full raw data, 14bytes */
	//	I2C_ReadMulti(MPU6050_I2C, DataStruct->Address, MPU6050_ACCEL_XOUT_H, data, 14);
	//	MPU6050_Init(&MPU6050_Data, MPU6050_Accelerometer_8G, MPU6050_Gyroscope_2000s);

	mpu6050_register_read(MPU6050_ACCEL_XOUT_H, data, 14);
	/* Format accelerometer data */
	DataStruct->Accelerometer_X = (int16_t)(data[0] << 8 | data[1]);
	DataStruct->Accelerometer_Y = (int16_t)(data[2] << 8 | data[3]);
	DataStruct->Accelerometer_Z = (int16_t)(data[4] << 8 | data[5]);

	/* Format temperature */
	temp = (data[6] << 8 | data[7]);
	DataStruct->Temperature = (float)((float)((int16_t)temp) / (float)340.0 + (float)36.53);

	/* Format gyroscope data */
	DataStruct->Gyroscope_X = (int16_t)(data[8] << 8 | data[9]);
	DataStruct->Gyroscope_Y = (int16_t)(data[10] << 8 | data[11]);
	DataStruct->Gyroscope_Z = (int16_t)(data[12] << 8 | data[13]);

	/* Return OK */
	return MPU6050_Result_Ok;
}


 // ///////////////////////////////////////////////////////////////////////////
 /* Fonctions perso */

 static volatile uint32_t t = 0;

 void process(void)
 {
 	if(t)
 		t--;
 }

 /*
  * Fonction de test combinant l'utilisation du gyroscope et celle de l'accéléromètre
  * Les resultats semblent être x2
  */
void mpu6050_test() {
	Systick_add_callback_function(&process);
	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
	loop_timer = SYSTICK_get_time_us();
	period = (1000000 / FREQ) ;

	while(1){
		while((SYSTICK_get_time_us() - loop_timer) < period);
		readSensor();
		calculateAngles();
		loop_timer = SYSTICK_get_time_us();

		test[X] = (int32_t)(measures[X]);
		test[Y] = (int32_t)(measures[Y]);
		test[Z] = (int32_t)(measures[Z]);
		debug_printf("X: %ld | Y: %ld | Z: %ld \n", test[X], test[Y], test[Z]);
	}



}
/*
 * Fonction de test utilisant seulement l'accéléromètre
 *
 */

void mpu6050_test2() {
	Systick_add_callback_function(&process);
	time_start = SYSTICK_get_time_us();
	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);


	while(1){
		if((SYSTICK_get_time_us() - time_start)>1000000){
			readSensor();
			calculateAccelerometerAngles();
			test[X] = (int32_t)(acc_angle[X]);
			test[Y] = (int32_t)(acc_angle[Y]);
			test[Z] = (int32_t)(acc_angle[Z]);
			debug_printf("X: %ld | Y: %ld | Z: %ld \n", test[X], test[Y], test[Z]);
			time_start = SYSTICK_get_time_us();
		}
	}



}

void readSensor(void){
	MPU6050_ReadAllType1(&mpu_datas);

	gyro_raw[X] = mpu_datas.Gyroscope_X;
	gyro_raw[Y] = mpu_datas.Gyroscope_Y;
	gyro_raw[Z] = mpu_datas.Gyroscope_Z;

	acc_raw[X] = mpu_datas.Accelerometer_X;
	acc_raw[Y] = mpu_datas.Accelerometer_Y;
	acc_raw[Z] = mpu_datas.Accelerometer_Z;
}

void calculateAngles(){
	calculateGyroAngles();
	calculateAccelerometerAngles();

	measures[ROLL]  = measures[ROLL]  * 0.9 + gyro_angle[X] * 0.1;
	measures[PITCH] = measures[PITCH] * 0.9 + gyro_angle[Y] * 0.1;
	measures[YAW]   = -gyro_raw[Z] / SSF_GYRO; // Store the angular motion for this axis

}

void calculateGyroAngles()
{
  // Subtract offsets
  gyro_raw[X] -= gyro_offset[X];
  gyro_raw[Y] -= gyro_offset[Y];
  gyro_raw[Z] -= gyro_offset[Z];

  // Angle calculation using integration
  gyro_angle[X] += (gyro_raw[X] / (FREQ * SSF_GYRO));
  gyro_angle[Y] += (-gyro_raw[Y] / (FREQ * SSF_GYRO)); // Change sign to match the accelerometer's one

  // Transfer roll to pitch if IMU has yawed
  gyro_angle[Y] += gyro_angle[X] * sin(gyro_raw[Z] * (PI / (FREQ * SSF_GYRO * 180)));
  gyro_angle[X] -= gyro_angle[Y] * sin(gyro_raw[Z] * (PI / (FREQ * SSF_GYRO * 180)));
}

void calculateAccelerometerAngles()
{
  // Calculate total 3D acceleration vector : sqrt(X + Y + Z)
  acc_total_vector = sqrt(pow(acc_raw[X], 2) + pow(acc_raw[Y], 2) + pow(acc_raw[Z], 2));

  // To prevent asin to produce a NaN, make sure the input value is within [-1;+1]
  if (fabs(acc_raw[X]) < acc_total_vector) {
    acc_angle[X] = asin((float)acc_raw[Y] / acc_total_vector) * (180 / PI); // asin gives angle in radian. Convert to degree multiplying by 180/pi
  }

  if (fabs(acc_raw[Y]) < acc_total_vector) {
    acc_angle[Y] = asin((float)acc_raw[X] / acc_total_vector) * (180 / PI);
  }
}




void afficher_offset(void){
	int32_t gyr_x = 0;
	int32_t gyr_y = 0;
	int32_t gyr_z = 0;
	debug_printf("coucou\n");
	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);

	int max_samples = 2000;

	for (int i = 0; i < max_samples; i++) {
		MPU6050_ReadAllType1(&mpu_datas);

		gyr_x += mpu_datas.Gyroscope_X;
		gyr_y += mpu_datas.Gyroscope_Y;
		gyr_z += mpu_datas.Gyroscope_Z;

	}

	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);


	// Calculate average offsets
	gyr_x /= max_samples;
	gyr_y /= max_samples;
	gyr_z /= max_samples;

	debug_printf("X : %ld | Y : %ld | Z : %ld \n", gyr_x,gyr_y,gyr_z);

	gyr_x = 0;
	gyr_y = 0;
	gyr_z = 0;
}

void molkky_init_mpu(){
	MPU6050_Init(&mpu_datas, acc_sens, gyro_sens);
}

void molkky_get_angle(int32_t *angleX, int32_t *angleY, int32_t *angleZ){
	readSensor();
	calculateAccelerometerAngles();
	*angleX = (int32_t)(acc_angle[X]);
	*angleY = (int32_t)(acc_angle[Y]);
	*angleZ = (int32_t)(acc_angle[Z]);
}

void molkky_get_acc(int32_t *accX, int32_t *accY, int32_t *accZ){
	readSensor();
	*accX = acc_raw[X];
	*accY = acc_raw[Y];
	*accZ = acc_raw[Z];
}


#endif //USE MPU6050
