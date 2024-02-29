package com.example.socialprojectgui.domain;

public class FriendRequest extends Entity<Integer>{
    Integer idFromWhom = 0;
    Integer idToWhom = 0;
    String nameFrom ="";
    String nameTo ="";
    String status = "";

    public FriendRequest(Integer idFromWhom, Integer idToWhom,String nameFrom, String nameTo, String status) {
        this.idFromWhom = idFromWhom;
        this.nameFrom = nameFrom;
        this.nameTo = nameTo;
        this.idToWhom = idToWhom;
        this.status = status;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    public Integer getIdFromWhom() {
        return idFromWhom;
    }

    public void setIdFromWhom(Integer idFromWhom) {
        this.idFromWhom = idFromWhom;
    }

    public Integer getIdToWhom() {
        return idToWhom;
    }

    public void setIdToWhom(Integer idToWhom) {
        this.idToWhom = idToWhom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
