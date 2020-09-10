package com.zidiv.realty.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.loc.l;
import com.zidiv.realty.BaseFragment;
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.activity.AboutActivity;
import com.zidiv.realty.activity.AdDetailActivity;
import com.zidiv.realty.activity.BrowserActivity;
import com.zidiv.realty.activity.GoActivity;
import com.zidiv.realty.activity.NearActivity;
import com.zidiv.realty.activity.PayOKActivity;
import com.zidiv.realty.activity.RentActivity;
import com.zidiv.realty.activity.SellActivity;
import com.zidiv.realty.activity.TestActivity;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.BannerImageList;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.customview.DownloadImageTask;
import com.zidiv.realty.util.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.zidiv.realty.R.layout.fragment_first;
import static com.zidiv.realty.R.layout.fragment_rent;
import static com.zidiv.realty.R.layout.item_house_adapter;

public class FirstFragment extends BaseFragment implements View.OnClickListener {

    private Context context;
    private Gson gson;
    private ImageView imgvone, imgvtwo, imgvthree;
    private ViewPager viewPager;
    private List<ImageView> imageViewList;
    private static final int TIME = 2500;
    private Handler mHandler = new Handler();
    private int itemPostion;
    private List<BannerImageList.BannerImageInfo> bannerImageInfoList = new ArrayList<>();

    private ImageView imgv_sell, imgv_rent, imgv_near;

    private ImageView imgv_browser, imgv_go, imgv_calculate;
    private ImageView imgv_myhouse, imgv_mycustom, imgv_house_input, imgv_custom_input;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contain, Bundle saveInstancState) {
        View view = inflater.inflate(R.layout.fragment_first, null);
        TextView tv_title = (TextView) view.findViewById(R.id.title_text);
        tv_title.setText("首 页");
        initView(view);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        imgvone = (ImageView) view.findViewById(R.id.imgvone);
        imgvtwo = (ImageView) view.findViewById(R.id.imgvtwo);
        imgvthree = (ImageView) view.findViewById(R.id.imgvthree);

        getData();


            getUpdate();


        if (isFirstStart(context)) {
            if (isNotificationEnabled(context)) {
            } else {
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setMessage("检测到您未开启通知！请前往设置中开启通知功能。");
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }

        // 初始化控件和声明事件
        imgv_sell = (ImageView) view.findViewById(R.id.imgvsell);
        imgv_sell.setOnClickListener(this);
        imgv_rent = (ImageView) view.findViewById(R.id.imgvrent);
        imgv_rent.setOnClickListener(this);
        imgv_near = (ImageView) view.findViewById(R.id.imgvnear);
        imgv_near.setOnClickListener(this);

        imgv_browser = (ImageView) view.findViewById(R.id.imgvbrowser);
        imgv_browser.setOnClickListener(this);
        imgv_go = (ImageView) view.findViewById(R.id.imgvgo);
        imgv_go.setOnClickListener(this);
        imgv_calculate = (ImageView) view.findViewById(R.id.imgvcalculator);
        imgv_calculate.setOnClickListener(this);

        imgv_myhouse = (ImageView) view.findViewById(R.id.imgvmyhouse);
        imgv_myhouse.setOnClickListener(this);
        imgv_mycustom = (ImageView) view.findViewById(R.id.imgvmycustom);
        imgv_mycustom.setOnClickListener(this);
        imgv_house_input = (ImageView) view.findViewById(R.id.imgvhouseinput);
        imgv_house_input.setOnClickListener(this);
        imgv_custom_input = (ImageView) view.findViewById(R.id.imgvcustominput);
        imgv_custom_input.setOnClickListener(this);

        return view;
    }

    public void initView(View view) {
        context = getActivity();
    }

    public boolean isFirstStart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "SHARE_APP_TAG", 0);
        Boolean isFirst = preferences.getBoolean("FIRSTStart", true);
        if (isFirst) {// 第一次
            preferences.edit().putBoolean("FIRSTStart", false).commit();
            return true;
        } else {
            return false;
        }
    }

    //获取用户资料
    private void getUpdate() {
        String url = HttpUrls.GETUPDATE_URL;
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                try {
                    L.e("获取软件版本：" + stringResponseInfo.result);
                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                    String status = jsonObject.getString("Status");
                    String message = jsonObject.getString("Message");
                    if (status.equals("1"))
                    {
                        int  version=  getVersionCode(context);
                        if (Integer.parseInt(message)>version)
                        {
                            AlertDialog dialog = new AlertDialog.Builder(context).create();
                            dialog.setMessage("检测到新版本，请及时下载更新。");
                            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri apk_url = Uri.parse("http://www.80mf.com/upload/80mf.apk");
                                    intent.setData(apk_url);
                                    startActivity(intent);//打开浏览器
                                    dialog.dismiss();
                                }
                            });
                            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    }

                } catch (JSONException e) {
                    L.e(e.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("检测异常");
            }
        });
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgvone:
                Intent intentone = new Intent(context, AdDetailActivity.class);
                intentone.putExtra("title", "活动详情");
                intentone.putExtra("src", bannerImageInfoList.get(0).getUrl());
                startActivity(intentone);
                break;
            case R.id.imgvtwo:
                Intent intenttwo = new Intent(context, AdDetailActivity.class);
                intenttwo.putExtra("title", "活动详情");
                intenttwo.putExtra("src", bannerImageInfoList.get(1).getUrl());
                startActivity(intenttwo);
                break;
            case R.id.imgvthree:
                Intent intentthree = new Intent(context, AdDetailActivity.class);
                intentthree.putExtra("title", "活动详情");
                intentthree.putExtra("src", bannerImageInfoList.get(2).getUrl());
                startActivity(intentthree);
                break;
            case R.id.imgvsell:
                startActivity(new Intent(context, SellActivity.class));

                break;
            case R.id.imgvrent:
                startActivity(new Intent(context, RentActivity.class));
                break;
            case R.id.imgvnear:
                startActivity(new Intent(context, NearActivity.class));
                break;
            case R.id.imgvbrowser:
                startActivity(new Intent(context, BrowserActivity.class));
                break;
            case R.id.imgvgo:
                startActivity(new Intent(context, GoActivity.class));
                break;
            case R.id.imgvcalculator:
                Intent intent = new Intent(context, AdDetailActivity.class);
                intent.putExtra("title", "计算器");
                intent.putExtra("src", "http://m.db.house.qq.com/index.php?mod=calculator&type=sd&rf=");
                startActivity(intent);
                break;
            default:
                T.showShort(context, "即将开通");
                break;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;

        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initBannerImage() {
        new DownloadImageTask(imgvone).execute(bannerImageInfoList.get(0).getImageurl());
        new DownloadImageTask(imgvtwo).execute(bannerImageInfoList.get(1).getImageurl());
        new DownloadImageTask(imgvthree).execute(bannerImageInfoList.get(2).getImageurl());
        imgvone.setOnClickListener(this);
        imgvtwo.setOnClickListener(this);
        imgvthree.setOnClickListener(this);

        imageViewList = new ArrayList<ImageView>();// 将要分页显示的View装入数组中
        imageViewList.add(imgvone);
        imageViewList.add(imgvtwo);
        imageViewList.add(imgvthree);


        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return imageViewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(imageViewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.removeView(imageViewList.get(position));
                container.addView(imageViewList.get(position));

                return imageViewList.get(position);
            }
        };

        viewPager.setAdapter(pagerAdapter);
    }

    private void getData() {
        gson = new Gson();
        String url = HttpUrls.BANNER_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("position", "0");
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                BannerImageList bannerImageList = gson.fromJson(responseInfo.result, BannerImageList.class);
                if (bannerImageList.getStatus().equals("1")) {
                    bannerImageInfoList.clear();
                    bannerImageInfoList.addAll(bannerImageList.getDs());

                    initBannerImage();
                    mHandler.postDelayed(runnableForViewPager, TIME);
                    L.e("获取数据成功：" + responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("失败");
            }
        });
    }

    Runnable runnableForViewPager = new Runnable() {
        @Override
        public void run() {
            try {
                itemPostion++;
                mHandler.postDelayed(this, TIME);
                viewPager.setCurrentItem(itemPostion % 3);
            } catch (Exception ex) {
                L.e(ex.toString());
            }
        }
    };
}

