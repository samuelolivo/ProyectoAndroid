package com.example.chat.data;

public class User {
    private String idUser;
    private String name;
    private String correo;
    private String pictureProfile;
    private boolean online;

    public User () {}
    public User (String idUser,
                 String name,
                 String correo,
                 String pictureProfile,
                 boolean online) {
        this.idUser = idUser;
        this.name = name;
        this.correo = correo;
        this.pictureProfile = pictureProfile;
        this.online = online;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }
    public String getCorreo() {
        return correo;
    }

    public String getPictureProfile() {
        return pictureProfile;
    }

    public boolean isOnline() {
        return online;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setCorreo(String correo) {
        this.name = correo;
    }

    public void setPictureProfile(String pictureProfile) {
        this.pictureProfile = pictureProfile;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
