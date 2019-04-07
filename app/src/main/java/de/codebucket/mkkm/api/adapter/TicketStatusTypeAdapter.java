package de.codebucket.mkkm.api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import de.codebucket.mkkm.model.Ticket;

public class TicketStatusTypeAdapter extends TypeAdapter<Ticket.TicketStatus> {

    @Override
    public void write(JsonWriter out, Ticket.TicketStatus value) throws IOException {
        out.value(value.toString().toLowerCase());
    }

    @Override
    public Ticket.TicketStatus read(JsonReader in) throws IOException {
        return Ticket.TicketStatus.valueOf(in.nextString().toUpperCase());
    }
}