package com.template.young.util;

import android.util.Log;

import com.template.young.callback.ILrcBulider;
import com.template.young.callback.impl.DefaultLrcBulider;
import com.template.young.model.LrcRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class LyricUtil {

    private static ILrcBulider mLiricBuilder = new DefaultLrcBulider();
    //,okhttp3.Callback callback
    public static List<LrcRow> requstLrcData(final String name) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Log.i(TAG, "requstLrcData: " + name);
        String nameWithOutBlank = name.trim();//去掉空格
        Log.i(TAG, "requstLrcData:" + nameWithOutBlank);
        Request request = new Request.Builder()
                .url("http://gecimi.com/api/lyric/" + nameWithOutBlank)
                .build();
        Response execute = client.newCall(request).execute();
        String lyricUri = parseJOSNWithGSON(execute, 1);
        String lyric = getLrcFromAssets(lyricUri);
        List<LrcRow> lrcRows = mLiricBuilder.getLrcRows(lyric);
        return lrcRows;
    }


    public static String parseJOSNWithGSON(Response response, int c) {
        try {
            String ResponsData = response.body().string();
            JSONObject jsonObject = new JSONObject(ResponsData);
            int count = Integer.parseInt(jsonObject.getString("count"));
            Log.i("TAG", "parseJOSNWithGSONCOUNT:" + count);
            if (count >= c) {
                String result = jsonObject.getString("result");
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject1 = jsonArray.getJSONObject(c - 1);
                String url = jsonObject1.getString("lrc");
                Log.i("TAG", "parseJOSNWithGSON:1 " + url);
                return url;
            } else {
                Log.i("TAG", "parseJOSNWithGSON: " + c);
                return "";
            }
        } catch (Exception e) {

        }
        return "";

    }


    public static String getLrcFromAssets(String Url) {
        Log.i("first", "getLrcFromAssets: " + Url);
        if (Url.equals("")) {
            return "";
        }
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            InputStream input = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String line = "";
            String result = "";
            while ((line = in.readLine()) != null) {//逐行读取
                if (line.trim().equals(""))
                    continue;
                result += line + "\r\n";
                Log.i("first", "getLrcFromAssets: " + result);
            }
            Log.i("total", "getLrcFromAssets: " + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
