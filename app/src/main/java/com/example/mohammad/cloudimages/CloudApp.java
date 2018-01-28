package com.example.mohammad.cloudimages;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohammad on 1/19/2018.
 */

public class CloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Map config = new HashMap();
        config.put("cloud_name", "ds4im0kg6");
        MediaManager.init(this,config);
    }
}
