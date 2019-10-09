package com.readboy.factorytest.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查需要的权限是否获取
 *
 * @author dinghmcn
 * @date 2018 /4/20 11:31
 */
public class CheckPermissionUtils {
    /**
     * 需要获取的权限
     */
    @NonNull
    private static String[] permissions = new String[]{
            // 地址信息
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            // 相机
            Manifest.permission.CAMERA,
            // 读写存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            // 电话
            Manifest.permission.CALL_PHONE,
            // 获取IMEI
            Manifest.permission.READ_PHONE_STATE,
            // 录音
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * 检查权限是否都已获取
     *
     * @param context the context
     * @return the string [ ]
     */
    public static String[] checkPermission(@NonNull Context context) {
        List<String> data = new ArrayList<>();
        for (String permission : permissions) {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                data.add(permission);
            }
        }
        return data.toArray(new String[0]);
    }
}
