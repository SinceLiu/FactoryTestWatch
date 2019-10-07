package com.dinghmcn.android.wificonnectclient.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

import static android.app.ActivityThread.TAG;


/**
 * 获取蓝牙相关信息
 */
public class BluetoothUtils {
    @SuppressLint("StaticFieldLeak")
    private static BluetoothUtils instance = null;
    /**
     * The M context.
     */
    Context mContext;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Set<BluetoothDevice> bluetoothDevices;
    private Set<String> bluetoothDeviceAddresses;
    private BroadcastReceiver blueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            Log.e("czl", "onReceive: " + action);
            Log.e("czl", "确实走了");
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    // 蓝牙状态改变
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                        // 蓝牙开启
                        if (!mBluetoothAdapter.isDiscovering()) {
                            Log.e("czl", "确实走了3");
                            mBluetoothAdapter.startDiscovery();
                        } else {
                            mBluetoothAdapter.startDiscovery();
                            Log.e("czl", "确实走了2");
                        }
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    // 发现蓝牙设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 保存发现的蓝牙设备，并根据MAC地址设备防止重复加入
                    if (!bluetoothDeviceAddresses.contains(device.getAddress())) {
                        Log.d(TAG, device.getName() + "---" + device.getAddress());
                        bluetoothDeviceAddresses.add(device.getAddress());
                        bluetoothDevices.add(device);
                        Log.e("czl", "确实走了6" + bluetoothDevices);
                        EventBus.getDefault().post("bluetooth");
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:


                    break;

                default:
            }
        }
    };

    private BluetoothUtils(Context mContext) {
        this.mContext = mContext;
        registerReceiver();
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static BluetoothUtils getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new BluetoothUtils(context);
        }

        return instance;
    }

    // 监听蓝牙事件广播
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        // 开始搜索蓝牙设置
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // 蓝牙状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 搜索蓝牙设置完成
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 发现蓝牙设备
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(blueToothReceiver, intentFilter);
        Log.e("czl", "确实走了1");
    }

    /**
     * 开启蓝牙
     */
    public void bluetoothOpen() {
        Log.w(TAG, "BlueToothOpen: ");
        bluetoothDevices = new ArraySet<>();
        bluetoothDeviceAddresses = new ArraySet<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            // 蓝牙未开启则开启蓝牙
            mBluetoothAdapter.enable();
        } else {
            // 蓝牙已开启则开始搜索蓝牙设备
            assert mBluetoothAdapter != null;
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 返回发现的蓝牙设备
     *
     * @return the bluetooth devices
     */
    public Set<BluetoothDevice> getBluetoothDevices() {
        return bluetoothDevices;
    }

    /**
     * 清理
     */
    public void exits() {
        mContext.unregisterReceiver(blueToothReceiver);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            mBluetoothAdapter.disable();
            Log.e(TAG, "关闭蓝牙");
        }
    }

    /**
     * 清理
     */
    public void exit() {
        mContext.unregisterReceiver(blueToothReceiver);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            mBluetoothAdapter.disable();
            Log.e(TAG, "关闭蓝牙");
        }
    }
}
