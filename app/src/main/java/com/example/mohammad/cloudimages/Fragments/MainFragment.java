package com.example.mohammad.cloudimages.Fragments;


import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mohammad.cloudimages.Database.API.AppDatabase;
import com.example.mohammad.cloudimages.Database.Entity.Picture;
import com.example.mohammad.cloudimages.R;
import com.example.mohammad.cloudimages.Util;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    AppDatabase db;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        db = Room.databaseBuilder(getContext(),AppDatabase.class,"pictures-db").build();
        final ImageView lastPicture = rootView.findViewById(R.id.last_picture);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final Picture picture = db.pictureDao().getLastPicture();
                if(picture != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getContext()).load(picture.getFilePath()).into(lastPicture);
                        }
                    });
                }
            }
        });
//        SharedPreferences sp = getContext().getSharedPreferences("CloudApp",0);
//        String lastImagePath = sp.getString("lastImagePath",null);
//        Glide.with(this).load(lastImagePath).into(lastPicture);
//        lastPicture.setVisibility(View.INVISIBLE);
//        if(lastImagePath != null){
//            Bitmap bitmap = null;
//            try {
//                bitmap = Util.fixBitmap(lastImagePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if(bitmap != null) {
//                lastPicture.setVisibility(View.VISIBLE);
//                lastPicture.setImageBitmap(bitmap);
//            }
//        }
        return rootView;
    }

}
