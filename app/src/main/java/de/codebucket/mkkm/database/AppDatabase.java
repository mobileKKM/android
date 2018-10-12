package de.codebucket.mkkm.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.codebucket.mkkm.database.converter.BitmapBase64Converter;
import de.codebucket.mkkm.database.converter.DateTypeConverter;
import de.codebucket.mkkm.database.converter.StringArrayConverter;
import de.codebucket.mkkm.database.converter.TicketStatusConverter;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.AccountDao;
import de.codebucket.mkkm.database.model.Photo;
import de.codebucket.mkkm.database.model.PhotoDao;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.database.model.TicketDao;

@Database(entities = {Account.class, Photo.class, Ticket.class }, version = 4, exportSchema = false)
@TypeConverters({ BitmapBase64Converter.class, DateTypeConverter.class, StringArrayConverter.class, TicketStatusConverter.class })
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountDao accountDao();

    public abstract PhotoDao photoDao();

    public abstract TicketDao ticketDao();
}
