package com.dinghmcn.android.wificonnectclient.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * 获取传感器相关信息
 *
 * @author dinghmcn
 * @date 2018 /4/28 15:35
 */
public class SensorManagerUtils implements SensorEventListener {
    private static final String TAG = "SensorManagerUtils";

    @Nullable
    private static SensorManagerUtils instance = null;

    /**
     * 需要获取信息的传感器
     */
    @NonNull
    private List<Integer> mSensorList = Arrays.asList(Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_PROXIMITY, Sensor.TYPE_LIGHT, Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE);
    private SensorManager mSensorManager;
    @NonNull
    private JSONObject mJSONObject = new JSONObject();

    private SensorManagerUtils(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        registerListeners();
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    @Nullable
    public static SensorManagerUtils getInstance(@NonNull Context context) {
        if (null == instance) {
            instance = new SensorManagerUtils(context);
        }
        return instance;
    }

    /**
     * 存储传感器信息
     *
     * @param event the event
     */
    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        try {
            Log.w(TAG, "onSensorChanged: " + event.sensor.getType() + "---" + Arrays.toString(event.values));
            mJSONObject.put(event.sensor.getType() + "", Arrays.toString(event.values));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * On accuracy changed.
     *
     * @param sensor   the sensor
     * @param accuracy the accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 监听传感器改变信息
     */
    private void registerListeners() {
        for (int sensor : mSensorList) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(sensor),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Gets json object.
     *
     * @return the json object
     */
    @NonNull
    public JSONObject getJSONObject() {
        return mJSONObject;
    }
}
