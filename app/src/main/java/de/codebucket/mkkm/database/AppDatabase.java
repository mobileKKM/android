package de.codebucket.mkkm.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.codebucket.mkkm.database.converter.DateTypeConverter;
import de.codebucket.mkkm.database.converter.StringArrayConverter;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.AccountDao;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.database.model.TicketDao;

@Database(entities = {Account.class, Ticket.class }, version = 2)
@TypeConverters({ DateTypeConverter.class, StringArrayConverter.class })
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountDao accountDao();

    public abstract TicketDao ticketDao();
}
