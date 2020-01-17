package com.template.young;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.template.young.util.ImageFilter;

public class MusicDetailActivity extends AppCompatActivity {

    private ImageView mMusicBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);

        //隐藏statusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mMusicBackground = findViewById(R.id.music_detail_background);

        String mediaUri = "/sdcard/Music/Alan Silvestri - The Avengers.mp3";
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaUri);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        if (picture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            Bitmap blurBitmap = ImageFilter.blurBitmap(this, bitmap, 20f);
            mMusicBackground.setImageBitmap(blurBitmap);
        }
    }
}
