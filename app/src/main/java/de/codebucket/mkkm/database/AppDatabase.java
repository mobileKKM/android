package de.codebucket.mkkm.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.codebucket.mkkm.database.converter.*;
import de.codebucket.mkkm.database.model.*;

@Database(entities = {Account.class, Photo.class, Ticket.class }, version = 6, exportSchema = false)
@TypeConverters({ BitmapBase64Converter.class, DateTypeConverter.class, TicketLineConverter.class, TicketStatusConverter.class })
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountDao accountDao();

    public abstract PhotoDao photoDao();

    public abstract TicketDao ticketDao();
}
