package com.template.young.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.template.young.MusicDetailActivity;
import com.template.young.R;
import com.template.young.model.Music;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlaybar extends Fragment {

    //playbar所绑定的为当前歌单,待定   service和该类中存放当前歌单最合适，到时候看哪个更方便
    private List<Integer> mIndexList;
    private int mPosition = 0;
    private ArrayList<Music> mMusicList;

    private ImageView mImageViewLast;
    private ImageView mImageViewPlay;
    private ImageView mImageViewNext;
    private MusicService.MyBinder mBinder;
    private PlaybarCallbcak mPlayCallback;
    private MyApplication mApplication;
    private boolean mLocalMusicFlag = false;
    private Context mContext = getContext();
    private View mView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:mPlayCallback = mApplication.getmFragmentSingle();break;
                case 2:mLocalMusicFlag = mApplication.ismLocalMusicFlag();break;
                case 3:mMusicList = mApplication.getmMusicList();break;
                case 4:mApplication.setmHomepageBar(mView);break;
                case 5:mApplication.setmLocalMusicBar(mView);break;
                case 6:setPlaybarData(mApplication.getmPosition(), mApplication.getmHomepageBar());break;
                case 7:setPlaybarData(mApplication.getmPosition(), mApplication.getmLocalMusicBar());break;
            }
        }
    };

    /**
     * 回调接口，用于实现本地音乐界面的同时更新
     */
    public interface PlaybarCallbcak {
        public void lastMusic();
        public void nextMusic();
    }

    public FragmentPlaybar(MusicService.MyBinder mBinder, Context mContext) {
        this.mBinder = mBinder;
        this.mContext = mContext;
        mApplication = (MyApplication) mContext.getApplicationContext();
        mApplication.setmHandler(mHandler);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_playbar, container, false);
        initClick(view);
        mView = view;
        return view;
    }

    /**
     * 初始化playbar的单击事件
     * @param view
     */
    private void initClick(final View view) {
        mImageViewLast = view.findViewById(R.id.playbar_last);
        mImageViewLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = mApplication.getmPosition();
                if (mLocalMusicFlag) {
                    mPlayCallback.lastMusic();
                } else {
                    if (mPosition == 0) {
                        mPosition = mMusicList.size() - 1;
                    } else {
                        mPosition = mPosition - 1;
                    }
                    mApplication.setmPosition(mPosition);
                    mBinder.setMusic(mMusicList.get(mPosition).getmFolder());
                    mBinder.playMusic();
                }
                setPlayBarStart();
                setPlaybarData(mApplication.getmPosition(), view);
            }
        });

        mImageViewPlay = view.findViewById(R.id.playbar_play_image);
        mImageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.playMusic();
                setPlayBarPlayImage();
            }
        });

        mImageViewNext = view.findViewById(R.id.playbar_next);
        mImageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = mApplication.getmPosition();
                if (mLocalMusicFlag) {
                    mPlayCallback.nextMusic();
                } else {
                    if (mPosition == mMusicList.size() - 1) {
                        mPosition = 0;
                    } else {
                        mPosition = mPosition + 1;
                    }
                    mApplication.setmPosition(mPosition);
                    mBinder.setMusic(mMusicList.get(mPosition).getmFolder());
                    mBinder.playMusic();
                }
                setPlaybarData(mApplication.getmPosition(), view);
                setPlayBarStart();
            }
        });

        LinearLayout playbar = view.findViewById(R.id.playbar);
        playbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMusicDetail = new Intent(mContext, MusicDetailActivity.class);
                startActivity(intentMusicDetail);
            }
        });
    }

    private void setPlayBarStart() {
        mImageViewPlay.setImageResource(R.drawable.playbar_pause);
    }

    private void setPlayBarPlayImage() {
        if (mBinder.isPlaying()) {
            mImageViewPlay.setImageResource(R.drawable.playbar_pause);
        } else {
            mImageViewPlay.setImageResource(R.drawable.playbar_start);
        }
    }

    /**
     * 更新playbar的信息
     * @param position
     * @param view
     */
    private void setPlaybarData(int position, View view) {
        ImageView playBarImage = view.findViewById(R.id.playbar_imageview);
        String mediaUri = mMusicList.get(position).getmFolder();

        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaUri);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        if (picture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            playBarImage.setImageBitmap(bitmap);
        }

        TextView playBarSinger = view.findViewById(R.id.playbar_singer);
        playBarSinger.setText(mMusicList.get(position).getmSinger());

        TextView playBarSonger = view.findViewById(R.id.playbar_song);
        playBarSonger.setText(mMusicList.get(position).getmSong());

        setPlayBarPlayImage();
    }
}
