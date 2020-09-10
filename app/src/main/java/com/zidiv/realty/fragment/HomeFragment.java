package com.zidiv.realty.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zidiv.realty.BaseFragment;
import com.zidiv.realty.R;
import com.zidiv.realty.adapter.PagerAdapter;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.ImageInfo;
import com.zidiv.realty.customview.ImageCycleView;
import com.zidiv.realty.urls.HttpUrls;
import com.zidiv.realty.util.L;
import com.zidiv.realty.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener{
    private Context context;
    private TextView tv_title;
    private View iv_001, iv_002;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private int currentIndex = 0;
    private LinearLayout ll_left, ll_right;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        context = getActivity();
        tv_title = (TextView) view.findViewById(R.id.title_text);
        tv_title.setText("帮你忙房源");
        iv_001 = view.findViewById(R.id.iv_001);
        iv_002 = view.findViewById(R.id.iv_002);
        ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
        ll_right = (LinearLayout) view.findViewById(R.id.ll_right);
        ll_left.setOnClickListener(this);
        ll_right.setOnClickListener(this);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mPagerAdapter = new PagerAdapter(getActivity());
    }

    private void initData() {
        mViewPager.setOnPageChangeListener(new MyOnpageChangeList());
        mPagerAdapter.addTab(SellFragment.class, null);
        mPagerAdapter.addTab(RentFragment.class, null);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_left:
                mViewPager.setCurrentItem(0);
                setBtnImgColAll(0);
                L.e("left");
                break;
            case R.id.ll_right:
                mViewPager.setCurrentItem(1);
                setBtnImgColAll(1);
                L.e("right");
                break;
        }
    }


    private class MyOnpageChangeList implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }
        @Override
        public void onPageSelected(int i) {
            setBtnImgColAll(i);
        }
        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

    private void setBtnImgColAll(int i) {
        if (i == 0) {
            iv_001.setVisibility(View.VISIBLE);
            iv_002.setVisibility(View.INVISIBLE);
            ll_left.setClickable(false);
            ll_right.setClickable(true);
        } else {
            iv_001.setVisibility(View.INVISIBLE);
            iv_002.setVisibility(View.VISIBLE);
            ll_left.setClickable(true);
            ll_right.setClickable(false);
        }
    }

}
