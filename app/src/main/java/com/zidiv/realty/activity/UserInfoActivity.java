package com.zidiv.realty.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.avatar.ClippingPageActivity;
import com.zidiv.realty.avatar.ConstantSet;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.SDCardUtils;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Administrator on 2016/3/18.
 */
public class UserInfoActivity extends BaseActivity{
    public static UserInfoActivity instance;
    private Context context = this;
    private ImageView iv_back; //返回
    private TextView tv_title; //标题
    private RelativeLayout rl_avatar_activityuserinfo; //头像跳
    private ImageView img_avatar_activityuserinfo; //头像
    private RelativeLayout rl_nickname_activityuserinfo; //昵称跳
    private TextView txt_nickname_activityuserinfo; //昵称
    private RelativeLayout rl_phonenumber_activityuserinfo; //手机号码跳
    private TextView txt_phonenumber_activityuserinfo; //手机号码
    private RelativeLayout rl_changepwd_activityuserinfo; //修改密码跳

    //2016 07 11
    private TextView tv_zhucetime, tv_daoqitime;
    private final int maxNum = 1;
    private final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;

    File file;
    Uri imageUri;
    private static final String IMAGE_FILE_LOCATION = ConstantSet.LOCALFILE;//临时文件
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_user_info);
    }

    @Override
    public void getIntentData() {
        instance = this;
    }

    @Override
    public void initView() {

        if (checkPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {

            }
        }

        iv_back = (ImageView)findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);
        tv_title = (TextView)findViewById(R.id.title_text);
        tv_title.setText(getResources().getString(R.string.grzl));
        rl_avatar_activityuserinfo = (RelativeLayout)findViewById(R.id.rl_avatar_activityuserinfo);
        rl_nickname_activityuserinfo = (RelativeLayout)findViewById(R.id.rl_nickname_activityuserinfo);
        rl_phonenumber_activityuserinfo = (RelativeLayout)findViewById(R.id.rl_phonenumber_activityuserinfo);
        rl_changepwd_activityuserinfo = (RelativeLayout)findViewById(R.id.rl_changepwd_activityuserinfo);
        img_avatar_activityuserinfo = (ImageView)findViewById(R.id.img_avatar_activityuserinfo);
        tv_zhucetime = (TextView) findViewById(R.id.tv_zhucetime);
        tv_daoqitime = (TextView) findViewById(R.id.tv_daoqitime);
        //加载头像
        String imageUrl = (String)SPUtils.get("SPUserInfo", context, "user_avatar", "");
        if(!imageUrl.equals("")) {
            MApplication.getMApplication().getBitmapUtils().display(img_avatar_activityuserinfo, imageUrl);
        }
        txt_nickname_activityuserinfo = (TextView)findViewById(R.id.txt_nickname_activityuserinfo);
        txt_phonenumber_activityuserinfo = (TextView)findViewById(R.id.txt_phonenumber_activityuserinfo);
        txt_phonenumber_activityuserinfo.setText(MApplication.getMApplication().getPhoto());
        txt_nickname_activityuserinfo.setText(MApplication.getMApplication().getUserNickName());
        getAccountInfo();
    }

    //获取用户资料
    private void getAccountInfo(){
        String url = HttpUrls.GETINFO_URL;
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET,url,new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                try {
                    L.e("获取用户资料：" + stringResponseInfo.result);
                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ds");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    //及时保存用户资料到SP
                    SPUtils.put("SPUserInfo",context,"user_name",obj.getString("trueName"));
                    SPUtils.put("SPUserInfo",context,"phone",obj.getString("phone"));
                    SPUtils.put("SPUserInfo",context,"user_avatar",obj.getString("avatar"));
                    SPUtils.put("SPUserInfo",context,"CardPass",obj.getString("CardPass"));
                    tv_zhucetime.setText(obj.getString("CreatedTime"));
                    tv_daoqitime.setText(obj.getString("EndTime"));

                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("获取个人资料失败");
            }
        });
    }

    @Override
    public void initLinstener() {
        if ("19966175125".equals(MApplication.getMApplication().getPhoto())) {
            return;
        }
        //修改头像
        rl_avatar_activityuserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = getLayoutInflater().from(context).inflate(R.layout.upload_user_pic,null);
                TextView photograph = (TextView) view.findViewById(R.id.Photograph);
                TextView selectPic = (TextView) view.findViewById(R.id.selectImage_from_local);
                TextView dimissDialoag = (TextView) view.findViewById(R.id.dimiss_dialoag);
                builder.setView(view);
                AlertDialog alertDialog = builder.show();
                MyDialogListener myDialogListener = new MyDialogListener(alertDialog);
                photograph.setOnClickListener(myDialogListener);
                selectPic.setOnClickListener(myDialogListener);
                dimissDialoag.setOnClickListener(myDialogListener);
            }
        });
        //修改昵称
        rl_nickname_activityuserinfo.setOnClickListener(new View.OnClickListener() { //昵称跳
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(context, NicknameActivity.class), 0);
            }
        });
        //修改密码
        rl_changepwd_activityuserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 用startforresult 如果修改成功就让本界面关闭
                startActivity(new Intent(context, ChangePwdActivity.class));
            }
        });
        //返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toBack();
            }
        });
    }

    @Override
    public void initData() {
        file = new File(IMAGE_FILE_LOCATION);
        if(!file.exists()) {
            SDCardUtils.makeRootDirectory(IMAGE_FILE_LOCATION);
        }
        file = new File(IMAGE_FILE_LOCATION + ConstantSet.USERTEMPPIC);
        imageUri = Uri.fromFile(file);
    }

    private Bitmap bitmap = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 20: //昵称
                T.showShort(context, "修改成功");
                txt_nickname_activityuserinfo.setText(MApplication.getMApplication().getUserNickName());
                break;
            case RESULT_OK: //修改头像
                switch (requestCode) {
                    case ConstantSet.TAKEPICTURE:   //拍照返回
                        Intent tCutIntent = new Intent(context, ClippingPageActivity.class);
                        tCutIntent.putExtra("type","takePicture");
                        startActivityForResult(tCutIntent,ConstantSet.CROPPICTURE);
                        break;
//                    case ConstantSet.SELECTPICTURE:
//                        break;
                    case REQUEST_IMAGE:
                        Intent sCuIntent = new Intent(context, ClippingPageActivity.class);
                        sCuIntent.putExtra("type", "selectPicture");
                        mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        sCuIntent.putExtra("path", mSelectPath.get(0));
                        L.e("选择相片的路径。。。。。。。。。" + mSelectPath.get(0));
                        startActivityForResult(sCuIntent, ConstantSet.CROPPICTURE);
                        break;

                    case ConstantSet.CROPPICTURE:
                        byte[] bis = data.getByteArrayExtra("result");
                        bitmap = BitmapFactory.decodeByteArray(bis,0,bis.length);

                        //对图片进行压缩，等比例压缩
                        Matrix matrix = new Matrix();
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();
                        int t = w > h ? w : h;
                        float bi = 300.f / t;
                        matrix.postScale(bi, bi); //长和宽放大缩小的比例
                        bitmap = Bitmap.createBitmap(bitmap, 0 , 0 ,bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                        //经过上面的压缩之后，保存到sd卡
                        File f = new File("/sdcard/" + "tx" + 0 + ".png");
                        try {
                            f.createNewFile();
                            FileOutputStream fos = null;
                            fos = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            L.e("在保存头像图片到sd卡时出错",e.toString());
                        }
                        //保存之后上传
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("正在上传头像");
                        String url = HttpUrls.AVATAR_URL;
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("zidiv",f);
                        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                                    if(jsonObject.getString("Status").equals("1")) {
                                        finish();
                                        T.showShort(context, "头像修改成功");
                                        JSONArray jsonArray = jsonObject.getJSONArray("ds");
                                        JSONObject obj = jsonArray.getJSONObject(0);
                                        String imgName = obj.getString("imgName ");
                                        //及时保存头像地址到SPUserInfo
                                        SPUtils.put("SPUserInfo",context,"user_avatar",HttpUrls.SERVER_URL +  "//Upload/Avatar/" + imgName);
                                        //跳回主界面
                                        Intent intent = new Intent(context, MainFragmentActivity.class);
                                        intent.putExtra("index", 3);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    L.e("解析上传头像返回数据失败" + e.toString());
                                }

                            }

                            @Override
                            public void onStart() {
                                super.onStart();

                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                T.showShort(context, "上传头像失败，请检查网络");
                            }
                        });
                        break;
                }
                break;
//            case
        }
    }
    //头像dialog事件监听
    private class MyDialogListener implements View.OnClickListener {
        private AlertDialog alertDialog;
        public MyDialogListener (AlertDialog alertDialog) {
            this.alertDialog = alertDialog;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Photograph: //拍照
                    alertDialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent,ConstantSet.TAKEPICTURE);
                    break;
                case R.id.selectImage_from_local:
                    Intent selectIntent = new Intent(context, MultiImageSelectorActivity.class);
                    //是否显示调用相机
                    selectIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                    //设置图片选择数量为1
                    selectIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                    // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                    selectIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                    //默认选择图片，回填选项（支持String List)
                    if(mSelectPath != null && mSelectPath.size() > 0){
                        selectIntent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                    }
                    startActivityForResult(selectIntent, REQUEST_IMAGE);
                    alertDialog.dismiss();
                    break;
                case R.id.dimiss_dialoag:
                    alertDialog.dismiss();
                    break;
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK) {
            toBack();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void toBack() {
        Intent intent = new Intent(context, MainFragmentActivity.class);
        intent.putExtra("index",3);
        startActivity(intent);
        finish();
        UserInfoActivity.this.overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right); //打开Activity动画
    }
}
