package com.zidiv.realty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.activity.LoginActivity;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.customview.MDialog;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public abstract class BaseFragment extends Fragment implements MDialog.MDialogDo {

    private MDialog mDialog;
    public FragmentTransaction mFragmentTransaction;
    public android.support.v4.app.FragmentManager fragmentManager;
    public String curFragmentTag = "";

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        ActivityManager.getInstance().startActivity(getActivity());
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        ActivityManager.getInstance().startActivity(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case MyConfig.USERINFOAC_BACK_CODE: //个人资料界面finish
//                if (data.getStringExtra("intent_Str").equals(MyConfig.USERINFOAC_BACK_STRING1)) {
//
//                }
//                break;
//        }
        super.onActivityResult(requestCode, resultCode, data);
//        Fragment f = fragmentManager.findFragmentByTag(curFragmentTag);
//        f.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onResume() {
        super.onResume();

        Date dateNow = new Date();
        long interval = (dateNow.getTime() - MApplication.getMApplication().getSendTime()) / 1000;

        if (interval > 3) {
            System.out.println(interval + "KimoTest");
            Date dateTemp = new Date();
            SPUtils.put("SPUserInfo", MApplication.getMApplication(), "sendTime", dateTemp.getTime());
            onLoginResume();
        }
    }

    //登录
    public void onLoginResume() {


        if (MApplication.getMApplication().getLoginOut().equals("1")) {
            SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
            SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
            SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
            return;
        }

        mDialog = new MDialog(getContext(), this);

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("loginname", MApplication.getMApplication().getLoginName());
        requestParams.addBodyParameter("loginpwd", MApplication.getMApplication().getUserPwd());
        requestParams.addBodyParameter("sign", MApplication.sign);

        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, HttpUrls.LOGIN_URL, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                MStatus mStatus = gson.fromJson(responseInfo.result, MStatus.class);
                onLoginProcess(mStatus);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("我的登录失败");
            }
        });
    }

    //登录完成数据处理
    protected void onLoginProcess(MStatus mStatus) {
        //若登录成功F
        switch (mStatus.getStatus()) {
            case "1"://登录成功、是VIP、已实名认证
                //保存登录状态、vip、实名
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", true);
                //获取用户资料
                onGetUserInfo();
                break;
            case "2"://普通类型登录失败
                //保存登录状态、vip、实名
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                break;
            case "3"://登录成功、不是VIP、已实名认证
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", true);
                //获取用户资料
                onGetUserInfo();
                break;
            case "4"://登录成功、是VIP、未实名认证
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                //获取用户资料
                onGetUserInfo();
                break;
            case "5"://登录成功、不是VIP、未实名认证
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                //获取用户资料
                onGetUserInfo();
                break;
            case "6"://其他设备登录，导致的失败
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                SqliteLogin.addLoginBean(MApplication.getMApplication(), MApplication.getMApplication().getLoginName(), MApplication.getMApplication().getUserPwd(), "1");
                onClearSP();
                mDialog.show();
                break;
            default:
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                break;
        }
    }

    //获取用户资料
    protected void onGetUserInfo() {
        String url = HttpUrls.GETINFO_URL;
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                try {
                    L.e("获取用户资料：" + stringResponseInfo.result);
                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ds");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    //及时保存用户资料到SP
                    SPUtils.put("SPUserInfo", MApplication.getMApplication(), "true_name", obj.getString("trueName"));
                    SPUtils.put("SPUserInfo", MApplication.getMApplication(), "user_name", obj.getString("userName"));
                    SPUtils.put("SPUserInfo", MApplication.getMApplication(), "phone", obj.getString("phone"));
                    SPUtils.put("SPUserInfo", MApplication.getMApplication(), "user_avatar", obj.getString("avatar"));
                    SPUtils.put("SPUserInfo", MApplication.getMApplication(), "CardPass", obj.getString("CardPass"));
                } catch (JSONException e) {
                    L.e("LoginActivity解析json失败");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("访问网络失败，请检查网络设置");
            }
        });
    }

    //清理用户资料
    private void onClearSP() {
        SPUtils.clear("SPLogin", MApplication.getMApplication());
        SPUtils.clear("SPUserInfo", MApplication.getMApplication());
    }

    @Override
    public void onMDialogClick() {
        mDialog.dismiss();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

}
