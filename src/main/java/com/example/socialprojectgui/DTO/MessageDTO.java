package com.example.socialprojectgui.DTO;

import java.time.LocalDateTime;

public class MessageDTO {
    private Integer messageID;
    private Integer idSender;
    private Integer idReplier;

    private String messageToRespond;
    private LocalDateTime sentTime;
    private String text;

    public String getMessageToRespond() {
        return messageToRespond;
    }

    public void setMessageToRespond(String messageToRespond) {
        this.messageToRespond = messageToRespond;
    }

    public Integer getIdSender() {
        return idSender;
    }

    public void setIdSender(Integer idSender) {
        this.idSender = idSender;
    }

    public Integer getIdReplier() {
        return idReplier;
    }

    public void setIdReplier(Integer idReplier) {
        this.idReplier = idReplier;
    }
    public MessageDTO(Integer messageID, String string, LocalDateTime sentTime, String messageToRespond) {
        this.messageID = messageID;
        this.text = string;
        this.sentTime = sentTime;
        this.messageToRespond = messageToRespond;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
