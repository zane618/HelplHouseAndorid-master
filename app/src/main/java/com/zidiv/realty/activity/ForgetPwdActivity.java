package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.T;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 找回密码验证页面
 * Created by Administrator on 2016/3/14.
 */
public class ForgetPwdActivity extends BaseActivity{
    private Context context;
    private ImageView iv_back;
    private TextView tv_title;
    private EditText txt_phone,txt_keycode;
    private Button btn_next,btn_keycode;

    private int second = 60;
    Timer timer;
    TimerTask timerTask;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                second --;
                btn_keycode.setText("剩余"+second+"s");
                if(second == 0){
                    if(timer !=null){
                        timer.cancel();timer = null;
                    }
                    if(timerTask !=null) {
                        timerTask.cancel();
                        timerTask = null;
                    }
                    btn_keycode.setText("获取验证码");
                    btn_keycode.setEnabled(true);
                    second = 60;
                }
            }
        }
    };
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_forget_pwd);
    }

    @Override
    public void getIntentData() {

    }

    @Override
    public void initView() {
        iv_back = (ImageView)findViewById(R.id.back_image);
        tv_title = (TextView)findViewById(R.id.title_text);
        txt_phone = (EditText)findViewById(R.id.txt_phone);
        txt_keycode = (EditText)findViewById(R.id.txt_keycode);
        btn_next = (Button)findViewById(R.id.btn_next);
        btn_keycode = (Button)findViewById(R.id.btn_keycode);
        tv_title.setText("找回密码");

        //获取验证码
        btn_keycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txt_phone.getText().toString().trim())){
                    T.showShort(context,"手机号码不能为空");
                    return;
                }

                btn_keycode.setEnabled(false);
                //表示0秒钟之后每隔1秒执行一次
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask,0,1000);

                String url = HttpUrls.GETBACKKEY_URL + txt_phone.getText().toString().trim();
                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET,url,new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        String json = stringResponseInfo.result;
                        Gson gson = new Gson();
                        MStatus mStatus = gson.fromJson(json,MStatus.class);
                        //若获取验证码成功
                        if(mStatus.getStatus().equals("1")){
                            T.showShort(context,"验证码已发送");

                        } else {
                            T.showShort(context,"此账号不存在");
                            if(timer !=null){
                                timer.cancel();timer = null;
                            }
                            if(timerTask !=null) {
                                timerTask.cancel();
                                timerTask = null;
                            }
                            btn_keycode.setText("获取验证码");
                            btn_keycode.setEnabled(true);
                            second = 60;
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        if(timer !=null){
                            timer.cancel();timer = null;
                        }
                        if(timerTask !=null) {
                            timerTask.cancel();
                            timerTask = null;
                        }
                        btn_keycode.setText("获取验证码");
                        btn_keycode.setEnabled(true);
                        second = 60;
                        T.showShort(context, "网络异常,请检查网络设置");
                    }
                });
            }
        });
        //下一步
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txt_phone.getText().toString().trim()) || TextUtils.isEmpty(txt_keycode.getText().toString().trim())){
                    T.showShort(context,"手机号码或验证码不能为空");
                    return;
                }
                //验证码 验证
                String url = HttpUrls.ISKEY_URL2 + txt_phone.getText().toString().trim() + "&key=" + txt_keycode.getText().toString().trim();
                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET,url,new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        String json = stringResponseInfo.result;
                        Gson gson = new Gson();
                        MStatus mStatus =gson.fromJson(json,MStatus.class);
                        //如果验证成功
                        if(mStatus.getStatus().equals("1")) {
                            //带参数跳转
                            Intent intent = new Intent(context,ResetPwdActivity.class);
                            intent.putExtra("phone",txt_phone.getText().toString().trim());
                            intent.putExtra("key",txt_keycode.getText().toString().trim());
                            startActivity(intent);
                            ForgetPwdActivity.this.finish();
                        } else {
                            T.showShort(context,mStatus.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        T.showShort(context, "网络异常，请检查网络");
                    }
                });
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivityAnimation();
            }
        });
    }

    @Override
    public void initLinstener() {
    }

    @Override
    public void initData() {
        context = this;
    }

}
