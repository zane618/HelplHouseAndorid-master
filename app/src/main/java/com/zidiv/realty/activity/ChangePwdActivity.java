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
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

/**
 * Created by Administrator on 2016/3/21.
 */
public class ChangePwdActivity extends BaseActivity{
    private Context context = this;
    private TextView tv_title;
    private ImageView iv_back;
    private EditText edt_oldpwd, edt_newpwd,edt_newpwd2;
    private Button btn_sure;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_changepwd);
    }

    @Override
    public void getIntentData() {
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.title_text);
        iv_back = (ImageView) findViewById(R.id.back_image);
        tv_title.setText("修改密码");
        edt_oldpwd = (EditText) findViewById(R.id.et_oldpwd_activitychangepwd);
        edt_newpwd = (EditText) findViewById(R.id.et_newpwd_activitychangepwd);
        edt_newpwd2 = (EditText) findViewById(R.id.et_confirmpwd_activitychangepwd);
        btn_sure = (Button) findViewById(R.id.btn_confirmchange_activitychangepwd);
    }

    @Override
    public void initLinstener() {
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_oldpwd.getText().toString().trim())) {
                    T.showShort(context,"请输入密码");
                    return;
                }
                if (TextUtils.isEmpty(edt_newpwd.getText().toString().trim())) {
                    T.showShort(context,"请输入密码");
                    return;
                }
                if (TextUtils.isEmpty(edt_newpwd2.getText().toString().trim())) {
                    T.showShort(context,"请输入确认密码");
                    return;
                }
                if(!edt_newpwd.getText().toString().trim().equals(edt_newpwd2.getText().toString().trim())) {
                    T.showShort(context,"两次密码输入不一致");
                }
                String url = HttpUrls.CHANGEPWD_URL;
                RequestParams params = new RequestParams();
                params.addBodyParameter("loginpwd",edt_newpwd.getText().toString().trim());
                params.addBodyParameter("oldpwd",edt_oldpwd.getText().toString().trim());
                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST,url,params,new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        String json = stringResponseInfo.result;
                        Gson gson = new Gson();
                        MStatus mStatus = gson.fromJson(json,MStatus.class);
                        if(mStatus.getStatus().equals("1")) {
                            //修改成功
                            T.showShort(context, "修改密码成功");
                            //修改登录状态
                            SPUtils.clear("SPUserInfo",context);
                            SPUtils.clear("SPLogin",context);
                            MainFragmentActivity.instance.finish();
                            UserInfoActivity.instance.finish();
                            //跳转到登录界面
                            Intent intent = new Intent(context,MainFragmentActivity.class);
                            startActivity(intent);
                            exitActivityAnimation();
                        } else {
                            T.showShort(context,"旧密码不正确");
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        T.showShort(context,"访问网络失败，请检查网络设置");
                    }
                });
            }
        });
        //返回
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
