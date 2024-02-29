package com.example.socialprojectgui.domain;


import com.example.socialprojectgui.domain.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class Friendship extends Entity<Tuple<User,User>> {
    LocalDate friendsFrom;


    public Friendship() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        this.friendsFrom = LocalDate.parse(friends, formatter);
        this.friendsFrom = LocalDate.now();


    }

    @Override
    public Tuple getId() {
        return super.getId();
    }

    public LocalDate getFriendsFrom() {
        return this.friendsFrom;
    }

    public void setFriendsFrom(LocalDate friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {

        return "Friendship{" +
                "friendsFrom=" + friendsFrom +
                ", id=" + id +
                '}';
    }
}
































