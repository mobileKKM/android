package de.codebucket.mkkm.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.codebucket.mkkm.database.model.Ticket.TicketLine;

public class TicketLineConverter {

    private static final Gson sGson = new GsonBuilder().create();

    @TypeConverter
    public static String toString(TicketLine[] value) {
        return sGson.toJson(value);
    }

    @TypeConverter
    public static TicketLine[] toArray(String value) {
        return sGson.fromJson(value, TicketLine[].class);
    }
}
