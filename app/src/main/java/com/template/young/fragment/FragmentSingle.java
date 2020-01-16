package com.template.young.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.template.young.Adapter.SingleAdapter;
import com.template.young.Constant.DbConstant;
import com.template.young.R;
import com.template.young.model.Music;
import com.template.young.service.MusicService;
import com.template.young.util.DatabaseHelper;

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

    public FragmentSingle(MusicService.MyBinder mBinder) {
        this.mBinder = mBinder;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localmusic_single, container, false);
        initList();
        mListView = view.findViewById(R.id.single_list);
        mListView.setAdapter(new SingleAdapter(getContext(), mMusicList,this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPosition = position;
                if (mLastPosition == -1) {
                    //更改图标样式
                    View currentHeadIcon = mListView.getChildAt(mCurrentPosition).findViewById(R.id.single_list_head_icon);
                    currentHeadIcon.setVisibility(View.VISIBLE);

                    mLastPosition = mCurrentPosition;
                    Music music = mMusicList.get(mCurrentPosition);
                    String path = music.getmFolder();
                    //设置新音乐
                    mBinder.setMusic(path);
                    //播放音乐
                    mBinder.playMusic();
                } else {
                    if (mCurrentPosition == mLastPosition) {

                    } else {
                        //更改图标样式
                        View lastHeadIcon = mListView.getChildAt(mLastPosition).findViewById(R.id.single_list_head_icon);
                        lastHeadIcon.setVisibility(View.GONE);
                        View currentHeadIcon = mListView.getChildAt(mCurrentPosition).findViewById(R.id.single_list_head_icon);
                        currentHeadIcon.setVisibility(View.VISIBLE);

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
        return view;
    }

    private void initList() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = mDb.query(DbConstant.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int columnIdIndex = cursor.getColumnIndex(DbConstant.MUSIC_COLUMN_ID);
                int columnSingerIndex = cursor.getColumnIndex(DbConstant.MUSIC_COLUMN_SINGER);
                int columnSongIndex = cursor.getColumnIndex(DbConstant.MUSIC_COLUMN_SONG);
                int columnSpecialIndex = cursor.getColumnIndex(DbConstant.MUSIC_COLUMN_SPECIAL);
                int columnFolderIndex = cursor.getColumnIndex(DbConstant.MUSIC_COLUMN_FOLDER);
                int id = cursor.getInt(columnIdIndex);
                mIndexList.add(id);
                String singer = cursor.getString(columnSingerIndex);
                String song = cursor.getString(columnSongIndex);
                String special = cursor.getString(columnSpecialIndex);
                String folder = cursor.getString(columnFolderIndex);
                mMusicList.add(new Music(singer, song, special, folder));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void itemClick(View v, int position, View headIcon) {
        mCurrentPosition = position;
        mHeadIcon = headIcon;
    }

    @Override
    public void playMusic() {

    }

    @Override
    public void lastMusic() {

    }

    @Override
    public void nextMusic() {

    }
}
