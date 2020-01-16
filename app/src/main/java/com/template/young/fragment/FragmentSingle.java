package com.template.young.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.template.young.Adapter.SingleAdapter;
import com.template.young.Constant.DbConstant;
import com.template.young.R;
import com.template.young.model.Music;
import com.template.young.util.DatabaseHelper;

import java.util.ArrayList;

public class FragmentSingle extends Fragment {

    private ListView mListView;
    private SQLiteDatabase mDb;
    private ArrayList<Music> mMusicList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localmusic_single, container, false);
        initList();
        mListView = view.findViewById(R.id.single_list);
        mListView.setAdapter(new SingleAdapter(getContext(), mMusicList));
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
                String singer = cursor.getString(columnSingerIndex);
                String song = cursor.getString(columnSongIndex);
                String special = cursor.getString(columnSpecialIndex);
                String folder = cursor.getString(columnFolderIndex);
                mMusicList.add(new Music(singer, song, special, folder));
            } while (cursor.moveToNext());
        }
    }
}
