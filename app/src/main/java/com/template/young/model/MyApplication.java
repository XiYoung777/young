package com.template.young.model;

import android.app.Application;
import android.os.Handler;
import android.view.View;

import com.template.young.fragment.FragmentSingle;
import com.template.young.service.MusicService;

import java.util.ArrayList;

public class MyApplication extends Application {

    private MusicService.MyBinder mBinder;
    private FragmentSingle mFragmentSingle;
    private Handler mHandler;
    private boolean mLocalMusicFlag = false;
    private int mPosition = 0;
    private ArrayList<Music> mMusicList;
    private View mHomepageBar;
    private View mLocalMusicBar;
    private boolean mHomepageFirstStart = true;
    private boolean mLocalMusicFirstStart = true;

    public MusicService.MyBinder getmBinder() {
        return mBinder;
    }

    public void setmBinder(MusicService.MyBinder mBinder) {
        this.mBinder = mBinder;
    }

    public FragmentSingle getmFragmentSingle() {
        return mFragmentSingle;
    }

    public void setmFragmentSingle(FragmentSingle mFragmentSingle) {
        this.mFragmentSingle = mFragmentSingle;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public boolean ismLocalMusicFlag() {
        return mLocalMusicFlag;
    }

    public void setmLocalMusicFlag(boolean mLocalMusicFlag) {
        this.mLocalMusicFlag = mLocalMusicFlag;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public ArrayList<Music> getmMusicList() {
        return mMusicList;
    }

    public void setmMusicList(ArrayList<Music> mMusicList) {
        this.mMusicList = mMusicList;
    }

    public View getmHomepageBar() {
        return mHomepageBar;
    }

    public void setmHomepageBar(View mHomepageBar) {
        this.mHomepageBar = mHomepageBar;
    }

    public View getmLocalMusicBar() {
        return mLocalMusicBar;
    }

    public void setmLocalMusicBar(View mLocalMusicBar) {
        this.mLocalMusicBar = mLocalMusicBar;
    }

    public boolean ismHomepageFirstStart() {
        return mHomepageFirstStart;
    }

    public void setmHomepageFirstStart(boolean mHomepageFirstStart) {
        this.mHomepageFirstStart = mHomepageFirstStart;
    }

    public boolean ismLocalMusicFirstStart() {
        return mLocalMusicFirstStart;
    }

    public void setmLocalMusicFirstStart(boolean mLocalMusicFirstStart) {
        this.mLocalMusicFirstStart = mLocalMusicFirstStart;
    }
}
