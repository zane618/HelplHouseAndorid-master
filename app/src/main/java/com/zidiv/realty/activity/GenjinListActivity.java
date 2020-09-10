package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import com.zidiv.realty.adapter.GenjinAdater;
import com.zidiv.realty.adapter.HouseAdater;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.GenjinInfoList;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.DateUtil;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class GenjinListActivity extends BaseActivity implements XListView.IXListViewListener{
    private Context context;
    private ImageView iv_back;
    private TextView tv_title;
    private Gson gson;
    private XListView xListView = null;
    private GenjinAdater adater;
    private List<GenjinInfoList.GenjinInfo> list = new ArrayList<>();
    private int page = 1;
    private String id;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_genjin_list);
    }

    @Override
    public void getIntentData() {
        context = this;
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
        }
    }

    @Override
    public void initView() {
        iv_back = (ImageView)findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);
        tv_title = (TextView)findViewById(R.id.title_text);
        tv_title.setText("跟进列表");
        gson = new Gson();
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        adater = new GenjinAdater(context, list);
        xListView.setAdapter(adater);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        getData(page + "");

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

    private void getData (final String page) {
        String url = HttpUrls.HOUSE_GENJIN_LIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("page", page);
        params.addBodyParameter("id", id);
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("跟进列表接口调通：" + responseInfo.result);
                GenjinInfoList infoList = gson.fromJson(responseInfo.result, GenjinInfoList.class);
                if (infoList.getStatus().equals("1")) {
                    if (page.equals("1")) {
                        list.clear();
                    }
                    list.addAll(infoList.getDs());
                    adater.notifyDataSetChanged();
                    onLoad();
                    L.e("获取数据成功：" + responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("失败");
                onLoad();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void onRefresh() {
        L.e("onRefresh");
        page = 1;
        getData(page + "");
    }

    @Override
    public void onLoadMore() {
        L.e("onLoadMore");
        if(list.size() % MyConfig.ItemNum == 0) {
            getData(++page + "");
        } else {
            ToastUtils.showToast(context, "没有更多数据了 - -");
            onLoad();
        }
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(DateUtil.getNowTime("hh:mm:ss"));
    }
}
