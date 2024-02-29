package com.example.socialprojectgui.utils;

import com.example.socialprojectgui.domain.User;

public class UserChangeEvent implements Event{
    private ChangeEventType type;
    private User newUser;
    private User oldUser;

    public UserChangeEvent(ChangeEventType type, User newUser, User oldUser) {
        this.type = type;
        this.newUser = newUser;
        this.oldUser = oldUser;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getNewUser() {
        return newUser;
    }

    public User getOldUser() {
        return oldUser;
    }
}
