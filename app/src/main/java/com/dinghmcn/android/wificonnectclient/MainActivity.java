package com.dinghmcn.android.wificonnectclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinghmcn.android.wificonnectclient.model.DataModel;
import com.dinghmcn.android.wificonnectclient.scanCode.WifiUtils;
import com.dinghmcn.android.wificonnectclient.utils.BatteryChargeUtils;
import com.dinghmcn.android.wificonnectclient.utils.BluetoothUtils;
import com.dinghmcn.android.wificonnectclient.utils.CallUtils;
import com.dinghmcn.android.wificonnectclient.utils.CheckPermissionUtils;
import com.dinghmcn.android.wificonnectclient.utils.ConnectManagerUtils;
import com.dinghmcn.android.wificonnectclient.utils.ConnectManagerUtils.EnumCommand;
import com.dinghmcn.android.wificonnectclient.utils.CustomPopDialog2;
import com.dinghmcn.android.wificonnectclient.utils.GPSUtilss;
import com.dinghmcn.android.wificonnectclient.utils.HeadsetLoopbackUtils;
import com.dinghmcn.android.wificonnectclient.utils.IRbciService;
import com.dinghmcn.android.wificonnectclient.utils.LogcatFileManager;
import com.dinghmcn.android.wificonnectclient.utils.MyActivityManager;
import com.dinghmcn.android.wificonnectclient.utils.RbciManager;
import com.dinghmcn.android.wificonnectclient.utils.SensorManagerUtils;
import com.dinghmcn.android.wificonnectclient.utils.SignalUtils;
import com.dinghmcn.android.wificonnectclient.utils.StorageUtils;
import com.dinghmcn.android.wificonnectclient.utils.USBDiskReceiver;
import com.dinghmcn.android.wificonnectclient.utils.USBDiskUtils;
import com.dinghmcn.android.wificonnectclient.utils.VersionUtils;
import com.dinghmcn.android.wificonnectclient.utils.WifiManagerUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主界面窗口
 *
 * @author dinghmcn
 * @date 2018 /4/20 10:47
 */
public class MainActivity extends Activity {
    final int MSG_TESTACTIVITY = 0x1000;
    /**
     * 命令标识
     */
    public static final int CMD_CODE = 0xd9;
    /**
     * 相机测试结果返回标识
     */
    public static final int REQUEST_CAMERA_CODE = 9;
    /**
     * 全屏显示纯色判断屏幕是否有坏点返回标识
     */
    public static final int REQUEST_SHOWPICTUREFULL = 10;
    /**
     * 是否输出日志
     */
    private static final boolean mPrintLog = true;
    private static final String GET = "get";
    private static final String Start = "start";
    private static final String END = "end";
    private static boolean isCatchKey = false;
    private static boolean isCatchTouch = false;
    @Nullable
    private static JSONObject mKeyJsonObject;
    @Nullable
    private static JSONArray mTouchJsonArray;
    @Nullable
    private static JSONObject mTouchJsonObject;
    private static JSONArray mTouchMoveJsonObject;
    private static JSONArray mTouchMoveJsonObject2;

    private String originalSSID;
    private String originalPassword;

    private int mResult = RESULT_OK;
    private static int mOK = 2;

    private static String cameraInfod = "";

    private boolean unknownSim = false;


    /**
     * 日志标志.
     */
    protected final String TAG = getClass().getSimpleName();
    /**
     * 是否在测试相机
     */
    public boolean isCameraOpen = false;
    private ScrollView mScrollView;
    private TextView mTextView, mSeq, mViewTestResult;
    private SpannableStringBuilder mConnectMessage;
    private SpannableStringBuilder mTestResult;
    private Handler mMainHandler;
    @Nullable
    private ConnectManagerUtils mConnectManager = null;
    @Nullable
    private WifiManagerUtils mWifiManagerUtils = null;
    private GPSUtilss mGPSUtilss = null;
    private SignalUtils mSignalUtils;
    private CallUtils mCallUtils;
    private BatteryChargeUtils batteryChargeUtils;
    private BluetoothUtils bluetoothUtils;
    private LogcatFileManager logcatFileManager;
    private VersionUtils versionUtils;
    private StorageUtils storageUtils;
    private HeadsetLoopbackUtils headsetLoopbackUtils;
    private Gson gson = new Gson();
    private DataModel dataModel;
    private DataModel mShowPictureFullDataModel;
    private DataModel mKeyDataModel;
    private DataModel mRecordDataModel;
    private USBDiskReceiver usbDiskReceiver;
    private final static int MY_PERMISSION_REQUEST_CONSTANT = 1001;
    private static CustomPopDialog2 dialog;
    private static boolean isScreen;
    private RbciManager mRbciManager;
    private IRbciService mIRbciService;
    private Intent serviceIntent;


    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    private String mMobile;

    public static final int REQUEST_CALL_PERMISSION = 1012; //拨号请求码
    public static final int REQUEST_ACCESS_FINE_LOCATION = 1013; //GPS请求码
    private final int HANDER_CALL_RETURN = 1;

     private ActivityManager  mActivityManager;
    Camera mCamera = null;



    String dir = "cache";

    public static MainActivity Instance;
    private boolean isBlueToothSearchFinish = false;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Integer integer) {
        if (integer == 1010) {
            sendservermessage(true, mResult);
        }
        if (integer == 1011) {
            isBlueToothSearchFinish = true;
//            sendBluetooth();
        }

    }

/*    //发送蓝牙指令给服务器
    private void sendBluetooth() {
        JSONArray jsonArray = new JSONArray();
        Set<BluetoothDevice> bluetoothDevices = bluetoothUtils.getBluetoothDevices();
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            jsonArray.put(bluetoothDevice.getName() + "," + bluetoothDevice.getAddress());
        }
        if (jsonArray.length() <= 0) {
            dataModel.setBluetooth("无可连接的蓝牙设备");
        } else {
            dataModel.setBluetooth(jsonArray.toString().replace("\\", "").replace("\\", "").replace("[", "").replace("]", ""));
        }
        mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
    }*/


    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyRunnable.Instance = this;
        EventBus.getDefault().register(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);
        Instance = this;
        mScrollView = findViewById(R.id.message_scrollview);
        mTextView = findViewById(R.id.connect_message);
        mViewTestResult = findViewById(R.id.test_result);
        mSeq = findViewById(R.id.seq);
        usbDiskReceiver = new USBDiskReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        registerReceiver(usbDiskReceiver, filter);


        if (getIntent() != null) {   //获取到已经连接的wifi 帐号和密码
            originalSSID = getIntent().getStringExtra("ssid");
            originalPassword = getIntent().getStringExtra("password");
        }

        testNext();
        dir = System.currentTimeMillis() + "";


    }

    /**
     * 准备工作，初始化测试项、打开Wifi并连接服务
     */
    private void testNext() {
        initPermission();

        mConnectMessage = new SpannableStringBuilder();
        mMainHandler = new MainHandel(this);
//        Message message = new Message();
//        message.what = MSG_TESTACTIVITY;
//        mMainHandler.sendMessageDelayed(message, 1000);

        mWifiManagerUtils = WifiManagerUtils.getInstance(this);
        batteryChargeUtils = BatteryChargeUtils.getInstance(this);
        getBatteryInfo();

        mRbciManager = new RbciManager(this, mIRbciService);



//        mSignalUtils=SignalUtils.getInstance(this);
        bluetoothUtils = BluetoothUtils.getInstance(this);
        bluetoothUtils.bluetoothOpen();
        headsetLoopbackUtils = HeadsetLoopbackUtils.getInstance(this);
//        outPutMessage("headsetLoopbackUtils.start()");
        outPutMessage(getVersionName(this));

//        logcatFileManager = LogcatFileManager.getInstance();
//        logcatFileManager.startLogcatManager(this);
        versionUtils = VersionUtils.getInstance(this);
        storageUtils = StorageUtils.getInstance(this);

        setscreemlights();

//        Handler handler = new Handler();handler.postDelayed(new Runnable() {
//            @Override
//            public void run() { mGPSUtilss = new GPSUtilss(MainActivity.this);//测试G500X时候要注销掉不然崩溃
//                 }}, 5000);
        assert mWifiManagerUtils != null;
        // 获取服务器信息
        String ip = loadFromSDFile("socketIP.txt");
        if (null == ip || ip.trim().isEmpty()) {
//			prepareConnectServer("{\"IP\":\"192.168.0.128\",\"Port\":12345,\"SSID\":\"rd123\"," +
//					"\"PWD\":\"50515051aa.." + "\",\"Station\":1}");
//            prepareConnectServer("{\"IP\":\"192.168.1.253\",\"Port\":12345,\"SSID\":\"readboy-factory-fqc-test\"," +
//                    "\"PWD\":\"readboy@fqc" + "\",\"Station\":1}");
            prepareConnectServer("{\"IP\":\"192.168.1.253\",\"Port\":12345,\"SSID\":\"" + originalSSID + "\"," +
                    "\"PWD\":\"" + originalPassword + "\",\"Station\":1}");
            // prepareConnectServer("{\"IP\":\"192.168.1.253\",\"Port\":12345,\"SSID\":\""+(TextUtils.isEmpty(originalSSID)?"readboy-factory-fqc-test1":originalSSID)+"\"," +
            //         "\"PWD\":\""+(TextUtils.isEmpty(originalPassword)?"readboy@fqc1":originalPassword)+ "\",\"Station\":1}");

        } else {
            prepareConnectServer("{\"IP\":" + ip + ",\"Port\":12345,\"SSID\":\"tianxi\"" +
                    ",\"PWD\":\"28896800\",\"Station\":1}");
//            prepareConnectServer("{\"IP\":\"192.168.1.253\",\"Port\":12345,\"SSID\":\""+originalSSID+"\"," +
//                    "\"PWD\":\""+originalPassword+ "\",\"Station\":1}");
        }

    }

    /**
     * 设置屏幕亮度
     */
    private   void setscreemlight(){
        int brightness = BrightnessTools.getScreenBrightness(MainActivity.this);

       if (BrightnessTools.isAutoBrightness(MainActivity.this)==true){
           BrightnessTools.stopAutoBrightness(MainActivity.this);
       }
        if (brightness < 100 || brightness>160) {
            BrightnessTools.setBrightness(this, 128);
            BrightnessTools.saveBrightness(MainActivity.this,128);
        }
    }

    /**
     * 设置屏幕亮度
     */
    private   void setscreemlights(){
        if(getScreenMode()==1){
            BrightnessTools.stopAutoBrightness(MainActivity.this);
        }
        Log.e("CHEN","getScreenBrightness:"+getScreenBrightness());
        if(getScreenBrightness()<=100)
        {
            saveScreenBrightness(142);
            setScreenBrightness(142);
        }
        if (getScreenBrightness()>=160){
            saveScreenBrightness(142);
            setScreenBrightness(142);
        }
        Log.e("CHEN","ScreenBrightness:"+getScreenBrightness());

    }
    /**
     * 获得当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private int getScreenMode() {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException) {

        }
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private void setScreenMode(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    private void saveScreenBrightness(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    private void setScreenBrightness(int paramInt) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    /**
     * 展示序列号二维码
     */

    private void showCodeScan() {

        //  Toast.makeText(this, getDeviceSerial(), Toast.LENGTH_SHORT).show();
        Bitmap bitmap = ZXingUtils.createQRImage(getDeviceSerial(), 500, 500);// 这里是获取图片Bitmap，也可以传入其他参数到Dialog中
        CustomPopDialog2.Builder dialogBuild = new CustomPopDialog2.Builder(this);
        dialogBuild.setImage(bitmap);
        // 点击外部区域关闭
        dialog = dialogBuild.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void closedialog() {
        dialog.dismiss();
    }

    /**
     * 获取设备的序列号
     */
    public static String getDeviceSerial() {
        String serial = "unknown";
        try {
            Class clazz = Class.forName("android.os.Build");
            Class paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", paraTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            serial = (String) method.invoke(null, "ro.serialno");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return serial;
    }


    /**
     * 初始化权限事件.
     */
    protected void initPermission() {
        Log.d(TAG, "check permissions");
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            Log.d(TAG, "permission all");
        } else {
            Log.d(TAG, "request permissions : " + Arrays.toString(permissions));
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * On activity result.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "result:" + requestCode + "/" + resultCode);
        Log.v("hqb", "hqb__MainActivity__onActivityResult__requestCode == REQUEST_CAMERA_CODE = " + (requestCode == REQUEST_CAMERA_CODE)
                + "__resultCode == RESULT_OK = " + (resultCode == RESULT_OK) + "__data = " + data);
        if (requestCode == REQUEST_CAMERA_CODE) {
            isCameraOpen = false;
            Log.e(TAG, "onActivityResult: " + "11 " + (null == data));
            if (resultCode == RESULT_OK && null != data) {
                Uri pictureUri = data.getData();
                assert pictureUri != null;
                Log.w(TAG, "onActivityResult: " + pictureUri.getPath());
                if (ConnectManagerUtils.mConnected) {
                    assert mConnectManager != null;
                    File file = new File(pictureUri.getPath());
                    dataModel.setCamera("ok");
                    mConnectManager.sendFileToServer(this,file, gson.toJson(dataModel, DataModel.class));
                    mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                    outPutLog(getString(R.string.send_file, pictureUri.toString()));
                }
            } else if (null == data) {
                if (ConnectManagerUtils.mConnected) {
                    assert mConnectManager != null;
                    dataModel.setCamera("error");
//                mConnectManager.sendFileToServer(file, gson.toJson(dataModel, DataModel.class));
                    mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                }

                // 相机
//                String cameraInfo=cameraInfod;
//                if (null != cameraInfo && cameraInfo.contains("-")) {
//                    Intent intent20 = new Intent(MainActivity.this, CameraActivity.class)
//                            .putExtra("CameraInfo", cameraInfo);
//                    if (isCameraOpen) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivityForResult(intent20, REQUEST_CAMERA_CODE);
//                            }
//                        }, 4000);
//                    } else {
//                        isCameraOpen = true;
//                        startActivityForResult(intent20, REQUEST_CAMERA_CODE);
//                    }
//                }
//                else {
//                    outPutLog(R.string.command_type_error);
//                    mConnectManager.sendMessageToServerNotJson("cameraInfod__type_null");
//                    Log.e(TAG, "return cameraInfod__type_null.");
//                }
            } else if (resultCode == RESULT_CANCELED) {
                outPutLog(R.string.command_file_error);
                mConnectManager.sendMessageToServerNotJson("createFile__failed");
                Log.e(TAG, "return createFile__failed.");
            } else {
                outPutLog(R.string.execute_command_error);
                mConnectManager.sendMessageToServerNotJson("execute command error");
                Log.e(TAG, "return result failed.");
            }
        } else {
        }
    }
/**
 * 反馈服务器数据
 *
 * @param
 */


    /**
     * 打印消息
     *
     * @param message
     */
    public void outPutMessage(String message) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        String timeStr = df.format(new Date());
        SpannableString spannableString = new SpannableString(timeStr + " ");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#0099EE"));
        spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mConnectMessage.append(spannableString).append(message).append("\r\n");

        mTextView.setText(mConnectMessage);
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void outPutMessage(int idRes) {
        outPutMessage(getString(idRes));
    }

    /**
     * 打印日志
     *
     * @param message
     */
    public void outPutLog(String message) {
        if (mPrintLog) {
            outPutMessage(message);
        }
    }

    private void outPutLog(int idRes) {
        outPutMessage(getString(idRes));
    }

    private void outPutLogSeq(String message) {
        if (!TextUtils.isEmpty(message)) {
            if (mSeq != null) {
                mSeq.setText(message);
            }
        }
    }

    private void outPutTestResult(String message) {
        if (!TextUtils.isEmpty(message)) {
            if (mViewTestResult != null) {
                SpannableString spannableString = new SpannableString(message + " ");
                if (message.equals("PASS")) {
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ff00")), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                spannableString.setSpan(new RelativeSizeSpan(4.5f), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mViewTestResult.setText((spannableString));
            }
        }
    }

    public void ShowMessage(String msg) {
        this.runOnUiThread(new MyRunnable(msg));
    }

    /**
     * 记录按键信息
     *
     * @param keyCode the key code
     * @param event   the event
     * @return the boolean
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isCatchKey) {
            outPutLog(keyCode + "|" + KeyEvent.keyCodeToString(keyCode));
            if (mKeyJsonObject == null) {
                mKeyJsonObject = new JSONObject();
            }
            try {
                mKeyJsonObject.putOpt(KeyEvent.keyCodeToString(keyCode), keyCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private int mTouchRepeat = 0; //过滤掉长按的情况
    private boolean mPoint2Down = false;  //是否出现双指按下的情况
    /*   */

    /**
     * 记录触摸信息
     */
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        int index = ev.getActionIndex();
        Log.d(TAG, "onTouchEvent");
        int count = ev.getPointerCount();
        int actionMasked = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        int pointerCount = ev.getPointerCount();
        Log.d(TAG, "onTouchEventnum" + count);
        if (isCatchTouch) {
            if (mTouchJsonArray == null) {
                mTouchJsonArray = new JSONArray();
            }

            Log.w(TAG, "dispatchTouchEvent: " + ev.getAction());
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "down");
                    mTouchJsonObject = new JSONObject();
                    mTouchMoveJsonObject = new JSONArray();
                    mTouchMoveJsonObject2 = new JSONArray();
                    try {
                        //打印第一个手指的点击操作
                        mTouchJsonObject.put("DOWN", "(" + ev.getRawX() + "," + ev.getRawY() + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "move");
                    Log.d(TAG, "手指数：" + ev.getPointerCount());
                    for (int i = 0; i < pointerCount; i++) {
                        Log.d("CHEN", "pointerIndex=" + i + ",pointerId=" + ev.getPointerId(i));
                        Log.d("CHEN", "第" + (i + 1) + "个手指的X坐标=" + ev.getX(i) + ",Y坐标=" + ev.getY(i));
                        if (i == 1) {
                            if (mTouchMoveJsonObject2 != null) {
                                Log.d(TAG, "move");
                                //打印手指的滑动坐标
                                mTouchMoveJsonObject2.put("(" + ev.getX(1) + "," + ev.getY(1) + ")");
                            }
                        }
                    }
//                    if (ev.getPointerId(index) == 2){
//                        int pointIndex = ev.findPointerIndex(1);
//                        Log.d(TAG, "第er根手指位置：x="+ev.getX(pointIndex)+";y="+ev.getY(pointIndex));
//
//                    }
//                    if(ev.getPointerCount()==1) {
                    if (mTouchMoveJsonObject != null) {
                        Log.d(TAG, "move3");
                        //打印手指的滑动坐标
                        mTouchMoveJsonObject.put("(" + ev.getRawX() + "," + ev.getRawY() + ")");
                    }
//                    }

                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "up");
                    try {
                        //打印最后一个手指抬起的坐标
                        assert mTouchJsonObject != null;
                        mTouchJsonObject.put("MOVE", mTouchMoveJsonObject);
                        mTouchJsonObject.put("UP", "(" + ev.getRawX() + "," + ev.getRawY() + ")");
                        mTouchJsonArray.put(mTouchJsonObject);
                        Log.d(TAG, mTouchJsonObject.toString() + " | " + mTouchJsonArray.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    mTouchJsonArray.put(mTouchJsonObject);
//                    Log.d(TAG, mTouchJsonObject.toString() + " | " + mTouchJsonArray.toString());
                    mTouchMoveJsonObject = null;
                    mTouchJsonObject = null;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
//                    mPoint2Down = true;
                    Log.d(TAG, "move2");
                    Log.d("CHEN", "第" + (actionIndex + 1) + "个手指按下");
                    try {
                        if (mTouchJsonObject != null)
                            mTouchJsonObject.put("TWODOWN", "(" + ev.getX(1) + "," + ev.getY(1) + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;


                case MotionEvent.ACTION_POINTER_UP:
                    Log.d(TAG, "move2");
                    Log.d("CHEN", "第" + (actionIndex + 1) + "个手指抬起");
//                    if (mPoint2Down && mTouchRepeat < 10) {
                    //do something here
                    try {
                        assert mTouchJsonObject != null;
                        mTouchJsonObject.put("TWOMOVE", mTouchMoveJsonObject2);
                        mTouchJsonObject.put("TWOUP", "(" + ev.getX(1) + "," + ev.getY(1) + ")");
//                            mTouchJsonArray.put(mTouchJsonObject);
//                            Log.d(TAG, mTouchJsonObject.toString() + " | " + mTouchJsonArray.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mTouchMoveJsonObject2 = null;
//                    mTouchJsonArray.put(mTouchJsonObject);
//                    Log.d(TAG, mTouchJsonObject.toString() + " | " + mTouchJsonArray.toString());

//                        Log.v("tap_tap_event", "It works!");
//                    }
                    break;

                default:
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 获取服务器信息，准备连接服务器
     *
     * @param connectInfo
     */
    private void prepareConnectServer(@Nullable String connectInfo) {
        Log.w(TAG, "prepareConnectServer: " + connectInfo);
        if (connectInfo != null && !connectInfo.isEmpty()) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(connectInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert jsonObject != null;
            String serverIp = jsonObject.optString("IP", "");
            int serverPort = jsonObject.optInt("Port", -1);
            String wifiSsid = jsonObject.optString("SSID", "");
            String wifiPassword = jsonObject.optString("PWD", "");

            if (ConnectManagerUtils.isIp(serverIp) && serverPort > 0) {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp, serverPort);
                mConnectManager = ConnectManagerUtils.newInstance(mMainHandler, inetSocketAddress);
//                assert mConnectManager != null;
//                assert mWifiManagerUtils != null;
                Log.e("CHEN", "Main:ssid:" + wifiSsid);

//                if (WifiManagerUtils.getInstance(this).isWifiConnected(wifiSsid)) {
//                    //不用在连接了
//                    ShowMessage( "Connect wifi success!");
//                } else {
                if (mConnectManager != null && mWifiManagerUtils != null) {
                    mConnectManager.connectServer(mWifiManagerUtils, wifiSsid, wifiPassword);
                    outPutMessage(getString(R.string.connect_loading, inetSocketAddress.toString()));
                }
//                }
            } else {
                outPutMessage(serverIp + ":" + serverPort + getString(R.string.ip_or_port_illegal));
            }
        } else {
            outPutMessage(connectInfo + " " + getString(R.string.connect_info_error));
        }
    }

    /**
     * 获取电池信息
     */
    private void getBatteryInfo() {
        Log.w(TAG, "getBatteryInfo: " + batteryChargeUtils.getBatteryStatus() + "---"
                + batteryChargeUtils.getQuality() + "---" + batteryChargeUtils.getCurrentChargingCurrent() +
                "---" + batteryChargeUtils.getmLevel() + "---" + batteryChargeUtils.getPlugType() + "---" +
                batteryChargeUtils.getStatus() + "---" + batteryChargeUtils.getTemperature() + "----" +
                batteryChargeUtils.getVoltage() + "---" + batteryChargeUtils.isChargingPass());
    }

    @Override
    public void finish() {
        if (null != headsetLoopbackUtils) {
            headsetLoopbackUtils.stop();
        }
        super.finish();
    }

    /**
     * On destroy.
     */
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        try {
            ConnectManagerUtils.mConnected = false;
            if (null != mConnectManager) {
                mConnectManager.disconnectServer();
                mConnectManager = null;
            }
            if (null != batteryChargeUtils) {
                batteryChargeUtils.unregisterReceiver();
            }
//            if (null != logcatFileManager) {
//                logcatFileManager.stopLogcatManager();
//            }
//            if (null != bluetoothUtils) {。
//                bluetoothUtils.exit();
//            }
//            if (null != headsetLoopbackUtils) {
//                headsetLoopbackUtils.stop();
//            }
            if(null!=mCamera){
                mCamera.release();
                mCamera = null;
            }
            if (null != usbDiskReceiver) {
                unregisterReceiver(usbDiskReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private String loadFromSDFile(String fileName) {
        fileName = "/" + fileName;
        String result = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + fileName);
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "没有找到指定文件", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    /**
     * 关机
     */
    private void shutdownSystem() {
        try {
            Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    @Override
    protected void onResume() {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        super.onResume();
    }

    /**
     * 重启
     */
    private void rebootSystem() {
        Intent intent = new Intent("android.intent.action.REBOOT");
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        sendBroadcast(intent);
    }

    @SuppressLint("HandlerLeak")
    private class MainHandel extends Handler {
        /**
         * The M activity weak reference.
         */
        WeakReference<MainActivity> mActivityWeakReference;

        /**
         * Instantiates a new Main handel.
         *
         * @param activity the activity
         */
        MainHandel(MainActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        private String mLastPartInfo = "";

        /**
         * Handle message.
         *
         * @param msg the msg
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            final MainActivity mainActivity = mActivityWeakReference.get();

            assert mConnectManager != null;
            assert mainActivity.mConnectManager != null;

            if (msg.what == CMD_CODE) {
                Gson gson = new Gson();
                String data = (String) msg.obj;
                String saveData = (String) msg.obj;
                String tmpData = data.trim();
                if (!tmpData.startsWith("{") && !TextUtils.isEmpty(mLastPartInfo)) {
                    data = mLastPartInfo + data;
                    mLastPartInfo = "";
                }
                if (!TextUtils.isEmpty(data)) {
                    int index = data.indexOf("}");
                    if (index >= 0) data = data.substring(0, index + 1);
                    if (index < data.length() - 1) {
                        mLastPartInfo = data.substring(index + 1);
                    }
                }
                try {
//					dataModel = gson.fromJson((String) msg.obj, DataModel.class);
                    saveDatabjectToPath(getExternalCacheDir() + "/" + dir + "/" + System.currentTimeMillis() + ".txt", saveData);
                    dataModel = gson.fromJson(data, DataModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    String path = getExternalCacheDir() + "/" + System.currentTimeMillis() + ".txt";
//					saveDatabjectToPath(path, (String) msg.obj);
                    saveDatabjectToPath(path, data);
                    dataModel = null;
                    mConnectManager.sendMessageToServerNotJson("error");
                    return;
                }
//                dataModel = gson.fromJson((String) msg.obj, DataModel.class);

                // 关机
                if (GET.equals(dataModel.getShutdown())) {

                    shutdownSystem();
                }

                // 消息
                String message = dataModel.getShowMessage();
                if (null != message && !message.isEmpty()) {
                    outPutMessage(message);
                }

                if (dataModel != null) {
                    dataModel.setSn(VersionUtils.getSerialNumber());
                }
                if (GET.equals(dataModel.getSn()) || GET.equals(dataModel.getDisk())
                        || GET.equals(dataModel.getSd()) || GET.equals(dataModel.getVersion())
                        || GET.equals(dataModel.getBattery())) {
                    // 串号
                    dataModel.setSn(VersionUtils.getSerialNumber());

                    // 存储
                    dataModel.setDisk(storageUtils.getRomAvailableStorage() + "，" + storageUtils.getRomTotalStorage());

                    // sd卡
                    dataModel.setSd(storageUtils.getSdAvailableStorage() + "，" + storageUtils.getSdTotalStorage());

                    // 版本号
                    dataModel.setVersion(versionUtils.getSoftwareVersion());

                    // 电池
                    dataModel.setBattery(batteryChargeUtils.getmLevel() + "%，" +
                            "" + (batteryChargeUtils.getTemperature() / 10.0f) + "C，" +
                            "" + (batteryChargeUtils.getVoltage() / 1000.0f) + "V");

                    // otg
//                    USBDiskUtils usbDiskUtils = USBDiskUtils.getInstance(MainActivity.this);
//                    dataModel.setOtg(USBDiskUtils.getInstance(MainActivity.this).getSDAllSize()
//                            + "," + usbDiskUtils.getSDFreeSize());

                    mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                }


                if ("close".equals(dataModel.getWifi())) {
                    outPutMessage("Close Wifi");
                    mWifiManagerUtils.closeWifi();
                }

                if (GET.equals(dataModel.getOtg())) {
                    // otg
                    USBDiskUtils usbDiskUtils = USBDiskUtils.getInstance(MainActivity.this);
//					dataModel.setOtg(USBDiskUtils.getInstance(MainActivity.this).getSDAllSize()
//							+ "," + usbDiskUtils.getSDFreeSize());
                    usbDiskUtils.startTest();
                    int time = dataModel.getTimeout() * 1000;
                    postDelayed(() -> {
                        String result = usbDiskUtils.mIsTestSuccess ? "ok" : "error";
                        dataModel.setOtg(result);
                        mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                    }, time);
                }

                // 传感器
                if (GET.equals(dataModel.getAccelerometer()) || GET.equals(dataModel.getLight())
                        || GET.equals(dataModel.getProximity()) || GET.equals(dataModel.getMagnetometer())
                        || GET.equals(dataModel.getGyroscope())) {
                    SensorManagerUtils sensorManagerUtils = SensorManagerUtils.getInstance(mainActivity);
                    postDelayed(() -> {
                        assert sensorManagerUtils != null;
                        // 加速度传感器
                        String accelerometer = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_ACCELEROMETER + "", "error");
                        dataModel.setAccelerometer(null != accelerometer ? accelerometer : "error");
                        // 光感
                        String light = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_LIGHT + "", "error");
                        dataModel.setLight(light);
                        // 距离传感器
                        String proximity = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_PROXIMITY + "", "error");
                        dataModel.setProximity(proximity);
                        // 磁感应器
                        String magnetometer = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_MAGNETIC_FIELD + "", "error");
                        dataModel.setMagnetometer(magnetometer);
                        // 陀螺仪
                        String gyroscope = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_GYROSCOPE + "", "error");
                        dataModel.setGyroscope(gyroscope);

                        Log.d("dhm_sensor", gson.toJson(dataModel, DataModel.class));
                        mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                    }, 1000);
                }

                // 相机
                String cameraInfo = dataModel.getCamera();
                cameraInfod = cameraInfo;
                if (null != cameraInfo && cameraInfo.contains("-")) {
                    Intent intent20 = new Intent(MainActivity.this, CameraActivity.class)
                            .putExtra("CameraInfo", cameraInfo);
                    if (isCameraOpen) {
                        postDelayed(() -> startActivityForResult(intent20, REQUEST_CAMERA_CODE), 4000);
                    } else {
                        isCameraOpen = true;
                        startActivityForResult(intent20, REQUEST_CAMERA_CODE);
                    }
                }
                // 蓝牙
                if (GET.equals(dataModel.getBluetooth())) {
                    Timer blueToothTimer = new Timer();
                    TimerTask blueToothTask = new TimerTask() {
                        int i = 0;

                        @Override
                        public void run() {
                            i++;
                            JSONArray jsonArray = new JSONArray();
                            Set<BluetoothDevice> bluetoothDevices = bluetoothUtils.getBluetoothDevices();
                            if (bluetoothDevices.size() > 0 && isBlueToothSearchFinish) {
                                for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
                                    jsonArray.put(bluetoothDevice.getName() + "," + bluetoothDevice.getAddress());
                                }
                                dataModel.setBluetooth(jsonArray.toString().replace("\\", "").replace("\\", "").replace("[", "").replace("]", ""));
                                mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                                blueToothTimer.cancel();
                                isBlueToothSearchFinish = false;
                            } else if (bluetoothDevices.size() <= 0 && isBlueToothSearchFinish) {
//
                                if (i >= 60) {
                                    dataModel.setBluetooth("");
                                    Log.e("CHEN", "12秒内没有搜到有蓝牙");
                                    mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                                    blueToothTimer.cancel();
                                } else {
                                    bluetoothUtils.bluetoothOpen();
                                    isBlueToothSearchFinish = false;
                                    Log.e("CHEN", "重启蓝牙搜索");
                                }

                            } else if (bluetoothDevices.size() <= 0 ){
                                Log.e("CHEN", "搜寻蓝牙中");
                            }
                        }
                    };
                    blueToothTimer.schedule(blueToothTask, 0, 200);
                   /* if (bluetoothUtils != null) {
                        bluetoothUtils.exits();
                        bluetoothUtils = null;
                    }
                    bluetoothUtils = BluetoothUtils.getInstance(MainActivity.this);
                    if (PermissionUtils.isGranted(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)) {
                        bluetoothUtils.bluetoothOpen();
                    } else {
                        PermissionUtils.permission(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                                .callBack(new PermissionUtils.PermissionCallBack() {
                                    @Override
                                    public void onGranted(PermissionUtils permissionUtils) {
                                        bluetoothUtils.bluetoothOpen();
                                    }

                                    @Override
                                    public void onDenied(PermissionUtils permissionUtils) {
                                        Toast.makeText(MainActivity.this, "拒绝了打开蓝牙的权限", Toast.LENGTH_SHORT).show();
                                    }
                                }).request();
                    }
//                  postDelayed(() -> {
////                      JSONArray jsonArray = new JSONArray();
//                        Set<BluetoothDevice> bluetoothDevices = bluetoothUtils.getBluetoothDevices();
////                        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
////                            jsonArray.put(bluetoothDevice.getName() + "," + bluetoothDevice.getAddress());
////                            Log.e("czl", "jsonArray.bluetoothDevice.getName(): "+bluetoothDevice.getName() );
////                        }
//                        if (bluetoothDevices.size() == 0){
//                            dataModel.setBluetooth("未在可用列表里搜索到蓝牙");
//                            mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
//                        }else {
//
//                            }
//
//                   }, 1000);*/

                }


                // 振动
                if (GET.equals(dataModel.getVibrator())) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (null != vibrator && vibrator.hasVibrator()) {
                        vibrator.vibrate(dataModel.getTimeout());
                    }
                }

                //辅助摄像头进光量
                if (GET.equals(dataModel.getAuxiliaryCamera())) {
                    openAuxCameraBrightness();
                    Timer CameraTimer = new Timer();
                    TimerTask cameraTask = new TimerTask() {
                        int i=0;
                        @Override
                        public void run() {
                           i++;
                        int AuxCameraBrightness = getAuxCameraBrightness();
                            if (AuxCameraBrightness >0) {
                                String s = String.valueOf(AuxCameraBrightness);
                                dataModel.setAuxiliaryCamera(s);
//                    setAuxiliaryCamera
                                mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
//                                closeCamera();
                                closeAuxCameraBrightness();
                                CameraTimer.cancel();

                            }else if(i>=10){
                                dataModel.setAuxiliaryCamera("");
                                mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
//                                closeCamera();
                                closeAuxCameraBrightness();
                                CameraTimer.cancel();

                            }
                        }
                    };
                    CameraTimer.schedule(cameraTask, 0, 500);

//                    closeCamera();
                }

//                GPS
                if (GET.equals(dataModel.getGps())) {
                    if(mGPSUtilss==null) {
                        try {
                            mGPSUtilss = new GPSUtilss(MainActivity.this);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Timer GPSTimer = new Timer();
                    TimerTask gpsTask = new TimerTask() {
                        int i=0;
                        @Override
                        public void run() {
                            i++;
//                        postDelayed(() -> {
                                assert mGPSUtilss != null;
                                int GPSREES = mGPSUtilss.getcount();
                                if (GPSREES > 0) {
                                    String s = String.valueOf(GPSREES);
                                    dataModel.setGps(s);
                                    mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                                    mGPSUtilss.remolistener();
                                    GPSTimer.cancel();
//
                                } else if(i>=20) {
                                    dataModel.setGps("error");
                                    mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                                    mGPSUtilss.remolistener();
                                    GPSTimer.cancel();
//
                                }

                        }
                    };
                    GPSTimer.schedule(gpsTask, 0, 500);
//                            },2000);

                }

                // 拨号
                if (GET.equals(dataModel.getDial())) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE) !=
                    PackageManager.PERMISSION_GRANTED){;
                        Log.e("CHEN","no permission");
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},
                                REQUEST_CALL_PERMISSION);
                    }else {
                        Log.e("CHEN","has permission");
                        callPhone();
                    }

//
                }

                // wifi
                if (GET.equals(dataModel.getWifi())) {
                    Timer wifiTimer = new Timer();
                    TimerTask wifiTask = new TimerTask() {
                        @Override
                        public void run() {
                            assert mWifiManagerUtils != null;
//                            List<ScanResult> rssis = mWifiManagerUtils.getWifis();
                            int wifiREES = mWifiManagerUtils.getWifisRSSI();
//                            Log.e("czl", "run: "+ rssis.size());
                            if (wifiREES < 0 && wifiREES > -100) {
                                JSONArray jsonArray = new JSONArray();
//                                for (ScanResult wifi : rssis) {
//                                    jsonArray.put(wifi.SSID + "," + wifi.level);
//                                }
//                                dataModel.setWifi(jsonArray.toString().replace("\"", "")
//                                        .replace("[", "").replace("]", ""));
                                String WIFIREESd = WifiUtils.getSSID(MainActivity.this) + "," + wifiREES;
                                dataModel.setWifi(WIFIREESd);
                                mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                                wifiTimer.cancel();
                            }

                        }
                    };
                    wifiTimer.schedule(wifiTask, 0, 200);

                }

                // 录音
                if (GET.equals(dataModel.getRecord())) {
                    int time = dataModel.getTimeout() * 1000;
                    mRecordDataModel = dataModel;
//                    headsetLoopbackUtils.start();
                    headsetLoopbackUtils.start();
                    postDelayed(() -> {
                        if (mRecordDataModel != null) {
                            mRecordDataModel.setRecord(headsetLoopbackUtils.mIsStartRecordSuccess ? "ok" : "error");
                            mConnectManager.sendMessageToServer(gson.toJson(mRecordDataModel, DataModel.class));
                            headsetLoopbackUtils.stop();
                        }
                    }, time);
                }

                // 按键
                if (GET.equals(dataModel.getKey())) {
                    mKeyDataModel = dataModel;
                    int time = dataModel.getTimeout() * 1000;
                    isCatchKey = true;
                    postDelayed(() -> {
                        Log.v("hqb", "hqb__key__mKeyJsonObject = " + mKeyJsonObject);
                        if (mKeyJsonObject != null) {
                            mainActivity.mConnectManager.sendMessageToServer(mKeyJsonObject.toString());
                        } else {
                            if (mKeyDataModel != null) {
                                mKeyDataModel.setKey("ok");
                                mConnectManager.sendMessageToServer(gson.toJson(mKeyDataModel, DataModel.class));
                            }
                        }
                        mKeyJsonObject = null;
                        isCatchKey = false;
                    }, time);
                }

                //判断测试总结果
                if (dataModel.getShowMessage() != null) {
                    String ret = dataModel.getShowMessage();
                    if (ret.contains("PASS") && !ret.contains("FAIL")) {
                        outPutTestResult("PASS");
                    } else {
                        outPutTestResult("FAIL");
                    }
                }

                // 触摸
                if (Start.equals(dataModel.getTouch())) {
                    String touch = dataModel.getTouch();
                    Log.d("TAG", "touchsuccess" + touch);
                    isCatchTouch = true;
                    dataModel.setTouch("ok");
                    mainActivity.mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));

//
//                         isCatchTouch = false;
//                    int time = dataModel.getTimeout() * 1000;
//                    postDelayed(() -> {
//
//                    }, time);

                }
                // 触摸
                if (END.equals(dataModel.getTouch())) {
                    String touch = dataModel.getTouch();
                    Log.d("TAG", "touchsuccess" + touch);
                    isCatchTouch = false;

                    if (mTouchJsonArray != null) {
                        mainActivity.mConnectManager.sendMessageToServer(mTouchJsonArray.toString());
                    } else {
                        dataModel.setTouch("ok");
                        mainActivity.mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                    }
                    mTouchJsonArray = null;
//
                }


                // 屏幕
                if (dataModel.getScreen() != null) {
                    String imageName = dataModel.getScreen();
                    mShowPictureFullDataModel = dataModel;
                    closedialog();//关闭二维码
                    int resId = mainActivity.getResources()
                            .getIdentifier(imageName, "drawable",
                                    mainActivity.getPackageName());

                    if (resId > 0) {    //背景id
                        if (dataModel.getScreenoperation() == 0) {   //开
                            Log.v("hqb", "hqb__ShowPictureFullActivity__dataModel.getScreenopeneration() = " + dataModel.getScreenoperation());
//                        dataModel.getTimeout() + "__dataModel.getTimeout() * 1000 = " + (dataModel.getTimeout() * 1000)
                            final Intent intent30 = new Intent(mainActivity,
                                    ShowPictureFullActivity.class).putExtra("res_id", resId).putExtra("getScreenopeneration", dataModel.getScreenoperation());
                            mainActivity.startActivity(intent30);
//                            mainActivity.startActivityForResult(intent30, REQUEST_SHOWPICTUREFULL);
//                            seuccess(); //执行到这个时候
                        } else if (dataModel.getScreenoperation() == 1) {     //关掉
                            Activity topActivity = MyActivityManager.getInstance().getCurrentActivity();
                            if (topActivity instanceof ShowPictureFullActivity) {

                                topActivity.finish();   //关掉
                            }
                            sendservermessage(true, mResult);
                        }

                        mainActivity.outPutLog(mainActivity.getString(R.string.show_file, imageName));
                        Log.d(mainActivity.TAG, mainActivity.getString(R.string.show_file, imageName));
                    } else {    //非法背景id
                        mainActivity.outPutLog(mainActivity.getString(R.string.file_not_exist, imageName));
                        Log.d(mainActivity.TAG, mainActivity.getString(R.string.file_not_exist, imageName));
                    }

                }
            } else if (msg.what == MSG_TESTACTIVITY) {
//				mShowPictureFullDataModel = new DataModel();
//				String imageName = "red";
//				int resId = mainActivity.getResources()
//						.getIdentifier(imageName, "drawable",
//								mainActivity.getPackageName());
//				if (resId > 0) {
//					final Intent intent30 = new Intent(mainActivity,
//							ShowPictureFullActivity.class).putExtra("res_id", resId);
////                        mainActivity.startActivity(intent30);
//					mainActivity.startActivityForResult(intent30, REQUEST_SHOWPICTUREFULL);
//					mainActivity.outPutLog(mainActivity.getString(R.string.show_file, imageName));
//					Log.d(mainActivity.TAG, mainActivity.getString(R.string.show_file, imageName));
//				} else {
//					mainActivity.outPutLog(mainActivity.getString(R.string.file_not_exist, imageName));
//					Log.d(mainActivity.TAG, mainActivity.getString(R.string.file_not_exist, imageName));
//				}

//				Message message = new Message();
//				message.what = MSG_TESTACTIVITY;
//				mMainHandler.sendMessageDelayed(message, 5000);
//				headsetLoopbackUtils.start();
//				postDelayed(() -> {
//					String result = headsetLoopbackUtils.mIsStartRecordSuccess ? "ok" : "error";
//					headsetLoopbackUtils.stop();
//					Log.v("hqb", "hqb__record result = " + result);
//				}, 3000);

//				USBDiskUtils usbDiskUtils = USBDiskUtils.getInstance(MainActivity.this);
////				String otg = USBDiskUtils.getInstance(MainActivity.this).getSDAllSize()
////						+ "," + usbDiskUtils.getSDFreeSize() + usbDiskUtils.fileInfo();
//				usbDiskUtils.startTest();
//				postDelayed(() -> {
//					usbDiskUtils.stopTest();
//					String result = usbDiskUtils.mIsTestSuccess ? "ok" : "error";
//					Log.v("hqb", "hqb__usb result = " + result);
//					outPutLog("usb test " + result);
//				}, 3000);
//				Message message = new Message();
//				message.what = MSG_TESTACTIVITY;
//				mMainHandler.sendMessageDelayed(message, 4000);
            } else {
                switch (EnumCommand.values()[msg.what]) {
                    // 连接服务器返回状态
                    case CONNECT:
                        int connect = msg.arg1;
                        switch (connect) {
                            case ConnectManagerUtils.CONNECT_FAILED:
                                mainActivity.outPutMessage(R.string.connect_failed);
                                break;
                            case ConnectManagerUtils.CONNECT_CLOSED:
                                mainActivity.outPutMessage(R.string.connect_closed);
                                break;
                            case ConnectManagerUtils.CONNECT_SUCCESS:
                                showCodeScan();  //展示二维码
                                mainActivity.outPutMessage(R.string.connect_success);
                                break;
                            default:
                        }
                        break;
                    // 接收命令状态
                    case COMMAND:
                        int command = msg.arg1;
                        switch (command) {
                            case ConnectManagerUtils.COMMAND_ERROR:
                                mainActivity.outPutLog(R.string.command_error);
                                break;
                            case ConnectManagerUtils.COMMAND_RECEIVE:
                                mainActivity.outPutLog(R.string.wait_command);
                                break;
                            case ConnectManagerUtils.COMMAND_SEND:
                                mainActivity.outPutLog(msg.obj.toString());
                                break;
                            default:
                        }
                        break;
                    case SEQ:
                        mainActivity.outPutLogSeq(msg.obj.toString());
                        mConnectManager.sendMessageToServerNotJson("seq=ok");
                        break;
                    case Alive:
                        mainActivity.outPutMessage(msg.obj.toString());
                        mConnectManager.sendMessageToServerNotJson("I am alive!!");
                        mainActivity.outPutMessage("I am alive!!");
                        break;
                    default:
                        mainActivity.outPutLog(Integer.toString(msg.what));
                }
            }
        }
    }




//
    private void callPhone() {

        try {
            Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:" + 112));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        // 延迟5秒后自动挂断电话
                        // 首先拿到TelephonyManager
                        TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        Class<TelephonyManager> c = TelephonyManager.class;

                        // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
                        Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
                        //允许访问私有方法
                        mthEndCall.setAccessible(true);
                        final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

                        // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
                        Method mt = obj.getClass().getMethod("endCall");
                        //允许访问私有方法
                        mt.setAccessible(true);
                        mt.invoke(obj);
//                        Toast.makeText(MainActivity.this, "挂断电话！", Toast.LENGTH_SHORT).show();
                        dataModel.setDial("ok");
                        mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                    } catch (Exception e) {
                        dataModel.setDial("error");
                        mConnectManager.sendMessageToServer(gson.toJson(dataModel, DataModel.class));
                        e.printStackTrace();
                    }
                }
            }, 8 * 1000);
        }catch (Exception e) {
                            e.printStackTrace();
                        }
    }


    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Log.e("CHEN1","no permission");
                    Toast.makeText(this, "请允许拨号权限后再试", Toast.LENGTH_SHORT).show();
                } else {//成功
                    Log.e("CHEN1","has permission");
//                    callPhone();
                }
                break;
            case REQUEST_ACCESS_FINE_LOCATION: //GPS
                if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Log.e("CHEN1","no permission");
                    Toast.makeText(this, "请允许GPS权限后再试", Toast.LENGTH_SHORT).show();
                } else {//成功
                    Log.e("CHEN1","has permission");
//                    callPhone();
                    checkGPS();
                }
                break;

        }
    }

    /**
     * 检测GPS权限
     *
     * @param
     */
    public void checkGPS() {
        if (checkReadPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION)) {
//
        }
    }


    //获取辅摄像头进光量
    //由于是调用读书郎本地的东西，下面报错不需要管

    public int getAuxCameraBrightness() {
        try {
            @SuppressLint("WrongConstant") Object manager = getSystemService("rbci");
            Class rbciManager = manager.getClass();
            Method getAuxCameraBrightness = rbciManager.getMethod("GetAuxCameraBrightness");
            getAuxCameraBrightness.setAccessible(true);
            Log.e("CHEN","打印辅摄像头进光量"+getAuxCameraBrightness.invoke(manager,null));
            return (int) getAuxCameraBrightness.invoke(manager, null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int openAuxCameraBrightness() {
        try {
            @SuppressLint("WrongConstant") Object manager = getSystemService("rbci");
            Class rbciManager = manager.getClass();
            Method closeAuxCamera = rbciManager.getMethod("OpenAuxCamera");
            closeAuxCamera.setAccessible(true);
            Log.e("CHEN", "开启摄像头进光量");
            return (int) closeAuxCamera.invoke(manager, null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int closeAuxCameraBrightness() {
        try {
            @SuppressLint("WrongConstant") Object manager = getSystemService("rbci");
            Class rbciManager = manager.getClass();
            Method closeAuxCamera = rbciManager.getMethod("CloseAuxCamera");
            closeAuxCamera.setAccessible(true);
            Log.e("CHEN", "关闭摄像头进光量");
            return (int) closeAuxCamera.invoke(manager, null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    /**
     * 关闭相机，释放资源。
     */
    private void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void openAuxCameraBrightnessd() {
        int numberOfCameras = Camera.getNumberOfCameras();// 获取摄像头个数
        //遍历摄像头信息
        for (int cameraId = 1; cameraId < numberOfCameras; cameraId++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//后摄像头
                mCamera = Camera.open(cameraId);//打开摄像头
            }
        }
    }


    //返回切黑白屏的指令给服务器
    private void sendservermessage(boolean requestCode, int resultCode) {

        if (requestCode == true) {
            Log.v("hqb", "hqb__onActivityResult__mShowPictureFullDataModel = " + mShowPictureFullDataModel);
            if (mShowPictureFullDataModel != null) {

                if (resultCode == RESULT_OK) {
                    mShowPictureFullDataModel.setScreen("ok");
                } else {
                    mShowPictureFullDataModel.setScreen("cancel");
                }
                mConnectManager.sendMessageToServer(gson.toJson(mShowPictureFullDataModel, DataModel.class));
            }
        }

    }


    public static final String NOMEDIA = ".nomedia";

    /**
     * 创建文件夹（带nomedia）
     *
     * @param dirPath
     */
    public static void mkdirs(String dirPath) {
        try {
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filePath = dirPath.endsWith(File.separator) ? (dirPath + NOMEDIA) : (dirPath + File.separator + NOMEDIA);
            File f = new File(filePath);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字符串保存到文件
     *
     * @param path
     */
    public static boolean saveDatabjectToPath(String path, String data) {
        Log.v("hqb", "hqb__path = " + path);
//		outPutMessage(data);
        if (TextUtils.isEmpty(path) || data == null) {
            return false;
        }
        File fp = new File(path);
        if (!fp.getParentFile().exists()) {
            fp.getParentFile().mkdirs();
        }
        mkdirs(fp.getParent());
        try {
            FileOutputStream out = new FileOutputStream(path);
            // 将json数据加密之后存入缓存
            byte[] source = null;
            source = data.getBytes("utf-8");
            if (source != null) {
                out.write(source, 0, source.length);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            String pkName = context.getPackageName();
            versionName = "版本：" + context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;

// 			int versionCode = this.getPackageManager()
// 					.getPackageInfo(pkName, 0).versionCode;
// 			return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return versionName;
    }


}

