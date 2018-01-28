package com.example.mohammad.cloudimages.Database.API;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.mohammad.cloudimages.Database.Entity.Picture;

/**
 * Created by Mohammad on 1/24/2018.
 */

@Database(entities =  {Picture.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract  PictureDAO pictureDao();
}
