package de.codebucket.mkkm.login;

public class LoginFailedException extends Exception {

    private final String mMessage;

    public LoginFailedException(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
