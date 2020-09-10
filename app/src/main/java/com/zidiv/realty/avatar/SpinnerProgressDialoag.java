package com.zidiv.realty.avatar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.zidiv.realty.R;


/**
 * Created by yinwei on 2015-09-23.
 */
public class SpinnerProgressDialoag extends ProgressDialog {


    public SpinnerProgressDialoag(Context context, int theme) {
        super(context, theme);

    }

    public SpinnerProgressDialoag(Context context) {
        this(context, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressdialoag);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}
