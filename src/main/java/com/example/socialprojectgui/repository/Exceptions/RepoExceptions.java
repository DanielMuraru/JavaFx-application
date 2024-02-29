package com.example.socialprojectgui.repository.Exceptions;

public class RepoExceptions extends RuntimeException {
    public RepoExceptions(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
