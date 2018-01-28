package com.example.mohammad.cloudimages.Fragments;


import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mohammad.cloudimages.Adapter.GalleryRVAdapter;
import com.example.mohammad.cloudimages.Database.API.AppDatabase;
import com.example.mohammad.cloudimages.Database.Entity.Picture;
import com.example.mohammad.cloudimages.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    AppDatabase db;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        db = Room.databaseBuilder(getContext(),AppDatabase.class,"pictures-db").build();
        final RecyclerView galleryRV = rootView.findViewById(R.id.picture_gallery);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                List<Picture> pictures = db.pictureDao().getAll();
               final GalleryRVAdapter galleryRVAdapter = new GalleryRVAdapter(getContext(),pictures);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        galleryRV.setLayoutManager(new GridLayoutManager(getContext(),2));
                        galleryRV.setAdapter(galleryRVAdapter);
                    }
                });
            }
        });
        return rootView;
    }

}
