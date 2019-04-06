package de.codebucket.mkkm.api.model;

public class ErrorMessage {

    private int code;

    private String message;

    private String description;

    public ErrorMessage(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
