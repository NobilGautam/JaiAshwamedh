package com.example.ashwamedh.model;

public class Script {
    private String title;
    private String scenes;
    private String timeAdded;
    private String downloadLink;
    private String currentTimeMiliSec;



    public Script(String title, String scenes, String timeAdded, String downloadLink, String currentTimeMiliSec) {
        this.title = title;
        this.scenes = scenes;
        this.timeAdded = timeAdded;
        this.downloadLink = downloadLink;
        this.currentTimeMiliSec = currentTimeMiliSec;
    }

    public Script() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScenes() {
        return scenes;
    }

    public void setScenes(String scenes) {
        this.scenes = scenes;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getCurrentTimeMiliSec() {
        return currentTimeMiliSec;
    }

    public void setCurrentTimeMiliSec(String currentTimeMiliSec) {
        this.currentTimeMiliSec = currentTimeMiliSec;
    }
}
