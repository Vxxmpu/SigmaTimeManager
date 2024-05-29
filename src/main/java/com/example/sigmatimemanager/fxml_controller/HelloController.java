package com.example.sigmatimemanager.fxml_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.IOException;

public class HelloController {
    @FXML
    Button create;
    @FXML
    Button update;
    @FXML
    Button view_schedule;
    @FXML
    Button view_tutorial;
    @FXML
    Pane HelloPane;

    @FXML
    void handleCreateButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sigmatimemanager/create_schedule.fxml"));
            AnchorPane MainPane = loader.load();
            HelloPane.getChildren().clear();
            HelloPane.getChildren().add(MainPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleUpdateButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sigmatimemanager/update_schedule.fxml"));
            AnchorPane MainPane = loader.load();
            HelloPane.getChildren().clear();
            HelloPane.getChildren().add(MainPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleScheduleAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sigmatimemanager/completed_schedule_viewer.fxml"));
            AnchorPane MainPane = loader.load();
            HelloPane.getChildren().clear();
            HelloPane.getChildren().add(MainPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void handleTutorialAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sigmatimemanager/tutorial.fxml"));
            AnchorPane MainPane = loader.load();
            HelloPane.getChildren().clear();
            HelloPane.getChildren().add(MainPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}