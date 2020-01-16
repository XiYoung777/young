package com.template.young.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.template.young.Adapter.LocalmusicViewPagerAdapter;
import com.template.young.R;
import com.template.young.service.MusicService;

import java.util.ArrayList;

public class FragmentContentLocalMusic extends Fragment {

    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private ViewPager mViewpager;
    private TabLayout mTabLayout;
    private MusicService.MyBinder mBinder;
    private FragmentSingle mFragmentSingle;

    public FragmentContentLocalMusic(MusicService.MyBinder mBinder) {
        this.mBinder = mBinder;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_localmusic, container, false);
        mViewpager = view.findViewById(R.id.localmusic_viewpager);
        mTabLayout = view.findViewById(R.id.localmusic_tablayout);
        //初始化fragment并绑定Viewpager和TabLayout
        initFragment();
        return view;
    }

    private void initFragment() {
        mFragmentSingle = new FragmentSingle(mBinder);
        mFragmentList.add(mFragmentSingle);
        mFragmentList.add(new FragmentSinger());
        mFragmentList.add(new FragmentSpecial());
        mFragmentList.add(new FragmentFolder());
        initViewpager();
    }

    private void initViewpager() {
        LocalmusicViewPagerAdapter adapter = new LocalmusicViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
        mViewpager.setAdapter(adapter);
        mViewpager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewpager);
    }
}
