package de.codebucket.mkkm.model;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tickets")
public class Ticket {

    @PrimaryKey
    private String ticketId;

    private String passengerId;

    private TicketStatus status;

    private TicketKind kind;

    private TicketType type;

    private boolean citizen;

    private Date purchaseDate;

    private Date validFrom;

    private Date expireDate;

    private int monthsPeriod;

    private int daysPeriod;

    private double price;

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

    public enum TicketStatus {
        ACTIVE, FUTURE, PENDING
    }

    public enum TicketKind {
        NORMAL, HALF_PRICE, SEMESTER
    }

    public enum TicketType {
        NETWORK_FIRST_ZONE, NETWORK_SECOND_ZONE, SELECTED_LINES
    }

    public class TicketLine {

        private int line;

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
