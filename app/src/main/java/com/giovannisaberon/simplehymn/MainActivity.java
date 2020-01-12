package com.giovannisaberon.simplehymn;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HymnListAdapter.HymnAdapterListener {

    String[] dataset;
    private RecyclerView recyclerView;
    private HymnListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<String> hymnList;
    private SearchView searchView;
    private SharedPreferences pref;  // 0 - for private mode
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        dataset = res.getStringArray(R.array.titles_array);
        hymnList = Arrays.asList( dataset );
        Log.i("dataset", dataset.toString());
        loadRecyclerView();

    }

    private void loadRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        mAdapter = new HymnListAdapter(hymnList, this, this);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onHymnSelected(HymnData hymnData) {
        Toast.makeText(getApplicationContext(), "Selected: " + hymnData.getNumber(), Toast.LENGTH_LONG).show();
        pref = this.getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        editor.putInt("selectedHymnNumber", hymnData.getNumber());
        editor.putString("selectedHymnTitle", hymnData.getTitle());
        editor.commit();
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
    }


}
