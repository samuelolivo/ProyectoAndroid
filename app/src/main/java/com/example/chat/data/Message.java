package com.example.chat.data;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private String idMessage;
    private String idChat;
    private String idUserSender;
    private String content;
    private MessageType type;
    private List<String> readBy;
    private boolean deleted, edited;
    private long timeStamp;

    public Message () {}
    public Message (String idMessage,
                    String idChat,
                    String idUserSender,
                    String content,
                    MessageType type,
                    List<String> readBy,
                    boolean deleted,
                    boolean edited,
                    long timeStamp) {
        this.idMessage = idMessage;
        this.idChat = idChat;
        this.idUserSender = idUserSender;
        this.content = content;
        this.type = type;
        this.readBy = new ArrayList<>(readBy);
        this.deleted = deleted;
        this.edited = edited;
        this.timeStamp = timeStamp;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public String getIdChat() {
        return idChat;
    }

    public String getIdUserSender() {
        return idUserSender;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public List<String> getReadBy() {
        return readBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isEdited() {
        return edited;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public void setIdUserSender(String idUserSender) {
        this.idUserSender = idUserSender;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReadBy(List<String> readBy) {
        this.readBy = readBy;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
