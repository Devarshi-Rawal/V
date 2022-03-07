package com.devarshi.vault;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.devarshi.Adapter.HiddenItemsAdapter;
import com.devarshi.safdemo.R;

import java.util.ArrayList;

public class RetrievedImagesActivity extends AppCompatActivity {

    RecyclerView recyclerViewRetrieved;
    ArrayList<String> retrievedArrayList;

    HiddenItemsAdapter hiddenItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieved_images);

        recyclerViewRetrieved = findViewById(R.id.retrievedRv);

        GridLayoutManager manager = new GridLayoutManager(RetrievedImagesActivity.this, 4);
        recyclerViewRetrieved.setLayoutManager(manager);

        Intent intent;
        intent = getIntent();
        retrievedArrayList = intent.getStringArrayListExtra("restoreImagesList");

        Log.d(TAG, "onCreate: retrievedArrayList: " + retrievedArrayList);

        hiddenItemsAdapter = new HiddenItemsAdapter(this,retrievedArrayList);
        recyclerViewRetrieved.setAdapter(hiddenItemsAdapter);
    }
}