package com.dinghmcn.android.wificonnectclient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * WiFi相关信息获取
 *
 * @author dinghmcn
 * date 2018 /4/25 16:09
 */
public class WifiManagerUtils {
    private static final String TAG = "WifiManagerUtils";

    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    @Nullable
    private static WifiManagerUtils instance = null;

    private static Context mContext;
    private static WifiManager mWifiManager;

    private WifiManagerUtils(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    @Nullable
    public static WifiManagerUtils getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new WifiManagerUtils(context);
        }
        return instance;
    }

    /**
     * 连接指定wifi
     *
     * @param ssid     the ssid
     * @param password the password
     * @return the boolean
     */
    public boolean connectWifi(@NonNull String ssid, String password) {
        Log.d(TAG, "SSID:" + ssid + " password:" + password);

        //如果之前有类似的配置
        WifiConfiguration tempConfig = isExist(ssid);

        if (tempConfig != null) {
            //则清除旧有配置
            int netId = mWifiManager.updateNetwork(
                    createWifiConfig(ssid, password, getType(ssid), tempConfig.networkId));
            Log.d(TAG, "netId1:" + netId);
            return mWifiManager.enableNetwork(netId < 0 ? tempConfig.networkId : netId, true);
        } else {
            int netId = mWifiManager.addNetwork(createWifiConfig(ssid, password, getType(ssid)));
            Log.d(TAG, "netId2:" + netId);
            return mWifiManager.enableNetwork(netId, true);
        }
    }

    /**
     * Is wifi enabled boolean.
     *
     * @return the boolean
     */
    public boolean isWifiEnabled() {
        Log.d(TAG, "isWifiEnabled():" + mWifiManager.isWifiEnabled());
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 判断wifi是否连接
     *
     * @param ssid the ssid
     * @return the boolean
     */
    public boolean isWifiConnected(String ssid) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        // 判断是否是wifi连接而不是数据
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetInfo.isConnected()) {
            Log.d(TAG, activeNetInfo.toString());
//            Log.d(TAG, "isWifiConnected():" + ('"' + ssid + '"').equals(activeNetInfo.getExtraInfo()));
//            return ('"' + ssid + '"').equals(activeNetInfo.getExtraInfo());
            return true;
        }
        Log.d(TAG, "isWifiConnected():false");
        return false;
    }

    /**
     * 开启
     */
    public void openWifi() {
        mWifiManager.setWifiEnabled(true);
    }

    public void closeWifi() {
        mWifiManager.setWifiEnabled(false);
    }

    /**
     * 新建wifi配置
     *
     * @param ssid
     * @param password
     * @param type
     * @return
     */
    @NonNull
    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";

        //不需要密码的场景
        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    @NonNull
    private WifiConfiguration createWifiConfig(String ssid, String password, int type, int netId) {
        WifiConfiguration wifiConfiguration = createWifiConfig(ssid, password, type);
        wifiConfiguration.networkId = netId;
        return wifiConfiguration;
    }

    /**
     * 判断是否发现该wifi
     *
     * @param ssid
     * @return
     */
    @Nullable
    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    /**
     * 获取wifi加密类型
     *
     * @param ssid
     * @return
     */
    private int getType(@NonNull String ssid) {

        List<ScanResult> mScanResultList = mWifiManager.getScanResults();

        for (ScanResult scanResult : mScanResultList) {
            Log.w(TAG, "getType: " + scanResult.level);
            if (ssid.equals(scanResult.SSID)) {
                if (scanResult.capabilities.contains("WPA")) {
                    // 使用 WPA 加密
                    return WIFICIPHER_WPA;
                } else if (scanResult.capabilities.contains("WEP")) {
                    // 使用 WEP 加密
                    return WIFICIPHER_WEP;
                } else {
                    // 未加密
                    return WIFICIPHER_NOPASS;
                }
            } else {
                Log.d(TAG, ssid + "isn't discover.");
            }
        }

        return WIFICIPHER_WPA;
    }

    /**
     * 获取周围Wifi列表
     *
     * @return the list
     */
    public List<ScanResult> getWifis() {
        return mWifiManager.getScanResults();

    }
}
