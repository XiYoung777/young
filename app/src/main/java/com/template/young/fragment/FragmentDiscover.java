package com.template.young.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.template.young.Adapter.LoopViewAdapter;
import com.template.young.R;
import com.template.young.callback.ViewpagerCallback;

import java.util.ArrayList;

public class FragmentDiscover extends Fragment {

    private ArrayList<ImageView> mImageViewList;
    private Context mContext;
    private ViewpagerCallback mViewpagerCallback;
    private ViewPager mViewPager;
    private Handler mHandler;

    public FragmentDiscover(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initLooperViewpager();
        LoopViewAdapter loopViewAdapter = new LoopViewAdapter(mImageViewList);
        mViewPager = (ViewPager) view.findViewById(R.id.discover_looper_view);
        mViewPager.setAdapter(loopViewAdapter);
        mViewPager.setCurrentItem(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(3000);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = mViewPager;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
        return view;
    }

    private void initLooperViewpager() {
        mImageViewList = new ArrayList<>();
        int[] imageIds = new int[]{
                R.drawable.poster_first,
                R.drawable.poster_second,
                R.drawable.poster_third,
                R.drawable.poster_fourth,
                R.drawable.poster_fifth,
                R.drawable.poster_sixth,
                R.drawable.poster_seventh,
                R.drawable.poster_eighth,
                R.drawable.poster_ninth
        };
        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(imageIds[i]);
            mImageViewList.add(imageView);
        }
    }

}
