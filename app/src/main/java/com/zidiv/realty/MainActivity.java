package com.zidiv.realty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.InstrumentedActivity;



public class MainActivity extends InstrumentedActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = this;
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Intent in = new Intent();
                //判断是否已经登录
                in.setClass(context,MainFragmentActivity.class);
                startActivity(in);
                finish();
            }
        };
        timer.schedule(tt,2000);

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化

        String rid = JPushInterface.getRegistrationID(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
