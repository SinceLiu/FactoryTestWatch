package com.dinghmcn.android.wificonnectclient.utils;

import android.content.Context;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The type Usb disk utils.
 */
public class USBDiskUtils {
    private static final String MOUNTS_FILE = "/proc/mounts";
    /**
     * The constant instance.
     */
    public static USBDiskUtils instance;
    private static String path = "/mnt/usbhost1";
    public boolean mIsTestSuccess = false;
    Thread mUsbTestThread;
    boolean mIsRun = false;
    private Context mContext;
    private File mfile = null;
    private String strFromFile = "";

    /**
     * Instantiates a new Usb disk utils.
     *
     * @param mContext the m context
     */
    public USBDiskUtils(Context mContext) {
        this.mContext = mContext;
        isMounted();
    }

    /**
     * Is mounted boolean.
     *
     * @return the boolean
     */
    public boolean isMounted() {

        boolean blnRet = false;
        String strLine = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(MOUNTS_FILE));

            while ((strLine = reader.readLine()) != null) {
                if (strLine.contains(path)) {
                    blnRet = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reader = null;
            }
        }

        return blnRet;
    }

    /**
     * Get sd free size long.
     *
     * @return the long
     */
    public long getSDFreeSize() {
        if (!isMounted()) {
            return 0;
        }
        path = DiskManager.getUsbStoragePath(mContext);
        StatFs sf = new StatFs(path);
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * Gets sd all size.
     *
     * @return the sd all size
     */
    public long getSDAllSize() {
        if (!isMounted()) {
            return 0;
        }
        //取得SD卡文件路径
        path = DiskManager.getUsbStoragePath(mContext);
        StatFs sf = new StatFs(path);
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; //单位MB

    }

    public String fileInfo() {
        String path = DiskManager.getUsbStoragePath(mContext);
        if (path == null) {
            return "path is " + path;
        } else {
            return "path is " + path + "   file exists is " + new File(path).exists();
        }
    }

    public void stopTest() {
        mIsRun = false;
    }

    public void startTest() {
        if (mUsbTestThread != null && mUsbTestThread.isAlive()) {
            return;
        }
        mIsRun = true;
        mIsTestSuccess = false;
        mUsbTestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsRun) {
                    String tmpPath = DiskManager.getUsbStoragePath(mContext);
                    if (tmpPath != null) {
                        mIsRun = false;
                        mIsTestSuccess = true;
//                        ArrayList<String> allPathList = new ArrayList<>();
//                        allPathList.add(tmpPath);
//                        String pathString;
//                        int size = allPathList.size();
//                        for (int i = 0; i < size; i++) {
//                            pathString = allPathList.get(i);
//                            pathString = pathString + "/test.txt";
//                            mfile = new File(pathString);
//                            if (mfile.exists()) {
//                                try {
//                                    mfile.delete();
//                                } catch (Exception e) {
//
//                                    e.printStackTrace();
//                                }
//                                addFile();
//                                if (!doTest(i == (size - 1))) {
//                                    break;
//                                }
//                            } else {
//                                addFile();
//                                if (!doTest(i == (size - 1))) {
//                                    break;
//                                }
//                            }
//                        }
                    }
                }
            }
        });
        mUsbTestThread.start();
    }

    public void addFile() {
        try {
            Log.v("hqb", "hqb__addFile path = " + mfile.getAbsolutePath());
            mfile.createNewFile();
            writeFIle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFIle() {
        FileWriter fw;
        BufferedWriter bw;
        try {
            fw = new FileWriter(mfile);
            Log.v("hqb", "hqb__writeFIle path = " + mfile.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write("Udisk test successfully.");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean doTest(boolean end) {
        strFromFile = openFile(mfile);
        Log.v("hqb", "hqb strFromFile: " + strFromFile);
        if (!"".equals(strFromFile)) {
            if (end) {
                mIsRun = false;
                mIsTestSuccess = true;
//                Intent data = new Intent();
//                data.putExtra("readfile", strFromFile);
//                setResult(RESULT_OK, data);
//                Log.v(TAG, " onDestroy : " + strFromFile);
//                finish();
            }
        } else {
            Log.v("hqb", "hqb strFromFile: is null");
            strFromFile = "read or write fail,please delete the test.txt in udisk and test again";
            mIsTestSuccess = false;
//            Intent data = new Intent();
//            data.putExtra("readfile", strFromFile);
//            setResult(RESULT_CANCELED, data);
//            Log.v(TAG, " onDestroy : " + strFromFile);
//            finish();
            return false;
        }
        return true;
    }

    public String openFile(File file) {
        FileReader fr;
        BufferedReader br;
        String str = "";
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            int line = 1;
            if (br.ready()) {
                str = br.readLine();
                // line ++;
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
}