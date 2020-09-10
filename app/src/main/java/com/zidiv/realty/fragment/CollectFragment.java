package com.zidiv.realty.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseFragment;
import com.zidiv.realty.R;
import com.zidiv.realty.activity.DetailsActivity;
import com.zidiv.realty.activity.LoginActivity;
import com.zidiv.realty.activity.SearchCollectActivity;
import com.zidiv.realty.adapter.HouseAdater;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.customview.MDialog;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.DateUtil;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.xlistview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CollectFragment extends BaseFragment implements XListView.IXListViewListener {
    private Context context;
    private TextView tv_title;
    private Gson gson;
    private XListView xListView = null;
    private HouseAdater adater;
    private List<HouseInfoList.HouseInfo> list = new ArrayList<>();
    private int page = 1;
    private boolean first = true; //首次创建这个Fragment

    private ImageView imgv_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        context = getActivity();
        tv_title = (TextView) view.findViewById(R.id.title_text);
        tv_title.setText("我的收藏");
        imgv_search = (ImageView) view.findViewById(R.id.imgv_search);
        imgv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchCollectActivity.class));
            }
        });
        gson = new Gson();
        xListView = (XListView) view.findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        adater = new HouseAdater(context, list);
        xListView.setAdapter(adater);
        xListView.setXListViewListener(CollectFragment.this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(i - 1));
                intent.putExtra("flag", true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }

    private void getData(final String page) {
        String url = HttpUrls.HOUSE_COLLECT_LIST;
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
                    L.e("收藏界面获取数据成功：" + responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (!MApplication.getMApplication().getIsLoginFlag()) {
                    ToastUtils.showToast(context, "你还没有登录 - -");
                }
                L.e("收藏界面获取数据失败" + s);
                onLoad();
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
        if (list.size() % MyConfig.ItemNum == 0) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (MApplication.getMApplication().getIsLoginFlag()) {
            if (first) {
                getData(page + "");
                imgv_search.setVisibility(View.VISIBLE);
            }
            first = false;
        } else {
            list.clear();
            if (adater != null) {
                adater.notifyDataSetChanged();
            }
        }
    }
}
