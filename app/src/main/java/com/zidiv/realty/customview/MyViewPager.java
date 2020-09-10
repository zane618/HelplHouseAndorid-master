package com.zidiv.realty.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 点击按钮切换不闪屏
 * Created by Administrator on 2016/3/17.
 */
public class MyViewPager extends ViewPager{
    public MyViewPager(Context context) {
        super(context);
    }
    public MyViewPager(Context context,AttributeSet attrs) {
        super(context,attrs);
    }
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
}
