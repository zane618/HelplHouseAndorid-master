package com.zidiv.realty.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zidiv.realty.R;
import com.zidiv.realty.activity.AdDetailActivity;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.util.SPUtils;
import com.zidiv.realty.util.Util;

/**
 * create by zhangshi on 2019/10/25.
 * 隐私弹框
 */
public class AgreementDialog extends Dialog {

    private View view;
    private Context mContext;
    private int width, height; //宽高值
    private static final float DIALOG_WIDTH_PER = 0.75f; //宽度比

    private TextView tvLook;
    private IListener listener;

    public interface IListener {
        void onOk();

        void onCancel();
    }

    public AgreementDialog setListener(IListener listener) {
        this.listener = listener;
        return this;
    }

    public AgreementDialog(@NonNull Context context) {
        super(context, R.style.Theme_dialog);
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_agreement, null);
        tvLook = view.findViewById(R.id.tv_look);

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("《隐私政策》");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intentone = new Intent(mContext, AdDetailActivity.class);
//                intentone.putExtra("title", "用户服务协议");
//                intentone.putExtra("src", "http://www.80mf.com/admin/notlogin.htm");
                intentone.putExtra("title", "隐私政策");
                intentone.putExtra("src", "http://www.80mf.com/admin/snotlogin.htm");
                mContext.startActivity(intentone);
            }
        };
        ssb.setSpan(clickableSpan, 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#D84B4C"));
        ssb.setSpan(foregroundColorSpan, 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tvLook.setMovementMethod(LinkMovementMethod.getInstance());
        tvLook.setText(ssb);
        view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.put("APP", MApplication.getMApplication(), "firstInto", false);
                dismiss();
                if (listener != null) {
                    listener.onOk();
                }
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        Window window = getWindow();
        if (null != window) {
            /*window.setWindowAnimations();*/
            WindowManager.LayoutParams params = window.getAttributes();
            width = (int) (Util.getWidthPixels((Activity) mContext) * DIALOG_WIDTH_PER);
            height = (Util.getHeightPixels((Activity) mContext) - Util.getBottomStatusHeight(mContext)) / 5 * 3;
            params.width = width;
//            params.height = height;
            window.setAttributes(params);
        }
    }

}
