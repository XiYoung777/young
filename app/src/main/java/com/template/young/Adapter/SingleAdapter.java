package com.template.young.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.template.young.R;
import com.template.young.model.Music;

import java.util.List;

public class SingleAdapter extends BaseAdapter {
    private List<Music> mContentList;
    private LayoutInflater mInflater;

    public SingleAdapter(Context context, List<Music> contentList) {
        mContentList = contentList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public Music getItem(int position) {
        return mContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            viewHolder.mSingerViewTel = view.findViewById(R.id.list_item_singer);
            viewHolder.mOptionViewTel = view.findViewById(R.id.list_item_option);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mVoiceImageView.setImageResource(R.drawable.icon_bar_voice);
        viewHolder.mSongTextView.setText(item.getmSong());
        viewHolder.mCheckImageView.setImageResource(R.drawable.icon_bar_voice);
        viewHolder.mSingerViewTel.setText(item.getmSinger());
        viewHolder.mOptionViewTel.setImageResource(R.drawable.toolbar_more);
        /*viewHolder.mOptionViewTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.click(v, position, viewHolder.mVhSpinner);
            }
        });*/
        return view;
    }

    public class ViewHolder {
        private ImageView mVoiceImageView;
        private TextView mSongTextView;
        private ImageView mCheckImageView;
        private TextView mSingerViewTel;
        private ImageView mOptionViewTel;
    }
}