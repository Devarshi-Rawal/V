package com.devarshi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devarshi.safdemo.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ImageSlidingAdapter extends RecyclerView.Adapter<ImageSlidingAdapter.ImageSlidingViewHolder> {

    final Context context;
    final ArrayList<String> imagePathArrayList;
    public ClickInterface clickInterface;

    public ImageSlidingAdapter(Context context, ArrayList<String> imagePathArrayList, ClickInterface clickInterface) {
        this.context = context;
        this.imagePathArrayList = imagePathArrayList;
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public ImageSlidingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_image_viewing, parent, false);
        return new ImageSlidingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSlidingViewHolder holder, int position) {

        Glide.with(context).load(imagePathArrayList.get(position)).into(holder.photoViewHiddenImage);

        holder.imageViewWriteToDb.setOnClickListener(v -> {
            clickInterface.actionOnClickOfWriteToDb(position);
            /*InfoRepository repository = new InfoRepository();
            repository.writeInfo(imagePathArrayList.get(position));
            if (repository != null){
                Toast.makeText(context, "DB written successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Writing to DB failed", Toast.LENGTH_SHORT).show();
            }*/
        });

        holder.imageViewUploadToDrive.setOnClickListener(v -> clickInterface.actionOnClickOfUploadToDrive(position));
    }

    @Override
    public int getItemCount() {
        return imagePathArrayList.size();
    }

    public static class ImageSlidingViewHolder extends RecyclerView.ViewHolder {

        PhotoView photoViewHiddenImage;
        ImageView imageViewUploadToDrive, imageViewWriteToDb;

        public ImageSlidingViewHolder(@NonNull View itemView) {
            super(itemView);

            photoViewHiddenImage = itemView.findViewById(R.id.pVHiddenImage);
            imageViewUploadToDrive = itemView.findViewById(R.id.iVUploadToDrive);
            imageViewWriteToDb = itemView.findViewById(R.id.iVWriteToDb);
        }
    }

    public interface ClickInterface {

        void actionOnClickOfWriteToDb(int position);

        void actionOnClickOfUploadToDrive(int position);
    }
}
