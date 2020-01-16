package com.template.young.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.template.young.Constant.DbConstant;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "young.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_MUSIC = "music";

    public static final String SQL_CREATE_TABLE_MUSIC = "create table " + TABLE_MUSIC + " (" +
            DbConstant.MUSIC_COLUMN_ID + " integer primary key autoincrement," +
            DbConstant.MUSIC_COLUMN_SINGER + " varchar(20)," +
            DbConstant.MUSIC_COLUMN_SONG + " varchar(11)," +
            DbConstant.MUSIC_COLUMN_SPECIAL + " varchar(20)," +
            DbConstant.MUSIC_COLUMN_FOLDER + " varchar(20));";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_MUSIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
