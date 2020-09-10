package com.zidiv.realty.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseFragment;
import com.zidiv.realty.R;
import com.zidiv.realty.activity.DetailsActivity;
import com.zidiv.realty.adapter.HouseAdater;
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

/**
 */
public class RentFragment extends BaseFragment implements XListView.IXListViewListener{
    private Context context;
    private Gson gson;
    private XListView xListView = null;
    private HouseAdater adater;
    private List<HouseInfoList.HouseInfo> list = new ArrayList<>();
    private int page = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent,null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        context = getActivity();
        gson = new Gson();
        xListView = (XListView) view.findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        adater = new HouseAdater(context, list);
        xListView.setAdapter(adater);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(i - 1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        getData(page + "");
    }

    private void getData (final String page) {
        String url = HttpUrls.HOUSE_RENT_LIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("page", page);
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
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
            }
        });
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
        xListView.setRefreshTime(DateUtil.getNowTime("HH:mm:ss"));
    }
}
