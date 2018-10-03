package de.codebucket.mkkm.database.model;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TicketDao {

    @Query("SELECT * FROM tickets")
    List<Ticket> getAll();

    @Query("SELECT * FROM tickets WHERE passengerId = :id")
    List<Ticket> getAllForPassenger(String id);

    @Query("SELECT * FROM tickets WHERE passengerId = :id AND status = 'active' AND expireDate < :expiration")
    List<Ticket> getExpiredForPassenger(String id, Date expiration);

    @Insert
    void insertAll(List<Ticket> tickets);

    @Delete
    void delete(Ticket ticket);
}