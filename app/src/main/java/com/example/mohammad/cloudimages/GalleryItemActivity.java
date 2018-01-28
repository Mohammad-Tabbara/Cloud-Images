package com.example.mohammad.cloudimages;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.mohammad.cloudimages.Database.API.AppDatabase;
import com.example.mohammad.cloudimages.Database.Entity.Picture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GalleryItemActivity extends AppCompatActivity {

    AppDatabase db;

    private ImageView pictureView;
    private Button reupload;
    private Picture picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item);
        db = Room.databaseBuilder(this,AppDatabase.class,"pictures-db").build();
        Intent intent = getIntent();
        picture = (Picture) intent.getSerializableExtra("selected_picture");
        initViews();
        if(picture.getUrl() == null){
            enableUpload();
        }
    }

    private void initViews() {
        pictureView = findViewById(R.id.picture);
        Glide.with(this).load(picture).into(pictureView);
        reupload = findViewById(R.id.reupload);
    }

    private void enableUpload() {
        reupload.setVisibility(View.VISIBLE);
        reupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(db,picture.getFilePath(),picture.getPid());
            }
        });
    }
    private void uploadImage(final AppDatabase db, String photoPath, final long pid) {
        MediaManager.get().upload(photoPath).unsigned("gm0z0bac").callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                if (resultData != null && resultData.size() > 0) {
                    if (resultData.containsKey("url")) {
                        final String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        final String imageURL = (String) resultData.get("url");
                        Thread updatePictureRow = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db.pictureDao().update(pid,imageURL,dateTime);
                            }
                        });
                        updatePictureRow.start();
                    }
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch();
    }
}
