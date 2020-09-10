package com.zidiv.realty.activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

/**
 * Created by Administrator on 2016/3/18.
 */
public class NicknameActivity extends BaseActivity{
    private Context context = this;
    private ImageView iv_back;
    private TextView tv_title;
    private EditText et_newnickname_activitynickname;
    private Button btn_ok_activitynickname;
    private int resultCode = 20;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_nickname);
    }

    @Override
    public void getIntentData() {

    }

    @Override
    public void initView() {
        iv_back = (ImageView)findViewById(R.id.back_image);
        tv_title = (TextView)findViewById(R.id.title_text);
        tv_title.setText(getResources().getString(R.string.nickname));
        et_newnickname_activitynickname = (EditText)findViewById(R.id.et_newnickname_activitynickname);
        btn_ok_activitynickname = (Button)findViewById(R.id.btn_ok_activitynickname);
    }

    @Override
    public void initLinstener() {
        btn_ok_activitynickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_newnickname_activitynickname.getText().toString().trim())) {
                    T.showShort(context,"请输入新昵称");
                    return;
                }
                //修改昵称
                String url = HttpUrls.UPDATEINFO_URL;
                RequestParams params = new RequestParams();
                final String userName = et_newnickname_activitynickname.getText().toString().trim();
                params.addBodyParameter("user_name",userName);

                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST,url,params,new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        T.showShort(context,"成功。。。。。。。。");
                        //及时将昵称保存在SP文件中
                        SPUtils.put("SPUserInfo",context,"user_name",userName);
                        setResult(resultCode);
                        exitActivityAnimation();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        T.showShort(context,"网络问题 失败。。。。。。。。");
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
    public void initData() {

    }
}
