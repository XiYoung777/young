package com.template.young;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.template.young.Constant.MessageOrder;
import com.template.young.fragment.FragmentContentLocalMusic;
import com.template.young.fragment.FragmentPlaybar;
import com.template.young.fragment.FragmentSingle;
import com.template.young.model.MyApplication;
import com.template.young.service.MusicService;


public class LocalMusicActivity extends AppCompatActivity {

    private LinearLayout mMusicPlayBar;
    private MusicService.MyBinder mBinder;
    private Context mContext = this;
    private FragmentSingle mFragmentSingle;
    private MyApplication mApplication;
    private View mPlayBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        mApplication = (MyApplication) getApplication();

        //初始化单击事件
        initClick();
        //动态加载content
        dynamicLoadingContent();
        //动态加载playBar
        dynamicLoadingPlaybar();
        //设置playbar单击事件
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

    /**
     * 初始化LocalMusicContent
     */
    private void dynamicLoadingContent() {
        MyApplication application = (MyApplication) getApplicationContext();
        mBinder = application.getmBinder();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.localmusic_content, new FragmentContentLocalMusic(mBinder, mContext));
        transaction.commit();
    }

    /**
     * 初始化PlayBar
     */
    private void dynamicLoadingPlaybar() {
        MyApplication application = (MyApplication) getApplicationContext();
        mBinder = application.getmBinder();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentPlaybar fragmentPlaybar = new FragmentPlaybar(mBinder, mContext);
        transaction.replace(R.id.activity_localmusic, fragmentPlaybar);
        transaction.commit();
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.OBTAIN_MUSICLIST);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.SAVE_LOCALMUSIC_BAR);
        mApplication.getmHandler().sendEmptyMessage(MessageOrder.LOAD_LOCALMUSIC_PLAYBAR);
    }

    /**
     * 设置单击事件
     */
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


    /**
     * 设置下拉菜单
     * @param view
     */
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

    @Override
    protected void onResume() {
        super.onResume();
        //设置本地音乐界面的Flag
        mApplication.setmLocalMusicFlag(true);
        if (!mApplication.ismLocalMusicFirstStart()) {
            //加载playbar的具体信息
            mApplication.getmHandler().sendEmptyMessage(MessageOrder.LOAD_LOCALMUSIC_PLAYBAR);
        }
        mApplication.setmLocalMusicFirstStart(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //设置本地音乐界面的Flag
        mApplication.setmLocalMusicFlag(false);
    }
}
