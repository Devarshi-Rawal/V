package com.devarshi.vault;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.devarshi.Adapter.HiddenItemsAdapter;
import com.devarshi.safdemo.R;

import java.io.File;
import java.util.ArrayList;

public class RetrievedImagesActivity extends AppCompatActivity {

    RecyclerView recyclerViewRetrieved;
    SwipeRefreshLayout retrievedSwipeRefresh;

    HiddenItemsAdapter hiddenItemsAdapter;
    ArrayList<String> retrievedArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieved_images);

        findViews();

        initViews();

    }

    public void findViews(){
        recyclerViewRetrieved = findViewById(R.id.retrievedRv);
        retrievedSwipeRefresh = findViewById(R.id.swipeRefreshRetrieved);
    }

    public void initViews(){
        retrievedArrayList = new ArrayList<>();

        /*if (retrievedArrayList == null){
            recyclerViewRetrieved.setVisibility(View.INVISIBLE);
        }*/

        loadRetrievedRecyclerView();

        retrievedSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrievedSwipeRefresh.setRefreshing(true);
                loadRetrievedRecyclerView();
                retrievedSwipeRefresh.setRefreshing(false);
            }
        });
    }

    public void loadRetrievedRecyclerView(){

        retrievedArrayList.clear();
        hiddenItemsAdapter = new HiddenItemsAdapter(this,retrievedArrayList);

        if (retrievedArrayList != null){
            File folder = new File(Environment.getExternalStorageDirectory(),"/Backup");
            if (folder.exists()) {
                for (File file : folder.listFiles()) {
                    String fname = file.getPath();
                    if (hiddenItemsAdapter.getItemCount() != 0) {
                        retrievedArrayList.add(hiddenItemsAdapter.getItemCount(), fname);
                    } else {
                        retrievedArrayList.add(0, fname);
                    }
                }
            }
        }
        GridLayoutManager manager = new GridLayoutManager(RetrievedImagesActivity.this, 4);
        recyclerViewRetrieved.setLayoutManager(manager);
        recyclerViewRetrieved.setAdapter(hiddenItemsAdapter);
    }
}