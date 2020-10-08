package de.codebucket.mkkm.database.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo
    @SerializedName("passenger_id")
    private String passengerId;

    @ColumnInfo
    @SerializedName("passenger_sequence")
    private String passengerSequence;

    @ColumnInfo
    @SerializedName("first_name")
    private String firstName;

    @ColumnInfo
    @SerializedName("last_name")
    private String lastName;

    @ColumnInfo
    @SerializedName("pesel")
    private String pesel;

    @ColumnInfo
    @SerializedName("birth_date")
    private Date birthDate;

    @ColumnInfo
    @SerializedName("email")
    private String email;

    @ColumnInfo
    @SerializedName("photo_id")
    private String photoId;

    @ColumnInfo
    @SerializedName("status")
    private String status;

    @ColumnInfo
    @SerializedName("create_date")
    private Date createDate;

    @ColumnInfo
    @SerializedName("citizen_guid")
    private String citizenGuid;

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerSequence() {
        return passengerSequence;
    }

    public void setPassengerSequence(String passengerSequence) {
        this.passengerSequence = passengerSequence;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCitizenGuid() {
        return citizenGuid;
    }

    public void setCitizenGuid(String citizenGuid) {
        this.citizenGuid = citizenGuid;
    }
}
