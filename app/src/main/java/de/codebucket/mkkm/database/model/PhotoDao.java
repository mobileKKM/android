package de.codebucket.mkkm.database.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photos")
    List<Photo> getAll();

    @Query("SELECT * FROM photos WHERE passengerId = :id")
    List<Photo> getAllByPassenger(String id);

    @Query("SELECT * FROM photos WHERE photoId = :id")
    Photo getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo);
}
