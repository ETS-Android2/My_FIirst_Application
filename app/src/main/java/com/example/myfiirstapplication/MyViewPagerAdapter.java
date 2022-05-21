package com.example.myfiirstapplication;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    MyViewPagerAdapter(FragmentManager fm, List<Fragment> fg_list) {
        super(fm);
        this.fragmentList=fg_list;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
