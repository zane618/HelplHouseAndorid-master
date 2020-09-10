package com.zidiv.realty.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.db.annotation.Check;
import com.zidiv.realty.BaseActivity;
import com.zidiv.realty.BaseFragment;
import com.zidiv.realty.R;
import com.zidiv.realty.activity.SearchResultActivity;
import com.zidiv.realty.adapter.RecordAdapter;
import com.zidiv.realty.adapter.SendTimeAdapter;
import com.zidiv.realty.bean.HistoryBean;
import com.zidiv.realty.database.OperateDataBaseStatic;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.ToastUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索（Fragment）
 * Created by Administrator on 2016/3/17.
 */
public class SearchCollectActivity extends BaseActivity implements RecordAdapter.ToDelete {
    private Context context;
    private TextView tv_title, tv_choose_qu;
    static ViewGroup viewGroup;
    private EditText et_bianhao, et_donghao, et_xq_mingcheng, et_phone_number;

    private EditText et_mianji1, et_mianji2;
    private EditText et_zongjia1, et_zongjia2;
    private EditText et_danjia1, et_danjia2;
    private EditText et_louceng1, et_louceng2;
    private Button btn_search, btn_clear;

    private CheckBox chk_luyang, chk_shushan, chk_jingkai, chk_xinzhan, chk_baohe, chk_gaoxin, chk_zhengwu, chk_yaohai;
    private CheckBox chk_binhu, chk_changfeng, chk_feixi, chk_feidong, chk_lujiang, chk_chaohu, chk_beicheng, chk_xinqiao;
    //2016 07 11添加状态，和小区历史搜索
    private TextView tv_choose_status; //状态选择
    //    private TextView tv_qu_lishi; //小区历史
    private boolean isQu = false; //标识 区域的popw 或 状态的popw
    private boolean notQu = true; //不是历史搜索

    private ListView lishi_listview; //历史搜索 listview
    private RecordAdapter recordAdapter;
    private List<HistoryBean> hisDatas = new ArrayList<>();
    private List<CheckBox> chkList = new ArrayList<CheckBox>();
    private TextView deleteAll;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_collect);
    }

    @Override
    public void getIntentData() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initLinstener() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notQu = true;
                btnSearch(null);
                String content = et_xq_mingcheng.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    OperateDataBaseStatic.dataAdd(context, content);
                    initRecord();
                }
            }
        });
        //选择区域
        tv_choose_qu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isQu = true;
                strList.clear();
                strList.addAll(initList());
                sendTimeAdapter.notifyDataSetChanged();
                popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);
            }
        });
        //选择状态
        tv_choose_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isQu = false;
                strList.clear();
                strList.addAll(initList2());
                sendTimeAdapter.notifyDataSetChanged();
                popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);
            }
        });
        //重置
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClear();
            }
        });
    }

    @Override
    public void initView() {
        context = this;

        chk_luyang = (CheckBox) findViewById(R.id.chk_luyang);
        chk_shushan = (CheckBox) findViewById(R.id.chk_shushan);
        chk_jingkai = (CheckBox) findViewById(R.id.chk_jingkai);
        chk_xinzhan = (CheckBox) findViewById(R.id.chk_xinzhan);
        chk_baohe = (CheckBox) findViewById(R.id.chk_baohe);
        chk_gaoxin = (CheckBox) findViewById(R.id.chk_gaoxin);
        chk_zhengwu = (CheckBox) findViewById(R.id.chk_zhengwu);
        chk_yaohai = (CheckBox) findViewById(R.id.chk_yaohai);
        chk_binhu = (CheckBox) findViewById(R.id.chk_binhu);
        chk_changfeng = (CheckBox) findViewById(R.id.chk_changfeng);
        chk_feixi = (CheckBox) findViewById(R.id.chk_feixi);
        chk_feidong = (CheckBox) findViewById(R.id.chk_feidong);
        chk_lujiang = (CheckBox) findViewById(R.id.chk_lujiang);
        chk_chaohu = (CheckBox) findViewById(R.id.chk_chaohu);
        chk_beicheng = (CheckBox) findViewById(R.id.chk_beicheng);
        chk_xinqiao = (CheckBox) findViewById(R.id.chk_xinqiao);

        chkList.add(chk_luyang);
        chkList.add(chk_shushan);
        chkList.add(chk_jingkai);
        chkList.add(chk_xinzhan);
        chkList.add(chk_baohe);
        chkList.add(chk_gaoxin);
        chkList.add(chk_zhengwu);
        chkList.add(chk_yaohai);
        chkList.add(chk_binhu);
        chkList.add(chk_changfeng);
        chkList.add(chk_feixi);
        chkList.add(chk_feidong);
        chkList.add(chk_lujiang);
        chkList.add(chk_chaohu);
        chkList.add(chk_beicheng);
        chkList.add(chk_xinqiao);

        viewGroup = (ViewGroup) findViewById(R.id.ll_group);
        //title
        tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText("搜 索");
        //初始化控件
        et_bianhao = (EditText) findViewById(R.id.et_bianhao);
        et_donghao = (EditText) findViewById(R.id.et_donghao);
        et_xq_mingcheng = (EditText) findViewById(R.id.et_xq_mingcheng);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_mianji1 = (EditText) findViewById(R.id.et_mianji1);
        et_mianji2 = (EditText) findViewById(R.id.et_mianji2);
        et_zongjia1 = (EditText) findViewById(R.id.et_zongjia1);
        et_zongjia2 = (EditText) findViewById(R.id.et_zongjia2);
        et_danjia1 = (EditText) findViewById(R.id.et_danjia1);
        et_danjia2 = (EditText) findViewById(R.id.et_danjia2);
        et_louceng1 = (EditText) findViewById(R.id.et_louceng1);
        et_louceng2 = (EditText) findViewById(R.id.et_louceng2);
        btn_search = (Button) findViewById(R.id.btn_search);
        tv_choose_qu = (TextView) findViewById(R.id.tv_choose_qu);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        tv_choose_status = (TextView) findViewById(R.id.tv_choose_status);
        lishi_listview = (ListView) findViewById(R.id.lishi_listview);
        deleteAll = (TextView) findViewById(R.id.deleteAll);
        lishi_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                notQu = false;
                btnSearch(hisDatas.get(i).getContent());
            }
        });
        recordAdapter = new RecordAdapter(hisDatas, context, SearchCollectActivity.this);
        lishi_listview.setAdapter(recordAdapter);
        initPopView();
        initRecord();
        //清除搜索记录
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hisDatas.size() > 0) {
                    OperateDataBaseStatic.dataDeleteAll(context);
                    initRecord();
                    L.e("清除全部记录");
                }
            }
        });
    }

    //点击搜索按钮
    private void btnSearch(String mc) {
        String bianhao = et_bianhao.getText().toString().trim();
        String donghao = et_donghao.getText().toString().trim();
        String mingcheng;
        if (mc == null) {
            mingcheng = et_xq_mingcheng.getText().toString().trim();
        } else {
            mingcheng = mc;
        }
        String phone_number = et_phone_number.getText().toString().trim();
        String mianji1 = et_mianji1.getText().toString().trim();
        String mianji2 = et_mianji2.getText().toString().trim();
        String zongjia1 = et_zongjia1.getText().toString().trim();
        String zongjia2 = et_zongjia2.getText().toString().trim();
        String danjia1 = et_danjia1.getText().toString().trim();
        String danjia2 = et_danjia2.getText().toString().trim();
        String louceng1 = et_louceng1.getText().toString().trim();
        String louceng2 = et_louceng2.getText().toString().trim();

        String househuxing = tv_choose_qu.getText().toString().trim();
        //2016 07 11
        String zhuangtai = tv_choose_status.getText().toString().trim();
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra("notQu", notQu);
        intent.putExtra("bianhao", bianhao);
        intent.putExtra("donghao", donghao);
        intent.putExtra("mingcheng", mingcheng);
        intent.putExtra("phone_number", phone_number);
        intent.putExtra("mianji1", mianji1);
        intent.putExtra("mianji2", mianji2);
        intent.putExtra("zongjia1", zongjia1);
        intent.putExtra("zongjia2", zongjia2);
        intent.putExtra("danjia1", danjia1);
        intent.putExtra("danjia2", danjia2);
        intent.putExtra("louceng1", louceng1);
        intent.putExtra("louceng2", louceng2);

        intent.putExtra("househuxing", househuxing.equals("选择房型") ? "" : househuxing);

        intent.putExtra("zhuangtai", zhuangtai.equals("选择状态") ? "" : zhuangtai);

        intent.putExtra("iscollect", "1");
        StringBuffer sb = new StringBuffer();
        //遍历集合中的checkBox,判断是否选择，获取选中的文本
        for (CheckBox checkbox : chkList) {
            if (checkbox.isChecked()) {
                sb.append(checkbox.getText().toString() + "|");
            }
        }
        intent.putExtra("quyu", sb.toString());
        L.e("选择1：" + sb.toString());
        L.e("选择2：" + zhuangtai);
        startActivity(intent);
    }

    //初始化 历史搜索
    private void initRecord() {
        List<HistoryBean> data = OperateDataBaseStatic.dataSelectAll(context);
        hisDatas.clear();
        if (data != null && data.size() > 0) {
            hisDatas.addAll(data);
        }
        recordAdapter.notifyDataSetChanged();
    }

    private int width;
    private PopupWindow popupWindow;
    private SendTimeAdapter sendTimeAdapter;
    private ListView mListView;
    private List<String> strList = new ArrayList<>();
    private View popView;

    private void initPopView() {

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        LayoutInflater inflater = LayoutInflater.from(context);
        popView = inflater.inflate(R.layout.sendtime_dialog, null);
        sendTimeAdapter = new SendTimeAdapter(strList, context);
        mListView = (ListView) popView.findViewById(R.id.send_listView);
        mListView.setAdapter(sendTimeAdapter);
        //选择区域
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isQu) {
                    tv_choose_qu.setText(strList.get(i));
                } else {
                    tv_choose_status.setText(strList.get(i));
                }
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(500, width);
        popupWindow.setContentView(popView);
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x55000000));
        popupWindow.setFocusable(true);
    }

    //清除按钮
    private void btnClear() {
        tv_choose_qu.setText("");
        et_bianhao.setText("");
        et_donghao.setText("");
        et_xq_mingcheng.setText("");
        et_phone_number.setText("");
        et_mianji1.setText("");
        et_mianji2.setText("");
        et_zongjia1.setText("");
        et_zongjia2.setText("");
        et_danjia1.setText("");
        et_danjia2.setText("");
        et_louceng1.setText("");
        et_louceng2.setText("");
        //2016 07 11添加
        tv_choose_status.setText("");
    }

    private List<String> initList() {
        List<String> data = new ArrayList<>();
        data.add("一室一厅");
        data.add("二室一厅");
        data.add("二室二厅");
        data.add("三室一厅");
        data.add("三室二厅");
        data.add("四室一厅");
        data.add("四室二厅");
        data.add("四室以上");
        data.add("别墅");
        data.add("商铺");
        data.add("挑高");
        data.add("写字楼");
        data.add("单间");
        data.add("复式");
        data.add("门面");
        data.add("其他");
        return data;
    }

    private List<String> initList2() {
        List<String> data = new ArrayList<>();
        data.add("不限");
        data.add("在售");
        data.add("已售");
        data.add("停售");
        data.add("出租");
        return data;
    }

    @Override
    public void onDelete(int position) {
        OperateDataBaseStatic.dataDelete(context, hisDatas.get(position).getContent());
        initRecord();
    }
}
