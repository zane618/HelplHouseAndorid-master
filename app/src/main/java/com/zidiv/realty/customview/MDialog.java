package com.zidiv.realty.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.zidiv.realty.R;

/**
 * 强迫下线 dialog
 * Created by Administrator on 2016/6/7.
 */
public class MDialog extends Dialog{
    private Context context;
    private LayoutInflater inflater;
    private TextView txt_ok;
    public MDialogDo mDialogDo;
    public interface MDialogDo {
        public void onMDialogClick();
    }

    public MDialog(Context context, MDialogDo mDialogDo) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mDialogDo = mDialogDo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_layout, null);
        this.setContentView(view);
        this.setCancelable(false);
        txt_ok = (TextView) view.findViewById(R.id.btn_sure);
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogDo != null) {
                    mDialogDo.onMDialogClick();
                }
            }
        });
    }
}
