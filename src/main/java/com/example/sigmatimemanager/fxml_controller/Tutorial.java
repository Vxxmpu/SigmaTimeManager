package com.example.sigmatimemanager.fxml_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;

public class Tutorial {
    final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    final String DATABASE_USER = "postgres";
    final String DATABASE_PASSWORD = "1234";

    // Пути к изображениям
    private static final String[] imagePaths = {
            "/images/view_1.png",
            "/images/create_schedule2.png",
            "/images/create_schedule3.png",
            "/images/view_1.png",
            "/images/update_schedule2.png",
            "/images/update_schedule3.png",
            "/images/update_schedule4.png",
            "/images/update_schedule6.png",
            "/images/update_schedule5.png",
            "/images/view_1.png",
            "/images/view_schedule2.png",
            "/images/view_schedule3.png"
    };

    private int currentIndex = 0;

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
    Text explanation;
    @FXML
    Label stepLabel;
    @FXML
    ComboBox<String> choose;

    @FXML
    void initialize() {
        // Загружаем изображение по умолчанию при запуске
        loadImage(imagePaths[currentIndex]);
        // Устанавливаем текущий шаг
        updateCurrentStepLabel();
        // Обновляем explanation text
        updateExplanationText();
    }

    @FXML
    void handlePreviousButtonAction(ActionEvent event) {
        if (currentIndex > 0) {
            currentIndex--;
            loadImage(imagePaths[currentIndex]);
            updateCurrentStepLabel();
            updateExplanationText();
        }
    }

    @FXML
    void handleNextButtonAction(ActionEvent event) {
        if (currentIndex < imagePaths.length - 1) {
            currentIndex++;
            loadImage(imagePaths[currentIndex]);
            updateCurrentStepLabel();
            updateExplanationText();
        }
    }

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

    private void loadImage(String path) {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());
            imageView.setImage(image);
        } else {
            System.err.println("File not found: " + path);
        }
    }

    private void updateCurrentStepLabel() {
        stepLabel.setText("Current step: " + (currentIndex + 1) + " of " + imagePaths.length);
    }

    private void updateExplanationText() {
        switch (currentIndex) {
            case 0:
                explanation.setText("Page to choose what you want to do");
                break;
            case 1:
                explanation.setText("Press this button to create a new schedule");
                break;
            case 2:
                explanation.setText("There you must choose and fill every field, them you must press 'check' button, if everything good" +
                        "and no problems, you can press button 'Confirm' or 'Confirm and stay' if you want make many more schedules");
                break;
            case 3:
                explanation.setText("Page to choose what you want to do");
                break;
            case 4:
                explanation.setText("Press this button to update existing schedules");
                break;
            case 5:
                explanation.setText("As you can see, here are 3 main groups. On the top you have objects to choose the schedule you want change. Then below it you have almost the same controllers as in create page" +
                        ", on the right you have table of recent made/updated schedules");
                break;
            case 6:
                explanation.setText("There you must choose course -> direction -> day of the week -> class number -> group.");
                break;
            case 7:
                explanation.setText("Besides the default controllers you have the radio buttons next to the controllers, it disables the controller so you can leave it as it is");
                break;
            case 8:
                explanation.setText("Next in very right of the page you have last created or updated schedules so you can use it in case you forgot the group that you created or updated");
                break;
            case 9:
                explanation.setText("Page to choose what you want to do");
                break;
            case 10:
                explanation.setText("Press this button to view the completed schedules");
                break;
            case 11:
                explanation.setText("There you must choose choose course -> direction -> day of the week -> class number -> group. So the programm wont confuse which group you want to see");
                break;
            default:
                explanation.setText("Oops... an unexpected error occurred. Please leave and re-enter this page");
                break;
        }
    }
}
