package com.zidiv.realty.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 主界面的适配器
 * Created by Administrator on 2016/3/16.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter{
    private List<Fragment> mFragments;
    public MainFragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
