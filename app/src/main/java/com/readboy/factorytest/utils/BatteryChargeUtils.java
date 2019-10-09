package com.readboy.factorytest.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import com.readboy.factorytest.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import static android.content.Context.BATTERY_SERVICE;

/**
 * 获取电池相关信息
 *
 * @author zl121325
 * @date 2019/4/10
 */
public class BatteryChargeUtils {
    private final String TAG = getClass().getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String batteryStatus;
    private String quality;
    private boolean isChargingPass = false;
    private int plugType;
    private int status;
    private int mLevel;
    private int voltage;
    private int temperature;
    private BatteryManager batteryManager;
    private BroadcastReceiver mChargeInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                //获取电源信息
                plugType = intent.getIntExtra("plugged", 0);
                //获取电池状态
                status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                //电池剩余电量
                mLevel = intent.getIntExtra("level", 0);
                //获取电池满电量数值
                intent.getIntExtra("scale", 0);
                //获取电池电压
                voltage = intent.getIntExtra("voltage", 0);
                //获取电池温度
                temperature = intent.getIntExtra("temperature", 0);
                quality = " (" + mLevel + "%)";
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    batteryStatus = mContext.getString(R.string.charging);
                    isChargingPass = true;
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    batteryStatus = mContext.getString(R.string.please_input_charger);
                    isChargingPass = false;
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    batteryStatus = mContext.getString(R.string.please_input_charger);
                    isChargingPass = false;
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    batteryStatus = mContext.getString(R.string.battery_info_status_full);
                    isChargingPass = true;
                } else {
                    batteryStatus = "unknown state";
                    isChargingPass = false;
                }
            }
        }
    };

    public BatteryChargeUtils(Context mContext) {
        this.mContext = mContext;
        batteryManager = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
        // 注册电池事件监听器
        mContext.registerReceiver(mChargeInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /**
     * 移除监听器
     */
    public void unregisterReceiver() {
        mContext.unregisterReceiver(mChargeInfoReceiver);
    }

    /**
     * 当前电流 mA
     *
     * @return the current current
     */
    public double getCurrentChargingCurrent() {
        double result = 0;
        String path1 = "/sys/devices/platform/battery/FG_Battery_CurrentConsumption";
        String path2 = "/sys/class/power_supply/battery/current_now";
        try {
            if (new File(path1).exists()) {
                result = getValue(path1) / 10.0f;
            } else {
                result = getValue(path2) / 1000.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取电池状态
     *
     * @return the battery status
     */
    public String getBatteryStatus() {
        return batteryStatus;
    }

    /**
     * Gets quality.
     *
     * @return the quality
     */
    public String getQuality() {
        return quality;
    }

    /**
     * //获取电源信息
     *
     * @return the plug type
     */
    public int getPlugType() {
        return plugType;
    }

    /**
     * 获取电池状态
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * 获取电量
     *
     * @return the level
     */
    public int getmLevel() {
        int battery = 0;
        if (Build.VERSION.SDK_INT > 21) {
            battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        }
        if (mLevel != 0) {
            return mLevel;
        } else {
            return battery;
        }
    }

    /**
     * 获取电池电压
     *
     * @return the voltage
     */
    public int getVoltage() {
        return voltage;
    }

    /**
     * 获取电池温度
     *
     * @return the temperature
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * 是否在充电
     *
     * @return the boolean
     */
    public boolean isChargingPass() {
        return isChargingPass;
    }

    public Double getCpuTemperature() {
        try {
            return getValue("/sys/class/thermal/thermal_zone1/temp") / 1000;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public String getAdc() {
        byte[] buff = new byte[50];
        try {
            Class<?> c = Class.forName("android.util.AdcCheckNative");
            // open
            Method open = c.getMethod("openDev");
            open.invoke(c);
            //get
            Method get = c.getMethod("SetAdcCheck", byte[].class);
            get.invoke(c, buff);
            //close
            Method close = c.getMethod("closeDev");
            close.invoke(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(buff).trim();
    }

    public static Double getValue(String path) throws FileNotFoundException {
        String str;
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            str = bufferedReader.readLine();
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            Double value;
            if (str == null) {
                return 0.0;
            }
            try {
                value = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                value = null;
            }
            return value;
        } catch (FileNotFoundException e1) {
            throw e1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
