/*
*
* product_test.h
*
*
*
* Copyright (C) 2010 Broadcom Corporation.
*
*

This software is licensed under the terms of the GNU General Public License, version 2, as published by the Free Software Foundation (the "GPL"), and may be copied, distributed, and modified under those terms.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.


A copy of the GPL is available at http://www.broadcom.com/licenses/GPLv2.php or by

writing to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, 

Boston, MA 02111-1307, USA

*
*
*/

#ifndef PRODUCT_TEST_H
#define PRODUCT_TEST_H

#define Mode_B 0
#define Mode_G 1
#define Mode_NML 2
#define Mode_NMS 3
#define Mode_NGL 4
#define Mode_NGS 5

#define LOG_TAG "product test"

#ifdef __cplusplus
  extern "c" {
#endif


typedef enum
{
    AG_N4010A = 0,
    WIFI_USB1C,
    MT8860
}wifi_equ_type;

/*wifi���Բ�������:
1) ��wl���߷���device/hisi/pecos/ Ŀ¼
2) ��device/hisi/pecos/AndroidBoard.mk�����:
    PRODUCT_COPY_FILES += \
            $(LOCAL_PATH)/wl:system/bin/wl
   �����Ϳ��԰�wl���߱���system.img
3) ϵͳ������, 
    adb shell
    chmod 777 /system/bin/wl
   �����Ϳ��Կ�ʼwifi������
*/

/*
Parameter:
   iMode: Mode_B,Mode_G,Mode_NML,Mode_NMS,Mode_NGL,Mode_NGS
   iRate: 1/2/5/11 for Mode_B
          6/9/12/18/24/36/48/54 for Mode_G
          0/1/2/3/4/5/6/7 for Mode_NML,Mode_NMS,Mode_NGL,Mode_NGS
          
Return:
   1 means Success
   0 means Fail
   
Usage:  Set Wifi mode and rate
*/
 int BCM_Set_Wifi_Mode(int iMode,int iRate);


/*
Parameter:
    iChannel: from 1 to 13
    
Return:
   1 means Success
   0 means Fail
   
Usage: Start to receive at a certian channel
*/
 int BCM_Start_Rx(int iChannel);

/*
Parameter:
    
Return:
   1 means Success
   0 means Fail
   
Usage: Stop receive
*/
 int BCM_Stop_Rx();


/*
Parameter:
    piTotalPackets:  store the number of total received packets
    piPer: store the PER, (8 means 0.8%)
    
Return:
   1 means Success
   0 means Fail
   
Usage:  read the receive result
*/
 int BCM_Read_Rx_Result(int *piTotalPackets, int *piPer);


/*
Parameter:
    
Return:
   the RSSI or 0 means Fail
   
Usage: read RSSI
*/
 int BCM_Read_Rx_RSSI();


/*
Parameter:
   iChannel: the transmisson channel, from 1 to 13
    
Return:
   1 means Success
   0 means Fail
   
Usage: Start transmit at a certian channel
*/
 int BCM_Start_Tx(int iChannel);

/*
Parameter:
    
Return:
   1 means Success
   0 means Fail
   
Usage: Stop transmit
*/
 int BCM_Stop_Tx();


/*
Parameter:
    NULL
Return:
   0 means wifi is off
   other wifi current data rate
Usage:  get wifi current status
*/

 int BCM_GetWiFi_Status(void);

/*
Parameter:
    pChannel:  store the Channel of RX
    piPer: store the PER, (8 means 0.8%)
    
Return:
   1 means Success
   0 means Fail
   
Usage:  read the receive result
*/

 int BCM_Read_Rx_Status(int *pChannel, int *piPer);

/*
Parameter:
    NULL
Return:
   -1 failed
   other is sucress
Usage:  clear Rx per status
*/

  int BCM_Clear_Rx_Status(void);

/*
Parameter:
    input:RxPacket
    the total Packet for Rx test   
Usage: set  total Packet for Rx test
*/

void BCM_Rx_TotalPacket(int RxPacket);

/*
Parameter:
    input:TestEquType
      
Usage: set  wifi test equipment type
*/

void BCM_Set_Equ_Type(int TestEquType);

/*
Parameter:
    NULL
Return:
   -1 failed
   other is current TX Channel
Usage:  clear Rx per status
*/

  int BCM_Get_Tx_Channel(void);

/*
Parameter:
    sAddr: store the mac address
    sAddr should be formated like 112233445566
Return:
   1 means Success
   0 means Fail
Usage:  get the current mac address
*/
  int BCM_Get_Addr(char *sAddr);

/*
Parameter:
   sAddr: the channel to be set
   sAddr should be formated like 112233445566
Return:
   1 means Success
   0 means Fail
   
Usage: set sAddr as the mac address
*/
  int BCM_Set_Addr(char *sAddr);

/*
Parameter:
   piChannel: store the channel
    
Return:
   1 means Success
   0 means Fail
   
Usage: get the current channel
*/
  int BCM_Get_Channel(int *piChannel);

/*
Parameter:
    NULL
Return:
   1 sucess enable wifi test mode
   0 enable wiwi test mode failed
Usage:  start wifi test mode
*/
  int BCM_Start_TestMode(void);

/*
Parameter:
    NULL
Return:
   1 sucess disabel wifi test mode
   0 disable wiwi test mode failed
Usage:  Stop wifi test mode
*/
  int BCM_Stop_TestMode(void);

/*
Parameter:
    NULL
Return:
   0
Usage: init wifi test mode
*/
  int Init_Wifi_TestMode();

/*
Parameter:
    NULL
Return:
   0
Usage: Deinit wifi test mode
*/
  int Deinit_Wifi_TestMode(void);


//Set TX Gain
int SetWifiTxGain(int nGain);

//Set SIFS
int SetWifiTxSIFS(int nSifs);

// Set Number of Frames
int SetWifiTxNumberofFrames(int nNumberofFrames);

// Set Payload Length
int SetWifiTxPayloadLength(int nNumberofFrames);

//Set Preamble
int SetPreamble(int nPreamble);

//Set Mac address
int SetDesMacAdd(char *pMac);

// Get Number of Frames
int GetWifiTxNumberofFrames();

// Get Payload Length
int GetWifiTxPayloadLength();

//Get Preamble
int GetPreamble();

//Added by Yuhaipeng 20110505
int SetCarrierSuppressionMode(int enable);
//Added by Yuhaipeng 20110505
int RunCommand(const char *cmd);

#ifdef __cplusplus
}
#endif

#endif



