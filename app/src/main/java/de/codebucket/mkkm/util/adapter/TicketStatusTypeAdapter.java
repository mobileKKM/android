package de.codebucket.mkkm.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import de.codebucket.mkkm.database.model.Ticket.TicketStatus;

public class TicketStatusTypeAdapter extends TypeAdapter<TicketStatus> {

    @Override
    public void write(JsonWriter out, TicketStatus value) throws IOException {
        out.value(value.toString().toLowerCase());
    }

    @Override
    public TicketStatus read(JsonReader in) throws IOException {
        return TicketStatus.valueOf(in.nextString().toUpperCase());
    }
}
