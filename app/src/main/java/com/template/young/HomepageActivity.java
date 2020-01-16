package com.template.young;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.template.young.Adapter.HomepageViewPagerAdapter;
import com.template.young.Constant.DbConstant;
import com.template.young.fragment.FragmentDiscover;
import com.template.young.fragment.FragmentMine;
import com.template.young.fragment.FragmentPlaybar;
import com.template.young.fragment.FragmentVideo;
import com.template.young.fragment.FragmentYoung;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;
import com.template.young.util.DatabaseHelper;

import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {

    private DatabaseHelper mDbHelper;
    private Context mContext = this;
    private SQLiteDatabase mDb;
    private TabLayout mTabLayout;
    private ArrayList<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private MusicService.MyBinder mBinder;
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
            MyApplication application = (MyApplication) getApplicationContext();
            application.setmBinder(mBinder);
            //动态加载fragment
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
        transaction.replace(R.id.homepage_drawerlayout, new FragmentPlaybar(mBinder));
        int commit = transaction.commit();
    }

    private void loopView(Object obj) {
        ViewPager viewPager = (ViewPager) obj;
        int position = viewPager.getCurrentItem() + 1;
        if (position == 8) {
            position = 0;
        }
        viewPager.setCurrentItem(position);
    }

    private void initDB() {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();

        ContentValues values1 = new ContentValues();
        values1.put(DbConstant.MUSIC_COLUMN_SINGER, "隔壁老樊");
        values1.put(DbConstant.MUSIC_COLUMN_SONG, "四块五");
        values1.put(DbConstant.MUSIC_COLUMN_SPECIAL, "四块五");
        values1.put(DbConstant.MUSIC_COLUMN_FOLDER, "/sdcard/Music/四块五 - 隔壁老樊.mp3");
        mDb.insert(DbConstant.TABLE_NAME, null, values1);

        ContentValues values2 = new ContentValues();
        values2.put(DbConstant.MUSIC_COLUMN_SINGER, "王胜");
        values2.put(DbConstant.MUSIC_COLUMN_SONG, "情深深雨蒙蒙");
        values2.put(DbConstant.MUSIC_COLUMN_SPECIAL, "新专辑");
        values2.put(DbConstant.MUSIC_COLUMN_FOLDER, "/sdcard/Music/hello.mp3");
        mDb.insert(DbConstant.TABLE_NAME, null, values2);

        ContentValues values3 = new ContentValues();
        values3.put(DbConstant.MUSIC_COLUMN_SINGER, "杨胖雨");
        values3.put(DbConstant.MUSIC_COLUMN_SONG, "情深深雨濛濛");
        values3.put(DbConstant.MUSIC_COLUMN_SPECIAL, "新专辑");
        values3.put(DbConstant.MUSIC_COLUMN_FOLDER, "情深深雨濛濛 - 杨胖雨.mp3");
        mDb.insert(DbConstant.TABLE_NAME, null, values3);


        ContentValues values4 = new ContentValues();
        values4.put(DbConstant.MUSIC_COLUMN_SINGER, "一棵小葱");
        values4.put(DbConstant.MUSIC_COLUMN_SONG, "青花瓷 (戏曲版) (Live)");
        values4.put(DbConstant.MUSIC_COLUMN_SPECIAL, "Live");
        values4.put(DbConstant.MUSIC_COLUMN_FOLDER, "/sdcard/Music/青花瓷 (戏曲版) (Live) - 一棵小葱.mp3");
        mDb.insert(DbConstant.TABLE_NAME, null, values4);
    }

    private void initLooperViewpager() {
        ArrayList<ImageView> imageViewList = new ArrayList<>();
        int[] imageId = new int[]{
                R.drawable.poster_first,
                R.drawable.poster_second,
                R.drawable.poster_third,
                R.drawable.poster_fourth,
                R.drawable.poster_fifth,
                R.drawable.poster_sixth,
                R.drawable.poster_seventh,
                R.drawable.poster_eighth,
                R.drawable.poster_ninth
        };
        for (int i = 0; i < imageId.length; i++) {
            ImageView imageView = new ImageView(this);

        }

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

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.homepage_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.homepage_tablayout);
        HomepageViewPagerAdapter viewPagerAdapter = new HomepageViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mViewPager);
    }

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
    }

}
