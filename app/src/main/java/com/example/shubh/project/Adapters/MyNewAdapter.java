package com.example.shubh.project.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.example.shubh.project.Fragments.FragmentProfile;
import com.example.shubh.project.Fragments.FragmentSearch;
import com.example.shubh.project.Fragments.FragmentWall;
import com.example.shubh.project.Models.UserInfo;

import static com.example.shubh.project.Activity.Dashboard.USER_INFO_KEY;

/**
 * Created by Shubh on 16-10-2016.
 */
public class MyNewAdapter extends FragmentPagerAdapter {
    private UserInfo mUserInfo;

    public MyNewAdapter(FragmentManager fragmentManager, UserInfo userinfo) {
        super(fragmentManager);
        mUserInfo = userinfo;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new FragmentWall();
            Bundle wallHead = new Bundle();
            wallHead.putParcelable(USER_INFO_KEY, mUserInfo);
            fragment.setArguments(wallHead);
        }
        if (position == 1) {
            fragment = new FragmentSearch();
            Bundle editProfile = new Bundle();
            editProfile.putParcelable(USER_INFO_KEY, mUserInfo);
            fragment.setArguments(editProfile);
        }
        if (position == 2) {
            fragment = new FragmentProfile();
            Bundle editProfile = new Bundle();
            editProfile.putParcelable(USER_INFO_KEY, mUserInfo);
            fragment.setArguments(editProfile);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
