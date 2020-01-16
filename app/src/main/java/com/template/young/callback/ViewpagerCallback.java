package com.template.young.callback;

import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public interface ViewpagerCallback {

    public void init(ViewPager viewPager, ArrayList<ImageView> imageViewList);
}
