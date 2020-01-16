package com.template.young.Constant;

import android.net.Uri;

public class DbConstant {

    public static final String TAG = "MUSIC";

    public static final String PATH = "android.resource://com.template.young/";

    public static final String TABLE_NAME = "music";
    public static final String AUTHORITY = "com.template.young.MusicProvider";
    public static final Uri URI_PATH_MUSIC = Uri.parse("content://" + AUTHORITY + "/music");
    public static final String MUSIC_COLUMN_ID = "_id";
    public static final String MUSIC_COLUMN_SINGER = "singer";
    public static final String MUSIC_COLUMN_SONG = "song";
    public static final String MUSIC_COLUMN_SPECIAL = "special";
    public static final String MUSIC_COLUMN_FOLDER = "folder";
}
