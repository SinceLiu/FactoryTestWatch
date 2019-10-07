#include "fm_hal.h"
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <string.h>
#include <stdint.h>
#include <unistd.h>
#include <errno.h>
#include <android/log.h>
#ifdef __KERNEL__
#include <linux/ioctl.h>
#include <linux/miscdevice.h>
#include <linux/kernel.h>
#include <linux/types.h>
#include <linux/common.h>
#endif

#define TAG11 "JNI_FMRADIO"
#define TRUE 1
#define FALSE  0
#define MAX_VOL 12
int ret;

static char downflag=1;

fm_ctrl_block fm_block = {
    24100, // start freq = 88.1MHz
    200,   //volume
    100,   //step
    FM_TUNE_AUTOSTEREO | FM_REGION_EUR,    // default region FM_REGION_EUR
    0,    // rssi
    100,    // rssi_threshold
    0,    // snr
    7,     // snr_threshold
    FM_MUTE_OFF
};

void 
fm_get_region_tune_mode_cb(uint8_t param)
{
    fm_block.region_tune_mode = param;
    printf("in cb, region param = %x\n", param);
    switch( param) {
    case 0:
        printf("FM_REGION_EUR | FM_TUNE_MONO\n");
        break;
    case 1:
        printf("FM_REGION_JAN | FM_TUNE_MONO\n");
        break;
    case 2:
        printf("FM_REGION_EUR | FM_TUNE_AUTOSTEREO\n");
        break;
    case 3:
        printf("FM_REGION_JAN | FM_TUNE_AUTOSTEREO\n");
        break;
    }
}

void
fm_get_channel_freq_cb(uint16_t param)
{
    fm_block.freq = param;
    printf("in cb, current freq =%d, %dKHz\n", param, param + 64000);
}

void
fm_get_current_rssi_cb(uint8_t param)
{
    fm_block.rssi = ~param;
    printf("in cb, current rssi = -%d dBm\n", (uint8_t)~param);
}

void
fm_get_current_snr_cb(uint8_t param)
{
    fm_block.snr = param;
    printf("in cb, current snr = %d \n", (uint8_t)param);
}

void
fm_get_search_steps_cb(uint8_t param)
{
    fm_block.steps = param;
    printf("in cb, search steps = %dkHz\n", param);
}

void
fm_get_mute_state_cb(uint8_t param)
{
    fm_block.mute_state = param;
    if (param == FM_MUTE_OFF)
        printf("in cb, mute_state = mute off\n");
    else
        printf("in cb, mute_state = mute on\n");
}

void
fm_get_volume_gain_cb(uint16_t param)
{
    fm_block.volume = param;
    printf("in cb, volume gain = %d\n", param);
}

void
fm_get_preset_channels_cb(uint8_t plen, uint8_t *param)
{
    uint16_t channel;
    uint8_t *p;
    int i;

    printf("in cb, preset_channels=%d\n", plen>>2);

    p = param;
    i = plen>>2;
    while (i>0) {
        channel = ((*(p+1))<<8) + (*p);
        if (channel == 0)
            break;
        printf("%d KHz\n", channel + 64000);
        p +=4;
        i--;
    }
}

void
fm_get_search_rssi_threshold_cb(uint8_t param)
{
    printf("in cb, rssi_threshold = -%ddBm\n", param);
}

void
fm_get_snr_threshold_cb(uint8_t param)
{
    printf("in cb, snr_threshold = %d\n", param);
}

void 
fm_get_stereo_mono_status_cb(uint8_t param)
{
    if (param == FM_AUDIO_STEREO)
        printf("in cb, audio STEREO\n");
    else
        printf("in cb, audio MONO\n");
}

fm_callbacks fm_cbs = {
    &fm_get_region_tune_mode_cb,
    &fm_get_channel_freq_cb,
    &fm_get_current_rssi_cb,
    &fm_get_current_snr_cb,
    &fm_get_search_steps_cb,
    &fm_get_mute_state_cb,
    &fm_get_volume_gain_cb,
    &fm_get_preset_channels_cb,
    &fm_get_search_rssi_threshold_cb,
    &fm_get_snr_threshold_cb,
    &fm_get_stereo_mono_status_cb
};

//[SIMT-guliangzeng-20111107]{
void AddRootPermission()
{
//        __android_log_print(4, LOG_TAG, "AddRootPermission");
//
//        int handle = open("/dev/threadright0", O_RDWR);
//    if(handle == -1)
//    {
//         __android_log_print(4, LOG_TAG, "open(threadright0) failed:fd = %d\n", handle);
//    }
//        __android_log_print(4, LOG_TAG, "+");
//    close(handle);
}

static jint open_fm_cit_mode(){

        AddRootPermission();

    void *handle;
    int (*bluetoothag)(void);
    int val = 0;
    __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("libbluedroid.so",RTLD_NOW);

    if(handle == NULL)
        {
        __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
        return -1;
    }

    __android_log_print(4, LOG_TAG, "dlsym exec start");
    bluetoothag = dlsym(handle,"fmcitopen");
    __android_log_print(4, LOG_TAG, "fmcit exec start");
    val = bluetoothag();
    __android_log_print(4, LOG_TAG, "val = %d",val);
    dlclose(handle);

    return (val);
}
static jint close_fm_cit_mode(){

        AddRootPermission();

    void *handle;
    int (*bluetoothag)(void);
    int val = 0;
    __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("libbluedroid.so",RTLD_NOW);

    if(handle == NULL){
        __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
        return -1;
    }

    __android_log_print(4, LOG_TAG, "dlsym exec start");
    bluetoothag = dlsym(handle,"fmcitclose");
    __android_log_print(4, LOG_TAG, "fmcit exec start");
    val = bluetoothag();
    __android_log_print(4, LOG_TAG, "val = %d",val);
    dlclose(handle);

    return (val);
}

//[SIMT-guliangzeng-20111107]}
/*
* Class:     com_android_sim_FMRadio
* Method:    init
* Signature: ()Z
*/
JNIEXPORT jint JNICALL Java_com_sim_cit_FMRadio_init
(JNIEnv *env, jobject obj){
    int r;
    
    // need start the bluetoothd daemon and
    // start the service hciattach(brcm_patchram_plus)
    open_fm_cit_mode();
    
    r = fm_init(&fm_cbs);
    if (r != 0) { 
      __android_log_print(4, TAG11, "fm_init failed.\n");
      return -1;
    }
    __android_log_print(4, TAG11, "fm_init success.\n");
      
    
    r = fm_func_on(&fm_block);
    if (r != 0) {
       __android_log_print(4, TAG11, "fm_func_on failed.\n");
      return -2;
    }
    __android_log_print(4, TAG11, "fm_func_on success.\n");

      __android_log_print(4, TAG11, "JNICALL Java_com_sim_cit_FMRadio_init");

    return 0;
}

JNIEXPORT jint JNICALL Java_com_sim_cit_FMRadio_GetCurFreq
(JNIEnv *env, jobject obj){

    __android_log_print(4, TAG11, "run getCurFreq()\n");
    int nFreq = 0;

        ret = fm_get_current_freq();

    if(ret != 0)
    {
        nFreq = 98500;
    }
    else
    {
        nFreq=fm_block.freq + 64000;
    }
    return nFreq;
}
JNIEXPORT jboolean JNICALL Java_com_sim_cit_FMRadio_setVolume
(JNIEnv *env, jobject obj, jint nVolume){
    __android_log_print(4, TAG11, "run setVolume()\n");

        if(nVolume==MAX_VOL)
        {
            __android_log_print(4, TAG11, " setVolume 256");
            fm_set_volume(256);
        }
        else
        {
           __android_log_print(4, TAG11, " setVolume 125");
           fm_set_volume(40);
        }

    return TRUE;
}
JNIEXPORT jboolean JNICALL Java_com_sim_cit_FMRadio_forceMono
(JNIEnv *env, jobject obj, jboolean bMono){

    if(bMono)
        {
        //mono
        __android_log_print(4, TAG11, "mono  ");
          fm_set_region_tune_mode(fm_block.region_tune_mode & 0x1);
    }
    else
        {
        //sterro
        __android_log_print(4, TAG11, "sterro  ");
          fm_set_region_tune_mode((fm_block.region_tune_mode & 0x1)|0x2);
    }

    return TRUE;
}
JNIEXPORT jboolean JNICALL Java_com_sim_cit_FMRadio_setMute
(JNIEnv *env, jobject obj, jboolean bMuto){
    __android_log_print(4, TAG11, "run setMute()\n");

    if(bMuto)
    {
        __android_log_print(4, TAG11, "bMuto true ");
        fm_mute();
    }
    else
    {
        __android_log_print(4, TAG11, "bMuto flase ");
        fm_unmute();
    }
    return TRUE;
}
JNIEXPORT jint JNICALL Java_com_sim_cit_FMRadio_getRssi
(JNIEnv *env, jobject obj){

    int nRssi = 0;
    ret = fm_get_search_rssi_threshold();

    if(ret != 0)
    {
        nRssi = 0;
    }
        else
        {
           nRssi = fm_block.rssi;
        }

    __android_log_print(4, TAG11, "[getRssi][fm_get_rssi] the ret = %d\n",ret);
    return nRssi;
}
JNIEXPORT jboolean JNICALL Java_com_sim_cit_FMRadio_Tune
(JNIEnv *env, jobject obj, jint nFreq){

    __android_log_print(4, TAG11, "Enter the [Tune]\n");

    nFreq -= 64000;
    ret =fm_tune_freq((int)nFreq);
    __android_log_print(4, TAG11, "[Tune][fm_tune_freq] the ret = %d\n",ret);

    return TRUE;
}
JNIEXPORT jint JNICALL Java_com_sim_cit_FMRadio_searchFreq
(JNIEnv *env, jobject obj){

    int dir, val = 0;
    int band;
    int freq;

    fm_set_search_steps(FM_SEARCH_STEPS_100KHz);
      fm_get_current_freq();
    fm_search(FM_SEARCH_UP, (int)fm_block.freq+100);

        ret=fm_get_current_freq();
      if(ret != 0)
     {
         freq = 98500;
     }
     else
     {
         freq=fm_block.freq + 64000;
     }
     if(freq>=108000)
     {
         __android_log_print(4, TAG11, "set freq 88.1MHz \n");
             fm_tune_freq(24100);
     }
    return freq;
}
/*
* Class:     com_android_sim_FMRadio
* Method:    close
* Signature: ()Z
*/
JNIEXPORT jboolean JNICALL Java_com_sim_cit_FMRadio_close
(JNIEnv *env, jobject obj){
    __android_log_print(4,TAG11, "JNICALL Java_com_sim_cit_FMRadio_close");
    //exit
    //fm_func_off();
    fm_close();
        close_fm_cit_mode();

    return TRUE;
}






