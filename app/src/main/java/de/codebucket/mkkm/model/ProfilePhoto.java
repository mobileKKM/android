package de.codebucket.mkkm.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos")
public class ProfilePhoto {

    @PrimaryKey
    private String photoId;

    private String passengerId;

    private String filename;

    public ProfilePhoto(String photoId, String passengerId, String filename) {
        this.photoId = photoId;
        this.passengerId = passengerId;
        this.filename = filename;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFilename() {
        return filename;
    }
}
