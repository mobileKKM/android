package de.codebucket.mkkm.model;

import android.graphics.Bitmap;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "aztec_pictures")
public class AztecBitmap {

    @PrimaryKey
    private String ticketId;

    private String passengerId;

    private Bitmap bitmap;

    private Date createdAt;

    public AztecBitmap(String ticketId, String passengerId, Bitmap bitmap, Date createdAt) {
        this.ticketId = ticketId;
        this.passengerId = passengerId;
        this.bitmap = bitmap;
        this.createdAt = createdAt;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public Bitmap getBitmap() {
        return aztec;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
