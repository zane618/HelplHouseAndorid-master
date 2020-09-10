package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
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
import com.zidiv.realty.adapter.HouseAdater;
import com.zidiv.realty.adapter.HouseRentAdater;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.DateUtil;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

import static com.zidiv.realty.R.layout.activity_test;
import static com.zidiv.realty.R.layout.list_item_camera;
import static com.zidiv.realty.R.layout.view_image_cycle;
import static com.zidiv.realty.R.layout.xlistview_footer;

public class RentActivity extends BaseActivity implements XListView.IXListViewListener {
    private Context context;
    private ImageView iv_back;
    private XListView xListView = null;
    private Gson gson;
    private HouseRentAdater houseRentAdater;
    private List<HouseInfoList.HouseInfo> houseInfoList = new ArrayList<>();
    private int page = 1;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sell);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("出租房源");
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);

        gson = new Gson();
        houseRentAdater = new HouseRentAdater(context, houseInfoList);
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(houseRentAdater);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_Temp = (TextView) view.findViewById(R.id.housename);
                HouseInfoList.HouseInfo houseInfo = houseInfoList.get(position - 1);
                String str_Temp = houseInfo.getHousename() + "<font color='#09A7F0'>[已浏览]</font>";
                tv_Temp.setText(Html.fromHtml(str_Temp));

                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", houseInfoList.get(position - 1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        getData(page + "");
    }

    private void getData(final String page) {
        String url = HttpUrls.HOUSE_RENT_LIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("page", page);
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                HouseInfoList infoList = gson.fromJson(responseInfo.result, HouseInfoList.class);
                if (infoList.getStatus().equals("1")) {
                    if (page.equals("1")) {
                        houseInfoList.clear();
                    }
                    houseInfoList.addAll(infoList.getDs());
                    houseRentAdater.notifyDataSetChanged();
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
        page = 1;
        getData(page + "");
    }

    @Override
    public void onLoadMore() {
        if (houseInfoList.size() % MyConfig.ItemNum == 0) {
            getData(++page + "");
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

}
