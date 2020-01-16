package com.template.young.model;

import android.app.Application;

import com.template.young.service.MusicService;

public class MyApplication extends Application {

    private MusicService.MyBinder mBinder;

    public MusicService.MyBinder getmBinder() {
        return mBinder;
    }

    public void setmBinder(MusicService.MyBinder mBinder) {
        this.mBinder = mBinder;
    }
}
