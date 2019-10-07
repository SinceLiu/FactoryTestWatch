package com.dinghmcn.android.wificonnectclient.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemProperties;
import android.os.storage.StorageManager;

import com.dinghmcn.android.wificonnectclient.DiskManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.content.Context.AUDIO_SERVICE;


/**
 * Created by zl121325 on 2019/4/14.
 */

public class UsbUtils {
    Context mContext;
    String path;
    private String strFromFile = "";
    private File mfile = null;
    private StorageManager mStorageManager;
    private AudioManager am;
    private MediaPlayer mp = null;
    private int nCurrentMusicVolume;
    private UsbManager mUsbManager;
    private boolean mUsbAccessoryMode;
    private boolean isadbmode = false;
    String currentFunction = "none";
    private  final String MASS_ADB = "diag,serial_smd,rmnet_bam,adb";
    private  final String MASS = "diag,serial_smd,rmnet_bam";
    //add for port switch by songguangyu 20140429 start
    private  final String MTP_ADB = "mtp,adb";
    private  final String MTP = "mtp";
    private static UsbUtils instance;
    public UsbUtils(Context mContext) {
        this.mContext = mContext;
        getManager();
    }
    public static UsbUtils getInstance(Context mContext){
        if (instance==null)
            instance=new UsbUtils(mContext);
        return instance;
    }
    private void getManager() {
        mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    }

    public boolean isadbmode(){
        currentFunction = SystemProperties.get("sys.usb.config", "000");
        if (currentFunction != null && currentFunction.endsWith("adb")){
            isadbmode = true;
        }
        mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);
        return isadbmode;
    }
    public void setCurrentFunction(String function){
        if (function!=null&&function.equals(MASS)){
        if (isadbmode){
            SystemProperties.set("sys.usb.config", MASS_ADB);
        }else {
            SystemProperties.set("sys.usb.config", MASS);
        }
            function = MASS;
        }else {
        if (isadbmode){
            SystemProperties.set("sys.usb.config", MTP_ADB);
            function = "mtp";
        }else {
            SystemProperties.set("sys.usb.config", MTP);
            function = "mtp";
        }
        }
        setCurrentFunction(function, true);
    }
    private boolean setCurrentFunction(String function,boolean boo){
        Boolean invoke = false;
        try {
            Class cls = Class.forName("android.hardware.usb.UsbManager");
            try {
                UsbManager obj = (UsbManager) cls.newInstance();
                try {
                    Method meth = cls.getMethod("setCurrentFunction", String.class,Boolean.class);
                    meth.setAccessible(true);
                    try {
                         invoke = (Boolean) meth.invoke(obj, function,boo);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return invoke;
    }
    private boolean hadUsbDisk(){
        path = DiskManager.getUsbStoragePath(mContext);
        if(path == null){
            return false;
        }else {
            return true;
        }
    }
    private void creatTest(){
        ArrayList<String> allPathList = new ArrayList<String>();
        allPathList.add(path);
        int size = allPathList.size();
        String pathString;
        for (int i = 0; i < size; i++) {
            pathString = allPathList.get(i);
            pathString = pathString + "/test.txt";
            mfile = new File(pathString);
            if (mfile.exists()) {
                try {
                    mfile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addFile();
                if (!doTest(i == (size - 1))) {
                    break;
                }
            } else {
                addFile();
                if (!doTest(i == (size - 1))) {
                    break;
                }
            }
        }
    }

    public void addFile() {
        try {
            mfile.createNewFile();
            writeFIle();
        } catch (IOException ioe) {
//			System.out.println("addFile IOException");
//			Toast.makeText(this, mfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//			ioe.printStackTrace();
        }

    }

    public boolean doTest(boolean end) {
        strFromFile = openFile(mfile);
        if (!"".equals(strFromFile)) {
            if (end) {
                Intent data = new Intent();
                data.putExtra("readfile", strFromFile);
            }
        } else {
            strFromFile = "read or write fail,please delete the test.txt in udisk and test again";
            Intent data = new Intent();
            data.putExtra("readfile", strFromFile);
            return false;
        }
        return true;
    }
    public void writeFIle() {
        FileWriter fw;
        BufferedWriter bw;
        try {
            fw = new FileWriter(mfile);
            bw = new BufferedWriter(fw);
            bw.write("Udisk test successfully.");
            bw.close();
        } catch (IOException ioe) {
        }

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
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException Ie) {
            Ie.printStackTrace();
        }
        return str;
    }
    public void startSpeaker(){
        am = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        am = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        int nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        am.setStreamVolume(AudioManager.STREAM_MUSIC, nMaxMusicVolume, 0);
        String uDiskPath = "/storage/usbotg";

        String sound_path = uDiskPath + "/test.mp3";
        File f = new File(sound_path);
//        Uri uri = Uri.parse(CITTestHelper.COLLIGATE_SOUND_PATH);
        Uri uri = Uri.parse(sound_path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType( Uri.fromFile(f), "audio/x-mpeg");
//        startActivity(intent);

//        if (sdIsPass) {
        //           mp = new MediaPlayer();
//            try{
        //               mp.setDataSource("/storage/udisk/test.mp3");
//                mp.prepare();
        //           }catch (IOException e) {
//                        ;
//            }
//            mp = MediaPlayer.create(getApplicationContext(), uri);
//            } else {
//                  mp = MediaPlayer.create(getApplicationContext(), R.raw.test);
//            }
/*            if (mp != null) {
                  Log.i(TAG,"mp != null");
                  mp.setLooping(true);
                  mp.start();
            }*/
    }
    public void pauseSpeaker(){
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
        }
    }
}
