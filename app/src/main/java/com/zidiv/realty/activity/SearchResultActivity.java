package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.zidiv.realty.adapter.HouseAdater;
import com.zidiv.realty.application.MApplication;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class SearchResultActivity extends BaseActivity implements XListView.IXListViewListener{
    private Context context = this;
    private TextView tv_title;
    private ImageView iv_back;
    private Gson gson;
    private XListView xListView = null;
    private HouseAdater adater;
    private List<HouseInfoList.HouseInfo> list = new ArrayList<>();
    private String bianhao,donghao,mingcheng,phone_number,mianji1,mianji2,zongjia1,zongjia2,danjia1,danjia2,louceng1,louceng2,househuxing;
    private String quyu;
    private String zhuangtai;
    private int page = 1;
    private String isCollect="0";
    private boolean notQu = true; //不是小区历史

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_result);
    }

    @Override
    public void getIntentData() {
        Intent i = getIntent();
        if (i != null) {
            notQu = i.getBooleanExtra("notQu", true);
            if (notQu) {
                bianhao = i.getStringExtra("bianhao");
                donghao = i.getStringExtra("donghao");
                mingcheng = i.getStringExtra("mingcheng");
                phone_number = i.getStringExtra("phone_number");
                mianji1 = i.getStringExtra("mianji1");
                mianji2 = i.getStringExtra("mianji2");
                zongjia1 = i.getStringExtra("zongjia1");
                zongjia2 = i.getStringExtra("zongjia2");
                danjia1 = i.getStringExtra("danjia1");
                danjia2 = i.getStringExtra("danjia2");
                louceng1 = i.getStringExtra("louceng1");
                louceng2 = i.getStringExtra("louceng2");
                quyu = i.getStringExtra("quyu");
                zhuangtai = i.getStringExtra("zhuangtai");
                househuxing = i.getStringExtra("househuxing");
                isCollect=i.getStringExtra("iscollect");
            } else { //是小区历史
                mingcheng = i.getStringExtra("mingcheng");
            }
        }
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.title_text);
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);
        tv_title.setText("搜索结果");

        gson = new Gson();
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        adater = new HouseAdater(context, list);
        xListView.setAdapter(adater);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv_Temp = (TextView) view.findViewById(R.id.housename);
                HouseInfoList.HouseInfo houseInfo=   list.get(i - 1);
                String str=houseInfo.getHousename()+"<font color='#09A7F0'>[已浏览]</font>";
                tv_Temp.setText(Html.fromHtml(str));

                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(i - 1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        getData(page + "");
    }

    @Override
    public void initLinstener() {
        //返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivityAnimation();
            }
        });
    }

    private void getData (final String page) {

        RequestParams params = new RequestParams();
        if (notQu) {
            params.addBodyParameter("page", page);
            if (!quyu.equals("")) {
                params.addBodyParameter("zone", quyu);
            }
            if (!bianhao.equals("")) {
                params.addBodyParameter("id", bianhao);
            }
            if (!donghao.equals("")) {
                params.addBodyParameter("number", donghao);
            }
            if (!mingcheng.equals("")) {
                params.addBodyParameter("house_name", mingcheng);
            }
            if (!phone_number.equals("")) {
                params.addBodyParameter("phone", phone_number);
            }
            if (!mianji1.equals("")) {
                params.addBodyParameter("min_square", mianji1);
            }
            if (!mianji2.equals("")) {
                params.addBodyParameter("max_square", mianji2);
            }
            if (!zongjia1.equals("")) {
                params.addBodyParameter("min_total", zongjia1);
            }
            if (!zongjia2.equals("")) {
                params.addBodyParameter("max_total", zongjia2);
            }
            if (!danjia1.equals("")) {
                params.addBodyParameter("min_price", danjia1);
            }
            if (!danjia2.equals("")) {
                params.addBodyParameter("max_price", danjia2);
            }
            if (!louceng1.equals("")) {
                params.addBodyParameter("min_floor", louceng1);
            }
            if (!louceng2.equals("")) {
                params.addBodyParameter("max_floor", louceng2);
            }
            if (!zhuangtai.equals("")) {
                params.addBodyParameter("state", zhuangtai);
            }
            if (!househuxing.equals("")) {
                params.addBodyParameter("househuxing", househuxing);
            }
        } else {
            params.addBodyParameter("page", page);
            params.addBodyParameter("house_name", mingcheng);
        }
//        L.e(page +".." + quyu +".." + bianhao +".." + donghao +".." + mingcheng +".." + phone_number +".." + mianji1 +".." + mianji2
//                +".." + zongjia1 +".." + zongjia2 +".." + danjia1 +".." + danjia2 +".." + louceng1 +".." + louceng2);


        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST,  isCollect.equals("1")?HttpUrls.HOUSECOLLECT_LIST:HttpUrls.HOUSE_LIST, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                HouseInfoList infoList = gson.fromJson(responseInfo.result, HouseInfoList.class);
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
                ToastUtils.showToast(context, "没有匹配的房源 - -");
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
        xListView.setRefreshTime(DateUtil.getNowTime("HH:mm:ss"));
    }

}
