package com.template.young.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class LocalmusicViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList;
    private String[] mTitles = {"单曲","歌手","专辑","文件夹"};

    public LocalmusicViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        mFragmentList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
