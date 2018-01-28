package com.example.mohammad.cloudimages.Database.API;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mohammad.cloudimages.Database.Entity.Picture;

import java.util.List;

/**
 * Created by Mohammad on 1/24/2018.
 */

@Dao
public interface PictureDAO {

    @Query("SELECT * FROM picture")
    List<Picture> getAll();

    @Query("SELECT * FROM picture WHERE pid =(SELECT MAX(pid) FROM Picture)")
    Picture getLastPicture();

    @Query("UPDATE picture SET url = :url, updated_at = :updatedAt  WHERE pid = :pid")
    void update(long pid, String url, String updatedAt);

    @Insert
    long[] insertAll(Picture... pictures);

    @Delete
    void delete(Picture picture);

}
