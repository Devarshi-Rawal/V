package com.devarshi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devarshi.vault.ImageDetailActivity;
import com.devarshi.safdemo.R;
import com.devarshi.vault.VideoPlayActivity;

import java.io.File;
import java.util.ArrayList;

public class HiddenItemsAdapter extends RecyclerView.Adapter<HiddenItemsAdapter.RecyclerViewHolder>{

    private final Activity context;
    private final ArrayList<String> imagePathArrayList;

    // on below line we have created a constructor.
    public HiddenItemsAdapter(Activity context, ArrayList<String> imagePathArrayList) {
        this.context = context;
        this.imagePathArrayList = imagePathArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout in this method which we have created.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        // on below line we are getting th file from the
        // path which we have stored in our list.
        File imgFile = new File(imagePathArrayList.get(position));

        // on below line we are checking if tje file exists or not.
        if (imgFile.exists()) {

            if (imgFile.toString().endsWith(".jpg") || imgFile.toString().endsWith(".png") || imgFile.toString().endsWith(".jpeg")) {
                // if the file exists then we are displaying that file in our image view using picasso library.
//            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.imageIV);
                Glide.with(context).load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.imageIV);
                holder.imageViewPlay.setVisibility(View.INVISIBLE);

                // on below line we are adding click listener to our item of recycler view.
                holder.imageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // inside on click listener we are creating a new intent
                        Intent i = new Intent(context, ImageDetailActivity.class);

                        // on below line we are passing the image path to our new activity.
                        i.putExtra("imagePathArrayList", imagePathArrayList);
                        i.putExtra("position",position);

                        // at last we are starting our activity.
                        context.startActivityForResult(i,14);
                    }
                });
            }
            else if (imgFile.toString().endsWith(".mp4")){

                Glide.with(context).load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.imageIV);
                holder.imageViewPlay.setVisibility(View.VISIBLE);

                holder.imageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, VideoPlayActivity.class);

                        i.putExtra("vidPath",imagePathArrayList.get(position));
                        i.putExtra("position",position);

                        context.startActivity(i);
                    }
                });
            }
            else if (imgFile.toString().endsWith(".gif")){

                Glide.with(context).load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.imageIV);
                holder.imageViewPlay.setVisibility(View.VISIBLE);

                holder.imageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ImageDetailActivity.class);

                        i.putExtra("imagePathArrayList",imagePathArrayList);
                        i.putExtra("position",position);

                        context.startActivityForResult(i,14);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        // this method returns
        // the size of recyclerview
        return imagePathArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our views.
        private final ImageView imageIV;
        private final ImageView imageViewPlay;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            imageIV = itemView.findViewById(R.id.idIVImage);
            imageViewPlay = itemView.findViewById(R.id.playImageView);
        }
    }
}
