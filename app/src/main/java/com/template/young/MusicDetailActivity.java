package com.template.young;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.template.young.model.Music;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;
import com.template.young.util.CircleImageView;
import com.template.young.util.ImageFilter;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MusicDetailActivity extends AppCompatActivity {

    private MyApplication mApplication;
    private ImageView mMusicBackground;
    private CircleImageView mMusicDisc;
    private ImageView mPlayView;
    private MusicService.MyBinder mBinder;
    private ArrayList<Music> mMusicList;
    private ImageView mLastView;
    private ImageView mNextView;
    private ImageView mBackView;
    private int mPosition;
    private TextView mMusicDetailSong;
    private TextView mMusicDetailSinger;
    private TextView mTimeProgress;
    private TextView mTimeTotal;
    private boolean mIsPlaying = false;
    private SeekBar mMusicProgress;
    private boolean mIsChanged = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mMusicProgress.setProgress(mBinder.getProgress());
                    mTimeProgress.setText(mBinder.getCurrentTime());
                    break;
            }
        }
    };
    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(3);

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsPlaying) {
                mHandler.sendEmptyMessage(1);
                SystemClock.sleep(1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);

        //初始化变量
        initVariable();

        //隐藏statusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //一些初始化事件
        initMusicStart();


        //设置单击事件
        initClick();
    }

    private void initMusicStart() {
        //设置当前封面
        initBitMap(mMusicList.get(mPosition).getmFolder());
        //设置当前播放信息
        Music currentMusic = mMusicList.get(mPosition);
        //歌曲信息
        mMusicDetailSong.setText(currentMusic.getmSong());
        //歌手信息
        mMusicDetailSinger.setText(currentMusic.getmSinger());
        //当前时长
        mTimeProgress.setText(mBinder.getCurrentTime());
        //总时长
        mTimeTotal.setText(mBinder.getTotalTime());
        //设置进度条进度
        mMusicProgress.setMax(mBinder.getMaxProgress());
        //设置当前播放状态
        if (mBinder.isPlaying()) {
            mPlayView.setImageResource(R.drawable.icon_pause_white);
            mMusicDisc.playAnim();
            mThreadPool.execute(mRunnable);
            mTimeProgress.setText(mBinder.getCurrentTime());
        }

    }

    private void initVariable() {
        mMusicBackground = findViewById(R.id.music_detail_background);
        mMusicDisc = findViewById(R.id.music_detail_disc);
        mPlayView = findViewById(R.id.music_detail_play);
        mApplication = (MyApplication) getApplicationContext();
        mBinder = mApplication.getmBinder();
        mMusicList = mApplication.getmMusicList();
        mPosition = mApplication.getmPosition();
        mLastView = findViewById(R.id.music_detail_last);
        mNextView = findViewById(R.id.music_detail_next);
        mBackView = findViewById(R.id.music_detail_back);
        mTimeProgress = findViewById(R.id.music_detail_time_progress);
        mTimeTotal = findViewById(R.id.music_detail_time_total);
        mMusicDetailSong = findViewById(R.id.music_detail_song);
        mMusicDetailSinger = findViewById(R.id.music_detail_singer);
        mMusicProgress = findViewById(R.id.music_detail_seekbar);
    }

    /**
     * 初始化单击事件
     */
    private void initClick() {

        //SeekBar的监听事件
        mMusicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBinder.setProgress(seekBar.getProgress());
                mIsPlaying = true;
                if (!mBinder.isPlaying()) {
                    mBinder.playMusic();
                    initMusicStart();
                }
            }
        });

        //播放&暂停
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinder.isPlaying()) {
                    mIsPlaying = true;
                    mPlayView.setImageResource(R.drawable.icon_pause_white);
                    mBinder.playMusic();
                    mMusicDisc.playAnim();
                    mThreadPool.execute(mRunnable);
                } else {
                    mIsPlaying = false;
                    mPlayView.setImageResource(R.drawable.icon_start_white);
                    mBinder.playMusic();
                    mMusicDisc.pauseAnim();
                }
            }
        });

        //返回键
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //上一首
        mLastView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPlaying = true;
                mMusicProgress.setProgress(0);
                mMusicDisc.stopAnim();
                if (mPosition == 0) {
                    mPosition = mMusicList.size() - 1;
                } else {
                    mPosition = mPosition - 1;
                }
                mApplication.setmPosition(mPosition);
                String path = mMusicList.get(mPosition).getmFolder();
                mBinder.setMusic(path);
                mBinder.playMusic();
                initMusicStart();
            }
        });

        //下一首
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPlaying = true;
                mMusicProgress.setProgress(0);
                mMusicDisc.stopAnim();
                if (mPosition == mMusicList.size() - 1) {
                    mPosition = 0;
                } else {
                    mPosition = mPosition + 1;
                }
                mApplication.setmPosition(mPosition);
                String path = mMusicList.get(mPosition).getmFolder();
                mBinder.setMusic(path);
                mBinder.playMusic();
                initMusicStart();
            }
        });
    }

    /**
     * 获取封面并设置
     */
    private void initBitMap(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        if (picture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            mMusicDisc.setImageBitmap(bitmap);
            Bitmap blurBitmap = ImageFilter.blurBitmap(this, bitmap, 20f);
            mMusicBackground.setImageBitmap(blurBitmap);
        }
    }
}
