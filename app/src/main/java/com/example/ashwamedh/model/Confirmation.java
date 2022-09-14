package com.example.ashwamedh.model;

public class Confirmation {
    private String userId;
    private String practiceDate;
    private String confirmation;
    private String remarkOrReason;

    public Confirmation() {

    }

    public Confirmation(String userId, String practiceDate, String confirmation, String remarkOrReason) {
        this.userId = userId;
        this.practiceDate = practiceDate;
        this.confirmation = confirmation;
        this.remarkOrReason = remarkOrReason;
    }

    public String getRemarkOrReason() {
        return remarkOrReason;
    }

    public void setRemarkOrReason(String remarkOrReason) {
        this.remarkOrReason = remarkOrReason;
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
}
