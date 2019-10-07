package com.dinghmcn.android.wificonnectclient;


public class MyRunnable implements Runnable {
    public static MainActivity Instance;

    private String msg;
    public MyRunnable(String msg)
    {
        this.msg = msg;
    }
    public void run()
    {
        Instance.outPutMessage(msg);
    }
}
