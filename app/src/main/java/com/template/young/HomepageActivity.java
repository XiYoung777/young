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
        transaction.commit();
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
        //加点测试数据
        initTestData();
    }

    private void initTestData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < 5; i++) {
            values.put(DbConstant.MUSIC_COLUMN_SINGER, "张杰");
            values.put(DbConstant.MUSIC_COLUMN_SONG, "music_aisi");
            values.put(DbConstant.MUSIC_COLUMN_SPECIAL, "海贼王");
            values.put(DbConstant.MUSIC_COLUMN_FOLDER, "母鸡");
            db.insert(DbConstant.TABLE_NAME, null, values);
        }
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
