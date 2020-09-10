package com.zidiv.realty.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.zidiv.realty.adapter.NameAdapter;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.bean.NameList;
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.fragment.SearchFragment;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.DateUtil;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class SearchNameActivity extends BaseActivity implements XListView.IXListViewListener {
    private Context context;
    private ImageView iv_back;
    private XListView xListView = null;
    private Gson gson;
    private NameAdapter nameAdapter;
    private List<NameList.NameInfo> nameList = new ArrayList<>();
    private String name = "小区";
    private SearchView mSearchView;
    private Button btnOK;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_name);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("");
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);

        mSearchView=(SearchView)findViewById(R.id.mSearchView);

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
             name=query;
                getData();
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){

                }else{
                    name=newText;
                    getData();
                }
                return false;
            }
        });

        btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name", String.valueOf(mSearchView.getQuery()));//这里的values就是我们要传的值
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);//返回值调用函数，其中2为resultCode，返回值的标志
                finish();
            }
        });

        gson = new Gson();
        nameAdapter = new NameAdapter(context, nameList);
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(nameAdapter);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NameList.NameInfo nameInfo = nameList.get(position - 1);

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name",nameInfo.getHousename());//这里的values就是我们要传的值
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);//返回值调用函数，其中2为resultCode，返回值的标志
                finish();

            }
        });
        getData();
    }

    private void getData() {
        String url = HttpUrls.HOUSE_SNAME_LIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("name", name);
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                NameList infoList = gson.fromJson(responseInfo.result, NameList.class);
                if (infoList.getStatus().equals("1")) {

                        nameList.clear();

                    nameList.addAll(infoList.getDs());
                    nameAdapter.notifyDataSetChanged();
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
        getData();
    }

    @Override
    public void onLoadMore() {
        if (nameList.size() % MyConfig.ItemNum == 0) {
            getData();
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