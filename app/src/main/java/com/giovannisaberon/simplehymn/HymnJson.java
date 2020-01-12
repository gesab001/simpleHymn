package com.giovannisaberon.simplehymn;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HymnJson {

    private Context context;

    public HymnJson(Context context){
        this.context = context;
    }

    public String loadJSONFromAsset(String filename) throws IOException {
        String json = "there is nothing";
        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error";
        }
        Log.i("json bible: ", json);
        return json;
    }

    public HashMap<String, ArrayList<List>> convertToHashmap(String filename) throws IOException {
        InputStream is = context.getAssets().open(filename);
        JsonReader reader = new JsonReader(new InputStreamReader(is));
        final Gson gson = new Gson();
        HashMap<String, ArrayList<List>> map = gson.fromJson(reader, HashMap.class);
        return map;
    }

    public JSONObject getJsonObject(String file) throws JSONException {
        JSONObject obj = new JSONObject(file);
        return obj;
    }

    public JSONObject getBook(JSONObject jsonBible, String book) throws JSONException {
        JSONObject jsonbook = jsonBible.getJSONObject(book);
        return jsonbook;
    }

    public JSONArray getChapter(JSONObject bible, String book, String chapter) throws JSONException {
        JSONObject jsonbook = this.getBook(bible, book);
        JSONArray jsonchapter = jsonbook.getJSONArray(chapter);
        return jsonchapter;
    }
}
