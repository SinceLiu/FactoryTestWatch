package com.readboy.factorytest.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * The type Call utils.
 *
 * @author dinghmcn
 */
public class CallUtils {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;

    /**
     * The Manager.
     */
    TelephonyManager manager;

    public CallUtils(Context mContext) {
        this.mContext = mContext;
        manager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);

    }

    /**
     * End call boolean.
     *
     * @return the boolean
     */
    public boolean endCall() {
        boolean isEndCall = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelecomManager telecom = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
            if (telecom.isInCall()) {
                isEndCall = telecom.endCall();
            }
        } else {
            TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephony.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
                try {
                    Class c = Class.forName(telephony.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) m.invoke(telephony);
                    isEndCall = telephonyService.endCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return isEndCall;
    }

    /**
     * Gets imei.
     *
     * @return the imei
     */
    public String getImei() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return manager.getImei();
        } else {
            return manager.getDeviceId();
        }
    }
}
