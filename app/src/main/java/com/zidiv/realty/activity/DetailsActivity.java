package com.zidiv.realty.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.GenjinInfoList;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.config.MyConfig;
import com.zidiv.realty.customview.MDialog;
import com.zidiv.realty.database.SqliteBrowser;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.DateUtil;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;


/**
 * 详情界面
 */
public class DetailsActivity extends BaseActivity implements  XListView.IXListViewListener {
    private TextView tv_title;
    private ImageView img_Back;
    private Context context;
    private TextView tv_create_time, tv_bianhao, tv_zongjia, tv_quyu, tv_mianji, tv_zhuangtai, tv_danjia,
            tv_zhuanghuang, tv_huxing, tv_louxing, tv_louceng, tv_niandai, tv_fangzhu, tv_dianhua, tv_dizhi;
    private TextView tv_fanghao;
    private TextView tv_beizhu;
    private TextView tv_jiage_for_rent; //出租的要显示价格 fuck   对应的显示tv 是  tv_zongjia  uzi
    private Button btn_gj, btn_collect;
    private HouseInfoList.HouseInfo info = null;
    private boolean flag = false;
   // private Button btn_genjin;
    private Button btn_call;
    private LinearLayout ll_zongjia, ll_danjia,xlisttop;


    private Gson gson;
    private XListView xListView = null;
    private GenjinAdater adater;
    private List<GenjinInfoList.GenjinInfo> list = new ArrayList<>();
    private int page = 1;
    private String id;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_details);
    }

    @Override
    public void getIntentData() {
        context = this;
        Intent intent = getIntent();
        if (intent != null) {
            flag = intent.getBooleanExtra("flag", false);
            info = (HouseInfoList.HouseInfo) intent.getSerializableExtra("item");
            L.e("info ...: " + info.toString());
            id=info.getId();
        }
    }

    @Override
    public void initView() {
        xlisttop=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.activity_xlist_top, null);
        tv_title = (TextView) findViewById(R.id.title_text);
        img_Back = (ImageView) findViewById(R.id.back_image);
        img_Back.setVisibility(View.VISIBLE);
        ll_zongjia = (LinearLayout) xlisttop.findViewById(R.id.ll_zongjia);
        ll_danjia = (LinearLayout) xlisttop.findViewById(R.id.ll_danjia);
        tv_create_time = (TextView) xlisttop.findViewById(R.id.tv_create_time);
        tv_bianhao = (TextView) xlisttop.findViewById(R.id.tv_bianhao);
        tv_zongjia = (TextView) xlisttop.findViewById(R.id.tv_zongjia);
        tv_quyu = (TextView) xlisttop.findViewById(R.id.tv_quyu);
        tv_mianji = (TextView) xlisttop.findViewById(R.id.tv_mianji);
        tv_zhuangtai = (TextView) xlisttop.findViewById(R.id.tv_zhuangtai);
        tv_danjia = (TextView) xlisttop.findViewById(R.id.tv_danjia);
        tv_zhuanghuang = (TextView) xlisttop.findViewById(R.id.tv_zhuanghuang);
        tv_huxing = (TextView) xlisttop.findViewById(R.id.tv_huxing);
        tv_louxing = (TextView) xlisttop.findViewById(R.id.tv_louxing);
        tv_louceng = (TextView) xlisttop.findViewById(R.id.tv_louceng);
        tv_niandai = (TextView) xlisttop.findViewById(R.id.tv_niandai);
        tv_fangzhu = (TextView) xlisttop.findViewById(R.id.tv_fangzhu);
        tv_dianhua = (TextView) xlisttop.findViewById(R.id.tv_dianhua);
        tv_dizhi = (TextView) xlisttop.findViewById(R.id.tv_dizhi);
        tv_fanghao = (TextView) xlisttop.findViewById(R.id.tv_fanghao);
        tv_beizhu = (TextView) xlisttop.findViewById(R.id.tv_beizhu);
        tv_jiage_for_rent = (TextView) xlisttop.findViewById(R.id.tv_jiage_for_rent);
        btn_gj = (Button) xlisttop.findViewById(R.id.btn_gj);
        btn_collect = (Button) xlisttop.findViewById(R.id.btn_collect);
//        btn_genjin = (Button) findViewById(R.id.btn_genjin);
        btn_call = (Button) xlisttop.findViewById(R.id.btn_call);
        if (flag) {
            btn_collect.setText("取消收藏");
        }

        browser();

//        Intent intent = new Intent(context, DetailsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("item", info);
//        intent.putExtras(bundle);


        gson = new Gson();
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);

        xListView.addHeaderView(xlisttop,null,false);
        adater = new GenjinAdater(context, list);
        xListView.setAdapter(adater);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        getData(page + "");
        isCollect();

        if (!checkPermissionGranted(Manifest.permission.CALL_PHONE )) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }

    @Override
    public void initLinstener() {
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivityAnimation();
            }
        });
        //收藏
        btn_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MApplication.getMApplication().getIsLoginFlag()) {
                    if (flag) {
                        unCollect();
                    } else {
                        collect();
                    }
                }
            }
        });
        //跟进
        btn_gj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GenjinActivity.class);
                intent.putExtra("id", info.getId());
                startActivityForResult(intent, 1);
            }
        });
        //跟进列表
//        btn_genjin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, GenjinListActivity.class);
//                intent.putExtra("id", info.getId());
//                startActivity(intent);
//            }
//        });
        //拨打电话
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = tv_dianhua.getText().toString().trim();
                if (phone != null && phone.length() >= 11) {
                    phone = phone.substring(0, 11);
                    alert_dialog(phone, Intent.ACTION_CALL); //直接拨打
//                    call(phone, Intent.ACTION_DIAL);
                } else {
                    ToastUtils.showToast(context, "电话号码格式不正确 - -");
                }
            }
        });
    }

    private void call(String phone, String action) {
        Intent intent = new Intent(action, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void alert_dialog(final String phone, final String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认拨打此电话吗?")
                .setTitle("提示")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        call(phone, action);
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void initData() {
        tv_title.setText(info.getHousename());
        tv_create_time.setText("发布时间 " + info.getHouseCreateTime());
        tv_bianhao.setText(info.getId());
        tv_quyu.setText(info.getHouseZone());
        tv_mianji.setText(info.getHouseMianji() + "㎡");
        //状态
        switch (info.getHouse()) {
            case "1":
                tv_zhuangtai.setText("出售");
                break;
            case "2":
                tv_zhuangtai.setText("出租");
                break;
            case "3":
                tv_zhuangtai.setText("已售");
                break;
            case "4":
                tv_zhuangtai.setText("停售");
                break;
            case "5":
                tv_zhuangtai.setText("已租");
                break;
            default:
                tv_zhuangtai.setText("未知");
                break;
        }
        if (info.getHouse().equals("1")) { //出售
            tv_danjia.setText((int) (Double.parseDouble(info.getHouseSoujia()) * 10000 / Double.parseDouble(info.getHouseMianji())) + "元");
            tv_zongjia.setText(info.getHouseSoujia() + info.getHouseZhuJiaType());
        } else { //出租
//            ll_zongjia.setVisibility(View.INVISIBLE);
            tv_jiage_for_rent.setText("价格");
            tv_zongjia.setText(info.getHouseZhujia() + info.getHouseZhuJiaType());
            ll_danjia.setVisibility(View.INVISIBLE);
        }
        tv_zhuanghuang.setText(info.getHouseZhuangxiu());
        tv_huxing.setText(info.getHousehuxing());
        tv_louxing.setText(info.getHouseType());

        tv_niandai.setText(info.getHouseYear() + "年");
        if (MApplication.getMApplication().getIsLoginFlag()&&MApplication.getMApplication().getIsVIPFlag()) {
            tv_fangzhu.setText(info.getHouseLianxiren());
            tv_dianhua.setText(info.getHouseLianxifangshi());
            tv_louceng.setText(info.getHouseCen() + "/" + info.getHouseCen2());
            tv_fanghao.setText(info.getHouseZD() + "#" + info.getHouseFangHao());
        } else {
            tv_fangzhu.setText("***");
            if (info.getQq() != null) {
                tv_dianhua.setText("***(" + info.getQq() + ")");
            } else {
                tv_dianhua.setText("***");
            }
            tv_louceng.setText("*/**");
            tv_fanghao.setText("***");
            btn_collect.setVisibility(View.GONE);
            btn_gj.setVisibility(View.GONE);
            //btn_genjin.setVisibility(View.GONE);
            btn_call.setVisibility(View.GONE);
        }
        tv_dizhi.setText(info.getHouseAddr());
        tv_beizhu.setText(info.getHouseBeizhu());
    }

    //收藏
    private void collect() {
        String url = HttpUrls.HOUSE_COLLECT;
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", info.getId());
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("已收藏成功：" + responseInfo.result);
                ToastUtils.showToast(context, "收藏成功");
                flag=true;
                btn_collect.setText("取消收藏");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("收藏失败" + s);
            }
        });
    }

    //取消收藏
    private void unCollect() {
        String url = HttpUrls.HOUSE_UNCOLLECT;
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", info.getId());
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("取消收藏成功：" + responseInfo.result);
                ToastUtils.showToast(context, "取消收藏成功");
                flag=false;
                btn_collect.setText("收藏");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("取消收藏失败");
            }
        });
    }

    //收藏
    private void isCollect() {
        String url = HttpUrls.HOUSE_ISCOLLECT;
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", info.getId());
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if (mMstatus.getStatus().equals("1"))
                {
                    flag=true;
                    btn_collect.setText("取消收藏");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("收藏失败" + s);
            }
        });
    }

    //收藏
    private void browser() {
        if (info.getHouse().equals("2") || info.getHouse().equals("5")) {
            SqliteBrowser.dataAdd(this, "rent" + info.getId());

        } else {
            SqliteBrowser.dataAdd(this, "sell" + info.getId());
        }
        String url = HttpUrls.HOUSE_BROWSER_ADD;
        RequestParams params = new RequestParams();
        params.addBodyParameter("id", info.getId());
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }


    private void getData (final String page) {
        String url = HttpUrls.HOUSE_GENJIN_LIST;
        id=info.getId();
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

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(DateUtil.getNowTime("hh:mm:ss"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 11) {
        }
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
}
