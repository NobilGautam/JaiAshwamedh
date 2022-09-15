package com.example.ashwamedh.model;

public class Attendance {
    private String userId;
    private int daysPresent;
    private int totalDays;
    private int percentage;
    private String username;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Attendance(String userId, int daysPresent, int totalDays) {
        this.userId = userId;
        this.daysPresent = daysPresent;
        this.totalDays = totalDays;
    }

    public Attendance() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(int daysPresent) {
        this.daysPresent = daysPresent;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
}
