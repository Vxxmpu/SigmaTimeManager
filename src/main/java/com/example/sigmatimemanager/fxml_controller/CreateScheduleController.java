package com.example.sigmatimemanager.fxml_controller;

import com.example.sigmatimemanager.dbController.CreateScheduleDBController;
import com.example.sigmatimemanager.dbController.CreateScheduleDBProblemChecker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Collections;

public class CreateScheduleController {
    private Connection con;
    @FXML
    AnchorPane MainPane;
    @FXML
    ComboBox<String> groupChoiceBox;

    @FXML
    ChoiceBox<Integer> classChoiceBox;
    @FXML
    ChoiceBox<String> subjectChoiceBox;
    @FXML
    ChoiceBox<String> teacherChoiceBox;
    @FXML
    ChoiceBox<Integer> auditoryFloorChoiceBox;
    @FXML
    ChoiceBox<String> auditoryBuildingChoiceBox;
    @FXML
    ChoiceBox<Integer> auditoryRoomChoiceBox;
    @FXML
    ComboBox<String> dayOfWeek;
    @FXML
    Label problem1;
    @FXML
    Label problem2;
    @FXML
    Label problem3;
    @FXML
    Label problem4;
    @FXML
    Label problem5;
    @FXML
    Label problem6;
    @FXML
    Button checkButton;
    @FXML
    Button backButton;
    @FXML
    Button confirmButton;
    @FXML
    Button confrimButton2;
    CreateScheduleDBController createScheduleDBController = new CreateScheduleDBController();

    @FXML
    void initialize() throws SQLException {
        groupChoiceBox.setVisibleRowCount(10);
        fillChoiceBox();
        checkButton.setOnAction(event -> {
            checkProblems();
        });
        confirmButton.setOnAction(event -> {
            try {
                handleConfirmButtonAction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void fillChoiceBox() throws SQLException {
        ObservableList<String> Group = FXCollections.observableArrayList();
        ObservableList<Integer> Class = FXCollections.observableArrayList();
        ObservableList<String> Subject = FXCollections.observableArrayList();
        ObservableList<String> Teacher = FXCollections.observableArrayList();
        ObservableList<String> AuditoryBuilding = FXCollections.observableArrayList();
        ObservableList<Integer> AuditoryFloor = FXCollections.observableArrayList();
        ObservableList<Integer> AuditoryRoom = FXCollections.observableArrayList();
        final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
        final String DATABASE_USER = "postgres";
        final String DATABASE_PASSWORD = "1234";
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             Statement stmt = con.createStatement();){
            ResultSet rsGroup = stmt.executeQuery("SELECT DISTINCT group_name FROM groups");
            while (rsGroup.next()) {
                String groupString = rsGroup.getString("group_name");
                Group.add(groupString);
            }
            ResultSet rsClass = stmt.executeQuery("SELECT DISTINCT classes FROM class");
            while (rsClass.next()) {
                int classString = rsClass.getInt("classes");
                Class.add(classString);
            }
            ResultSet rsSubject = stmt.executeQuery("SELECT DISTINCT subject_name FROM subject");
            while (rsSubject.next()) {
                String subjectString = rsSubject.getString("subject_name");
                Subject.add(subjectString);
            }
            ResultSet rsTeacher = stmt.executeQuery("SELECT DISTINCT full_name FROM teacher");
            while (rsTeacher.next()) {
                String teacherString = rsTeacher.getString("full_name");
                Teacher.add(teacherString);
            }
            ResultSet rsBuilding = stmt.executeQuery("SELECT DISTINCT building FROM auditory");
            while (rsBuilding.next()) {
                String BuildingString = rsBuilding.getString("building");
                AuditoryBuilding.add(BuildingString);
            }
            ResultSet rsFloor = stmt.executeQuery("SELECT DISTINCT floor FROM auditory");
            while (rsFloor.next()) {
                Integer FloorInteger = rsFloor.getInt("floor");
                AuditoryFloor.add(FloorInteger);
            }
            ResultSet rsRoom = stmt.executeQuery("SELECT DISTINCT room_number FROM auditory");
            while (rsRoom.next()) {
                Integer RoomInteger = rsRoom.getInt("room_number");
                AuditoryRoom.add(RoomInteger);
            }

            Collections.sort(Group);
            groupChoiceBox.setItems(Group);
            classChoiceBox.setItems(Class);
            subjectChoiceBox.setItems(Subject);
            teacherChoiceBox.setItems(Teacher);
            auditoryBuildingChoiceBox.setItems(AuditoryBuilding);
            dayOfWeek.getItems().addAll(
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday"
            );
            auditoryBuildingChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                        String floorCheckSQL = "SELECT DISTINCT floor FROM auditory WHERE building LIKE ?";
                        PreparedStatement ps = connection.prepareStatement(floorCheckSQL);
                        ps.setString(1, newValue);
                        ResultSet rsFloorCheck = ps.executeQuery();
                        ObservableList<Integer> floors = FXCollections.observableArrayList();
                        while (rsFloorCheck.next()) {
                            Integer floor = rsFloorCheck.getInt("floor");
                            floors.add(floor);
                        }
                        auditoryFloorChoiceBox.setItems(floors);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            auditoryFloorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                        String roomCheckSQL = "SELECT DISTINCT room_number FROM auditory WHERE building LIKE ? AND floor = ?";
                        PreparedStatement ps = connection.prepareStatement(roomCheckSQL);
                        ps.setString(1, auditoryBuildingChoiceBox.getValue());
                        ps.setInt(2, newValue);
                        ResultSet rsRoomCheck = ps.executeQuery();
                        ObservableList<Integer> rooms = FXCollections.observableArrayList();
                        while (rsRoomCheck.next()) {
                            Integer room = rsRoomCheck.getInt("room_number");
                            rooms.add(room);
                        }
                        auditoryRoomChoiceBox.setItems(rooms);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });


        }
    }
    private void checkProblems() {
        CreateScheduleDBProblemChecker checker = new CreateScheduleDBProblemChecker();
        checker.SetValues(groupChoiceBox.getValue(), classChoiceBox.getValue(),
                subjectChoiceBox.getValue(), teacherChoiceBox.getValue(),
                auditoryFloorChoiceBox.getValue(),auditoryBuildingChoiceBox.getValue(),
                auditoryRoomChoiceBox.getValue(), dayOfWeek.getValue());

        String problem = checker.isGroupContainsInDataBase();

        if (problem != null) {
            switch (problem) {
                case "Группа уже имеет запись в расписании на эту дату.":
                    problem1.setText(problem);
                    break;
                case "Преподаватель уже занят в это время.":
                    problem2.setText(problem);
                    break;
                case "Аудитория уже занята в это время.":
                    problem3.setText(problem);
                    break;
                default:
                    showNotification("No problems seems to be occurring");
            }
        }
    }
    @FXML
    void handleConfirmButtonAction() throws SQLException {
        try {
            String groupName = groupChoiceBox.getValue();
            Integer class_number = classChoiceBox.getValue();
            String subjectName = subjectChoiceBox.getValue();
            String teacherName = teacherChoiceBox.getValue();
            String auditoryBuilding = auditoryBuildingChoiceBox.getValue();
            Integer auditoryRoom = auditoryRoomChoiceBox.getValue();
            Integer auditoryFloor = auditoryFloorChoiceBox.getValue();
            String date = dayOfWeek.getValue();
            createScheduleDBController.saveToDatabase(groupName, class_number, subjectName, teacherName, auditoryFloor, auditoryBuilding, auditoryRoom, date);
            showNotification("Successfully created a new schedule for : "+ groupChoiceBox.getValue());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sigmatimemanager/hello-view.fxml"));
                Pane HelloPane = loader.load();
                MainPane.getChildren().clear();
                MainPane.getChildren().add(HelloPane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void handleConfirmAndStayButtonAction() {
        try {
            String groupName = groupChoiceBox.getValue();
            Integer class_number = classChoiceBox.getValue();
            String subjectName = subjectChoiceBox.getValue();
            String teacherName = teacherChoiceBox.getValue();
            String auditoryBuilding = auditoryBuildingChoiceBox.getValue();
            Integer auditoryRoom = auditoryRoomChoiceBox.getValue();
            Integer auditoryFloor = auditoryFloorChoiceBox.getValue();
            String date = dayOfWeek.getValue();
            createScheduleDBController.saveToDatabase(groupName, class_number, subjectName, teacherName,auditoryFloor,auditoryBuilding,auditoryRoom,date);
            showNotification("Successfully created a new schedule for : "+ groupChoiceBox.getValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static void showNotification(String message) {
        Notifications notifications = Notifications.create()
                .title("Уведомление")
                .text(message)
                .hideAfter(Duration.seconds(5)) // Уведомление исчезнет через 5 секунд
                .position(Pos.TOP_RIGHT);
        notifications.show();
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

}
