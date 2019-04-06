package de.codebucket.mkkm.api.model;

public class LoginRequest {

    private String login;

    private String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
