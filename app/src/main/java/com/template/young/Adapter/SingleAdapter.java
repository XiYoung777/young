package com.template.young.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.template.young.R;
import com.template.young.model.Music;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class SingleAdapter extends BaseAdapter {

    private List<Music> mMusicList;
    private LayoutInflater mInflater;
    private SingleCallback mCallback;
    private Context mContext;
    private MyApplication mApplication;
    private RelativeLayout mLastHeadIcon;
    private int mLastPosition = 0;
    private List<Integer> mIndexList;
    private MusicService.MyBinder mBinder;
    private View mPlayBar;

    public interface SingleCallback {
        public void itemClick(View v,int position, View headIcon);
    }

    public SingleAdapter(Context context, List<Music> contentList, SingleCallback callback, List<Integer> mIndexList) {
        mContext = context;
        mMusicList = contentList;
        mInflater = LayoutInflater.from(context);
        mApplication = (MyApplication) context.getApplicationContext();
        mCallback = callback;
        this.mIndexList = mIndexList;
        mBinder = mApplication.getmBinder();
        mPlayBar = mApplication.getmLocalMusicBar();
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Music getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mMusicList.get(position).getmType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //获取当前项
        final Music item = getItem(position);
        final View view;
        final ViewHolder viewHolder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.localmusic_single_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mVoiceImageView = view.findViewById(R.id.list_item_voice);
            viewHolder.mSongTextView = view.findViewById(R.id.list_item_song);
            viewHolder.mCheckImageView = view.findViewById(R.id.list_item_check);
            viewHolder.mSingerView = view.findViewById(R.id.list_item_singer);
            viewHolder.mOptionView = view.findViewById(R.id.list_item_option);
            viewHolder.mHeadIcon = view.findViewById(R.id.single_list_head_icon);
            viewHolder.mSingleItem = view.findViewById(R.id.single_item);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mVoiceImageView.setImageResource(R.drawable.icon_bar_voice);
        viewHolder.mSongTextView.setText(item.getmSong());
        viewHolder.mCheckImageView.setImageResource(R.drawable.icon_bar_voice);
        viewHolder.mSingerView.setText(item.getmSinger());
        viewHolder.mOptionView.setImageResource(R.drawable.toolbar_more);
        viewHolder.mOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.itemClick(v, position, viewHolder.mHeadIcon);
            }
        });
        viewHolder.mSingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastPosition = mApplication.getmPosition();
                //将上一个设置为gone
                mLastHeadIcon = mApplication.getmSingleHeadIcon();
                if (mLastHeadIcon != null) {
                    mLastHeadIcon.setVisibility(View.GONE);
                }
                mMusicList.get(mLastPosition).setmType(0);
                //设置当前的为visible
                viewHolder.mHeadIcon.setVisibility(View.VISIBLE);
                //保存当前的组件
                mApplication.setmSingleHeadIcon(viewHolder.mHeadIcon);
                mMusicList.get(position).setmType(1);
                //更新playbar信息
                setPlaybarData(position, mPlayBar);
                //设置新音乐
                mBinder.setMusic(mMusicList.get(position).getmFolder());
                //播放音乐
                mBinder.playMusic();
                ImageView playImage = mPlayBar.findViewById(R.id.playbar_play_image);
                if (mBinder.isPlaying()) {
                    playImage.setImageResource(R.drawable.playbar_pause);
                } else {
                    playImage.setImageResource(R.drawable.playbar_start);
                }
                mApplication.setmPosition(position);
            }
        });
        /*if (mMusicList.get(position).getmType() == 1) {
            viewHolder.mHeadIcon.setVisibility(View.VISIBLE);
        }*/
        return view;
    }/**
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

    public class ViewHolder {
        private ImageView mVoiceImageView;
        private TextView mSongTextView;
        private ImageView mCheckImageView;
        private TextView mSingerView;
        private ImageView mOptionView;
        private RelativeLayout mHeadIcon;
        private LinearLayout mSingleItem;
    }
}
