package de.codebucket.mkkm.model;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "accounts")
public class UserAccount {

    @PrimaryKey
    @SerializedName("passenger_id")
    private String passengerId;

    @SerializedName("passenger_sequence")
    private int passengerSequence;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("pesel")
    private String pesel;

    @SerializedName("birth_date")
    private Date birthDate;

    @SerializedName("email")
    private String email;

    @SerializedName("photo_id")
    private String photoId;

    @SerializedName("status")
    private UserStatus status;

    @SerializedName("create_date")
    private Date createDate;

    public UserAccount(String passengerId, int passengerSequence, String firstName, String lastName, String pesel, Date birthDate, String email, String photoId, UserStatus status, Date createDate) {
        this.passengerId = passengerId;
        this.passengerSequence = passengerSequence;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.birthDate = birthDate;
        this.email = email;
        this.photoId = photoId;
        this.status = status;
        this.createDate = createDate;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public int getPassengerSequence() {
        return passengerSequence;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoId() {
        return photoId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public enum UserStatus {
        @SerializedName("approved")
        APPROVED,

        @SerializedName("pending")
        PENDING,

        @SerializedName("undefined")
        UNDEFINED
    }
}

