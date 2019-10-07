package com.dinghmcn.android.wificonnectclient.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
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
  private static CallUtils instance;
  private Context mContext;
  private boolean isFooHook;

  /**
   * The Manager.
   */
  TelephonyManager manager;

  private CallUtils(Context mContext) {
    this.mContext = mContext;
    getCallService();
  }

  /**
   * Gets instance.
   *
   * @param mContext the m context
   * @return the instance
   */
  public static CallUtils getInstance(Context mContext) {
    if (instance == null) {
      instance = new CallUtils(mContext);
    }
    return instance;
  }

  private void getCallService() {
    manager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
    manager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
  }

  private class MyPhoneListener extends PhoneStateListener {
    /**
     * On call state changed.
     *
     * @param state       the state
     * @param phoneNumber the phone number
     */
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
      Log.d(TAG, "onCallStateChanged: " + state);
      switch (state) {
        case TelephonyManager.CALL_STATE_IDLE:
          Log.d(TAG, "onCallStateChanged: CALL_STATE_IDLE" + phoneNumber);
          break;
        case TelephonyManager.CALL_STATE_RINGING:
          Log.d(TAG, "onCallStateChanged: CALL_STATE_RINGING" + phoneNumber);
          break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
          Log.d(TAG, "onCallStateChanged: CALL_STATE_OFFHOOK" + phoneNumber);
          isFooHook = true;
          break;
        default:
          break;
      }
      super.onCallStateChanged(state, phoneNumber);
    }
  }

  /**
   * End call boolean.
   *
   * @return the boolean
   */
  public boolean endCall() {
    if (!isFooHook) {
      return false;
    }
    boolean isEndCall = false;
    TelephonyManager telephony =
        (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    try {
      Class c = Class.forName(telephony.getClass().getName());
      Method m = c.getDeclaredMethod("getITelephony");
      m.setAccessible(true);
      ITelephony telephonyService = (ITelephony) m.invoke(telephony);
      isEndCall = telephonyService.endCallForSubscriber(0);
    } catch (Exception e) {
      e.printStackTrace();
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
