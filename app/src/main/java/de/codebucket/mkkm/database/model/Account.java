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
    @SerializedName("city")
    private String city;

    @ColumnInfo
    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("commune")
    private String commune;

    @ColumnInfo
    @SerializedName("street")
    private String street;

    @ColumnInfo
    @SerializedName("house_number")
    private String houseNumber;

    @ColumnInfo
    @SerializedName("flat_number")
    private String flatNumber;

    @ColumnInfo
    @SerializedName("email")
    private String email;

    @ColumnInfo
    @SerializedName("foreigner")
    private boolean foreigner;

    @ColumnInfo
    @SerializedName("notifications")
    private boolean notifications;

    @ColumnInfo
    @SerializedName("photo_id")
    private String photoId;

    @ColumnInfo
    @SerializedName("status")
    private String status;

    @ColumnInfo
    @SerializedName("create_date")
    private Date createDate;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isForeigner() {
        return foreigner;
    }

    public void setForeigner(boolean foreigner) {
        this.foreigner = foreigner;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
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
}
