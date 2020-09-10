package com.zidiv.realty.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.adapter.HouseNearAdapter;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.avatar.ClippingPageActivity;
import com.zidiv.realty.avatar.ConstantSet;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.bean.HouseNearList;
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.DateUtil;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.xlistview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.zidiv.realty.R.layout.activity_test;
import static com.zidiv.realty.R.layout.list_item_camera;
import static com.zidiv.realty.R.layout.view_image_cycle;
import static com.zidiv.realty.R.layout.xlistview_footer;

public class NearActivity extends BaseActivity implements XListView.IXListViewListener, AMapLocationListener {
    private Context context;
    private ImageView iv_back;
    private XListView xListView = null;
    private double lat = 31.834505;
    private double lon = 117.282012;
    private Gson gson;
    private HouseNearAdapter houseNearAdapter;
    private List<HouseNearList.NearInfo> houseNearList = new ArrayList<>();
    private int page = 1;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_near);
    }

    @Override
    public void getIntentData() {
        context = this;
    }


    @Override
    public void initView() {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("附近房源");
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);

        try{
            List<String> listPermission = new ArrayList<>();
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
            listPermission.add(Manifest.permission.READ_PHONE_STATE);
            listPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            for (String strPermisssion : listPermission) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, strPermisssion);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{strPermisssion}, 222);
                    }
                }
            }
        }catch (Exception e)
        {
            L.e("",e.getMessage());
        }

        onLoadListInfo();
    }

    protected void onLoadListInfo() {

        gson = new Gson();
        houseNearAdapter = new HouseNearAdapter(context, houseNearList);
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(houseNearAdapter);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HouseNearList.NearInfo nearInfo = houseNearList.get(position - 1);
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("notQu", false);
                intent.putExtra("mingcheng", nearInfo.getHousename());
                startActivity(intent);
            }
        });

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mOption);
        //启动定位
        mLocationClient.startLocation();

    }


    private void getData(final String page) {

        String url = HttpUrls.HOUSE_NEAR_LIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("page", page);
        params.addBodyParameter("lat", String.valueOf(lat));
        params.addBodyParameter("lng", String.valueOf(lon));
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                HouseNearList nearList = gson.fromJson(responseInfo.result, HouseNearList.class);
                if (nearList.getStatus().equals("1")) {
                    if (page.equals("1")) {
                        houseNearList.clear();
                    }
                    houseNearList.addAll(nearList.getDs());
                    houseNearAdapter.notifyDataSetChanged();
                    onLoad();
                    L.e("获取数据成功" + responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("失败");
            }
        });
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

    @Override
    public void onRefresh() {
        //启动定位
        mLocationClient.startLocation();
        page = 1;
        getData(page + "");
    }

    @Override
    public void onLoadMore() {
        if (houseNearList.size() % MyConfig.ItemNum == 0) {
            ++page;
            mLocationClient.startLocation();
        } else {
            ToastUtils.showToast(context, "没有更多数据了 - -");
            onLoad();
        }
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(DateUtil.getNowTime("HH:mm:ss"));
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        lat = aMapLocation.getLatitude();
        lon = aMapLocation.getLongitude();
        System.out.println(lat);
        System.out.println(lon);
        lat = aMapLocation.getLatitude();
        lon = aMapLocation.getLongitude();
        getData(page + "");
    }

}