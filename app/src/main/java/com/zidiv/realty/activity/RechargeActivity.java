package com.zidiv.realty.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.content.pm.PackageManager;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.BannerImageList;
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.bean.PriceList;
import com.zidiv.realty.customview.DownloadImageTask;
import com.zidiv.realty.customview.MDialog;
import com.zidiv.realty.customview.PayResult;
import com.zidiv.realty.database.SqliteLogin;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;
import com.zidiv.realty.util.ToastUtils;
import com.zidiv.realty.customview.DES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tencent.mm.opensdk.openapi.IWXAPI;

import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView iv_back, img_advert;
    private Gson gson;
    private List<PriceList.PriceInfo> priceInfoList = new ArrayList<>();
    private RadioButton rdo_one, rdo_two, rdo_three;
    private RadioButton rdo_alipay, rdo_wechatpay;
    private Button btn_pay;
    private TextView txt_totalprice;
    private String priceid = "1";
    private List<BannerImageList.BannerImageInfo> bannerImageInfoList = new ArrayList<>();


    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_recharge);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("充值续费");
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);
        getData();
        getImageData();
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

    private void initTheView() {

        rdo_one = (RadioButton) findViewById(R.id.rdo_one);
        rdo_one.setText(priceInfoList.get(0).getPriceName());
        rdo_one.setOnClickListener(this);

        rdo_two = (RadioButton) findViewById(R.id.rdo_two);
        rdo_two.setText(priceInfoList.get(1).getPriceName());
        rdo_two.setOnClickListener(this);

        rdo_three = (RadioButton) findViewById(R.id.rdo_three);
        rdo_three.setText(priceInfoList.get(2).getPriceName());
        rdo_three.setOnClickListener(this);

        rdo_alipay = (RadioButton) findViewById(R.id.rdo_alipay);
        rdo_alipay.setOnClickListener(this);
        rdo_wechatpay = (RadioButton) findViewById(R.id.rdo_wechatpay);
        rdo_wechatpay.setOnClickListener(this);

        txt_totalprice = (TextView) findViewById(R.id.txt_totalprice);
        txt_totalprice.setText("总计：" + priceInfoList.get(0).getPriceValue() + "元");

        btn_pay = (Button) findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(this);

    }

    private void initImageView() {
        img_advert = (ImageView) findViewById(R.id.img_advert);
        new DownloadImageTask(img_advert).execute(bannerImageInfoList.get(0).getImageurl());
        img_advert.setOnClickListener(this);
    }


    private void getData() {
        gson = new Gson();
        String url = HttpUrls.PRICE_URL;
        RequestParams params = new RequestParams();
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                PriceList priceList = gson.fromJson(responseInfo.result, PriceList.class);
                if (priceList.getStatus().equals("1")) {
                    priceInfoList.clear();
                    priceInfoList.addAll(priceList.getDs());
                    initTheView();
                    L.e("获取数据成功：" + responseInfo.result);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("失败");
            }
        });
    }

    private void getImageData() {
        gson = new Gson();
        String url = HttpUrls.BANNER_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("position", "1");
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                BannerImageList bannerImageList = gson.fromJson(responseInfo.result, BannerImageList.class);
                if (bannerImageList.getStatus().equals("1")) {
                    bannerImageInfoList.clear();
                    bannerImageInfoList.addAll(bannerImageList.getDs());

                    initImageView();

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rdo_one:
                priceid="1";
                txt_totalprice.setText("总计：" + priceInfoList.get(0).getPriceValue() + "元");
                break;
            case R.id.rdo_two:
                priceid="2";
                txt_totalprice.setText("总计：" + priceInfoList.get(1).getPriceValue() + "元");
                break;
            case R.id.rdo_three:
                priceid="3";
                txt_totalprice.setText("总计：" + priceInfoList.get(2).getPriceValue() + "元");
                break;
            case R.id.img_advert:
                Intent intentone = new Intent(context, AdDetailActivity.class);
                intentone.putExtra("title", "活动详情");
                intentone.putExtra("src", bannerImageInfoList.get(0).getUrl());
                startActivity(intentone);
                break;
            case R.id.btn_pay:
                btn_pay.setEnabled(false);
                if (rdo_alipay.isChecked()) {
                    payV2();
                } else {
                    toWXPay();
                }
                break;
            default:
                System.out.print("NO-imageView");
                break;

        }
    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2() {

        String url = HttpUrls.ALIPAY_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("priceid", priceid);

        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                btn_pay.setEnabled(true);
                String json = stringResponseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if (mMstatus.getStatus().equals("1")) {

                    try {
                        final String orderInfo;
                        orderInfo = DES.decrypt(mMstatus.getMessage(), "kimo2018");
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(RechargeActivity.this);
                                Map<String, String> result = alipay.payV2(orderInfo, true);
                                L.i("msp", result.toString());

                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };

                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    T.showShort(context, mMstatus.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                btn_pay.setEnabled(true);
                T.showShort(context, "网络异常，请检查网络");
            }
        });

    }

    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    ToastUtils.showToast(context, "无法获取支付宝 SDK 所需的权限, 请到系统设置开启");
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        ToastUtils.showToast(context, "无法获取支付宝 SDK 所需的权限, 请到系统设置开启");
                        return;
                    }
                }

                // 所需的权限均正常获取
                ToastUtils.showToast(context, "支付宝 SDK 所需的权限已经正常获取");

            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        ToastUtils.showToast(context, "支付成功: " + payResult);
                        Intent intenttest = new Intent(context, PayOKActivity.class);
                        intenttest.putExtra("title", "支付成功");
                        intenttest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intenttest);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        ToastUtils.showToast(context, "支付失败: " + payResult);
                        Intent intenttest = new Intent(context, PayFailActivity.class);
                        intenttest.putExtra("title", "支付失败");
                        intenttest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intenttest);
                        finish();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wx845613d85bc29e3e";
//    private IWXAPI iwxapi; //微信支付api

    /**
     * 调起微信支付的方法
     **/
    private void toWXPay() {


//        L.e("" + blt + "");
        String url = HttpUrls.WECHATPAY_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("priceid", priceid);

        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                btn_pay.setEnabled(true);
                String json = stringResponseInfo.result;
                Gson gson = new Gson();
                MStatus mMstatus = gson.fromJson(json, MStatus.class);
                if (mMstatus.getStatus().equals("1")) {

                    try {
                        final String orderInfo;
                        orderInfo = DES.decrypt(mMstatus.getMessage(), "kimo2018");
                        gson = new Gson();
                        final Map<String, String> mapOrder = gson.fromJson(orderInfo, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Runnable payRunnable = new Runnable() {  //这里注意要放在子线程
                            @Override
                            public void run() {
                                PayReq request = new PayReq(); //调起微信APP的对象
                                //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
                                request.appId = (String) mapOrder.get("appid");
                                request.partnerId = (String) mapOrder.get("partnerid");
                                request.prepayId = (String) mapOrder.get("prepayid");
                                request.packageValue =  (String) mapOrder.get("package");
                                request.nonceStr = (String) mapOrder.get("noncestr");
                                request.timeStamp = (String) mapOrder.get("timestamp");
                                request.sign = (String) mapOrder.get("sign");
                                IWXAPI iwxapi =  WXAPIFactory.createWXAPI(context, null);
                                boolean  blt=  iwxapi.registerApp("wx845613d85bc29e3e"); //注册appid  appid可以在开发平台获取
                                boolean bl = iwxapi.sendReq(request);//发送调起微信的请求
                                L.e("" + bl + "");
                            }
                        };
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    btn_pay.setEnabled(true);
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
