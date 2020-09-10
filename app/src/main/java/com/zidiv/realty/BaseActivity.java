package com.zidiv.realty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.activity.LoginActivity;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.customview.MDialog;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public abstract class BaseActivity extends Activity implements MDialog.MDialogDo {

    private MDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        setContentView();
        getIntentData();
        initView();
        initLinstener();
        initData();
    }

    /**
     * 初始化布局
     */
    public abstract void setContentView();

    /**
     * 获取Intent数据
     */
    public abstract void getIntentData();

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 初始化监听
     */
    public abstract void initLinstener();

    /**
     * 初始化数据
     */
    public abstract void initData();
	
	/*@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					onInputHidden();
					v.clearFocus();
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		try {
			if (getWindow().superDispatchTouchEvent(ev)) {
				return true;
			}
		} catch (Exception e) {
		}
		
		return onTouchEvent(ev);
	}*/

    public void onInputHidden() {
        Log.d("TAG", "input_hidden");
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().exitActivityAnimation(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        ActivityManager.getInstance().startActivity(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        ActivityManager.getInstance().startActivity(this);
    }


    public boolean checkPermissionGranted(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager pkgMgr = getPackageManager();
            return pkgMgr.checkPermission(permission, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void exitActivityAnimation() {
        ActivityManager.getInstance().exitActivityAnimation(this);
    }

    @Override
    protected void onResume() {
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

        mDialog = new MDialog(this, this);

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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        exitActivityAnimation();
    }

}
