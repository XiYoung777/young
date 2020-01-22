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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.template.young.Adapter.SingleAdapter;
import com.template.young.Constant.MessageOrder;
import com.template.young.Constant.MusicConstant;
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
    private View mView;
    private int mFirstPosition;

    public FragmentSingle(MusicService.MyBinder mBinder, Context mContext) {
        this.mBinder = mBinder;
        this.mContext = mContext;
        mApplication = (MyApplication) mContext.getApplicationContext();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localmusic_single, container, false);
        mView = view;
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
        mListView.setAdapter(new SingleAdapter(getContext(), mMusicList, this, mIndexList));
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
     *
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
        switch (v.getId()) {
            case R.id.list_item_option:
                showPopupMenu(v);
                break;
        }
    }

    @Override
    public void lastMusic() {
        mCurrentPosition = mApplication.getmPosition();
        //使上次位置的headIcon消失
        setHeadIcon(mCurrentPosition - mListView.getFirstVisiblePosition(), View.GONE);
        mMusicList.get(mCurrentPosition).setmType(0);
        if (mCurrentPosition == 0) {
            mCurrentPosition = mMusicList.size() - 1;
        } else {
            mCurrentPosition = mCurrentPosition - 1;
        }
        //保存headIcon对象用于跟随点击事件
        RelativeLayout headIcon = null;
        if (mCurrentPosition - 1 < mListView.getFirstVisiblePosition()) {
            mListView.setSelection(mCurrentPosition - 1);
            int firstVisiblePosition = mListView.getFirstVisiblePosition();
            headIcon = mListView.getChildAt(0).findViewById(R.id.single_list_head_icon);// 未生效
            setHeadIcon(mListView.getFirstVisiblePosition() - mCurrentPosition, View.VISIBLE);
        } else if (mCurrentPosition == mMusicList.size() - 1 && mListView.getFirstVisiblePosition() == 0) {
            mListView.setSelection(mMusicList.size() - 13);
            setHeadIcon(13, View.VISIBLE);
        } else {
            setHeadIcon(mCurrentPosition - mListView.getFirstVisiblePosition(), View.VISIBLE);
            headIcon = mListView.getChildAt(mCurrentPosition - mListView.getFirstVisiblePosition()).findViewById(R.id.single_list_head_icon);
        }
        mMusicList.get(mCurrentPosition).setmType(1);
        mApplication.setmSingleHeadIcon(headIcon);
        mApplication.setmPosition(mCurrentPosition);
        mBinder.setMusic(mMusicList.get(mCurrentPosition).getmFolder());
        mBinder.playMusic();
        setPlaybarData(mCurrentPosition, mPlayBar);
    }

    @Override
    public void nextMusic() {
        mCurrentPosition = mApplication.getmPosition();
        if (mCurrentPosition > MusicConstant.VISIBLECOUNT) {
            setHeadIcon(mCurrentPosition - mListView.getFirstVisiblePosition(), View.GONE);
        } else {
            //单个视图内去除
            setHeadIcon(mCurrentPosition - mListView.getFirstVisiblePosition(), View.GONE);
        }
        //更新mMusicList相应的选中类型为未选中
        mMusicList.get(mCurrentPosition).setmType(0);
        mLastPosition = mCurrentPosition;
        if (mCurrentPosition == mMusicList.size() - 1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition = mCurrentPosition + 1;
        }
        if (mCurrentPosition - mListView.getFirstVisiblePosition() > MusicConstant.VISIBLECOUNT) {
            //设置屏幕跟随
            mListView.setSelection(mListView.getFirstVisiblePosition() + 1);
        }
        //保存headIcon，使用点击的时候更改为消失
        RelativeLayout headIcon = null;
        if (mCurrentPosition == 0 && mListView.getFirstVisiblePosition() > MusicConstant.VISIBLECOUNT) {
            mListView.setSelection(mCurrentPosition);
            headIcon = mListView.getChildAt(mCurrentPosition).findViewById(R.id.single_list_head_icon); // 未生效
        } else {
            setHeadIcon(mCurrentPosition - mListView.getFirstVisiblePosition(), View.VISIBLE);
            headIcon = mListView.getChildAt(mCurrentPosition - mListView.getFirstVisiblePosition()).findViewById(R.id.single_list_head_icon);
        }
//        headIcon = mListView.getChildAt(mCurrentPosition - mListView.getFirstVisiblePosition()).findViewById(R.id.single_list_head_icon);
        mApplication.setmSingleHeadIcon(headIcon);
        //更新mMusicList相应的选中类型为选中，并保存至mApplication
        mMusicList.get(mCurrentPosition).setmType(1);
        mApplication.setmMusicList(mMusicList);
        mApplication.setmPosition(mCurrentPosition);
        mBinder.setMusic(mMusicList.get(mCurrentPosition).getmFolder());
        mBinder.playMusic();
        setPlaybarData(mCurrentPosition, mPlayBar);
    }

    /**
     * 更新playbar的信息
     *
     * @param position
     * @param view
     */
    private void setPlaybarData(int position, View view) {
        ImageView playBarImage = view.findViewById(R.id.playbar_imageview);
        String mediaUri = mMusicList.get(position).getmFolder();

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
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

    /**
     * 设置下拉菜单
     *
     * @param view
     */
    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_single_jtem_option, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.single_item_option_remove:
                        //删除单曲
                        removeSingle();
                        break;
                }
                return false;
            }
        });
        // PopupMenu关闭事件
//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(mContext, "关闭PopupMenu", Toast.LENGTH_SHORT).show();
//            }
//        });

        popupMenu.show();
    }

    /**
     * 删除单曲
     */
    private void removeSingle() {
        mFirstPosition = mListView.getFirstVisiblePosition();
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_id = ?", new String[]{Integer.toString(mIndexList.get(mCurrentPosition))});
        reloadMusicList();
    }

    /**
     * 重新加载歌曲列表
     */
    private void reloadMusicList() {
        mIndexList = new ArrayList<>();
        mMusicList = new ArrayList<>();
        initList();
        initAdapter(mView);
        mListView.setSelection(mFirstPosition);
    }
}
