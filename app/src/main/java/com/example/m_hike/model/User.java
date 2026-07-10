package com.example.m_hike.model;

public class User {

    private int id;
    private String userName;
    private String userEmail;
    private String password;
    private String avatarPath;
    private String createdAt;

    public User() {
    }

    public User(String userName,
                String userEmail,
                String password,
                String avatarPath,
                String createdAt) {

        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
        this.avatarPath = avatarPath;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}