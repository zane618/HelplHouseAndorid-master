package com.zidiv.realty.activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.EPutil;
import com.zidiv.realty.util.T;

/**
 * 关于我们
 */
public class AboutActivity extends BaseActivity{
    private Context context;
    private ImageView iv_back;
    @Override
    public void setContentView() {
          setContentView(R.layout.activity_about);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        ((TextView)findViewById(R.id.title_text)).setText("关于我们");
        iv_back = (ImageView)findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);


    }
    @Override
    public void initLinstener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivityAnimation();
            }
        });
    }
    @Override
    public void initData() {
    }
}
