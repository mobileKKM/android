package de.codebucket.mkkm.database.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos")
public class Photo {

    @NonNull
    @PrimaryKey
    @ColumnInfo
    private String photoId;

    @ColumnInfo
    private String passengerId;

    @NonNull
    @ColumnInfo
    private Bitmap bitmap;

    private Photo(String photoId, String passengerId, Bitmap bitmap) {
        this.photoId = photoId;
        this.passengerId = passengerId;
        this.bitmap = bitmap;
    }

    @NonNull
    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(@NonNull String photoId) {
        this.photoId = photoId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    @NonNull
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(@NonNull Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static Photo fromBitmap(Account account, Bitmap bitmap) {
        return new Photo(account.getPhotoId(), account.getPassengerId(), bitmap);
    }
}
