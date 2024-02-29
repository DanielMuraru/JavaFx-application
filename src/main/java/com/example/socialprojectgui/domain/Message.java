package com.example.socialprojectgui.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Tuple<Integer,List<Integer>>>{
    private Integer DBId;

    private User fromUser;

    private List<User> toUsers;
    private String message;
    private LocalDateTime date;
    public Message(User fromUser, String message, LocalDateTime date) {
        this.fromUser = fromUser;
        this.toUsers = new ArrayList<>();
        this.message = message;
        this.date = date;
    }

    public Integer getDBId() {
        return DBId;
    }

    public void setDBId(Integer DBId) {
        this.DBId = DBId;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public List<User> getToUsers() {
        return toUsers;
    }

    public void addToUsers(User u) {
        this.toUsers.add(u);

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
