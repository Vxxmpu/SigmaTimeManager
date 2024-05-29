package com.example.sigmatimemanager.fxml_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Tutorial {
    final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    final String DATABASE_USER = "postgres";
    final String DATABASE_PASSWORD = "1234";
    @FXML
    AnchorPane MainPane;
    @FXML
    Button buttonPrevious;
    @FXML
    Button buttonNext;
    @FXML
    ImageView imageView;
    @FXML
    Button BackButton;
    @FXML
    Label explanation;
    @FXML
    Label stepLabel;






    @FXML
    void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sigmatimemanager/hello-view.fxml"));
            Pane HelloPane = loader.load();
            MainPane.getChildren().clear();
            MainPane.getChildren().add(HelloPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

