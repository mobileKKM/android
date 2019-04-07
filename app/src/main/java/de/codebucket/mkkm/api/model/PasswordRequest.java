package de.codebucket.mkkm.api.model;

public class PasswordRequest {

    private String email;

    public PasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
