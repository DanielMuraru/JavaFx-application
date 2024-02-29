package com.example.socialprojectgui.DTO;

public class FriendRequestDTO {
    Integer id;
    String nameFromWhom ;
    String nameToWhom ;
    String status = "";

    public FriendRequestDTO(Integer id, String nameFromWhom, String nameToWhom, String status) {
        this.id = id;
        this.nameFromWhom = nameFromWhom;
        this.nameToWhom = nameToWhom;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameFromWhom() {
        return nameFromWhom;
    }

    public void setNameFromWhom(String nameFromWhom) {
        this.nameFromWhom = nameFromWhom;
    }

    public String getNameToWhom() {
        return nameToWhom;
    }

    public void setNameToWhom(String nameToWhom) {
        this.nameToWhom = nameToWhom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
