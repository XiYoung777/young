package com.template.young.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.template.young.Adapter.SingleAdapter;
import com.template.young.Constant.MessageOrder;
import com.template.young.R;
import com.template.young.model.Music;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class FragmentSingle extends Fragment implements SingleAdapter.SingleCallback, FragmentPlaybar.PlaybarCallbcak {

    private List<Integer> mIndexList = new ArrayList<>();
    private int mCurrentPosition = 0;
    private int mLastPosition = -1;
    private ListView mListView;
    private SQLiteDatabase mDb;
    private ArrayList<Music> mMusicList = new ArrayList<>();
    private View mHeadIcon;
    private MusicService.MyBinder mBinder;
    private FragmentSingle mFragment = this;
    private MyApplication mApplication;
    private Context mContext;
    private boolean mFlag = true;
    private View mPlayBar;

    public FragmentSingle(MusicService.MyBinder mBinder, Context mContext) {
        this.mBinder = mBinder;
        this.mContext = mContext;
        mApplication = (MyApplication) mContext.getApplicationContext();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localmusic_single, container, false);
        if (mFlag) {
            initList();
        }
        initAdapter(view);
        initSingleCallback();
        mPlayBar = mApplication.getmLocalMusicBar();
        return view;
    }

    private void initAdapter(View view) {
        mListView = view.findViewById(R.id.single_list);
        mListView.setAdapter(new SingleAdapter(getContext(), mMusicList,this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLastPosition = mApplication.getmPosition();
                mCurrentPosition = position;
                mApplication.setmPosition(mCurrentPosition);
                if (mLastPosition == -1) {
                    //更改图标样式
                    setHeadIcon(mCurrentPosition, View.VISIBLE);

                    mLastPosition = mCurrentPosition;
                    Music music = mMusicList.get(mCurrentPosition);
                    String path = music.getmFolder();
                    //设置新音乐
                    mBinder.setMusic(path);
                    //播放音乐
                    mBinder.playMusic();
                } else {
                    if (mCurrentPosition == mLastPosition) {
                        mBinder.playMusic();
                    } else {
                        //更改图标样式
                        setHeadIcon(mLastPosition, View.GONE);
                        setHeadIcon(mCurrentPosition - mListView.getFirstVisiblePosition(), View.VISIBLE);
                        setPlaybarData(mCurrentPosition, mPlayBar);

                        mLastPosition = mCurrentPosition;
                        Music music = mMusicList.get(mCurrentPosition);
                        String path = music.getmFolder();
                        //设置新音乐
                        mBinder.setMusic(path);
                        //播放音乐
                        mBinder.playMusic();
                    }
                }
            }
        });
    }

    /**
     * 当页面创建时,保存页面对象
     */
    private void initSingleCallback() {
        mApplication.setmFragmentSingle(mFragment);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.INIT_FRAGMENTSINGLE);
    }

    /**
     * 设置ListView的head是否隐藏
     * @param position
     * @param gone
     */
    private void setHeadIcon(int position, int gone) {
        View lastHeadIcon = mListView.getChildAt(position).findViewById(R.id.single_list_head_icon);
        lastHeadIcon.setVisibility(gone);
    }

    /**
     * 初始化MusicList,并更新至service中的歌单
     */
    private void initList() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            do {
                int columnIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int columnSingerIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int columnSongIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int columnSpecialIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int columnFolderIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int id = cursor.getInt(columnIdIndex);
                mIndexList.add(id);
                String singer = cursor.getString(columnSingerIndex);
                String song = cursor.getString(columnSongIndex);
                String special = cursor.getString(columnSpecialIndex);
                String folder = cursor.getString(columnFolderIndex);
                mMusicList.add(new Music(singer, song, special, folder));
            } while (cursor.moveToNext());
        }
        mApplication.setmMusicList(mMusicList);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.OBTAIN_MUSICLIST);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.OBTAIN_LOCALMMUSIC_FLAG);
        mFlag = false;
    }

    @Override
    public void itemClick(View v, int position, View headIcon) {
        mCurrentPosition = position;
        mHeadIcon = headIcon;
    }

    @Override
    public void lastMusic() {
        setHeadIcon(mCurrentPosition, View.GONE);
        mLastPosition = mCurrentPosition;
        if (mCurrentPosition == 0) {
            mCurrentPosition = mMusicList.size() - 1;
        } else {
            mCurrentPosition = mCurrentPosition - 1;
        }
        setHeadIcon(mCurrentPosition, View.VISIBLE);
        mApplication.setmPosition(mCurrentPosition);
        mBinder.setMusic(mMusicList.get(mCurrentPosition).getmFolder());
        mBinder.playMusic();
        setPlaybarData(mCurrentPosition, mPlayBar);
    }

    @Override
    public void nextMusic() {
        setHeadIcon(mCurrentPosition, View.GONE);
        mLastPosition = mCurrentPosition;
        if (mCurrentPosition == mMusicList.size() - 1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition = mCurrentPosition + 1;
        }
        setHeadIcon(mCurrentPosition, View.VISIBLE);
        mApplication.setmPosition(mCurrentPosition);
        mBinder.setMusic(mMusicList.get(mCurrentPosition).getmFolder());
        mBinder.playMusic();
        setPlaybarData(mCurrentPosition, mPlayBar);
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
    }
}
