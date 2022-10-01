package com.example.ashwamedh.model;

public class Jingle {
    private String title;
    private String scene;
    private String lyrics;
    private String dateAdded;
    private String currentMilliSeconds;

    public String getCurrentMilliSeconds() {
        return currentMilliSeconds;
    }

    public void setCurrentMilliSeconds(String currentMilliSeconds) {
        this.currentMilliSeconds = currentMilliSeconds;
    }

    public Jingle(String title, String scene, String lyrics, String dateAdded, String currentMilliSeconds) {
        this.title = title;
        this.scene = scene;
        this.lyrics = lyrics;
        this.dateAdded = dateAdded;
        this.currentMilliSeconds = currentMilliSeconds;
    }

    public Jingle() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
