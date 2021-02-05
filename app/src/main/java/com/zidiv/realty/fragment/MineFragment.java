package com.zidiv.realty.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseFragment;
import com.zidiv.realty.MainFragmentActivity;
import com.zidiv.realty.R;
import com.zidiv.realty.activity.AboutActivity;
import com.zidiv.realty.activity.AdDetailActivity;
import com.zidiv.realty.activity.PushActivity;
import com.zidiv.realty.activity.RechargeActivity;
import com.zidiv.realty.activity.TrueAuthorActivity;
import com.zidiv.realty.activity.UserInfoActivity;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.avatar.ClippingPageActivity;
import com.zidiv.realty.avatar.ConstantSet;
import com.zidiv.realty.database.SqliteLogin;
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
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2016/3/17.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {
    private FragmentActivity context;
    private ImageView img_avatar; //头像
    private TextView tv_nickname, tv_phonenum, tv_date, tv_auth; //昵称和手机号
    private RelativeLayout my_info;//个人资料
    private RelativeLayout my_loginout;//退出
    private RelativeLayout my_collect;//我的收藏
    private RelativeLayout my_aboutus;//关于我们
    private RelativeLayout my_calculate;//房贷计算器
    private RelativeLayout my_push;//推送
    private RelativeLayout my_price;//充值
    private AlertDialog alertDialog;
    private final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;

    File file;
    Uri imageUri;
    private static final String IMAGE_FILE_LOCATION = ConstantSet.LOCALFILE;//临时文件

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        context = getActivity();
        initData();

        //找控件
        img_avatar = (ImageView) view.findViewById(R.id.head_custom_ima);
        tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
        tv_phonenum = (TextView) view.findViewById(R.id.tv_phonenum);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_auth = (TextView) view.findViewById(R.id.tv_auth);
        //加载头像 昵称 手机号码
        String avatarUrl = MApplication.getMApplication().getUserAvatar();
        if (!TextUtils.isEmpty(avatarUrl)) {
            BitmapUtils bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.avatar);
            bitmapUtils.display(img_avatar, avatarUrl);
        }
        tv_nickname.setText(MApplication.getMApplication().getUserNickName());
        tv_phonenum.setText(MApplication.getMApplication().getPhoto());

        //是否实名认证
        String strPass = MApplication.getMApplication().getUserPass();
        switch (strPass) {
            case "3":
                tv_auth.setText("待审核");
                tv_auth.setTextColor(Color.YELLOW);
                break;
            case "4":
                tv_auth.setText("已认证");
                tv_auth.setTextColor(Color.WHITE);
                break;
            case "5":
                tv_auth.setText("未通过");
                break;
            default:
                tv_auth.setText("未认证");
                break;
        }

        getUserInfo();

        my_info = (RelativeLayout) view.findViewById(R.id.my_info);
        my_collect = (RelativeLayout) view.findViewById(R.id.my_collect);
        my_calculate = (RelativeLayout) view.findViewById(R.id.my_calculate);
        my_aboutus = (RelativeLayout) view.findViewById(R.id.my_aboutus);
        my_price = (RelativeLayout) view.findViewById(R.id.my_price);
        my_push = (RelativeLayout) view.findViewById(R.id.my_push);
        my_loginout = (RelativeLayout) view.findViewById(R.id.my_loginout);

        //点击事件
        my_info.setOnClickListener(this);
        my_collect.setOnClickListener(this);
        my_calculate.setOnClickListener(this);
        my_aboutus.setOnClickListener(this);
        my_price.setOnClickListener(this);
        my_push.setOnClickListener(this);
        my_loginout.setOnClickListener(this);
        img_avatar.setOnClickListener(this);
    }


    public boolean checkPermissionGranted(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager pkgMgr = context.getPackageManager();
            return pkgMgr.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
    }




    //获取用户资料
    private void getUserInfo() {
        String url = HttpUrls.GETINFO_URL;
        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                try {
                    L.e("获取用户资料：" + stringResponseInfo.result);
                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                    JSONArray jsonArray = jsonObject.getJSONArray("ds");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    //及时保存用户资料到SP
                    SPUtils.put("SPUserInfo", context, "user_name", obj.getString("trueName"));
                    SPUtils.put("SPUserInfo", context, "phone", obj.getString("phone"));
                    SPUtils.put("SPUserInfo", context, "user_avatar", obj.getString("avatar"));
                    SPUtils.put("SPUserInfo", context, "CardPass", obj.getString("CardPass"));
                    tv_date.setText(obj.getString("EndTime"));

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_info:
                //startActivityForResult(new Intent(context, UserInfoActivity.class),0);
                startActivity(new Intent(context, UserInfoActivity.class));
                getActivity().finish();
                break;
            case R.id.my_collect:
                ((MainFragmentActivity) context).setCurrentItem(2);
                break;
            case R.id.my_calculate:
                Intent intent = new Intent(context, AdDetailActivity.class);
                intent.putExtra("title", "计算器");
                intent.putExtra("src", "http://m.db.house.qq.com/index.php?mod=calculator&type=sd&rf=");
                startActivity(intent);
                break;
            case R.id.my_aboutus:
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.my_price:
                startActivity(new Intent(context, RechargeActivity.class));
                break;
            case R.id.my_push:
                startActivity(new Intent(context, PushActivity.class));
                break;
            case R.id.my_loginout:
                loginOut();
                break;
            case R.id.txt_true_author:
                startActivity(new Intent(context, TrueAuthorActivity.class));
                alertDialog.dismiss();
                break;
            case R.id.head_custom_ima:
//                if (!checkPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    }
//                }
//                if (!checkPermissionGranted(Manifest.permission.CAMERA)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
//                    }
//                }
                if ("19966175125".equals(MApplication.getMApplication().getPhoto())) {
                    return;
                }
                dialogShow();

                break;
            case R.id.Photograph: //拍照
                try{
                    List<String> listPermission = new ArrayList<>();
                    listPermission.add(Manifest.permission.CAMERA);
                    listPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    for (String strPermisssion : listPermission) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, strPermisssion);
                            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{strPermisssion}, 222);
                            }
                        }
                    }
                    Intent intentc = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentc.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intentc, ConstantSet.TAKEPICTURE);
                    alertDialog.dismiss();
                }catch (Exception e)
                {
                    L.e("",e.getMessage());
                }
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

    private void dialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View vUpload = getLayoutInflater().from(context).inflate(R.layout.upload_user_pic, null);
        TextView photograph = (TextView) vUpload.findViewById(R.id.Photograph);
        TextView selectPic = (TextView) vUpload.findViewById(R.id.selectImage_from_local);
        TextView dimissDialoag = (TextView) vUpload.findViewById(R.id.dimiss_dialoag);
        TextView txttrueauthor=(TextView)vUpload.findViewById(R.id.txt_true_author);
        builder.setView(vUpload);
        alertDialog = builder.show();
        //  MyDialogListener myDialogListener = new MyDialogListener(alertDialog);
        photograph.setOnClickListener(this);
        selectPic.setOnClickListener(this);

        dimissDialoag.setOnClickListener(this);
        //是否实名认证
        String strPass = MApplication.getMApplication().getUserPass();
        switch (strPass) {
            case "3":
                txttrueauthor.setText("待审核");
                txttrueauthor.setVisibility(View.VISIBLE);
                break;
            case "4":
                break;
            case "5":
                txttrueauthor.setVisibility(View.VISIBLE);
                txttrueauthor.setOnClickListener(this);
                break;
            default:
                txttrueauthor.setVisibility(View.VISIBLE);
                txttrueauthor.setOnClickListener(this);
                break;
        }
    }


    private Bitmap bitmap = null;
    private ProgressDialog progressDialog;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1001: //UserInfoActivity
                startActivity(new Intent(context, MainFragmentActivity.class));
                getActivity().finish();
                break;
            case RESULT_OK: //修改头像
                switch (requestCode) {
                    case ConstantSet.TAKEPICTURE:   //拍照返回
                        Intent tCutIntent = new Intent(context, ClippingPageActivity.class);
                        tCutIntent.putExtra("type", "takePicture");
                        startActivityForResult(tCutIntent, ConstantSet.CROPPICTURE);
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
                            L.e("在保存头像图片到sd卡时出错", e.toString());
                        }
                        //保存之后上传
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("正在上传头像");
                        String url = HttpUrls.AVATAR_URL;
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("zidiv", f);
                        MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                                    if (jsonObject.getString("Status").equals("1")) {

                                        T.showShort(context, "头像修改成功");
                                        JSONArray jsonArray = jsonObject.getJSONArray("ds");
                                        JSONObject obj = jsonArray.getJSONObject(0);
                                        String imgName = obj.getString("imgName ");
                                        //及时保存头像地址到SPUserInfo
                                        SPUtils.put("SPUserInfo", context, "user_avatar", HttpUrls.SERVER_URL + "//Upload/Avatar/" + imgName);
                                        //跳回主界面
                                        Intent intent = new Intent(context, MainFragmentActivity.class);
                                        intent.putExtra("index", 3);
                                        startActivity(intent);
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }
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
//
        }
    }


    public void initData() {
        file = new File(IMAGE_FILE_LOCATION);
        if (!file.exists()) {
            SDCardUtils.makeRootDirectory(IMAGE_FILE_LOCATION);
        }
        file = new File(IMAGE_FILE_LOCATION + ConstantSet.USERTEMPPIC);
        imageUri = Uri.fromFile(file);
    }


//    //头像dialog事件监听
//    private class MyDialogListener implements View.OnClickListener {
//        private AlertDialog alertDialog;
//
//        public MyDialogListener(AlertDialog alertDialog) {
//            this.alertDialog = alertDialog;
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//
//            }
//        }
//    }

    //退出
    private void loginOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否确认退出登录？").setIcon(R.mipmap.ic_launcher);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SqliteLogin.addLoginBean(MApplication.getMApplication(), MApplication.getMApplication().getLoginName(), MApplication.getMApplication().getUserPwd(), "1");
                SPUtils.put("SPLogin", MApplication.getMApplication(), "isLogin", false);
                SPUtils.put("SPLogin",  MApplication.getMApplication(), "isVIP", false);
                SPUtils.put("SPLogin",  MApplication.getMApplication(), "isTrueName", false);
                //注销接口
                MApplication.getMApplication().getHttpUtils().send(HttpRequest.HttpMethod.GET, HttpUrls.LOGINOUT_URL, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                    }
                });
                //清除SP文件
                SPUtils.clear("SPLogin", context);
                SPUtils.clear("SPUserInfo", context);
                MainFragmentActivity.instance.finish();
                startActivity(new Intent(context, MainFragmentActivity.class));
                getActivity().finish();
            }
        });
        builder.create().show();
    }
}
