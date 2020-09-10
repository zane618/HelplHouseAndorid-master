package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.adapter.SendTimeAdapter;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;
import com.zidiv.realty.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 跟进页面
 */
public class GenjinActivity extends BaseActivity{
    private Context context;
    private ImageView iv_back;
    private TextView tv_title;
    private TextView et_content;
    private TextView tv_choose_way;
    private Button btn_sure;
    private String id;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_genjin);
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
        tv_title.setText("房源跟进");
        btn_sure = (Button) findViewById(R.id.btn_sure);
        et_content = (TextView) findViewById(R.id.et_content);
        tv_choose_way = (TextView) findViewById(R.id.tv_choose_way);
    }

    @Override
    public void initLinstener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivityAnimation();
            }
        });
        tv_choose_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);
            }
        });
        //确认跟进
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tv_choose_way.getText().equals("选择方式")) {
                    genjin();
                } else {
                    ToastUtils.showToast(context, "请选择");
                }
            }
        });
    }

    @Override
    public void initData() {
        initPopView();
    }

    private int width;
    private PopupWindow popupWindow;
    private SendTimeAdapter sendTimeAdapter;
    private ListView mListView;
    private List<String> strList = new ArrayList<>();
    private View popView;
    private void initPopView() {
        initList();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        LayoutInflater inflater = LayoutInflater.from(context);
        popView = inflater.inflate(R.layout.sendtime_dialog, null);
        sendTimeAdapter = new SendTimeAdapter(strList, context);
        mListView = (ListView) popView.findViewById(R.id.send_listView);
        mListView.setAdapter(sendTimeAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv_choose_way.setText(strList.get(i));
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(500, width);
        popupWindow.setContentView(popView);
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x55000000));
        popupWindow.setFocusable(true);
    }
    //跟进
    private void genjin() {
        String url = HttpUrls.HOUSE_GOTO;
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", id);
        params.addBodyParameter("state", tv_choose_way.getText().toString().trim());
        params.addBodyParameter("content", et_content.getText().toString().trim());
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("跟进成功：" + responseInfo.result);
                setResult(11);
                exitActivityAnimation();
                ToastUtils.showToast(context, "跟进成功 - -");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("跟进失败：" + s);
            }
        });
    }
    private void initList() {
        strList.add("在卖");
        strList.add("已售");
        strList.add("关机");
        strList.add("停机");
        strList.add("不卖啦");
        strList.add("无房卖");
        strList.add("无法接通");
        strList.add("打错电话");
        strList.add("来电提醒");
        strList.add("无人接听");
    }
}
