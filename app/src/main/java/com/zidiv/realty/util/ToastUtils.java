package com.zidiv.realty.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zidiv.realty.R;


/**
 * Created by Administrator on 2016/1/19.
 */
public class ToastUtils {
    private static Toast mToast;

    /**
     * 显示吐司
     *
     * @param context 上下文
     * @param text    吐司文本
     */
    public static void showToast(Context context, String text) {
        //Toast要加载的view
        View view = view = View.inflate(context, R.layout.toast, null);
        TextView txt = ((TextView) view.findViewById(R.id.txt_toast));
        if (mToast == null) {
            //自定义Toast
            mToast = new Toast(context);
            txt.setText(text);
            //给Toast设置布局
            mToast.setView(view);
            mToast.setDuration(Toast.LENGTH_SHORT);
            //Toast在屏幕中间显示
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            txt.setText(text);
            mToast.setView(view);
        }
        mToast.show();
    }
}
