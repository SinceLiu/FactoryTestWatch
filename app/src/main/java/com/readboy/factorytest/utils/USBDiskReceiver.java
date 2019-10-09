package com.readboy.factorytest.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * USB设备插拔事件监听
 */
public class USBDiskReceiver extends BroadcastReceiver {
    private static final String TAG = "USBDiskReceiver";

    /**
     * On receive.
     *
     * @param context the context
     * @param intent  the intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)) {
            if ("android.intent.action.MEDIA_REMOVED".equals(action)) {
                Log.e(TAG, "onReceive: ---------------usb拨出-------------");
            }
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                Log.e(TAG, "onReceive: --------usb路径-------" + path);

            }
        }
    }


}
