package com.template.young.model;

public class Music {

    private int mId;
    private String mSinger;
    private String mSong;
    private String mSpecial;
    private String mFolder;
    private int mType = 0;

    public Music(String mSinger, String mSong, String mSpecial, String mFolder) {
        this.mSinger = mSinger;
        this.mSong = mSong;
        this.mSpecial = mSpecial;
        this.mFolder = mFolder;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmSinger() {
        return mSinger;
    }

    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    public String getmSong() {
        return mSong;
    }

    public void setmSong(String mSong) {
        this.mSong = mSong;
    }

    public String getmSpecial() {
        return mSpecial;
    }

    public void setmSpecial(String mSpecial) {
        this.mSpecial = mSpecial;
    }

    public String getmFolder() {
        return mFolder;
    }

    public void setmFolder(String mFolder) {
        this.mFolder = mFolder;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }
}
