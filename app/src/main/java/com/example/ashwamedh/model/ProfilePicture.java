package com.example.ashwamedh.model;

public class ProfilePicture {
    private String userId;
    private String imagePath;

    public ProfilePicture() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
