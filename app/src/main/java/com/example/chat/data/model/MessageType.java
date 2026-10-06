package com.example.chat.data.model;
public enum MessageType {
    TEXT("Text"),
    AUDIO("Audio"),
    IMAGE("Image"),
    VIDEO("Video");

    private final String type;
    MessageType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}