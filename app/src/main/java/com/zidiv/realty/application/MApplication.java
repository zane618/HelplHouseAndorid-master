package com.zidiv.realty.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.R;
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 全局应用程序类，用于保存和调用全局应用配置
 * Created by Administrator on 2016/3/15.
 */
public class MApplication extends Application {
    private static MApplication mApplication;
    private HttpUtils httpUtils;
    private BitmapUtils bitmapUtils;
    public static String sign;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        httpUtils = new HttpUtils();
        httpUtils.configHttpCacheSize(0); //取消缓存
        bitmapUtils = new BitmapUtils(getApplicationContext());
        //设置加载图片失败时显示的图片
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.avatar);
        onSetSign();
    }

    @SuppressLint("MissingPermission")
    public void onSetSign() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            sign = telephonyManager.getDeviceId();
            if (sign == null) {
                //android.provider.Settings;
                sign = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception ex) {
            sign = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    public static MApplication getMApplication() {
        if (mApplication == null) {
            mApplication = new MApplication();
        }
        return mApplication;
    }

    public HttpUtils getHttpUtils() {
        return httpUtils;
    }

    public BitmapUtils getBitmapUtils() {
        return bitmapUtils;
    }


    public boolean checkPermissionGranted(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager pkgMgr = getPackageManager();
            return pkgMgr.checkPermission(permission, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 是否首次打开app
     */
    public boolean isFisrtIntoApp() {
        return (boolean) SPUtils.get("APP", mApplication, "firstInto", true);
    }

    //登录请求时间
    public long getSendTime() {
        Date dateTemp=new Date();
        return (long) SPUtils.get("SPUserInfo", mApplication, "sendTime",  dateTemp.getTime()-10000);
    }

    //判断是否已经登录
    public boolean getIsLoginFlag() {
        return (Boolean) SPUtils.get("SPLogin", mApplication, "isLogin", false);
    }

    //判断是否VIP
    public boolean getIsVIPFlag() {
        return (Boolean) SPUtils.get("SPLogin", mApplication, "isVIP", false);
    }

    //判断是否实名认证
    public boolean getIsTrueNameFlag() {
        return (Boolean) SPUtils.get("SPLogin", mApplication, "isTrueName", false);
    }

    //获取用户头像
    public String getUserAvatar() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "user_avatar", "");
    }

    //获取用户身份证正面
    public String getUserFront() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "user_front", "");
    }

    //获取用户身份证反面
    public String getUserBack() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "user_back", "");
    }

    //获取是否实名认证
    public String getUserPass() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "CardPass", "0");
    }

    //获取用户昵称
    public String getUserName() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "user_name", "");
    }

    //获取用户昵称
    public String getUserNickName() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "true_name", "未登录");
    }

    //获取登录账号
    public String getPhoto() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "phone", "**");
    }

    //获取登录账号
    public String getLoginName() {
        LoginBean bean = SqliteLogin.getLoginBean(getMApplication());
        if (bean == null) {
            return "";
        } else {
            return bean.getLogin_name();
        }
    }

    //获取用户密码
    public String getUserPwd() {
        LoginBean bean = SqliteLogin.getLoginBean(getMApplication());
        if (bean == null) {
            return "";
        } else {
            return bean.getLogin_pwd();
        }
    }

    //获取登录账号
    public String getLoginOut() {
        LoginBean bean = SqliteLogin.getLoginBean(getMApplication());
        if (bean == null) {
            return "";
        } else {
            return bean.getLogin_out();
        }
    }

    //推送标识
    public String getRegistrationID() {
        return (String) SPUtils.get("SPUserInfo", mApplication, "RegistrationID", "");
    }
}
