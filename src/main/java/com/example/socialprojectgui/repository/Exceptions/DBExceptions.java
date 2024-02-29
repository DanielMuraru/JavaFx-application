package com.example.socialprojectgui.repository.Exceptions;

public class DBExceptions extends RuntimeException {
    public DBExceptions(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
