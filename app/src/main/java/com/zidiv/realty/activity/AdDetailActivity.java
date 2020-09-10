package com.zidiv.realty.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.R;

public class AdDetailActivity extends BaseActivity {
    private Context context = this;
    //    private ImageView iv_back;
//    private TextView tv_title;
    private WebView wv_show;
    private WebSettings settings;
    private String src;//网址
    private String title = "详情信息";//网址

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_ad_detail);
    }

    @Override
    public void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            src = intent.getStringExtra("src");
            title = intent.getStringExtra("title");
        }
    }

    @Override
    public void initView() {
        //头布局
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText(title);
        if (title.equals("计算器")) {
            LinearLayout linearLayoutHeader= (LinearLayout) findViewById(R.id.header);
            linearLayoutHeader.setVisibility(View.GONE);
        }

        ImageView iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivityAnimation();
            }
        });
        //webview
        wv_show = ((WebView) findViewById(R.id.wv_show_activityaddetail));
        settings = wv_show.getSettings();
        //设置支持缩放
        settings.setBuiltInZoomControls(true);

        settings.setJavaScriptEnabled(true);  //支持js


        settings.setUseWideViewPort(false);  //将图片调整到适合webview的大小

        settings.setSupportZoom(true);  //支持缩放

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局

        settings.supportMultipleWindows();  //多窗口

        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存

        settings.setAllowFileAccess(true);  //设置可以访问文件

        settings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点


        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片


        // 点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖
        // webview的WebViewClient对象
        wv_show.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
//                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        dialog = new ProgressDialog(context);
        dialog.setMessage("数据加载中...");
        dialog.show();
        wv_show.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    dialog.dismiss();
                }
            }
        });
        wv_show.loadUrl(src);
    }


    @Override
    public void initLinstener() {

    }

    @Override
    public void initData() {

    }
}
