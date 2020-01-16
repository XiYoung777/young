package com.template.young;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.template.young.fragment.FragmentContentLocalMusic;
import com.template.young.fragment.FragmentPlaybar;
import com.template.young.fragment.FragmentSingle;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;



public class LocalMusicActivity extends AppCompatActivity {

    private MusicService.MyBinder mBinder;
    private Context mContext = this;
    private FragmentSingle mFragmentSingle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        //初始化单击事件
        initClick();
        //动态加载content
        dynamicLoadingContent();
        //动态加载fragment
        dynamicLoadingPlaybar();
    }

    private void dynamicLoadingContent() {
        MyApplication application = (MyApplication) getApplicationContext();
        mBinder = application.getmBinder();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.localmusic_content, new FragmentContentLocalMusic(mBinder));
        transaction.commit();
    }

    private void dynamicLoadingPlaybar() {
        MyApplication application = (MyApplication) getApplicationContext();
        mBinder = application.getmBinder();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_localmusic, new FragmentPlaybar(mBinder));
        transaction.commit();
    }

    private void initClick() {
        ImageView imageViewBack = findViewById(R.id.localmusic_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ImageView imageViewMore = findViewById(R.id.localmusic_more);
        imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(imageViewMore);
            }
        });
    }


    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_localmusic, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }
}
