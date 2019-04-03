package de.codebucket.mkkm.model;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserData {

    @PrimaryKey
    private String passengerId;

    private int passengerSequence;

    private String firstName;

    private String lastName;

    private String pesel;

    private Date birthDate;

    private String email;

    private String photoId;

    private UserStatus status;

    private Date createDate;

    public UserData(String passengerId, int passengerSequence, String firstName, String lastName, String pesel, Date birthDate, String email, String photoId, UserStatus status, Date createDate) {
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
        APPROVED, PENDING, UNDEFINED
    }
}

