package com.zidiv.realty.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
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
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class TrueAuthorActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView imgv_idcard_down, imgv_idcard_up, iv_back;
    private AlertDialog alertDialog;
    private final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;
    private String strIsCardFront="1";

    File file;
    Uri imageUri;
    private static final String IMAGE_FILE_LOCATION = ConstantSet.LOCALFILE;//临时文件

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_true_author);
    }

    @Override
    public void getIntentData() {
        context = this;
    }

    @Override
    public void initView() {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("实名认证");
        iv_back = (ImageView) findViewById(R.id.back_image);
        iv_back.setVisibility(View.VISIBLE);

        imgv_idcard_up = (ImageView) findViewById(R.id.imgv_idcard_up);

        String strFront = MApplication.getMApplication().getUserFront();
        if (!TextUtils.isEmpty(strFront)) {
            BitmapUtils bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.icon_zidiv_idcard_down);
            bitmapUtils.display(imgv_idcard_up,strFront);
        }

        imgv_idcard_down = (ImageView) findViewById(R.id.imgv_idcard_down);

        String strBack = MApplication.getMApplication().getUserBack();
        if (!TextUtils.isEmpty(strBack)) {
            BitmapUtils bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.icon_zidiv_idcard_up);
            bitmapUtils.display(imgv_idcard_down,strBack);
        }

        String strPass = MApplication.getMApplication().getUserPass();
        if (strPass.equals("1")) {
            imgv_idcard_down.setOnClickListener(this);
        } else if (strPass.equals("2")) {
            imgv_idcard_up.setOnClickListener(this);
        } else {
            imgv_idcard_down.setOnClickListener(this);
            imgv_idcard_up.setOnClickListener(this);
        }
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
    public void onClick(View view) {

        if (!checkPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        if (!checkPermissionGranted(Manifest.permission.CAMERA)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        switch (view.getId()) {
            case R.id.imgv_idcard_down:
                strIsCardFront="2";
                onImageClick();
                break;
            case R.id.imgv_idcard_up:
                strIsCardFront="1";
                onImageClick();
                break;
            case R.id.Photograph: //拍照
                Intent intentc = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentc.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intentc, ConstantSet.TAKEPICTURE);
                alertDialog.dismiss();
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
                if (mSelectPath != null && mSelectPath.size() > 0) {
                    selectIntent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                }
                startActivityForResult(selectIntent, REQUEST_IMAGE);
                alertDialog.dismiss();
                break;
            case R.id.dimiss_dialoag:
                alertDialog.dismiss();
                break;
            default:
                System.out.print("NO-imageView");
                break;
        }

    }

    public  void onImageClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View vUpload = getLayoutInflater().from(context).inflate(R.layout.upload_user_pic, null);
        TextView photograph = (TextView) vUpload.findViewById(R.id.Photograph);
        TextView selectPic = (TextView) vUpload.findViewById(R.id.selectImage_from_local);
        TextView dimissDialoag = (TextView) vUpload.findViewById(R.id.dimiss_dialoag);
        builder.setView(vUpload);
        alertDialog = builder.show();
        photograph.setOnClickListener(this);
        selectPic.setOnClickListener(this);
        dimissDialoag.setOnClickListener(this);
    }


    private Bitmap bitmap = null;
    private ProgressDialog progressDialog;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1001: //UserInfoActivity
                startActivity(new Intent(context, MainFragmentActivity.class));
                finish();
                break;
            case RESULT_OK: //修改图片
                switch (requestCode) {
                    case ConstantSet.TAKEPICTURE:   //拍照返回
                        Intent tCutIntent = new Intent(context, ClippingPageActivity.class);
                        tCutIntent.putExtra("type", "takePicture");
                        startActivityForResult(tCutIntent, ConstantSet.CROPPICTURE);
                        break;
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
                        bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);

                        //对图片进行压缩，等比例压缩
                        Matrix matrix = new Matrix();
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();
                        int t = w > h ? w : h;
                        float bi = 300.f / t;
                        matrix.postScale(bi, bi); //长和宽放大缩小的比例
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
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
                            L.e("在保存图片到sd卡时出错", e.toString());
                        }
                        //保存之后上传
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("正在上传图片");
                        String url = strIsCardFront.equals("1")? HttpUrls.CARDFRONT_URL:HttpUrls.CARDBACK_URL;
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("zidiv", f);
                        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                                    if (jsonObject.getString("Status").equals("1")) {

                                        T.showShort(context, "图片上传成功");
                                        JSONArray jsonArray = jsonObject.getJSONArray("ds");
                                        JSONObject obj = jsonArray.getJSONObject(0);
                                        String imgName = obj.getString("imgName ");
                                        //及时保存图片地址到SPUserInfo
                                        if(strIsCardFront.equals("1"))
                                        {
                                            SPUtils.put("SPUserInfo", context, "user_front", HttpUrls.SERVER_URL + "//Upload/Avatar/" + imgName);
                                        }else
                                        {
                                            SPUtils.put("SPUserInfo", context, "user_back", HttpUrls.SERVER_URL + "//Upload/Avatar/" + imgName);
                                        }

//                                        //跳回主界面
//                                        Intent intent = new Intent(context, MainFragmentActivity.class);
//                                        intent.putExtra("index", 3);
//                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    L.e("解析上传图片返回数据失败" + e.toString());
                                }

                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                T.showShort(context, "上传图片失败，请检查网络");
                            }
                        });
                        break;
                }
                break;
//
        }
    }


    @Override
    public void initData() {
        file = new File(IMAGE_FILE_LOCATION);
        if (!file.exists()) {
            SDCardUtils.makeRootDirectory(IMAGE_FILE_LOCATION);
        }
        file = new File(IMAGE_FILE_LOCATION + ConstantSet.USERTEMPPIC);
        imageUri = Uri.fromFile(file);
    }

}
