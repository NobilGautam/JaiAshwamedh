package com.example.ashwamedh.model;

public class AdminConfirmation {
    private String userId;
    private String practiceDate;
    private String confirmation;
    private String remarkOrReason;
    private String username;

    public AdminConfirmation(String userId, String practiceDate, String confirmation, String remarkOrReason, String username) {
        this.userId = userId;
        this.practiceDate = practiceDate;
        this.confirmation = confirmation;
        this.remarkOrReason = remarkOrReason;
        this.username = username;
    }

    public AdminConfirmation() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPracticeDate() {
        return practiceDate;
    }

    public void setPracticeDate(String practiceDate) {
        this.practiceDate = practiceDate;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getRemarkOrReason() {
        return remarkOrReason;
    }

    public void setRemarkOrReason(String remarkOrReason) {
        this.remarkOrReason = remarkOrReason;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
