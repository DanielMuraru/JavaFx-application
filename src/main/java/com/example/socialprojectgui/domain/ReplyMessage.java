package com.example.socialprojectgui.domain;

import java.time.LocalDateTime;

public class ReplyMessage extends Message{
    private Integer messageToRespond;

    public ReplyMessage(User fromUser, String message, LocalDateTime date, Integer messageToRespond) {
        super(fromUser, message, date);
        this.messageToRespond = messageToRespond;
    }

    public Integer getMessageToRespond() {
        return messageToRespond;
    }

    public void setMessageToRespond(Integer messageToRespond) {
        this.messageToRespond = messageToRespond;
    }
}
