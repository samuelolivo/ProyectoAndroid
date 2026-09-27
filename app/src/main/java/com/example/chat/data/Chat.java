package com.example.chat.data;

import java.util.HashMap;

public class Chat {
    private String idChat;
    private HashMap<String, Boolean> users;
    private String ultimoMensaje;
    private long timestamp;

    public Chat () {}

    public Chat(String idChat, HashMap<String, Boolean> users, String ultimoMensaje, long timestamp){
        this.idChat = idChat;
        this.users = new HashMap<>(users);
        this.ultimoMensaje = ultimoMensaje;
        this.timestamp = timestamp;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public HashMap<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Boolean> users) {
        this.users = users;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}