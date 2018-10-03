package de.codebucket.mkkm.database.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photos")
    List<Photo> getAll();

    @Query("SELECT * FROM photos WHERE photoId = :id")
    Photo getById(String id);
}
