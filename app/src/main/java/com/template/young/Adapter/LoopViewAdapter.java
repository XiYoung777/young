package com.template.young.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class LoopViewAdapter extends PagerAdapter {

    private ArrayList<ImageView> mImageViewList;

    public LoopViewAdapter(ArrayList<ImageView> mImageViewList) {
        this.mImageViewList = mImageViewList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int newPosition = position % mImageViewList.size();
        ImageView imageView = mImageViewList.get(newPosition);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImageViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
