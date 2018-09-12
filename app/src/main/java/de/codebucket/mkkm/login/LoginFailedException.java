package de.codebucket.mkkm.login;

import de.codebucket.mkkm.MobileKKM;

public class LoginFailedException extends Exception {

    private final ErrorType mErrorType;
    private final String mErrorMessage;

    public LoginFailedException(String error) {
        this(ErrorType.BACKEND, error);
    }

    public LoginFailedException(ErrorType type, int resId) {
        this(type, MobileKKM.getInstance().getString(resId));
    }

    public LoginFailedException(ErrorType type, String error) {
        mErrorType = type;
        mErrorMessage = error;
    }

    public ErrorType getErrorType() {
        return mErrorType;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public enum ErrorType {
        SYSTEM, BACKEND, USER, UNKNOWN
    }
}
