package com.example.chat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat {
    private String idChat;
    private HashMap<String, User> users;

    public Chat () {};

    public Chat(String idChat,
                List<Message> messagesList,
                HashMap<String, User> users){
        this.idChat = idChat;
        this.users = new HashMap<String, User>(users);
    }
    public String getIdChat() {
        return idChat;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public void setUsers(HashMap<String, User> users) {
        this.users = users;
    }
}
