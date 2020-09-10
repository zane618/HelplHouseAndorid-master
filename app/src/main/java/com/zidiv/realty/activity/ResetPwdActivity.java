package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
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
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

/**
 * 重设密码页面
 * Created by Administrator on 2016/3/16.
 */
public class ResetPwdActivity extends BaseActivity{
    private Context context;
    private ImageView iv_back;
    private TextView tv_title;
    private EditText edt_pwd1,edt_pwd2;
    private Button btn_setPwd;
    private String phone,codes;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_reset_pwd);
    }

    @Override
    public void getIntentData() {
        phone = getIntent().getStringExtra("phone");
        codes = getIntent().getStringExtra("key");

    }

    @Override
    public void initView() {
        iv_back = (ImageView)findViewById(R.id.back_image);
        tv_title = (TextView)findViewById(R.id.title_text);
        tv_title.setText("重设密码");
        edt_pwd1 = (EditText)findViewById(R.id.edt_pwd1);
        edt_pwd2 = (EditText)findViewById(R.id.edt_pwd2);
        btn_setPwd = (Button)findViewById(R.id.btn_setpwd);
        btn_setPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edt_pwd1.getText().toString().trim()) || TextUtils.isEmpty(edt_pwd2.getText().toString())) {
                    T.showShort(context,"信息不能为空");
                    return;
                }
                if(!edt_pwd1.getText().toString().trim().equals(edt_pwd2.getText().toString().trim())) {
                    T.showShort(context,"两次密码不一致");
                    return;
                }
                //发送网络请求
                String url = HttpUrls.GETBACKPWD_URL;
                RequestParams params = new RequestParams();
                params.addBodyParameter("phone",phone);
                params.addBodyParameter("loginpwd",edt_pwd1.getText().toString().trim());
                params.addBodyParameter("key",codes);
                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST,url,params,new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        String json = stringResponseInfo.result;
                        Gson gson = new Gson();
                        MStatus mStatus = gson.fromJson(json,MStatus.class);
                        if(mStatus.getStatus().equals("1")) {
                            SPUtils.put("SPUserInfo",context,"login_pwd",edt_pwd1.getText().toString().trim());
                            startActivity(new Intent(context,LoginActivity.class));
                        } else {
                            T.showShort(context,"账号或者密码不正确");
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        T.showShort(context, "网络异常,请检查网络设置");
                    }
                });
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivityAnimation();
            }
        });
    }

    @Override
    public void initLinstener() {

    }

    @Override
    public void initData() {
        context = this;
    }
}
