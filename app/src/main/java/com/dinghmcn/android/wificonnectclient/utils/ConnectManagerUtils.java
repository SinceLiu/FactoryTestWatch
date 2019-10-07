package com.dinghmcn.android.wificonnectclient.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.dinghmcn.android.wificonnectclient.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import static com.dinghmcn.android.wificonnectclient.MainActivity.CMD_CODE;
import static com.dinghmcn.android.wificonnectclient.MainActivity.getDeviceSerial;

/**
 * 管理与服务器的交互
 *
 * @author dinghmcn
 * @date 2018 /4/20 10:47
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class ConnectManagerUtils {
    /**
     * 连接失败
     */
    public static final int CONNECT_FAILED = -1;
    /**
     * 连接关闭
     */
    public static final int CONNECT_CLOSED = 0;
    /**
     * 连接成功
     */
    public static final int CONNECT_SUCCESS = 1;
    /**
     * 接收信息
     */
    public static final int COMMAND_RECEIVE = 2;
    /**
     * 发送信息
     */
    public static final int COMMAND_SEND = 3;
    /**
     * 命令错误
     */
    public static final int COMMAND_ERROR = 4;

	public static final int COMMAND_SEQ = 1000;

	//心跳指令
    public static final int COMMAND_ALIVE = 1001;

    private static final String TAG = ConnectManagerUtils.class.getSimpleName();
    /**
     * 是否连接
     */
    public static boolean mConnected = false;
    @Nullable
    private static ConnectManagerUtils instance = null;
    @Nullable
    private Handler mMainHandler;

    private InetSocketAddress mInetSocketAddress;
    private Socket mSocket;
    @Nullable
    private ExecutorService mThreadPool;

    private InputStream is;
    private OutputStream out;

    private ConnectManagerUtils(@Nullable Handler handler, InetSocketAddress inetSocketAddress) {
        mMainHandler = handler;
        mInetSocketAddress = inetSocketAddress;
        // 初始化线程池
        mThreadPool = new ThreadPoolExecutor(5, 9, 5, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), r -> new Thread(r, "ConnectManagerUtils"));
    }

    /**
     * New instance connect manager.
     *
     * @param handler           the handler
     * @param inetSocketAddress the inet socket address
     * @return the connect manager
     */
    public static ConnectManagerUtils newInstance(Handler handler,
                                                  InetSocketAddress inetSocketAddress) {
        if (null == instance) {
            instance = new ConnectManagerUtils(handler, inetSocketAddress);
        }
        return instance;
    }


    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法.
     *
     * @param ipAddress ip address
     * @return boolean boolean
     */
    public static boolean isIp(@Nullable String ipAddress) {
        if (null != ipAddress && !ipAddress.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            // 返回判断信息
            // 返回判断信息
            return ipAddress.matches(regex);
        }
        return false;
    }

    /**
     * 从服务器接收信息
     */
    private void receiveMessageFromServer() {
        Log.d(TAG, "Receive message.");
        assert mThreadPool != null;
        mThreadPool.execute(() -> {
            sendMessage(EnumCommand.COMMAND.ordinal(), COMMAND_RECEIVE);
            int commandNullCount = 0;
            // 持续接收直到连接关闭
            while (mConnected && !mSocket.isClosed() && mSocket.isConnected()
                    && !mSocket.isInputShutdown()) {
                Log.d(TAG, "Start receive message.");
				String command = null;
                try {
                    is = mSocket.getInputStream();
                    byte[] tempBuffer = new byte[2048];
                    int numReadedBytes = is.read(tempBuffer, 0, tempBuffer.length);
                    if (numReadedBytes > 0) {
                        command = new String(tempBuffer, 0, numReadedBytes);
                    } else {
                        ++commandNullCount;
                        Log.d(TAG, "Command is null:" + commandNullCount);
                        if (commandNullCount > 3) {
                            sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_CLOSED);
                            return;
                        } else {
                            continue;
                        }
                    }
                    Log.d(TAG, "hqb Command:" + command);
                    // 处理接收到的消息
                    if (!command.isEmpty()) {
						if(command.contains("Seq=")) {
                            sendMessage(EnumCommand.SEQ.ordinal(), COMMAND_SEQ, command);
                        }
                        else if (command.contains("Alive"))
                        {
                            sendMessage(EnumCommand.Alive.ordinal(), COMMAND_ALIVE, command);
						}else {
							parsingCommand(command);
							sendMessage(EnumCommand.COMMAND.ordinal(), COMMAND_SEND, command);
						}
                    } else {
                        sendMessage(EnumCommand.COMMAND.ordinal(), COMMAND_ERROR);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "hqb Receive message error.");
//                    try {
//						if(!TextUtils.isEmpty(command)){
//							if(command.contains("Seq=")){
//								sendMessage(EnumCommand.SEQ.ordinal(), COMMAND_SEQ, command);
//							}
//						}
//					}catch (Exception e1){
//                    	e1.printStackTrace();
//					}
                    e.printStackTrace();
                }
            }
            if (null != mMainHandler) {
                sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_CLOSED);
            }

        });
    }

    /**
     * 连接服务器
     *
     * @param wifiManagerUtils the wifi manager utils
     * @param wifiSsid         the wifiSsid
     * @param wifiPassWord     the wifi pass word
     */
    public void connectServer(@NonNull final WifiManagerUtils wifiManagerUtils,
                              @NonNull final String wifiSsid,
                              final String wifiPassWord) {

        Log.d(TAG, "Connect server.");
        assert null != mThreadPool;
        mThreadPool.execute(() -> {
            long delayedTime = 8000L;
            long start;
            //新增重试次数
            int retry = 0;
            int maxretry = 3;

//            while (retry < maxretry) {
//                start = System.currentTimeMillis();
//                // 开启wifi
//                while (System.currentTimeMillis() < start + delayedTime ) {
//                    if (wifiManagerUtils.isWifiEnabled()) {
//                        if (!wifiManagerUtils.isWifiConnected(wifiSsid)) {
//                            wifiManagerUtils.connectWifi(wifiSsid, wifiPassWord);
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            break;
//                        }
//                    } else {
//                        wifiManagerUtils.openWifi();
//                        try {
//                            ShowMessage( "Open wifi!");
//                            Thread.sleep(500);
//                            continue;
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                Log.d(TAG, "Connect server1");
//                if (wifiManagerUtils.isWifiConnected(wifiSsid)) {
//                    ShowMessage( "Connect wifi success!");
//                    break;
//                } else {
//                    retry++;
//                    ShowMessage( "Retry connect wifi, times:" + retry);
//                }
//            }
            if (retry == maxretry)
            {
                ShowMessage( "Connect wifi failed,try reopen");
            }


            // 判断是否能连接服务器
            start = System.currentTimeMillis();
            while (System.currentTimeMillis() < start + delayedTime) {
                Process process = null;
                try {
                    process = Runtime.getRuntime()
                            .exec(
                                    "/system/bin/ping -c 1 -w 100 "
                                            + mInetSocketAddress.getHostName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int status;
                try {
                    status = process != null ? process.waitFor() : 0;
                    if (status == 0) {
                        break;
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 连接服务器
            Log.d(TAG, "Connect server2");
            mSocket = new Socket();
            try {
                mSocket.connect(mInetSocketAddress, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, mInetSocketAddress + "Connected :" + mSocket.isConnected());
            // 连接成功开始接收信息
            if (mSocket.isConnected()) {
                mConnected = true;
                receiveMessageFromServer();
                sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_SUCCESS);
            } else {
                sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_FAILED);
            }
            Log.d(TAG, "Connect server3");
        });
    }

    /**
     * 发送文件给服务器
     *
     * @param file    the file uri
     * @param message the message
     */
    public void sendFileToServer(Context context,File file, String message) {
        Log.e("CHEN", "Send File :" + file.getAbsolutePath());
        assert mThreadPool != null;
        mThreadPool.execute(() -> {
            InputStream in = null;
            OutputStream out = null;
            try {
                if (!file.exists()) {
                    Log.w(TAG, "sendFileToServer: null");
                    return;
                }
                // 使用SMB把文件传送到指定位置
//                String remoteUrl = "smb://software:*@192.168.1.240/software_2017/tmp";
				String remoteUrl = "smb://test:123@192.168.1.200/Image/CameraTemp";
                String sn = VersionUtils.getSerialNumber();
                SmbFile  remoteFile = new SmbFile(remoteUrl + "/" + sn + "-" + file.getName());
                in = new BufferedInputStream(new FileInputStream(file));
                out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));


                byte[] buffer = new byte[1024 * 4];
                while ((in.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer);
                }
                out.flush();

//                if (null != message) {
//                    sendMessageToServer(message);
//                }
                Log.e("CHEN", "sendFileToServer: success" );
            } catch (IOException e) {
                //使用Toast来显示异常信息
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(context, "远程共享文件纯文本密码已禁用.", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();//                DisplayToast();
                e.printStackTrace();
                Log.e("CHEN", "sendFileToServer: fail" );

            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != out) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        //收集设备参数信息
//        collectDeviceInfo(mContext);
        //保存日志文件
//        saveCrashInfo2File(ex);
        return true;
    }
    public void DisplayToast(String str)

    {



    }

    /**
     * 关闭连接，并清理
     */
    public void disconnectServer() {
        Log.d(TAG, "Disconnect server.");
        try {
            if (null != is) {
                is.close();
            }
            if (null != out) {
                out.close();
            }
            if (null != mSocket) {
                mSocket.close();
                mConnected = false;
                sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_CLOSED);
            }
            if (null != instance) {
                instance = null;
            }
            if (null != mMainHandler) {
                mMainHandler = null;
            }
            if (null != mThreadPool) {
                mThreadPool.shutdownNow();
                mThreadPool = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给服务器
     *
     * @param message the message
     */
    public void sendMessageToServer(final String message) {
        Log.d(TAG, "Send message :" + message);
        sendMessage(EnumCommand.COMMAND.ordinal(), COMMAND_SEND, message);
        assert mThreadPool != null;
        mThreadPool.execute(() -> {
            try {
                Log.d(TAG, "Socket status:" + !mConnected + mSocket.isClosed() + !mSocket.isConnected()
                        + mSocket.isOutputShutdown());
                // 判断连接是否正常
                if (!mConnected || mSocket.isClosed() || !mSocket.isConnected()
                        || mSocket.isOutputShutdown()) {
                    sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_CLOSED);
                    return;
                }
                out = mSocket.getOutputStream();
                byte[] bytes = message.replace("\\", "").getBytes(StandardCharsets.UTF_8);

                byte[] buffer = new byte[bytes.length + 1];
                buffer[0] = 0;
                System.arraycopy(bytes, 0, buffer, 1, buffer.length - 1);
                out.write(buffer);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

	/**
	 * 发送消息给服务器
	 *
	 * @param message the message
	 */
	public void sendMessageToServerNotJson(final String message) {
		Log.d(TAG, "Send error message :" + message);
		assert mThreadPool != null;
		mThreadPool.execute(() -> {
			try {
				Log.d(TAG, "Socket status:" + !mConnected + mSocket.isClosed() + !mSocket.isConnected()
						+ mSocket.isOutputShutdown());
				// 判断连接是否正常
				if (!mConnected || mSocket.isClosed() || !mSocket.isConnected()
						|| mSocket.isOutputShutdown()) {
					sendMessage(EnumCommand.CONNECT.ordinal(), CONNECT_CLOSED);
					return;
				}
				out = mSocket.getOutputStream();
				byte[] bytes = message.replace("\\", "").getBytes(StandardCharsets.UTF_8);

				byte[] buffer = new byte[bytes.length + 1];
				buffer[0] = 0;
				System.arraycopy(bytes, 0, buffer, 1, buffer.length - 1);
				out.write(buffer);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

    private void sendMessage(int what, int arg1) {
        sendMessage(what, arg1, null);
    }

    /**
     * 处理命令信息
     * @param commands
     */
    private void parsingCommand(String commands) {
        Message message = new Message();
        message.obj = commands;
        message.what = CMD_CODE;
        assert mMainHandler != null;
        mMainHandler.sendMessage(message);
    }

    /**
     * 处理消息
     * @param what
     * @param arg1
     * @param obj
     */
    private void sendMessage(int what, int arg1, Object obj) {
        Message message = Message.obtain();
        message.what = what;
        message.arg1 = arg1;
        message.obj = obj;
        if (mMainHandler != null) {
            mMainHandler.sendMessage(message);
        }
    }

    private void ShowMessage(String msg)
    {
        MainActivity.Instance.ShowMessage(msg);
    }
    /**
     * The enum Enum command.
     */
    public enum EnumCommand {
        /**
         * 连接信息
         */
        CONNECT,
        /**
         * 命令信息
         */
        COMMAND,

		SEQ,

        Alive
    }
}
