LOCAL_PATH:= $(call my-dir)

# jni library for libCommonDrive
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= com_sim_cit_CommonDrive.c


# Header files path
LOCAL_C_INCLUDES :=     \
    ./  \
    $(JNI_H_INCLUDE)    \
    $(call include-path-for, system-core)/cutils

LOCAL_SHARED_LIBRARIES    += libdl  \
                           libutils   \
                           libcutils  

LOCAL_MODULE_TAGS := optional eng

LOCAL_PRELINK_MODULE := false

LOCAL_CFLAGS += -DLOG_TAG=\"CommonDrive\"

ifeq ($(TARGET_SIMULATOR),true)
LOCAL_CFLAGS += -DSINGLE_PROCESS
endif

LOCAL_MODULE:= libCommonDrive

include $(BUILD_SHARED_LIBRARY)
