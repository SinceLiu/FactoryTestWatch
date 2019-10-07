package com.dinghmcn.android.wificonnectclient.scanCode;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dinghmcn.android.wificonnectclient.MainActivity;
import com.dinghmcn.android.wificonnectclient.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.szx.simplescanner.zbar.Result;
import cn.szx.simplescanner.zbar.ZBarScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {


    private ZBarScannerView zBarScannerView;
    private Handler handler = new Handler();

    private String netWorkName;
    private String password;
    private String netWorkType;
    private boolean isConnect = false;
    private Timer mTimer = null;
    private TimerTask mTimerTask = new TimerTask() {

        @Override
        public void run() {
            Log.e("CHEN", "ScanCodeActivity:SSD:" + WifiUtils.getSSID());
            Log.e("CHEN", "ScanCodeActivity:pwd:" + password);
            if (!TextUtils.isEmpty(WifiUtils.getSSID())
                    && WifiUtils.getSSID().equals(netWorkName)) {//连接到了扫描的wifi
                stopTimer();
                Intent intent = new Intent(ScanCodeActivity.this, MainActivity.class);
                intent.putExtra("ssid", netWorkName);
                intent.putExtra("password", password);
                startActivity(intent);  //将ssid和密码传到mainActivity
                finish();
            } else if (!WifiUtils.getSSID().equals(netWorkName)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanCodeActivity.this, "该WIFI无法连接，已连接其他WIFI", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ViewGroup container = (ViewGroup) findViewById(R.id.container);

        //ViewFinderView是根据需求自定义的视图，会被覆盖在相机预览画面之上，通常包含扫码框、扫描线、扫码框周围的阴影遮罩等
        zBarScannerView = new ZBarScannerView(this, new ViewFinderView(this),
                ScanCodeActivity.this);
        //设置是否要根据扫码框的位置去调整对焦区域的位置,默认不调整
        zBarScannerView.setShouldAdjustFocusArea(true);
        container.addView(zBarScannerView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (zBarScannerView != null) {
            zBarScannerView.stopCamera();//释放相机资源等各种资源
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermission() {
            zBarScannerView.startCamera();//打开系统相机，并进行基本的初始化
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult != null) {
            String content = rawResult.getContents();
            //WIFI:T:WPA;P:123456789ACD.;S:ABC;
            Log.e("CHEN", "content" + content);
            String passwordTemp = content.substring(content
                    .indexOf("P:"));
            password = passwordTemp.substring(2,
                    passwordTemp.indexOf(";"));
            String netWorkTypeTemp = content.substring(content
                    .indexOf("T:"));
            netWorkType = netWorkTypeTemp.substring(2,
                    netWorkTypeTemp.indexOf(";"));
            String netWorkNameTemp = content.substring(content
                    .indexOf("S:"));
            netWorkName = netWorkNameTemp.substring(2,
                    netWorkNameTemp.indexOf(";"));

            WifiUtils wifiUtils = new WifiUtils(ScanCodeActivity.this);
            wifiUtils.openWifi();
            if (!isConnect) {
                wifiUtils.quickConnWifi(netWorkName, password, WifiUtils.getWifiType(netWorkType));
                startTimer();
                isConnect = true;
            }
        }
        //2秒后再次识别
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                zBarScannerView.getOneMoreFrame();//再获取一帧图像数据进行识别
            }
        }, 2000);
    }

    private void startTimer() {
        if (mTimer != null) {
            stopTimer();
        }

        mTimer = new Timer(true);

        mTimer.schedule(mTimerTask, 0, 1000);
        Log.w("CHEN", "timer Run!");
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Log.w("CHEN", "Run StopTimer!");
    }


}
