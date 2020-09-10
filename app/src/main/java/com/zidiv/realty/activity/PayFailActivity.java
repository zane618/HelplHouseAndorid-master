package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.R;

public class PayFailActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView iv_back;
    private Button btn_back;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pay_fail);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("支付失败");
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);
        btn_back = (Button) findViewById(R.id.btn_back);
    }

    @Override
    public void initLinstener() {
        iv_back.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        exitActivityAnimation();
    }
}
