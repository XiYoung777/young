package com.template.young.service;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.template.young.Constant.DbConstant;
import com.template.young.R;
import com.template.young.model.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MusicService extends Service {

    private List<Integer> mIndexList = new ArrayList<>();
    private int mCurrentPosition = 0;
    private Context mContext = this;
    private ArrayList<Music> mMusicList;
    private SQLiteDatabase mDb;
    private MediaPlayer mMediaPlayer;
    private MyBinder mBinder = new MyBinder();

    public MusicService() {
    }

    @Override
    public void onCreate() {
        mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(DbConstant.PATH + R.raw.music_aisi));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {

    }

    public class MyBinder extends Binder {

        /**
         * 播放音乐
         */
        public void playMusic() {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }

        /**
         * 设置音乐
         *
         * @param path
         */
        public void setMusic(String path) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断是否正在播放
         * @return
         */
        public boolean isPlaying() {
            return mMediaPlayer.isPlaying();
        }
    }
}
