package com.zidiv.realty;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.customview.MDialog;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.fragment.FirstFragment;
import com.zidiv.realty.fragment.HomeFragment;
import com.zidiv.realty.fragment.MineFragment;
import com.zidiv.realty.fragment.SearchFragment;
import com.zidiv.realty.fragment.CollectFragment;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.NetUtils;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;
import com.zidiv.realty.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 主界面
 * Created by Administrator on 2016/3/16.
 */
public class MainFragmentActivity extends FragmentActivity implements View.OnClickListener, MDialog.MDialogDo {
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private Context context;
    private FragmentManager fm;
    private List<Fragment> lsFragment = new ArrayList<>();
    private Button btn_home, btn_order, btn_shoppingCart, btn_mine;
    private int currIndex = 0;

    private HomeFragment homeFragment;
    private FirstFragment firstFragment;
    private SearchFragment shaiDanFragment;
    private CollectFragment shoppingCartFragment;
    private MineFragment mineFragment;
    public static MainFragmentActivity instance;
    private MDialog mDialog;

    public FragmentTransaction mFragmentTransaction;
    public FragmentManager fragmentManager;
    public String curFragmentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        //检查网络，如果没有连接网路 发出提醒
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "网络异常，请检查网络设置");
        }
        setContentView(R.layout.activity_fragent_main);
        //获取sdcard权限
        init();
        /*try {
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 0x77);
            } else {
//                init();
            }
        } catch (Exception e){}*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0x77:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 授予权限
//                    init();
                } else {
//                    init();
                    // Permission Denied 权限被拒绝
//                    Toast.makeText(MainFragmentActivity.this, "Permission Denied",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //初始化
    private void init() {
        context = this;

        mDialog = new MDialog(context, this);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_order = (Button) findViewById(R.id.btn_order);
        btn_shoppingCart = (Button) findViewById(R.id.btn_shoppingCart);
        btn_mine = (Button) findViewById(R.id.btn_mine);
        btn_home.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_shoppingCart.setOnClickListener(this);
        btn_mine.setOnClickListener(this);
        fm = getSupportFragmentManager();
        //是否有intent跳转传值操作
        Intent intent = getIntent();
        if (intent != null) {
            currIndex = intent.getIntExtra("index", 0);
        }
        setBtnImgColAll(currIndex); //设置按钮颜色和背景
        changeFragment(currIndex);
    }

    public void setCurrentItem(int i) {
        setBtnImgColAll(i);
        changeFragment(i);
    }

    private void changeFragment(int i) {
        FragmentTransaction ft = fm.beginTransaction();
        switch (i) {
            case 0:
//                if(null == homeFragment) {
//                    homeFragment = new HomeFragment();
//                    ft.add(R.id.layout_fragme, homeFragment);
//                    lsFragment.add(homeFragment);
//                }
//                show(ft, homeFragment);
                if (null == firstFragment) {
                    firstFragment = new FirstFragment();
                    ft.add(R.id.layout_fragme, firstFragment);
                    lsFragment.add(firstFragment);
                }
                show(ft, firstFragment);
                break;
            case 1:
                if (null == shaiDanFragment) {
                    shaiDanFragment = new SearchFragment();
                    ft.add(R.id.layout_fragme, shaiDanFragment);
                    lsFragment.add(shaiDanFragment);
                }
                show(ft, shaiDanFragment);
                break;
            case 2:
                if (null == shoppingCartFragment) {
                    shoppingCartFragment = new CollectFragment();
                    ft.add(R.id.layout_fragme, shoppingCartFragment);
                    lsFragment.add(shoppingCartFragment);
                }
                show(ft, shoppingCartFragment);
                break;
            case 3:
                if (null == mineFragment) {
                    mineFragment = new MineFragment();
                    ft.add(R.id.layout_fragme, mineFragment);
                    lsFragment.add(mineFragment);
                }
                show(ft, mineFragment);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                setBtnImgColAll(0);
                changeFragment(0);
                break;
            case R.id.btn_order:
                setBtnImgColAll(1);
                changeFragment(1);
                break;
            case R.id.btn_shoppingCart:
                setBtnImgColAll(2);
                changeFragment(2);
                break;
            case R.id.btn_mine:
                login();
                break;
        }
    }

    //登录
    public void login() {
        //如果标记是已经登录
        if (MApplication.getMApplication().getIsLoginFlag()) {
            //已经登录不处理
            setBtnImgColAll(3);
            changeFragment(3);
        } else {

            if (MApplication.getMApplication().getLoginOut().equals("1")) {
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                return;
            }

            String url = HttpUrls.LOGIN_URL;
            RequestParams requestParams = new RequestParams();

            requestParams.addBodyParameter("loginname", MApplication.getMApplication().getLoginName());
            requestParams.addBodyParameter("loginpwd", MApplication.getMApplication().getUserPwd());
            requestParams.addBodyParameter("sign", MApplication.sign);

            MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    Gson gson = new Gson();
                    MStatus mStatus = gson.fromJson(responseInfo.result, MStatus.class);
                    onLoginProcess(mStatus);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    L.e("我的登录失败");
                    clearSP();
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void clearSP() {
        SPUtils.clear("SPLogin", context);
        SPUtils.clear("SPUserInfo", context);
//        ToastUtils.showToast(context, "请先登录 - -");
    }

    //根据currIndex设置底部按钮的背景
    private void setBtnImgColAll(int i) {
        switch (i) {
            case 0:
                setBtnImgCol(btn_home, R.drawable.icon_home_on, R.color.bottom_text_colcor2);
                setBtnImgCol(btn_order, R.drawable.icon_search, R.color.bottom_text_colcor);
                setBtnImgCol(btn_shoppingCart, R.drawable.icon_collect, R.color.bottom_text_colcor);
                setBtnImgCol(btn_mine, R.drawable.icon_me, R.color.bottom_text_colcor);
                btn_home.setClickable(false);
                btn_order.setClickable(true);
                btn_shoppingCart.setClickable(true);
                btn_mine.setClickable(true);
                break;
            case 1:
                setBtnImgCol(btn_home, R.drawable.icon_home, R.color.bottom_text_colcor);
                setBtnImgCol(btn_order, R.drawable.icon_search_on, R.color.bottom_text_colcor2);
                setBtnImgCol(btn_shoppingCart, R.drawable.icon_collect, R.color.bottom_text_colcor);
                setBtnImgCol(btn_mine, R.drawable.icon_me, R.color.bottom_text_colcor);
                btn_home.setClickable(true);
                btn_order.setClickable(false);
                btn_shoppingCart.setClickable(true);
                btn_mine.setClickable(true);
                break;
            case 2:
                setBtnImgCol(btn_home, R.drawable.icon_home, R.color.bottom_text_colcor);
                setBtnImgCol(btn_order, R.drawable.icon_search, R.color.bottom_text_colcor);
                setBtnImgCol(btn_shoppingCart, R.drawable.icon_collect_on, R.color.bottom_text_colcor2);
                setBtnImgCol(btn_mine, R.drawable.icon_me, R.color.bottom_text_colcor);
                btn_home.setClickable(true);
                btn_order.setClickable(true);
                btn_shoppingCart.setClickable(false);
                btn_mine.setClickable(true);
                break;
            case 3:
                setBtnImgCol(btn_home, R.drawable.icon_home, R.color.bottom_text_colcor);
                setBtnImgCol(btn_order, R.drawable.icon_search, R.color.bottom_text_colcor);
                setBtnImgCol(btn_shoppingCart, R.drawable.icon_collect, R.color.bottom_text_colcor);
                setBtnImgCol(btn_mine, R.drawable.icon_me_on, R.color.bottom_text_colcor2);
                btn_home.setClickable(true);
                btn_order.setClickable(true);
                btn_shoppingCart.setClickable(true);
                btn_mine.setClickable(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void show(FragmentTransaction ft, Fragment fragment) {
        for (Fragment _fragment : lsFragment) {
            ft.hide(_fragment);
        }
        ft.show(fragment);
        ft.commit();
    }

    /**
     * 改变button的图片和字体颜色
     *
     * @param btn    Button
     * @param imageR 图片
     * @param colorR 颜色
     */
    private void setBtnImgCol(Button btn, int imageR, int colorR) {
        Drawable img = getResources().getDrawable(imageR);
        img.setBounds(0, 0, 70, 70);
//        img.setBounds(0,0,img.getMinimumWidth(),img.getMinimumHeight());
        btn.setCompoundDrawables(null, img, null, null);
        btn.setTextColor(getResources().getColor(colorR));
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private int delay = 1000;
    long currentTime = 0;

    private void exit() {
        if (System.currentTimeMillis() - currentTime >= delay) {
            T.showShort(context, "再次点击退出");
            currentTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMDialogClick() {
        mDialog.dismiss();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    //登录完成数据处理
    protected void onLoginProcess(MStatus mStatus) {
        //若登录成功F
        switch (mStatus.getStatus()) {
            case "0"://普通类型登录失败
                //保存登录状态、vip、实名
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                Intent intentz = new Intent(context, LoginActivity.class);
                startActivity(intentz);
                break;
            case "1"://登录成功、是VIP、已实名认证
                //保存登录状态、vip、实名
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", true);
                //获取用户资料
                getUserInfo();
                setBtnImgColAll(3);
                changeFragment(3);
                break;
            case "2"://普通类型登录失败
                //保存登录状态、vip、实名
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                break;
            case "3"://登录成功、不是VIP、已实名认证
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", true);
                //获取用户资料
                getUserInfo();
                setBtnImgColAll(3);
                changeFragment(3);
                break;
            case "4"://登录成功、是VIP、未实名认证
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                //获取用户资料
                getUserInfo();
                break;
            case "5"://登录成功、不是VIP、未实名认证
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", true);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                //获取用户资料
                getUserInfo();
                setBtnImgColAll(3);
                changeFragment(3);
                break;
            case "6"://其他设备登录，导致的失败
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                SqliteLogin.addLoginBean(MApplication.getMApplication(), MApplication.getMApplication().getLoginName(), MApplication.getMApplication().getUserPwd(), "1");
                clearSP();
                mDialog.show();
                Intent intentt = new Intent(context, LoginActivity.class);
                startActivity(intentt);
                break;
            default:
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isTrueName", false);
                Intent intents = new Intent(context, LoginActivity.class);
                startActivity(intents);
                break;
        }
    }

    //获取用户资料
    protected void getUserInfo() {
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

}
