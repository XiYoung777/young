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
import com.template.young.util.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MusicService extends Service {

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

    private void addMusic(Music music) {
        if (!mMusicList.contains(music)) {
            ContentValues values = new ContentValues();
            values.put(DbConstant.MUSIC_COLUMN_SINGER, music.getmSinger());
            values.put(DbConstant.MUSIC_COLUMN_SONG, music.getmSong());
            values.put(DbConstant.MUSIC_COLUMN_SPECIAL, music.getmSpecial());
            values.put(DbConstant.MUSIC_COLUMN_FOLDER, music.getmFolder());
            mDb.insert(DatabaseHelper.TABLE_MUSIC, null, values);
        }
        mBinder.playMusic();
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
         * @param musicId
         */
        public void setMusic(int musicId) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mContext, Uri.parse(DbConstant.PATH + musicId));
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
         * 播放上一曲
         */
        public void playLast() {

        }

        /**
         * 播放下一曲
         */
        public void playNext() {

        }
    }
}
