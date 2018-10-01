package de.codebucket.mkkm.database.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TicketDao {

    @Query("SELECT * FROM tickets")
    List<Ticket> getAll();

    @Insert
    void insertAll(Ticket... tickets);

    @Delete
    void delete(Ticket ticket);
}
