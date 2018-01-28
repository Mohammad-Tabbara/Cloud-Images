package com.example.mohammad.cloudimages.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mohammad.cloudimages.Database.Entity.Picture;
import com.example.mohammad.cloudimages.GalleryItemActivity;
import com.example.mohammad.cloudimages.R;

import java.io.File;
import java.util.List;

/**
 * Created by Mohammad on 1/24/2018.
 */

public class GalleryRVAdapter extends RecyclerView.Adapter<GalleryRVAdapter.GalleryViewHolder> {

    private Context context;
    private List<Picture> pictures;

    public GalleryRVAdapter(Context context, List<Picture> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View rootView = View.inflate(context, R.layout.gallery_item,null);
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item,parent,false);
        return new GalleryViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, final int position) {
        //TODO: Add gallery functionality
        String filePath = pictures.get(position).getFilePath();
        Glide.with(context).load(filePath).into(holder.getPictureView());
        holder.getPictureView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GalleryItemActivity.class);
                intent.putExtra("selected_picture",pictures.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    protected class GalleryViewHolder extends RecyclerView.ViewHolder{

        private ImageView pictureView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            pictureView = itemView.findViewById(R.id.picture_view);
        }

        public ImageView getPictureView() {
            return pictureView;
        }

        public void setPictureView(ImageView pictureView) {
            this.pictureView = pictureView;
        }

    }
}
