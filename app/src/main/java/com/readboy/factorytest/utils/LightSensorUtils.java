package com.readboy.factorytest.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by zl121325 on 2019/4/11.
 */

public class LightSensorUtils {
    private static LightSensorUtils instance;
    Context mContext;
    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private int oldLight;
    private int[] luxes = new int[]{
            0, 50, 100, 150, 200, 400, 600, 800, 1000,
            2000, 5000, 10000, 20000, 30000
    };
    private int[] brights = new int[]{
            25, 45, 45, 63, 63, 82, 82, 100, 100,
            118, 135, 183, 198, 221
    };
    private PowerManager powerManager;
    private long SensorchangeTimes = 0L;
    private float mValue;
    private SensorEventListener mLightSensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            int blDegree = 0;
            for (int i = 0; i < brights.length; i++) {
                if (i + 1 < brights.length) {
                    if (lux >= luxes[i] && lux < luxes[i + 1]) {
                        blDegree = brights[i];
                        Log.i("AmbientTest", "blDegree = " + blDegree);
                    }
                } else {
                    if (lux >= luxes[brights.length - 1]) {
                        blDegree = brights[brights.length - 1];
                        Log.i("AmbientTest", "blDegree = " + blDegree);
                    }
                }
            }
            //Add SensorCheck for MotionSensor by xiasiping 20140626 start
            if (SensorchangeTimes == 0L) {
                mValue = lux;
            } else {
                float abs_mChange = Math.abs(lux - mValue);
                if (lux > 30 && abs_mChange > 10) {
                } else if (lux <= 30 && lux >= 10 && abs_mChange > 5) {
                } else if (lux < 10 && abs_mChange > 2) {
                }
            }
            SensorchangeTimes++;
            //Add SensorCheck for MotionSensor by xiasiping 20140626 end
        }
    };

    public LightSensorUtils(Context mContext) {
        this.mContext = mContext;
        getService();
    }

    public static LightSensorUtils getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new LightSensorUtils(context);
        }
        return instance;
    }

    private void getService() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//2012-12-06 add for test by lvhongshan--start
        if (null == mLightSensor) {
            Log.i("lvhongshan_LightSensor", "LightSensor is " + "null");
        } else {
            Log.i("lvhongshan_LightSensor", "LightSensor is " + "not null");
        }
//2012-12-06 add for test by lvhongshan--start
        powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //2012-12-06 add for test by lvhongshan--start
        if (null == powerManager) {
            Log.i("lvhongshan_powerManager", "powerManager is " + "null");
        } else {
            Log.i("lvhongshan_powerManager", "powerManager is " + "not null");
        }
        //2012-12-06 add for test by lvhongshan--start
        try {
            oldLight = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean registerListener() {
        return mSensorManager.registerListener(mLightSensorListener, mLightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
        mSensorManager.unregisterListener(mLightSensorListener);
    }
}
