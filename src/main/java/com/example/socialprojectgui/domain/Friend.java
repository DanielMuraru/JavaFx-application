package com.example.socialprojectgui.domain;

import java.time.LocalDate;

public class Friend extends Entity<Integer>{
    private int idUser;
    private int idFriend;
    private String nameUser;
    private String nameFriendsWith;
    private LocalDate friendsFrom;

    public Friend(int idUser, int idFriend, String nameUser, String nameFriendsWith, LocalDate friendsFrom) {
        this.idUser = idUser;
        this.idFriend = idFriend;
        this.nameUser = nameUser;
        this.nameFriendsWith = nameFriendsWith;
        this.friendsFrom = friendsFrom;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdFriend() {
        return idFriend;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNameFriendsWith() {
        return nameFriendsWith;
    }

    public void setNameFriendsWith(String nameFriendsWith) {
        this.nameFriendsWith = nameFriendsWith;
    }

    public LocalDate getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDate friendsFrom) {
        this.friendsFrom = friendsFrom;
    }
}
