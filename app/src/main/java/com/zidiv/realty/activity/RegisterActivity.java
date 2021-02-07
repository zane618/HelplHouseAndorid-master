package com.zidiv.realty.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.MStatus;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.R;
import com.zidiv.realty.util.EPutil;
import com.zidiv.realty.util.T;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 注册界面
 * Created by Administrator on 2016/3/14.
 */
public class RegisterActivity extends BaseActivity {
    private Context context;
    private ImageView iv_back;
    private EditText txt_name, txt_phone, txt_pwd, txt_keycode;
    private Button btn_register, btn_keycode;
    private CheckBox checkBox;

    private int second = 60;
    Timer timer;
    TimerTask timerTask;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        //返回
        iv_back = (ImageView) findViewById(R.id.back_image);
        //姓名
        txt_name = (EditText) findViewById(R.id.txt_name);
        //密码
        txt_pwd = (EditText) findViewById(R.id.txt_pwd);
        //验证码
        txt_keycode = (EditText) findViewById(R.id.txt_keycode);
        //手机号
        txt_phone = (EditText) findViewById(R.id.txt_phone);
        //获取验证码
        btn_keycode = (Button) findViewById(R.id.btn_keycode);
        //注册
        btn_register = (Button) findViewById(R.id.btn_register);
        checkBox = findViewById(R.id.chk_agree);

        //注册
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    T.showShort(context, "请您先阅读用户服务协议并同意协议内容");
                    return;
                }
                if (TextUtils.isEmpty(txt_phone.getText().toString().trim())) {
                    T.showShort(context, "手机号码不能为空");
                    return;
                }
                if (!EPutil.isMobile(txt_phone.getText().toString().trim())) {
                    T.showShort(context, "请输入11位手机号码");
                    return;
                }
                if (TextUtils.isEmpty(txt_pwd.getText().toString().trim())) {
                    T.showShort(context, "请输入密码");
                    return;
                }
                String realName = txt_name.getText().toString().trim();
                if (TextUtils.isEmpty(realName)|| (!checkname(realName)) || (realName.length() > 4 || realName.length() < 2)) {
                    T.showShort(context, "请输入2到4位中文姓名");
                    return;
                }


                //保存注册信息
                String url = HttpUrls.REGISTER_URL;
                RequestParams params = new RequestParams();
                params.addBodyParameter("name", realName);
                params.addBodyParameter("loginpwd", txt_pwd.getText().toString().trim());
                params.addBodyParameter("key", txt_keycode.getText().toString().trim());
                params.addBodyParameter("phone", txt_phone.getText().toString().trim());

                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String json = responseInfo.result;
                        Gson gson = new Gson();
                        MStatus mStatus = gson.fromJson(json, MStatus.class);
                        //保存成功
                        if (mStatus.getStatus().equals("1")) {
                            T.showShort(context, "注册信息成功");
                            exitActivityAnimation();//关闭activity
                        } else {
                            T.showShort(context, "" + mStatus.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        T.showShort(context, "网路异常，请检查网络设置");
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

        //获取验证码
        btn_keycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txt_phone.getText().toString().trim())) {
                    T.showShort(context, "手机号码不能为空");
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
                timer.schedule(timerTask, 0, 1000);

                String url = HttpUrls.GETKEY_URL + txt_phone.getText().toString().trim();
                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        String json = stringResponseInfo.result;
                        Gson gson = new Gson();
                        MStatus mStatus = gson.fromJson(json, MStatus.class);
                        //若获取验证码成功
                        if (mStatus.getStatus().equals("1")) {
                            T.showShort(context, "验证码已发送");

                        } else {
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            if (timerTask != null) {
                                timerTask.cancel();
                                timerTask = null;
                            }
                            btn_keycode.setText("获取验证码");
                            btn_keycode.setEnabled(true);
                            second = 60;
                            T.showShort(context, "此手机号已存在");
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (timerTask != null) {
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
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                second--;
                btn_keycode.setText("剩余" + second + "s");
                if (second == 0) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (timerTask != null) {
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
    public void initLinstener() {
    }

    @Override
    public void initData() {
        ((TextView) findViewById(R.id.title_text)).setText("注册");
        String str_Temp = "我已阅读,并同意<font color='#09A7F0'>《用户服务协议》</font>";
        TextView tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_protocol.setText(Html.fromHtml(str_Temp));
        tv_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentone = new Intent(context, AdDetailActivity.class);
//                intentone.putExtra("title", "用户服务协议");
//                intentone.putExtra("src", "http://www.80mf.com/admin/notlogin.htm");
                intentone.putExtra("title", "隐私政策");
                intentone.putExtra("src", "http://www.80mf.com/admin/snotlogin.htm");
                startActivity(intentone);
            }
        });

    }

    /**
     * 判断是否为汉字
     * @param name
     * @return
     */
    private boolean checkname(String name)
    {
        int n = 0;
        for(int i = 0; i < name.length(); i++) {
            n = (int)name.charAt(i);
            if(!(19968 <= n && n <40869)) {
                return false;
            }
        }
        return true;
    }
}
