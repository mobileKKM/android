package de.codebucket.mkkm.database.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts")
    List<Account> getAll();
}
