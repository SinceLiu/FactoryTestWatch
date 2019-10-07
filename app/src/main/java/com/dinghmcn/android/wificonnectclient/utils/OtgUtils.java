package com.dinghmcn.android.wificonnectclient.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.ActivityThread.TAG;

public class OtgUtils {
    public static final String USB_PATH = "/storage/12B9-40E7";
    private static final int OPEN_IN_MUSIC = 1;
    public static OtgUtils instance;
    private Context mContext;
    private Uri mUri;
    private long mMediaId = -1;
    private String strFromFile = "";
    private File mfile = null;
    private int size;

    public OtgUtils(Context mContext) {
        this.mContext = mContext;
        hadOtg();
    }

    public static OtgUtils getInstance(Context mContext) {
        if (instance == null)
            instance = new OtgUtils(mContext);
        return instance;
    }

    private void hadOtg() {
        String path = DiskManager.getUsbStoragePath(mContext);
        if (path == null) {
            Toast.makeText(mContext, "没有外接U盘", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<String> allPathList = new ArrayList<String>();
        allPathList.add(path);
//        ArrayList<String> allPathList = DiskManager.getAllStoragePath(this);
        size = allPathList.size();
        Log.i(TAG, "-----------------------size = " + size);
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

    public int getSize() {
        return size;
    }

    public boolean doTest(boolean end) {
        strFromFile = openFile(mfile);
        Log.v(TAG, " strFromFile: " + strFromFile);
        if (!"".equals(strFromFile)) {
            if (end) {
            }
        } else {
            Log.v(TAG, " strFromFile: is null");
            strFromFile = "read or write fail,please delete the test.txt in udisk and test again";
            return false;
        }
        return true;
    }

    public String getStrFromFile() {
        return strFromFile;
    }

    public void addFile() {
        try {
            mfile.createNewFile();
            writeFIle();
        } catch (IOException ioe) {
//            System.out.println("addFile IOException");
//            Toast.makeText(this, mfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//            ioe.printStackTrace();
        }

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
}
