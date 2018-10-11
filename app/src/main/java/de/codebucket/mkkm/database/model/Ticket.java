package de.codebucket.mkkm.database.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tickets")
public class Ticket implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo
    @SerializedName("ticket_id")
    private String ticketId;

    @ColumnInfo
    @SerializedName("passenger_id")
    private String passengerId;

    @ColumnInfo
    @SerializedName("status")
    private TicketStatus status;

    @ColumnInfo
    @SerializedName("kind")
    private String kind;

    @ColumnInfo
    @SerializedName("type")
    private String type;

    @ColumnInfo
    @SerializedName("purchase_date")
    private Date purchaseDate;

    @ColumnInfo
    @SerializedName("valid_from")
    private Date validFrom;

    @ColumnInfo
    @SerializedName("expire_date")
    private Date expireDate;

    @ColumnInfo
    @SerializedName("months_period")
    private int monthsPeriod;

    @ColumnInfo
    @SerializedName("days_period")
    private int daysPeriod;

    @ColumnInfo
    @SerializedName("price")
    private double price;

    @ColumnInfo
    @SerializedName("lines")
    private String[] lines;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public int getMonthsPeriod() {
        return monthsPeriod;
    }

    public void setMonthsPeriod(int monthsPeriod) {
        this.monthsPeriod = monthsPeriod;
    }

    public int getDaysPeriod() {
        return daysPeriod;
    }

    public void setDaysPeriod(int daysPeriod) {
        this.daysPeriod = daysPeriod;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Ticket) {
            return ticketId.equals(((Ticket) obj).getTicketId());
        }

        return super.equals(obj);
    }

    public enum TicketStatus {
        ACTIVE, FUTURE, PENDING;

        public boolean isActive() {
            return this == ACTIVE || this == FUTURE;
        }
    }
}
