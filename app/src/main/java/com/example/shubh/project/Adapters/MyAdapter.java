package com.example.shubh.project.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.shubh.project.Fragments.FragmentA;
import com.example.shubh.project.Fragments.FragmentB;
import com.example.shubh.project.Fragments.FragmentC;
import com.example.shubh.project.Fragments.FragmentD;

public class MyAdapter extends FragmentPagerAdapter {
    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new FragmentA();
    }
        if (position == 1) {
            fragment = new FragmentB();
        }
        if (position == 2) {
            fragment = new FragmentC();
        }
        if (position == 3) {
            fragment = new FragmentD();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}