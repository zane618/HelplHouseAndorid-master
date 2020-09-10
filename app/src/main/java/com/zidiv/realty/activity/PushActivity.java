package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.bean.PushInfoList;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

import java.util.ArrayList;
import java.util.List;

public class PushActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private Context context;
    private ImageView iv_back;
    private Switch switchone;
    private Gson gson;
    private List<PushInfoList.PushInfo> pushInfoList = new ArrayList<>();
    private List<CheckBox> chkList = new ArrayList<CheckBox>();
    private CheckBox chk_luyang, chk_shushan, chk_jingkai, chk_xinzhan, chk_baohe, chk_gaoxin, chk_zhengwu, chk_yaohai;
    private CheckBox chk_binhu, chk_changfeng, chk_feixi, chk_feidong, chk_lujiang, chk_chaohu, chk_beicheng, chk_xinqiao;
    private String strChkArea;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_push);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        iv_back = (ImageView) findViewById(R.id.back_image);
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("推送设置");
        switchone = (Switch) findViewById(R.id.switchone);


        gson=new Gson();

        chk_luyang = (CheckBox) findViewById(R.id.chk_luyang);
        chk_shushan = (CheckBox) findViewById(R.id.chk_shushan);
        chk_jingkai = (CheckBox) findViewById(R.id.chk_jingkai);
        chk_xinzhan = (CheckBox) findViewById(R.id.chk_xinzhan);
        chk_baohe = (CheckBox) findViewById(R.id.chk_baohe);
        chk_gaoxin = (CheckBox) findViewById(R.id.chk_gaoxin);
        chk_zhengwu = (CheckBox) findViewById(R.id.chk_zhengwu);
        chk_yaohai = (CheckBox) findViewById(R.id.chk_yaohai);
        chk_binhu = (CheckBox) findViewById(R.id.chk_binhu);
        chk_changfeng = (CheckBox) findViewById(R.id.chk_changfeng);
        chk_feixi = (CheckBox) findViewById(R.id.chk_feixi);
        chk_feidong = (CheckBox) findViewById(R.id.chk_feidong);
        chk_lujiang = (CheckBox) findViewById(R.id.chk_lujiang);
        chk_chaohu = (CheckBox) findViewById(R.id.chk_chaohu);
        chk_beicheng = (CheckBox) findViewById(R.id.chk_beicheng);
        chk_xinqiao = (CheckBox) findViewById(R.id.chk_xinqiao);

        chkList.add(chk_luyang);
        chkList.add(chk_shushan);
        chkList.add(chk_jingkai);
        chkList.add(chk_xinzhan);
        chkList.add(chk_baohe);
        chkList.add(chk_gaoxin);
        chkList.add(chk_zhengwu);
        chkList.add(chk_yaohai);
        chkList.add(chk_binhu);
        chkList.add(chk_changfeng);
        chkList.add(chk_feixi);
        chkList.add(chk_feidong);
        chkList.add(chk_lujiang);
        chkList.add(chk_chaohu);
        chkList.add(chk_beicheng);
        chkList.add(chk_xinqiao);

        initCheckBotton();
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

    public void initCheckBotton() {
        String url = HttpUrls.PUSHINFO_URL;
        RequestParams params = new RequestParams();
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                PushInfoList infoList = gson.fromJson(responseInfo.result, PushInfoList.class);
                if (infoList.getStatus().equals("1")) {
                    pushInfoList.addAll(infoList.getDs());

                    if (pushInfoList.get(0).getSwitch().equals("1")) {
                        switchone.setChecked(true);
                    } else {
                        switchone.setChecked(false);
                    }
                    strChkArea = pushInfoList.get(0).getPushArea();

                    //遍历集合中的checkBox,判断是否选择，获取选中的文本
                    for (CheckBox checkbox : chkList) {
                        if (strChkArea.contains(checkbox.getText().toString().trim())) {
                            checkbox.setChecked(true);
                        }
                    }
                    setListener();
                    L.e("获取数据成功" + responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("失败");
            }
        });

    }

    public  void  setListener()
    {
        chk_luyang.setOnCheckedChangeListener(this);
        chk_shushan.setOnCheckedChangeListener(this);
        chk_jingkai.setOnCheckedChangeListener(this);
        chk_xinzhan.setOnCheckedChangeListener(this);
        chk_baohe.setOnCheckedChangeListener(this);//5
        chk_gaoxin.setOnCheckedChangeListener(this);
        chk_zhengwu.setOnCheckedChangeListener(this);
        chk_yaohai.setOnCheckedChangeListener(this);
        chk_binhu.setOnCheckedChangeListener(this);
        chk_changfeng.setOnCheckedChangeListener(this);//10
        chk_feixi.setOnCheckedChangeListener(this);
        chk_feidong.setOnCheckedChangeListener(this);
        chk_lujiang.setOnCheckedChangeListener(this);
        chk_chaohu.setOnCheckedChangeListener(this);
        chk_beicheng.setOnCheckedChangeListener(this);
        chk_xinqiao.setOnCheckedChangeListener(this);
        switchone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPushButton(isChecked);
            }
        });
    };
    public void setPushButton(boolean isChecked) {
        String url = HttpUrls.PUSHUPDATE_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("switchs", isChecked ? "1" : "0");
        params.addBodyParameter("pusharea", strChkArea);
        params.addBodyParameter("isdefaultset", "0");
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                String json = stringResponseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if (mMstatus.getStatus().equals("1")) {
                    //若登录成功
                    T.showShort(context, "设置成功");
                } else {
                    T.showShort(context, mMstatus.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                T.showShort(context, "网络异常，请检查网络");
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        String strTemp = buttonView.getText().toString().trim();
        if (strChkArea.contains(strTemp)) {
            if (isChecked) {

            } else {
                strChkArea = strChkArea.replace(strTemp + "|", "");
            }
        } else {
            if (isChecked) {
                strChkArea = strChkArea + strTemp + "|";
            } else {

            }
        }
        String url = HttpUrls.PUSHUPDATE_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("pusharea", strChkArea);
        params.addBodyParameter("isdefaultset", "0");
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                String json = stringResponseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if (mMstatus.getStatus().equals("1")) {
                    //若登录成功
                    T.showShort(context, "设置成功");
                } else {
                    T.showShort(context, mMstatus.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                T.showShort(context, "网络异常，请检查网络");
            }
        });
    }
}
