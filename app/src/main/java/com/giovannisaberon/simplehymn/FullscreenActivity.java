package com.giovannisaberon.simplehymn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    int verseCount = 0;
    int fontminsize = 30;
    int fontmaxsize = 40;
    List<String> verseItem;
    List<String> refrain = new ArrayList();
    String refrainOn = "off";
    String verseTitle;
    private SharedPreferences pref;  // 0 - for private mode
    private SharedPreferences.Editor editor;
    private OnSwipeTouchListener onSwipeTouchListener;
    private static final boolean AUTO_HIDE = true;
    HymnJson hymnJson;
    HashMap<String, ArrayList<List>> hashMapHymns;
    TextView textView;
    ArrayList<List> versesListWithRefrains = new ArrayList<>();
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               startActivity(intent);
            }
        });
        hymnJson = new HymnJson(this);
        try {
            hashMapHymns = hymnJson.convertToHashmap("hymns.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        final int number = pref.getInt("selectedHymnNumber", 0);
        final String title = pref.getString("selectedHymnTitle", null);
        setTitle(Integer.toString(number) + " - " + title);
        textView = (TextView) mContentView;
        textView.setText(Integer.toString(number) + "\n" + title);
        final ArrayList<List> versesList = hashMapHymns.get(Integer.toString(number));
        List<String> numbertitle = new ArrayList<>();
        numbertitle.add(Integer.toString(number));
        numbertitle.add(title);
        versesListWithRefrains.add(numbertitle);
        for (int x=0; x<versesList.size(); x++){
            verseItem = versesList.get(x);
            if (verseItem.contains("Refrain")){
                refrain = verseItem;
                versesListWithRefrains.add(refrain);
            }
            else if (refrain.size()==0){
                versesListWithRefrains.add(verseItem);
            } else if (refrain.size()!=0){
                versesListWithRefrains.add(verseItem);
                versesListWithRefrains.add(refrain);
            }

        }
        verseItem = numbertitle;
        List<String> end = new ArrayList<>();
        end.add("END");
        end.add("");
        versesListWithRefrains.add(end);




        mContentView.setOnTouchListener(new OnSwipeTouchListener(FullscreenActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(FullscreenActivity.this, "top", Toast.LENGTH_SHORT).show();
                toggle();

            }
            public void onSwipeRight() {
                verseCount = verseCount - 1;
                if (verseCount<0){
                    verseCount = 0;
                }
//                Toast.makeText(FullscreenActivity.this, Integer.toString(verseCount), Toast.LENGTH_SHORT).show();

                Log.i("verseCount", Integer.toString(verseCount));
                verseItem = versesListWithRefrains.get(verseCount);
                if (verseItem!=null){
                    verseTitle = verseItem.get(0);
                    String text = verseTitle + "\n\n ";
                    String verseLyrics;
                    for (int i=1; i<verseItem.size(); i++){
                        verseLyrics = verseItem.get(i) + "\n";
                        text+= verseLyrics;
                    }
                    textView.setText(text);
                }

//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, fontminsize, fontmaxsize, 1,
//                        TypedValue.COMPLEX_UNIT_DIP);



            }
            public void onSwipeLeft() {
                verseCount = verseCount + 1;
                if (verseCount>=versesListWithRefrains.size()-1){
                    verseCount = versesListWithRefrains.size()-2;
                }
//                Toast.makeText(FullscreenActivity.this, Integer.toString(verseCount), Toast.LENGTH_SHORT).show();
                verseItem = versesListWithRefrains.get(verseCount);

                verseTitle = verseItem.get(0);
                String text = verseTitle + "\n\n ";
                String verseLyrics;
                for (int i=1; i<verseItem.size(); i++){
                    verseLyrics = verseItem.get(i) + "\n";
                    text+= verseLyrics;
                }

                textView.setText(text);
//                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, fontminsize, fontmaxsize, 1,
//                        TypedValue.COMPLEX_UNIT_DIP);






            }
            public void onSwipeBottom() {
//                Toast.makeText(FullscreenActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                toggle();
            }

        });
        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.back_button).setOnTouchListener(mDelayHideTouchListener);
    }

    public List<String> getNext(List<String> uid) {
        int idx = versesListWithRefrains.indexOf(uid);
        if (idx < 0 || idx+1 == versesListWithRefrains.size()) return null;
        return versesListWithRefrains.get(idx + 1);
    }

    public List<String> getPrevious(List<String> uid) {
        int idx = versesListWithRefrains.indexOf(uid);
        if (idx <= 0) return null;
        return  versesListWithRefrains.get(idx - 1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
