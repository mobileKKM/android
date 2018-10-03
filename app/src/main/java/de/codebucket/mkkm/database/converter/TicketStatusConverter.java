package de.codebucket.mkkm.database.converter;

import androidx.room.TypeConverter;

import de.codebucket.mkkm.database.model.Ticket.TicketStatus;

public class TicketStatusConverter {

    @TypeConverter
    public static String toString(TicketStatus value) {
        return value.toString().toLowerCase();
    }

    @TypeConverter
    public static TicketStatus toEnum(String value) {
        return TicketStatus.valueOf(value.toUpperCase());
    }
}
