package com.dinghmcn.android.wificonnectclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * 全屏显示纯色判断屏幕是否有坏点
 *
 * @author dinghmcn
date 2018 /4/20 10:47
 */
public class ShowPictureFullActivity extends AppCompatActivity {
  private static final String TAG = "ShowPictureFullActivity";

  private ImageView mImageView;
  private int mResult = RESULT_OK;
  int mTimeOut = 3 * 1000;
  int mScreenOperation=0;


  /**
   * On create.
   *
   * @param savedInstanceState the saved instance state
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);

    setContentView(R.layout.activity_full_show_picture);

    mImageView = findViewById(R.id.imageView);



//    mTimeOut = getIntent().getIntExtra("timeout", mTimeOut);
//    Log.v("hqb", "hqb__ShowPictureFullActivity__mTimeOut = " + mTimeOut);
  }

  /**
   * On resume.
   */
  @Override
  protected void onResume() {
    super.onResume();
    // 获取要显示的颜色
    int resId = getIntent().getIntExtra("res_id", -1);
    mScreenOperation = getIntent().getIntExtra("mScreenOperation", mScreenOperation);//携带的是否切屏开关的
    Log.v("hqb", "hqb__ShowPictureFullActivity__mScreenOperation = " + mScreenOperation  +"hqb__ShowPictureFullActivity__reid"+resId);

    if (resId > 0) {
      mImageView.setImageResource(resId);
      setResult(mResult);
      EventBus.getDefault().post(1010);
      // 5s 后自动退出
//      new Handler().postDelayed(() -> finish(), mTimeOut);
    } else {
      mResult = RESULT_CANCELED;
      finish();
    }
  }


  /**
   * On pause.
   */
  @Override
  protected void onPause() {
    super.onPause();
    // 暂停则退出
    finish();
  }

  /**
   * On back pressed.
   */
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  @Override
  public void finish() {
    setResult(mResult);
    super.finish();
  }
}
