package com.example.sigmatimemanager.fxml_controller;

import java.time.LocalDateTime;

public class GroupData {
    private String groupName;
    private int course;
    private LocalDateTime updatedAt;
    private  Integer classNumber;

    public GroupData(String groupName, int course, LocalDateTime updatedAt, int classNumber) {
        this.groupName = groupName;
        this.course = course;
        this.updatedAt = updatedAt;
        this.classNumber = classNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getCourse() {
        return course;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}