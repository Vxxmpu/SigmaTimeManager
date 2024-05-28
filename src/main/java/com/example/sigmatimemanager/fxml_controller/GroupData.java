package com.example.sigmatimemanager.fxml_controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GroupData {
    private String groupName;
    private int course;
    private LocalDateTime updatedAt;

    public GroupData(String groupName, int course, LocalDateTime updatedAt) {
        this.groupName = groupName;
        this.course = course;
        this.updatedAt = updatedAt;
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