package de.codebucket.mkkm.api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import de.codebucket.mkkm.model.Ticket;

public class TicketKindTypeAdapter extends TypeAdapter<Ticket.TicketKind> {

    @Override
    public void write(JsonWriter out, Ticket.TicketKind value) throws IOException {
        out.value(value.toString().toLowerCase());
    }

    @Override
    public Ticket.TicketKind read(JsonReader in) throws IOException {
        switch (in.nextString()) {
            case "normal":
                return Ticket.TicketKind.NORMAL;
            case "half_price":
                return Ticket.TicketKind.HALF_PRICE;
            case "semester":
                return Ticket.TicketKind.SEMESTER;
            default:
                return Ticket.TicketKind.UNDEFINED;
        }
    }
}
