
package com.zidiv.realty.wxapi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zidiv.realty.R;
import com.zidiv.realty.activity.PayFailActivity;
import com.zidiv.realty.activity.PayOKActivity;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {


    private IWXAPI api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wxpay_entry);
        api = WXAPIFactory.createWXAPI(this,  "wx845613d85bc29e3e");//这里填入自己的微信APPID
        api.handleIntent(getIntent(), this);
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType()==ConstantsAPI.COMMAND_PAY_BY_WX){
            if(baseResp.errCode==0){
//                Toast.makeText(this, "支付成功", Toast.LENGTH_LONG).show();
                Intent intenttest = new Intent(this, PayOKActivity.class);
                intenttest.putExtra("title","支付成功");
                intenttest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intenttest);
                finish();
            }
            else {
//                Toast.makeText(this, "支付失败", Toast.LENGTH_LONG).show();
                Intent intenttest = new Intent(this, PayFailActivity.class);
                intenttest.putExtra("title","支付失败");
                intenttest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intenttest);
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }
}
