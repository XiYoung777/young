package com.template.young.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.template.young.R;
import com.template.young.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlaybar extends Fragment {

    //playbar所绑定的为当前歌单,待定   service和该类中存放当前歌单最合适，到时候看哪个更方便
    private List<Integer> mIndexList;
    private int mPosition = 0;

    private ImageView mImageViewLast;
    private ImageView mImageViewPlay;
    private ImageView mImageViewNext;
    private MusicService.MyBinder mBinder;
    private ImageView mPlayImage;
    private int mPlayImageId = R.drawable.playbar_start;
    private PlaybarCallbcak mPlayCallback;

    public interface PlaybarCallbcak {
        public void playMusic();
        public void lastMusic();
        public void nextMusic();
    }

    public FragmentPlaybar(MusicService.MyBinder mBinder) {
        this.mBinder = mBinder;
    }

    public FragmentPlaybar(MusicService.MyBinder mBinder, PlaybarCallbcak mPlayCallback) {
        this.mBinder = mBinder;
        this.mPlayCallback = mPlayCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_playbar, container, false);
        initClick(view);
        return view;
    }

    private void initClick(View view) {
        mImageViewLast = view.findViewById(R.id.playbar_last);
        mImageViewLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.setMusic(R.raw.music_aisi);
                mBinder.playLast();
            }
        });

        mImageViewPlay = view.findViewById(R.id.playbar_play_image);
        mPlayImage = view.findViewById(R.id.playbar_play_image);
        mImageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayImageId == R.drawable.playbar_start) {
                    mPlayImage.setImageResource(R.drawable.playbar_pause);
                    mPlayImageId = R.drawable.playbar_pause;
                } else {
                    mPlayImage.setImageResource(R.drawable.playbar_start);
                    mPlayImageId = R.drawable.playbar_start;
                }
                mPlayCallback.playMusic();
            }
        });

        mImageViewNext = view.findViewById(R.id.playbar_next);
        mImageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.playNext();
            }
        });
    }
}
