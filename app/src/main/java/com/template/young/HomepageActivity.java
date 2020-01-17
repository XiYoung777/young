package com.template.young;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.template.young.Adapter.HomepageViewPagerAdapter;
import com.template.young.Constant.MessageOrder;
import com.template.young.fragment.FragmentDiscover;
import com.template.young.fragment.FragmentMine;
import com.template.young.fragment.FragmentPlaybar;
import com.template.young.fragment.FragmentVideo;
import com.template.young.fragment.FragmentYoung;
import com.template.young.model.Music;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;
import com.template.young.util.DatabaseHelper;

import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {

    private LinearLayout mMusicPlayBar;
    private DatabaseHelper mDbHelper;
    private Context mContext = this;
    private SQLiteDatabase mDb;
    private TabLayout mTabLayout;
    private ArrayList<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private MusicService.MyBinder mBinder;
    private MyApplication mApplication;
    private ArrayList<Music> mMusicList = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    loopView(msg.obj);
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MusicService.MyBinder) service;
            mApplication.setmBinder(mBinder);
            //动态加载playbar
            dynamicLoadingPlaybar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Intent intent = new Intent(mContext, MusicService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        //初始化Fragment
        initFragment();
        //初始化数据库
        initDB();
    }

    private void dynamicLoadingPlaybar() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentPlaybar fragmentPlaybar = new FragmentPlaybar(mBinder, mContext);
        transaction.replace(R.id.homepage_drawerlayout, fragmentPlaybar);
        int commit = transaction.commit();
        //初始化service里的歌单
        initServiceMusicList();
    }

    /**
     * 初始化service中的歌单
     */
    private void initServiceMusicList() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            do {
                int columnIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int columnSingerIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int columnSongIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int columnSpecialIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int columnFolderIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int id = cursor.getInt(columnIdIndex);
//                mIndexList.add(id);
                String singer = cursor.getString(columnSingerIndex);
                String song = cursor.getString(columnSongIndex);
                String special = cursor.getString(columnSpecialIndex);
                String folder = cursor.getString(columnFolderIndex);
                mMusicList.add(new Music(singer, song, special, folder));
            } while (cursor.moveToNext());
        }
        mBinder.setMusic(mMusicList.get(0).getmFolder());
        mApplication.setmMusicList(mMusicList);


        mApplication.getmHandler().sendEmptyMessage(MessageOrder.OBTAIN_MUSICLIST);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.SAVE_HOMEPAGE_BAR);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.LOAD_HOMEPAGE_PLAYBAR);
    }

    /**
     * 轮播图轮询方法
     * @param obj
     */
    private void loopView(Object obj) {
        ViewPager viewPager = (ViewPager) obj;
        int position = viewPager.getCurrentItem() + 1;
        if (position == 8) {
            position = 0;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 创建数据库
     */
    private void initDB() {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();

    }

    private void initButtonClick() {
        mDrawerLayout = findViewById(R.id.homepage_drawerlayout);
        Button button = (Button) findViewById(R.id.toolbar_btn_menu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "我终于找到问题原因了", Toast.LENGTH_LONG).show();
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    /**
     * 初始化主页的四个视图
     */
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.homepage_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.homepage_tablayout);
        HomepageViewPagerAdapter viewPagerAdapter = new HomepageViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 初始化主页的四个视图的Fragment
     */
    private void initFragment() {
        mFragmentList = new ArrayList<>();
        FragmentMine fragmentMine = new FragmentMine();
        FragmentDiscover fragmentDiscover = new FragmentDiscover(mHandler);
        FragmentYoung fragmentYoung = new FragmentYoung();
        FragmentVideo fragmentVideo = new FragmentVideo();
        mFragmentList.add(fragmentMine);
        mFragmentList.add(fragmentDiscover);
        mFragmentList.add(fragmentYoung);
        mFragmentList.add(fragmentVideo);
        //加载并绑定viewPage和tabLayout
        initViewPager();
        //绑定按钮的单击事件
        initButtonClick();
        //给playbar绑定单击事件
//        initClickOnPlayBar();
    }

    private void initClickOnPlayBar() {
        mMusicPlayBar = findViewById(R.id.playbar);
        mMusicPlayBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMusicDetail = new Intent(mContext, MusicDetailActivity.class);
                startActivity(intentMusicDetail);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApplication = (MyApplication) getApplicationContext();
        if (!mApplication.ismHomepageFirstStart()) {
            mApplication.getmHandler().sendEmptyMessage(MessageOrder.LOAD_HOMEPAGE_PLAYBAR);
        }
        mApplication.setmHomepageFirstStart(false);
    }
}
