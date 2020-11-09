package com.example.sunrin.myapplication;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> items = new ArrayList<Fragment>();

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        return items.get(i);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(Fragment item){
        items.add(item);
    }


}
