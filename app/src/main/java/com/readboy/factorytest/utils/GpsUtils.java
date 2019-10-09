package com.readboy.factorytest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by chenzhi on 2017/12/13 0013.
 * <p>
 * 如果需要适配6.0以上系统请处理权限问题
 */
@SuppressLint("MissingPermission")
public class GpsUtils {

    private static final String TAG = "GPSUtils";
    private static LocationManager mLocationManager;
    private static int mCount = 0;

    private static Location mLocation = null;

    private Context mContext;

    private boolean isLocationOpened = false;
    /**
     * 位置监听
     */
    private static LocationListener locationListener = new LocationListener() {

        //位置信息变化时触发
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            Log.i(TAG, "时间：" + location.getTime()
                    + "\n经度：" + location.getLongitude()
                    + "\n纬度：" + location.getLatitude()
                    + "\n海拔：" + location.getAltitude());
        }

        //GPS状态变化时触发
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
                default:
                    break;
            }
        }

        //GPS开启时触发
        @Override
        public void onProviderEnabled(String provider) {
            mLocation = mLocationManager.getLastKnownLocation(provider);
        }

        //GPS禁用时触发
        @Override
        public void onProviderDisabled(String provider) {
            mLocation = null;
        }
    };
    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext()) {
                        iters.next();
                        count++;
                    }
                    Log.i(TAG, "搜索到卫星颗数：" + count + "/" + mCount);
                    mCount = count > mCount ? count : mCount;
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
                default:
                    break;
            }
        }
    };

    public GpsUtils(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS是否正常启动
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 开位置
            isLocationOpened = false;
            Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER, true);
        } else {
            isLocationOpened = true;
        }
        // 为获取地理位置信息时设置查询条件
        String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        if (bestProvider != null) {
            mLocation = mLocationManager.getLastKnownLocation(bestProvider);
        }
        // 监听状态
        mLocationManager.addGpsStatusListener(listener);

        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    /**
     * @return Location--->getLongitude()获取经度/getLatitude()获取纬度
     */
    public Location getLocation() {
        if (mLocation == null) {
            Log.e("GPSUtils", "setLocationData: 获取当前位置信息为空");
            return null;
        }
        return mLocation;
    }

    public String getLocalCity() {
        if (mLocation == null) {
            Log.e("GPSUtils", "getLocalCity: 获取城市信息为空");
            return "";
        }
        List<Address> result = getAddress(mLocation);

        String city = "";
        if (result != null && result.size() > 0) {
            city = result.get(0).getLocality();//获取城市
        }
        return city;
    }

    public String getAddressStr() {
        if (mLocation == null) {
            Log.e("GPSUtils", "getAddressStr: 获取详细地址信息为空");
            return "";
        }
        List<Address> result = getAddress(mLocation);

        String address = "";
        if (result != null && result.size() > 0) {
            address = result.get(0).getAddressLine(0);//获取详细地址
        }
        return address;
    }

    // 获取地址信息
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(mContext, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCount() {
        return mCount;
    }

    public void release() {
        if (!isLocationOpened) {
            Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER, false);
        }
        mLocationManager.removeGpsStatusListener(listener);
        mLocationManager.removeUpdates(locationListener);
    }
}
