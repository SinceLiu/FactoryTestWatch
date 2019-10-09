package com.readboy.factorytest.utils;

/**
 * 获取磁盘挂载路径
 *
 * @author chenjd
 * @email chenjd@allwinnertech.com
 * @data 2011-8-10
 */

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * define the root path of flash,sdcard,usbhost
 *
 * @author chenjd
 */
public class DiskManager {
    private static final String TAG = DiskManager.class.getSimpleName();

    /**
     * 获取存储设备列表
     *
     * @param context the context
     * @return array list
     */
    public static ArrayList<String> initDevicePath(Context context) {
        ArrayList<String> totalDevicesList = new ArrayList<String>();
        try {
            StorageManager stmg = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method mMethod = stmg.getClass().getMethod("getVolumePaths");
            String[] list = (String[]) mMethod.invoke(stmg);
            // 获取存储器列表
            if (list == null || list.length < 1) {
                return totalDevicesList;
            }
            int length = list.length;
            for (int i = 0; i < length; i++) {
                if (isExistDisk(list[i])) {
                    totalDevicesList.add(list[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalDevicesList;
    }

    /**
     * 获取自带sdcard的路径
     *
     * @return inter storage path
     */
    public static String getInterStoragePath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取外部sd卡的路径
     *
     * @param context the context
     * @return sd storage path
     */
    public static String getSdStoragePath(Context context) {
        String path = null;
        ArrayList<String> totalDevicesList = initDevicePath(context);
        int size = totalDevicesList.size();
        for (int i = 0; i < size; i++) {
            if (!getInterStoragePath().equals(totalDevicesList.get(i))) {
//                if (totalDevicesList.get(i).contains("st")) {
//                }
                path = totalDevicesList.get(i);
                break;
            }
        }
        return path;
    }

    /**
     * 获取usb的路径
     *
     * @param context the context
     * @return usb storage path
     */
    public static String getUsbStoragePath(Context context) {
        String path = null;
        try {
            StorageManager stmg = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method mMethod = stmg.getClass().getMethod("getVolumes");
            List<VolumeInfo> volumeList = (List<VolumeInfo>) mMethod.invoke(stmg);
            ArrayList<VolumeInfo> list = new ArrayList<VolumeInfo>();
            for (int i = 0; i < volumeList.size(); i++) {
                list.add(volumeList.get(i));
//                System.out.println("volumeList.get(i)__path is" + volumeList.get(i).getPath().getAbsolutePath());
            }
            for (VolumeInfo vol : list) {
                if (vol.getType() == VolumeInfo.TYPE_PUBLIC) {
                    // 6.0中外置sd和usb标示为公共的
                    if (vol.getDisk() != null && vol.getDisk().isUsb()) {
                        File usbFile = vol.getPath();
                        if (usbFile != null) {
                            path = usbFile.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "--------------usbotg path = " + path);
        return path;
    }

    /**
     * 获取所有存储设备的路径
     *
     * @param context the context
     * @return all storage path
     */
    public static ArrayList<String> getAllStoragePath(Context context) {
        ArrayList<String> totalDevicesList = initDevicePath(context);
//        int size = totalDevicesList.size();
//        for (int i = 0; i < size; i++) {
//            if (!getInterStoragePath().equals(totalDevicesList.get(i))) {
//                if (totalDevicesList.get(i).contains("usb")) {
//                    path = totalDevicesList.get(i);
//                    break;
//                }
//            }
//        }
        return totalDevicesList;
    }

    /**
     * 判断路径是否由多部分组成
     *
     * @param context the context
     * @param dPath   the d path
     * @return boolean
     */
    public static boolean hasMultiplePartition(Context context, String dPath) {
        try {
            if (TextUtils.isEmpty(dPath)) {
                return false;
            }
            ArrayList<String> totalDevicesList = initDevicePath(context);
            int size = totalDevicesList.size();
            for (int i = 0; i < size; i++) {
                if (dPath.equals(totalDevicesList.get(i))) {
                    File file = new File(dPath);
                    String[] list = file.list();
                    int length = list.length;
                    for (int j = 0; j < length; j++) {
                        /* 如果目录命名规则不满足"主设备号:次设备号"(当前分区的命名规则),则返回false */
                        int lst = list[j].lastIndexOf("_");
                        if (lst != -1 && lst != (list[j].length() - 1)) {
                            try {
                                String major = list[j].substring(0, lst);
                                String minor = list[j].substring(lst + 1, list[j].length());
                                Integer.valueOf(major);
                                Integer.valueOf(minor);
                            } catch (NumberFormatException e) {
                                /* 如果该字符串不能被解析为数字,则退出 */
                                return false;
                            } catch (Throwable e) {
                                e.printStackTrace();
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        } catch (Throwable e) {
            Log.e(TAG, "hasMultiplePartition() exception : " + e);
            return false;
        }
    }


    /**
     * 判断外部sd卡是否存在
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isExistSDCard(Context context) {
        return isExistDisk(getSdStoragePath(context));
    }

    /**
     * 判断usb是否存在
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isExistUSB(Context context) {
        return isExistDisk(getUsbStoragePath(context));
    }

    /**
     * 判断路径是否存在
     *
     * @param path the path
     * @return boolean
     */
    public static boolean isExistDisk(String path) {
        boolean isTrue = false;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            // 如果总的大小为0，表示该存储器不存在
            if (file.getTotalSpace() > 0) {
                isTrue = true;
            }
        }
        Log.i(TAG, "isExistDisk(" + path + ") = " + isTrue);
        return isTrue;
    }

    /**
     * 获取空闲空间大小
     *
     * @param path the path
     * @return vacant space size
     */
    @SuppressWarnings("deprecation")
    public static long getVacantSpaceSize(String path) {
        StatFs fileStats = new StatFs(path);
        fileStats.restat(path);
        long size = ((long) fileStats.getAvailableBlocks() * (long) fileStats.getBlockSize());
        Log.i(TAG, "getVacantSpaceSize() size = " + size);
        return size;
    }
}
