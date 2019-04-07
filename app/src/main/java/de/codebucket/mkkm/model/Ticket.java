package de.codebucket.mkkm.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tickets")
public class Ticket {

    @PrimaryKey
    @SerializedName("ticket_id")
    private String ticketId;

    @SerializedName("passenger_id")
    private String passengerId;

    @SerializedName("status")
    private TicketStatus status;

    @SerializedName("kind")
    private TicketKind kind;

    @SerializedName("type")
    private TicketType type;

    @SerializedName("citizen")
    private boolean citizen;

    @SerializedName("purchase_date")
    private Date purchaseDate;

    @SerializedName("valid_from")
    private Date validFrom;

    @SerializedName("expire_date")
    private Date expireDate;

    @SerializedName("months_period")
    private int monthsPeriod;

    @SerializedName("days_period")
    private int daysPeriod;

    @SerializedName("price")
    private double price;

    @SerializedName("lines")
    private TicketLine[] lines;

    private boolean assigned;

    public Ticket(String ticketId, String passengerId, TicketStatus status, TicketKind kind, TicketType type, boolean citizen, Date purchaseDate, Date validFrom, Date expireDate, int monthsPeriod, int daysPeriod, double price, TicketLine[] lines, boolean assigned) {
        this.ticketId = ticketId;
        this.passengerId = passengerId;
        this.status = status;
        this.kind = kind;
        this.type = type;
        this.citizen = citizen;
        this.purchaseDate = purchaseDate;
        this.validFrom = validFrom;
        this.expireDate = expireDate;
        this.monthsPeriod = monthsPeriod;
        this.daysPeriod = daysPeriod;
        this.price = price;
        this.lines = lines;
        this.assigned = assigned;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketKind getKind() {
        return kind;
    }

    public TicketType getType() {
        return type;
    }

    public boolean isCitizen() {
        return citizen;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public int getMonthsPeriod() {
        return monthsPeriod;
    }

    public int getDaysPeriod() {
        return daysPeriod;
    }

    public double getPrice() {
        return price;
    }

    public TicketLine[] getLines() {
        return lines;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public enum TicketStatus {
        @SerializedName("active")
        ACTIVE,

        @SerializedName("future")
        FUTURE,

        @SerializedName("pending")
        PENDING
    }

    public enum TicketKind {
        @SerializedName("normal")
        NORMAL,

        @SerializedName("half_price")
        HALF_PRICE,

        @SerializedName("semester")
        SEMESTER,

        @SerializedName("undefined")
        UNDEFINED
    }

    public enum TicketType {
        @SerializedName("network_first_zone")
        NETWORK_FIRST_ZONE,

        @SerializedName("network_second_zone")
        NETWORK_SECOND_ZONE,

        @SerializedName("undefined")
        SELECTED_LINES
    }

    public static class TicketLine {

        @SerializedName("line")
        private int line;

        @SerializedName("second_zone")
        private boolean secondZone;

        public TicketLine(int line, boolean secondZone) {
            this.line = line;
            this.secondZone = secondZone;
        }

        public int getLine() {
            return line;
        }

        public boolean isSecondZone() {
            return secondZone;
        }
    }
}
