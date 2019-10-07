package com.dinghmcn.android.wificonnectclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.WindowManager;

/**
 * Created by zl121325 on 2019/4/14.
 */

public class SignalUtils extends Activity {
    Context mContext;
    SharedPreferences mSp;
    private boolean unknownSim = false;
    private static SignalUtils instance;
    public SignalUtils(Context mContext) {
        this.mContext = mContext;
        getsimOper();
    }
    public static SignalUtils getInstance(Context mContext){
        if (instance==null) {
            instance=new SignalUtils(mContext);
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void getsimOper() {
        String simOper = SystemProperties.get("gsm.sim.operator.numeric", "46099");
        if("46000".equals(simOper) || "46002".equals(simOper) || "46007".equals(simOper) || "46008".equals(simOper)) {
            unknownSim = false;
        } else if("46001".equals(simOper) || "46006".equals(simOper) || "46009".equals(simOper)) {
            unknownSim = false;
        } else if("46003".equals(simOper) || "46005".equals(simOper) || "46011".equals(simOper)) {
            unknownSim = false;
        } else {
            unknownSim = true;
        }
    }
    public void emergencyCall(){
//        Intent intent112 = new Intent(Intent.ACTION_CALL_PRIVILEGED);
        Intent intent112 = new Intent("android.intent.action.CALL_PRIVILEGED");
        intent112.setData(Uri.fromParts("tel", "112", null));
    }
    public void serviceCall(){
        Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED");
        String simOper = SystemProperties.get("gsm.sim.operator.numeric", "46099");
        if("46000".equals(simOper) || "46002".equals(simOper) || "46007".equals(simOper) || "46008".equals(simOper)) {
            intent.setData(Uri.fromParts("tel", "10086", null));
        } else if("46001".equals(simOper) || "46006".equals(simOper) || "46009".equals(simOper)) {
            intent.setData(Uri.fromParts("tel", "10010", null));
        } else if("46003".equals(simOper) || "46005".equals(simOper) || "46011".equals(simOper)) {
            intent.setData(Uri.fromParts("tel", "10000", null));
        } else {
            intent.setData(Uri.fromParts("tel", "112", null));
        }
    }
}
