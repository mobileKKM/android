package de.codebucket.mkkm.api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import de.codebucket.mkkm.model.UserAccount;

public class UserStatusTypeAdapter extends TypeAdapter<UserAccount.UserStatus> {

    @Override
    public void write(JsonWriter out, UserAccount.UserStatus value) throws IOException {
        out.value(value.toString().toLowerCase());
    }

    @Override
    public UserAccount.UserStatus read(JsonReader in) throws IOException {
        switch (in.nextString()) {
            case "approved":
                return UserAccount.UserStatus.APPROVED;
            case "pending":
                return UserAccount.UserStatus.PENDING;
            default:
                return UserAccount.UserStatus.UNDEFINED;
        }
    }
}
