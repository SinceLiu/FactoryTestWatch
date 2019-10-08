package com.dinghmcn.android.wificonnectclient.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * The type Version utils.
 *
 * @author zl121325
 * @date 2019 /4/10
 */
public class VersionUtils {

    /**
     * Get serial number string.
     *
     * @return the string
     */
    @SuppressLint("MissingPermission")
    public static String getSerialNumber() {
        String serial = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = Build.getSerial();
        } else {
            serial = SystemProperties.get("ro.serialno");
        }
        if (serial == null) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serial;
    }

    public String getHardwareVersion() {
        return SystemProperties.get("ro.hw.build.version");
    }

    /**
     * Get software version string.
     *
     * @return the string
     */
    public String getSoftwareVersion() {
        return Build.DISPLAY;
    }
}
