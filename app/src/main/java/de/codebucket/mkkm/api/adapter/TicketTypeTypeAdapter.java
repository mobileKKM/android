package de.codebucket.mkkm.api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import de.codebucket.mkkm.model.Ticket;

public class TicketTypeTypeAdapter extends TypeAdapter<Ticket.TicketType> {

    @Override
    public void write(JsonWriter out, Ticket.TicketType value) throws IOException {
        out.value(value.toString().toLowerCase());
    }

    @Override
    public Ticket.TicketType read(JsonReader in) throws IOException {
        switch (in.nextString()) {
            case "network_first_zone":
                return Ticket.TicketType.NETWORK_FIRST_ZONE;
            case "network_second_zone":
                return Ticket.TicketType.NETWORK_SECOND_ZONE;
            default:
                return Ticket.TicketType.SELECTED_LINES;
        }
    }
}
