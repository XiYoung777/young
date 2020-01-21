package com.template.young;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.template.young.callback.ILrcViewListener;
import com.template.young.fragment.FragmentMusicDetailLyric;
import com.template.young.fragment.FragmentPlaybar;
import com.template.young.model.LrcRow;
import com.template.young.model.Music;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;
import com.template.young.util.CircleImageView;
import com.template.young.util.ImageFilter;
import com.template.young.util.LrcView;
import com.template.young.util.LyricUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private SeekBar mMusicProgress;
    private LinearLayout mMusicLyricFragment;


    private List<LrcRow> mLrcRows;
    private LrcView mLrcView;
    //更新歌词的频率，每秒更新一次
    private int mPalyTimerDuration = 1000;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;

    private boolean mIsPlaying = false;
    private boolean mIsChanged = false;
    private Context mContext = this;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mMusicProgress.setProgress(mBinder.getProgress());
                    mTimeProgress.setText(mBinder.getCurrentTime());
                    break;
                case 2:
                    //获取歌曲播放的位置
                    final long timePassed = mBinder.getProgress();
                    //滚动歌词
                    mLrcView.seekLrcToTime(timePassed);
                    break;
            }
        }
    };
    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(3);

    private Runnable mRunnableSeekBar = new Runnable() {
        @Override
        public void run() {
            while (mIsPlaying) {
                mHandler.sendEmptyMessage(1);
                mHandler.sendEmptyMessage(2);
                SystemClock.sleep(1000);
            }
        }
    };

    private Runnable mRunnableLyric = new Runnable() {
        @Override
        public void run() {
            //获取当前歌曲名称
            String song = mMusicList.get(mPosition).getmSong();
            try {
                //设置歌词
                mLrcRows = LyricUtil.requstLrcData(song);
                mLrcView.setLrc(mLrcRows);
            } catch (IOException e) {
                e.printStackTrace();
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
        
        //设置歌词
        drawLyric();
    }

    private void drawLyric() {
        //获取歌词
        mThreadPool.execute(mRunnableLyric);
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
            mIsPlaying = true;
            mPlayView.setImageResource(R.drawable.icon_pause_white);
            mMusicDisc.playAnim();
            mThreadPool.execute(mRunnableSeekBar);
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
        mMusicLyricFragment = findViewById(R.id.fragment_music_detail_lyric);
        mLrcView = findViewById(R.id.music_detail_lyric);
    }

    /**
     * 初始化单击事件
     */
    private void initClick() {
        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (mBinder != null) {
                    mBinder.setProgress((int) row.time);
                }
            }
        });

        //点击歌词关闭歌词页面
        mLrcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicDisc.setVisibility(View.VISIBLE);
                mMusicLyricFragment.setVisibility(View.INVISIBLE);
            }
        });


        //点击唱片弹出歌词页面
        mMusicDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicDisc.setVisibility(View.INVISIBLE);
                mMusicLyricFragment.setVisibility(View.VISIBLE);
            }
        });

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
                    //设置歌词
                    drawLyric();
                    mThreadPool.execute(mRunnableSeekBar);
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
                //设置歌词
                drawLyric();
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
                //设置歌词
                drawLyric();
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
