package de.codebucket.mkkm.database.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts")
    List<Account> getAll();

    @Query("SELECT * FROM accounts WHERE passengerId = :id")
    Account getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);
}
