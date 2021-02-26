package com.zidiv.realty.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.bean.PushInfoList;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 登录界面
 * Created by Administrator on 2016/3/11.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title;
    private ImageView img_Back;
    private EditText edt_Phone, edt_Pwd;
    private TextView tv_Register, tv_ForgetPwd;
    private CheckBox checkBox;
    private Button btn_Login;
    private Context context;
    private String sign;
    private Gson gson;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        //标题
        tv_title = (TextView) findViewById(R.id.title_text);
        //返回
        img_Back = (ImageView) findViewById(R.id.back_image);
        //手机号
        edt_Phone = (EditText) findViewById(R.id.edt_phonenumber);
        //密码
        edt_Pwd = (EditText) findViewById(R.id.edt_pwd);
        //登录按钮
        btn_Login = (Button) findViewById(R.id.btn_login);
        //注册
        tv_Register = (TextView) findViewById(R.id.register);
        //忘记密码
        tv_ForgetPwd = (TextView) findViewById(R.id.forgetpwd);
        checkBox = findViewById(R.id.chk_agree);
    }

    @Override
    public void initLinstener() {
        tv_Register.setOnClickListener(this);
        tv_ForgetPwd.setOnClickListener(this);
        btn_Login.setOnClickListener(this);
        img_Back.setOnClickListener(this);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void initData() {
        tv_title.setText("登录");
        String str_Temp = "我已阅读,并同意<font color='#09A7F0'>《隐私政策》</font>";
        TextView tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_protocol.setText(Html.fromHtml(str_Temp));
        tv_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentone = new Intent(context, AdDetailActivity.class);
//                intentone.putExtra("title", "用户服务协议");
//                intentone.putExtra("src", "http://www.80mf.com/admin/notlogin.htm");
                intentone.putExtra("title", "隐私政策");
                intentone.putExtra("src", "http://www.80mf.com/admin/snotlogin.htm");
                startActivity(intentone);
            }
        });
        try {
            if (!checkPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                }
            }
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            sign = telephonyManager.getDeviceId();
            if (sign == null) {
                //android.provider.Settings;
                sign = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception ex) {
            sign = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        MApplication.sign = this.sign;
        edt_Phone.setText(MApplication.getMApplication().getLoginName());
        edt_Pwd.setText(MApplication.getMApplication().getUserPwd());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.back_image:
                exitActivityAnimation();
                break;
            //登录
            case R.id.btn_login:
                login();
                break;
            //注册
            case R.id.register:
                Intent intent3 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent3);
                break;
            //忘记密码
            case R.id.forgetpwd:
                Intent intent4 = new Intent(LoginActivity.this, ForgetPwdActivity.class);
                startActivity(intent4);
                break;

        }
    }

    //登录
    private void login() {
        if (!checkBox.isChecked()) {
            T.showShort(context, "请您先阅读用户服务协议并同意协议内容");
            return;
        }
        if (TextUtils.isEmpty(edt_Phone.getText().toString().trim()) || TextUtils.isEmpty(edt_Pwd.getText().toString().trim())) {
            T.showShort(context, "用户名或密码不能为空");
            return;
        }
        String url = HttpUrls.LOGIN_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("loginname", edt_Phone.getText().toString().trim());
        params.addBodyParameter("loginpwd", edt_Pwd.getText().toString().trim());
        params.addBodyParameter("login", "1");
        params.addBodyParameter("sign", sign);

        SqliteLogin.addLoginBean(MApplication.getMApplication(), edt_Phone.getText().toString().trim(), edt_Pwd.getText().toString().trim(), "0");

        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                String json = stringResponseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if ("1345".contains(mMstatus.getStatus())) {
                    SqliteLogin.addLoginBean(MApplication.getMApplication(), edt_Phone.getText().toString().trim(), edt_Pwd.getText().toString().trim(), "0");
                    onLoginProcess(mMstatus);
                    initJPush();
                    //TODO 登录成功后需要做处理
                    MainFragmentActivity.instance.finish();
                    Intent intent = new Intent();
                    intent.setClass(context, MainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    T.showShort(context, mMstatus.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                T.showShort(context, "网络异常，请检查网络");
            }
        });
    }

    public void initJPush() {
        String url = HttpUrls.PUSHUPDATE_URL;
        RequestParams params = new RequestParams();
        String rid = JPushInterface.getRegistrationID(getApplicationContext());
        params.addBodyParameter("registrationid", rid);
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                String json = stringResponseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if (mMstatus.getStatus().equals("1")) {
                    //若登录成功
//                    T.showShort(context, "设置成功");
                    L.e("设置成功");
                } else {
//                    T.showShort(context, mMstatus.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                {
                    L.e("访问网络失败，请检查网络设置");
                }
            }
        });
    }
}
