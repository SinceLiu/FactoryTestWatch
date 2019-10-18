package com.readboy.factorytest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.readboy.factorytest.model.DataModel;
import com.readboy.factorytest.utils.BatteryChargeUtils;
import com.readboy.factorytest.utils.BluetoothUtils;
import com.readboy.factorytest.utils.CallUtils;
import com.readboy.factorytest.utils.CheckPermissionUtils;
import com.readboy.factorytest.utils.ConnectManagerUtils;
import com.readboy.factorytest.utils.ConnectManagerUtils.EnumCommand;
import com.readboy.factorytest.utils.CustomPopDialog2;
import com.readboy.factorytest.utils.GpsUtils;
import com.readboy.factorytest.utils.HeadsetLoopbackUtils;
import com.readboy.factorytest.utils.MyActivityManager;
import com.readboy.factorytest.utils.SensorManagerUtils;
import com.readboy.factorytest.utils.StorageUtils;
import com.readboy.factorytest.utils.USBDiskReceiver;
import com.readboy.factorytest.utils.USBDiskUtils;
import com.readboy.factorytest.utils.VersionUtils;
import com.readboy.factorytest.utils.WifiManagerUtils;
import com.readboy.factorytest.utils.ZXingUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 主界面窗口
 *
 * @author dinghmcn
 * @date 2018 /4/20 10:47
 */
public class MainActivity extends Activity {
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
     * The constant NOMEDIA.
     */
    public static final String NOMEDIA = ".nomedia";
    /**
     * 是否输出日志
     */
    private static final boolean mPrintLog = true;
    private static final String GET = "get";
    private static final String Start = "start";
    private static final String END = "end";
    private final static int MY_PERMISSION_REQUEST_CONSTANT = 1001;
    /**
     * The constant Instance.
     */
    public static MainActivity Instance;
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
    private static int mOK = 2;
    private static String cameraInfod = "";
    private CustomPopDialog2 dialog;
    private boolean isDialogShown = false;
    private static boolean isScreen;
    /**
     * 日志标志.
     */
    protected final String TAG = getClass().getSimpleName();
    /**
     * The Msg testactivity.
     */
    final int MSG_TESTACTIVITY = 0x1000;
    /**
     * 是否在测试相机
     */
    public boolean isCameraOpen = false;
    /**
     * The Dir.
     */
    String dir = "cache";
    private String originalSSID;
    private String originalPassword;
    private int mResult = RESULT_OK;
    private ScrollView mScrollView;
    private TextView mTextView, mSeq, mViewTestResult;
    private SpannableStringBuilder mConnectMessage;
    private SpannableStringBuilder mTestResult;
    private Handler mMainHandler;
    @Nullable
    private ConnectManagerUtils mConnectManager = null;
    @Nullable
    private WifiManagerUtils mWifiManagerUtils = null;
    private GpsUtils mGpsUtils = null;
    private CallUtils mCallUtils = null;
    private BatteryChargeUtils mBatteryChargeUtils;
    private BluetoothUtils mBluetoothUtils;
    private VersionUtils mVersionUtils;
    private StorageUtils mStorageUtils;
    private HeadsetLoopbackUtils mHeadsetLoopbackUtils;
    private Gson gson = new Gson();
    private DataModel mDataModel;
    private DataModel mShowPictureFullDataModel;
    private DataModel mDialDataModel;
    private DataModel mKeyDataModel;
    private DataModel mRecordDataModel;
    private USBDiskReceiver mUsbDiskReceiver;
    private Vibrator mVibrator;
    //过滤掉长按的情况
    private int mTouchRepeat = 0;
    //是否出现双指按下的情况
    private boolean mPoint2Down = false;

    private static long mTimeOut;

    /**
     * 创建文件夹（带nomedia）
     *
     * @param dirPath the dir path
     */
    public static void mkdirs(String dirPath) {
        try {
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filePath = dirPath.endsWith(File.separator) ? (dirPath + NOMEDIA)
                    : (dirPath + File.separator + NOMEDIA);
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
     * @param path the path
     * @param data the data
     * @return the boolean
     */
    public static boolean saveDatabjectToPath(String path, String data) {
        Log.v("hqb", "hqb__path = " + path);
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

    /**
     * Gets version name.
     *
     * @param context the context
     * @return the version name
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            String pkName = context.getPackageName();
            versionName = "版本：" + context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * On get message.
     *
     * @param flag the flag
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(String flag) {
        switch (flag) {
            case "show_picture":
                sendservermessage(true, mResult);
                break;
            case "bluetooth":
                //sendBluetooth();
                break;
            default:
        }
    }

    //发送蓝牙指令给服务器
    private void sendBluetooth() {
        JSONArray jsonArray = new JSONArray();
        Set<BluetoothDevice> bluetoothDevices = mBluetoothUtils.getBluetoothDevices();
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            jsonArray.put(bluetoothDevice.getName() + "," + bluetoothDevice.getAddress());
        }
        mDataModel.setBluetooth(jsonArray.toString()
                .replace("\\", "")
                .replace("\\", "")
                .replace("[", "")
                .replace("]", ""));
        mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Instance = this;
        mScrollView = findViewById(R.id.message_scrollview);
        mTextView = findViewById(R.id.connect_message);
        mViewTestResult = findViewById(R.id.test_result);
        mSeq = findViewById(R.id.seq);
        mUsbDiskReceiver = new USBDiskReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        registerReceiver(mUsbDiskReceiver, filter);

        //获取到已经连接的wifi 帐号和密码
        if (getIntent() != null) {
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

        mWifiManagerUtils = new WifiManagerUtils(this);
        mGpsUtils = new GpsUtils(this);
        mCallUtils = new CallUtils(this);
        mBatteryChargeUtils = new BatteryChargeUtils(this);
        mBluetoothUtils = new BluetoothUtils(this);
        mHeadsetLoopbackUtils = new HeadsetLoopbackUtils(this);
        outPutMessage(getVersionName(this));

        mVersionUtils = new VersionUtils();
        mStorageUtils = new StorageUtils(this);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 获取服务器信息
        String ip /*= loadFromSDFile("socketIP.txt")*/;

        ip = "192.168.1.253";
        //MTK的才支持5G wifi（A6/A3/W7)
        //5G wifi
        originalSSID = "factory-fqc-test1";
        originalPassword = "readboy@fqc1";
//        //2.4G wifi
//        originalSSID = "readboy-factory-fqc-test1";
//        originalPassword = "readboy@fqc1";

        ip = "192.168.0.127";
        originalSSID = "SoftReadboy2";
        originalPassword = "kfbrjb2@readboy.com";

        if (null == ip || ip.trim().isEmpty()) {
            prepareConnectServer("{\"IP\":\"192.168.1.6\",\"Port\":12345,\"SSID\":\""
                    + originalSSID + "\"," + "\"PWD\":\"" + originalPassword + "\",\"Station\":1}");
        } else {
            prepareConnectServer("{\"IP\":" + ip + ",\"Port\":12345,\"SSID\":\""
                    + originalSSID + "\"," + "\"PWD\":\"" + originalPassword + "\",\"Station\":1}");
        }
    }

    /**
     * 展示序列号二维码
     */
    private void showCodeScan() {
        if (isDialogShown) {
            return;
        }
        // Toast.makeText(this, getDeviceSerial(), Toast.LENGTH_SHORT).show();
        // 这里是获取图片Bitmap，也可以传入其他参数到Dialog中
        Bitmap bitmap = ZXingUtils.createQRImage(VersionUtils.getSerialNumber(), 500, 500);
        CustomPopDialog2.Builder dialogBuild = new CustomPopDialog2.Builder(this);
        dialogBuild.setImage(bitmap);
        // 点击外部区域关闭
        dialog = dialogBuild.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        isDialogShown = true;
    }

    private void closedialog() {
        dialog.dismiss();
        isDialogShown = false;
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
        Log.v("hqb", "onActivityResult__requestCode == REQUEST_CAMERA_CODE = "
                + (requestCode == REQUEST_CAMERA_CODE)
                + "__resultCode == RESULT_OK = " + (resultCode == RESULT_OK) + "__data = " + data);
        if (requestCode == REQUEST_CAMERA_CODE) {
            isCameraOpen = false;
            Log.w(TAG, "onActivityResult: " + "11 " + (null == data));
            if (resultCode == RESULT_OK && null != data) {
                Uri pictureUri = data.getData();
                assert pictureUri != null;
                Log.w(TAG, "onActivityResult: " + pictureUri.getPath());
                if (ConnectManagerUtils.mConnected) {
                    assert mConnectManager != null;
                    File file = new File(pictureUri.getPath());
                    mDataModel.setCamera("ok");
                    mConnectManager.sendFileToServer(file, gson.toJson(mDataModel, DataModel.class));
                    mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                    outPutLog(getString(R.string.send_file, pictureUri.toString()));
                }
            } else if (null == data) {
                if (ConnectManagerUtils.mConnected) {
                    assert mConnectManager != null;
                    mDataModel.setCamera("error");
                    mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                }
            } else if (resultCode == RESULT_CANCELED) {
                outPutLog(R.string.command_file_error);
                mConnectManager.sendMessageToServerNotJson("createFile__failed");
                Log.e(TAG, "return createFile__failed.");
            } else {
                outPutLog(R.string.execute_command_error);
                mConnectManager.sendMessageToServerNotJson("execute command error");
                Log.e(TAG, "return result failed.");
            }
        }
    }

    /**
     * 打印消息
     *
     * @param message the message
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
     * @param message the message
     */
    public void outPutLog(String message) {
        if (mPrintLog) {
            outPutMessage(message);
        }
    }

    private void outPutLog(int idRes) {
        outPutMessage(getString(idRes));
    }
    /*   */

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
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ff00")),
                            0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),
                            0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                spannableString.setSpan(new RelativeSizeSpan(4.5f), 0,
                        spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mViewTestResult.setText((spannableString));
            }
        }
    }

    /**
     * Show message.
     *
     * @param msg the msg
     */
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

    /**
     * 记录触摸信息
     *
     * @param ev the ev
     * @return the boolean
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
                        Log.e("CHEN", "pointerIndex=" + i + ",pointerId=" + ev.getPointerId(i));
                        Log.e("CHEN", "第" + (i + 1) + "个手指的X坐标=" + ev.getX(i)
                                + ",Y坐标=" + ev.getY(i));
                        if (i == 1) {
                            if (mTouchMoveJsonObject2 != null) {
                                Log.d(TAG, "move");
                                //打印手指的滑动坐标
                                mTouchMoveJsonObject2.put("(" + ev.getX(1) + ","
                                        + ev.getY(1) + ")");
                            }
                        }
                    }
                    if (mTouchMoveJsonObject != null) {
                        Log.d(TAG, "move3");
                        //打印手指的滑动坐标
                        mTouchMoveJsonObject.put("(" + ev.getRawX() + "," + ev.getRawY() + ")");
                    }

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
                    Log.e("CHEN", "第" + (actionIndex + 1) + "个手指按下");
                    try {
                        if (mTouchJsonObject != null)
                            mTouchJsonObject.put("TWODOWN", "(" + ev.getX(1) + ","
                                    + ev.getY(1) + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;


                case MotionEvent.ACTION_POINTER_UP:
                    Log.d(TAG, "move2");
                    Log.e("CHEN", "第" + (actionIndex + 1) + "个手指抬起");
                    //do something here
                    try {
                        assert mTouchJsonObject != null;
                        mTouchJsonObject.put("TWOMOVE", mTouchMoveJsonObject2);
                        mTouchJsonObject.put("TWOUP", "(" + ev.getX(1) + ","
                                + ev.getY(1) + ")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mTouchMoveJsonObject2 = null;
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
                Log.e("CHEN", "Main:ssid:" + wifiSsid);
                if (mConnectManager != null && mWifiManagerUtils != null) {
                    mConnectManager.connectServer(mWifiManagerUtils, wifiSsid, wifiPassword);
                    outPutMessage(getString(R.string.connect_loading, inetSocketAddress.toString()));
                }
            } else {
                outPutMessage(serverIp + ":" + serverPort + getString(R.string.ip_or_port_illegal));
            }
        } else {
            outPutMessage(connectInfo + " " + getString(R.string.connect_info_error));
        }
    }

    private void prepareConnectClient(int port) {
        Log.w(TAG, "prepareConnectClient: " + port);
        if (port > 0) {
            mConnectManager = ConnectManagerUtils.newInstance(mMainHandler, port);
            mConnectManager.connectClient();
            outPutMessage(getString(R.string.connect_loading, port + ""));

        } else {
            outPutMessage(port + " " + getString(R.string.connect_info_error));
        }
    }

    /**
     * 获取电池信息
     */
    private void getBatteryInfo() {
        Log.w(TAG, "getBatteryInfo: " + mBatteryChargeUtils.getBatteryStatus() + "---"
                + mBatteryChargeUtils.getQuality() + "---" + mBatteryChargeUtils.getCurrentChargingCurrent()
                + "---" + mBatteryChargeUtils.getmLevel() + "---" + mBatteryChargeUtils.getPlugType()
                + mBatteryChargeUtils.getStatus() + "---" + mBatteryChargeUtils.getTemperature() + "----"
                + mBatteryChargeUtils.getVoltage() + "---" + mBatteryChargeUtils.isChargingPass());
    }

    /**
     * Finish.
     */
    @Override
    public void finish() {
        if (null != mHeadsetLoopbackUtils) {
            mHeadsetLoopbackUtils.stop();
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
            if (null != mGpsUtils) {
                mGpsUtils.release();
            }
            if (null != mConnectManager) {
                mConnectManager.disconnectServer();
                mConnectManager = null;
            }
            if (null != mBatteryChargeUtils) {
                mBatteryChargeUtils.unregisterReceiver();
            }
            if (null != mBluetoothUtils) {
                mBluetoothUtils.exit();
            }
            if (null != mHeadsetLoopbackUtils) {
                mHeadsetLoopbackUtils.stop();
            }
            if (null != mUsbDiskReceiver) {
                unregisterReceiver(mUsbDiskReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
        Log.e(TAG, "onDestroy");
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

    private void setTimeOut(int timeOut) {
        mTimeOut = timeOut * 1000;
    }

    /**
     * Gets time out.
     *
     * @return the time out
     */
    public static long getTimeOut() {
        return mTimeOut;
    }

    //返回切黑白屏的指令给服务器
    private void sendservermessage(boolean requestCode, int resultCode) {

        if (requestCode == true) {
            Log.v("hqb", "mShowPictureFullDataModel = " + mShowPictureFullDataModel);
            if (mShowPictureFullDataModel != null) {
                if (resultCode == RESULT_OK) {
                    mShowPictureFullDataModel.setScreen("ok");
                } else {
                    mShowPictureFullDataModel.setScreen("cancel");
                }
                mConnectManager
                        .sendMessageToServer(gson.toJson(mShowPictureFullDataModel, DataModel.class));
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private class MainHandel extends Handler {
        /**
         * The M activity weak reference.
         */
        WeakReference<MainActivity> mActivityWeakReference;
        private String mLastPartInfo = "";

        /**
         * Instantiates a new Main handel.
         *
         * @param activity the activity
         */
        MainHandel(MainActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        /**
         * Handle message.
         *
         * @param msg the msg
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            final MainActivity mainActivity = mActivityWeakReference.get();

            if (msg.what == CMD_CODE) {
                mDataModel = gson.fromJson((String) msg.obj, DataModel.class);
                if (mDataModel == null) {
                    return;
                }

                setTimeOut(mDataModel.getTimeout());

                // 串号
                mDataModel.setSn(VersionUtils.getSerialNumber());

                // 关机
                if (GET.equals(mDataModel.getShutdown())) {
                    shutdownSystem();
                }

                // 消息
                String message = mDataModel.getShowMessage();
                if (null != message && !message.isEmpty()) {
                    outPutMessage(message);
                }
                if (GET.equals(mDataModel.getDisk())
                        || GET.equals(mDataModel.getSd())
                        || GET.equals(mDataModel.getVersion())
                        || GET.equals(mDataModel.getBattery())
                        || GET.equals(mDataModel.getImei())
                        || GET.equals(mDataModel.getCpu())) {

                    // 存储
                    mDataModel.setDisk(mStorageUtils.getRomAvailableStorage() + "，"
                            + mStorageUtils.getRomTotalStorage());

                    // sd卡
                    mDataModel.setSd(mStorageUtils.getSdAvailableStorage() + "，"
                            + mStorageUtils.getSdTotalStorage());

                    // 版本号
                    mDataModel.setVersion("SW:" + mVersionUtils.getSoftwareVersion()
                            + ", HW:" + mVersionUtils.getHardwareVersion());

                    // IMEI
                    mDataModel.setImei(mCallUtils.getImei());

                    // CPU
                    mDataModel.setCpu(mBatteryChargeUtils.getCpuTemperature() + "C");

                    // 电池
                    mDataModel.setBattery("ADC:" + mBatteryChargeUtils.getAdc() + ", "
                            + "IsCharging:" + mBatteryChargeUtils.isChargingPass() + ", "
                            + mBatteryChargeUtils.getmLevel() + "%, "
                            + (mBatteryChargeUtils.getTemperature() / 10.0f) + "C, "
                            + mBatteryChargeUtils.getCurrentChargingCurrent() + "mA, "
                            + (mBatteryChargeUtils.getVoltage() / 1000.0f) + "V");

                    mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                }


                if ("close".equals(mDataModel.getWifi())) {
                    outPutMessage("Close Wifi");
                    mWifiManagerUtils.closeWifi();
                }

                if (GET.equals(mDataModel.getOtg())) {
                    // otg
                    USBDiskUtils usbDiskUtils = new USBDiskUtils(MainActivity.this);
                    usbDiskUtils.startTest();
                    int time = mDataModel.getTimeout() * 1000;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String result = usbDiskUtils.mIsTestSuccess ? "ok" : "error";
                            mDataModel.setOtg(result);
                            mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                        }
                    }, time);
                }

                // 传感器
                if (GET.equals(mDataModel.getAccelerometer()) || GET.equals(mDataModel.getLight())
                        || GET.equals(mDataModel.getProximity()) || GET.equals(mDataModel.getMagnetometer())
                        || GET.equals(mDataModel.getGyroscope())) {
                    SensorManagerUtils sensorManagerUtils = new SensorManagerUtils(mainActivity);
                    postDelayed(() -> {
                        // 加速度传感器
                        String accelerometer = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_ACCELEROMETER + "", "error");
                        mDataModel.setAccelerometer(null != accelerometer ? accelerometer : "error");
                        // 光感
                        String light = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_LIGHT + "", "error");
                        mDataModel.setLight(light);
                        // 距离传感器
                        String proximity = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_PROXIMITY + "", "error");
                        mDataModel.setProximity(proximity);
                        // 磁感应器
                        String magnetometer = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_MAGNETIC_FIELD + "", "error");
                        mDataModel.setMagnetometer(magnetometer);
                        // 陀螺仪
                        String gyroscope = sensorManagerUtils.getJSONObject()
                                .optString(Sensor.TYPE_GYROSCOPE + "", "error");
                        mDataModel.setGyroscope(gyroscope);

                        Log.d("dhm_sensor", gson.toJson(mDataModel, DataModel.class));
                        mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                        sensorManagerUtils.unregisterListeners();
                    }, 1000);
                }

                // 相机
                String cameraInfo = mDataModel.getCamera();
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

                if (GET.equals(mDataModel.getBluetooth())) {
                    sendBluetooth();
                }

                // 振动
                if ("1".equals(mDataModel.getVibrator())) {
                    if (null != mVibrator && mVibrator.hasVibrator()) {
                        mVibrator.vibrate(3600 * 1000);
                    }
                } else if ("-1".equals(mDataModel.getVibrator())) {
                    if (null != mVibrator && mVibrator.hasVibrator()) {
                        mVibrator.cancel();
                    }
                }

                // 拨号
                if ("1".equals(mDataModel.getDial())) {
                    mDialDataModel = mDataModel;
                    Intent intent1 = new Intent("android.intent.action.CALL_PRIVILEGED")
                            .setData(Uri.parse("tel:112"))
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    } else {
                        try {
                            startActivity(intent1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if ("-1".equals(mDataModel.getDial())) {
                    boolean isEndCall = mCallUtils.endCall();
                    if (isEndCall) {
                        mDataModel.setDial("ok");
                    } else {
                        mDataModel.setDial("error");
                    }
                    mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                }

                // gps
                if (GET.equals(mDataModel.getGps())) {
                    assert mWifiManagerUtils != null;
                    mDataModel.setGps(GpsUtils.getCount() + "");
                    mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                }

                // wifi
                if (GET.equals(mDataModel.getWifi())) {
                    assert mWifiManagerUtils != null;
                    postDelayed(() -> {
                        List<ScanResult> rssis = mWifiManagerUtils.getWifis();
                        JSONArray jsonArray = new JSONArray();
                        for (ScanResult wifi : rssis) {
                            jsonArray.put(wifi.SSID + "," + wifi.level);
                        }
                        mDataModel.setWifi(jsonArray.toString().replace("\"", "")
                                .replace("[", "").replace("]", ""));
                        mConnectManager.sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                    }, 1000);
                }

                // 录音
                if (GET.equals(mDataModel.getRecord())) {
                    int time = mDataModel.getTimeout() * 1000;
                    mRecordDataModel = mDataModel;
                    mHeadsetLoopbackUtils.start();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mRecordDataModel != null) {
                                mRecordDataModel.setRecord(mHeadsetLoopbackUtils.mIsStartRecordSuccess ? "ok" : "error");
                                mConnectManager.sendMessageToServer(gson.toJson(mRecordDataModel, DataModel.class));
                                mHeadsetLoopbackUtils.stop();
                            }
                        }
                    }, time);
                }

                // 按键
                if (GET.equals(mDataModel.getKey())) {
                    mKeyDataModel = mDataModel;
                    int time = mDataModel.getTimeout() * 1000;
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
                if (mDataModel.getShowMessage() != null) {
                    String ret = mDataModel.getShowMessage();
                    if (ret.contains("PASS") && !ret.contains("FAIL")) {
                        outPutTestResult("PASS");
                    } else {
                        outPutTestResult("FAIL");
                    }
                }

                // 触摸
                if (Start.equals(mDataModel.getTouch())) {
                    String touch = mDataModel.getTouch();
                    Log.e("TAG", "touchsuccess" + touch);
                    isCatchTouch = true;
                    mDataModel.setTouch("ok");
                    mainActivity.mConnectManager
                            .sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                }
                // 触摸
                if (END.equals(mDataModel.getTouch())) {
                    String touch = mDataModel.getTouch();
                    Log.e("TAG", "touchsuccess" + touch);
                    isCatchTouch = false;

                    if (mTouchJsonArray != null) {
                        mainActivity.mConnectManager.sendMessageToServer(mTouchJsonArray.toString());
                    } else {
                        mDataModel.setTouch("ok");
                        mainActivity.mConnectManager
                                .sendMessageToServer(gson.toJson(mDataModel, DataModel.class));
                    }
                    mTouchJsonArray = null;
                }


                // 屏幕
                if (mDataModel.getScreen() != null) {
                    String imageName = mDataModel.getScreen();
                    mShowPictureFullDataModel = mDataModel;
                    closedialog();//关闭二维码
                    int resId = mainActivity.getResources()
                            .getIdentifier(imageName, "drawable",
                                    mainActivity.getPackageName());

                    if (resId > 0) {    //背景id
                        if (mDataModel.getScreenoperation() == 0) {   //开
                            Log.v("hqb", "dataModel.getScreenopeneration() = "
                                    + mDataModel.getScreenoperation());
                            final Intent intent30 = new Intent(mainActivity,
                                    ShowPictureFullActivity.class).putExtra("res_id", resId)
                                    .putExtra("getScreenopeneration", mDataModel.getScreenoperation());
                            mainActivity.startActivity(intent30);
                        } else if (mDataModel.getScreenoperation() == 1) {     //关掉
                            MyActivityManager.getInstance().clearAllActivity();
                            sendservermessage(true, mResult);
                        }

                        mainActivity.outPutLog(mainActivity.getString(R.string.show_file, imageName));
                        Log.d(mainActivity.TAG, mainActivity.getString(R.string.show_file, imageName));
                    } else {    //非法背景id
                        mainActivity.outPutLog(mainActivity.getString(R.string.file_not_exist, imageName));
                        Log.d(mainActivity.TAG, mainActivity.getString(R.string.file_not_exist, imageName));
                    }
                }
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

    /**
     * The type My runnable.
     *
     * @author dinghmcn
     */
    public class MyRunnable implements Runnable {
        private String msg;

        /**
         * Instantiates a new My runnable.
         *
         * @param msg the msg
         */
        public MyRunnable(String msg) {
            this.msg = msg;
        }

        /**
         * Run.
         */
        @Override
        public void run() {
            outPutMessage(msg);
        }
    }
}

