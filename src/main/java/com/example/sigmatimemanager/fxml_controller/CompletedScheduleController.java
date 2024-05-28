package com.example.sigmatimemanager.fxml_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class CompletedScheduleController {
    final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    final String DATABASE_USER = "postgres";
    final String DATABASE_PASSWORD = "1234";
    @FXML
    AnchorPane MainPane;
    @FXML
    ChoiceBox<Integer> ChooseCourse;
    @FXML
    ComboBox<String> directionComboBox;
    @FXML
    ComboBox<String> groupComboBox;
    @FXML
    ComboBox<String> dateComboBox;
    @FXML
    ComboBox<Integer> classComboBox;

    @FXML
    TableView<String> Monday;
    @FXML
    TableColumn Subject1;
    @FXML
    TableColumn Teacher1;
    @FXML
    TableColumn Auditory1;
    @FXML
    TableView<String> Tuesday;
    @FXML
    TableColumn Subject2;
    @FXML
    TableColumn Teacher2;
    @FXML
    TableColumn Auditory2;
    @FXML
    TableView<String> Wednesday;
    @FXML
    TableColumn Subject3;
    @FXML
    TableColumn Teacher3;
    @FXML
    TableColumn Auditory3;
    @FXML
    TableView<String> Thursday;
    @FXML
    TableColumn Subject4;
    @FXML
    TableColumn Teacher4;
    @FXML
    TableColumn Auditory4;
    @FXML
    TableView<String> Friday;
    @FXML
    TableColumn Subject5;
    @FXML
    TableColumn Teacher5;
    @FXML
    TableColumn Auditory5;
    public void initialize() {
        fillChoices();
    }
    public void fillChoices(){
        ObservableList<Integer> courseList = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             Statement stmt = con.createStatement()) {

            ResultSet rsCourse = stmt.executeQuery("SELECT DISTINCT course FROM groups");
            while (rsCourse.next()) {
                Integer courseInt = rsCourse.getInt("course");
                courseList.add(courseInt);
            }
            ChooseCourse.setItems(courseList);

            ChooseCourse.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                        String directionQuery = "SELECT DISTINCT direction FROM directions";
                        PreparedStatement psDirection = connection.prepareStatement(directionQuery);
                        ResultSet rsDirection = psDirection.executeQuery();

                        ObservableList<String> directions = FXCollections.observableArrayList();
                        while (rsDirection.next()) {
                            String direction = rsDirection.getString("direction");
                            directions.add(direction);
                        }
                        directionComboBox.setItems(directions);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            directionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    dateComboBox.getItems().clear();

                    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                        String dateQuery = "SELECT DISTINCT day_of_week FROM completed_schedule WHERE group_name IN (SELECT DISTINCT group_name FROM groups WHERE SUBSTRING(group_name, 1, 2) = ? AND course = ?)";
                        PreparedStatement psDate = connection.prepareStatement(dateQuery);
                        psDate.setString(1, newValue.substring(0, 2));
                        psDate.setInt(2, ChooseCourse.getValue());
                        ResultSet rsDate = psDate.executeQuery();

                        ObservableList<String> dates = FXCollections.observableArrayList();
                        while (rsDate.next()) {
                            String date = rsDate.getString("day_of_week");
                            dates.add(date);
                        }
                        dateComboBox.setItems(dates);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            dateComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    classComboBox.getItems().clear();

                    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                        String classQuery = "SELECT DISTINCT class_number FROM completed_schedule WHERE group_name IN (SELECT DISTINCT group_name FROM groups WHERE SUBSTRING(group_name, 1, 2) = ?) AND day_of_week = ?";
                        PreparedStatement psClass = connection.prepareStatement(classQuery);
                        psClass.setString(1, directionComboBox.getValue().substring(0, 2));
                        psClass.setString(2, newValue);
                        ResultSet rsClass = psClass.executeQuery();

                        ObservableList<Integer> classes = FXCollections.observableArrayList();
                        while (rsClass.next()) {
                            int classNumber = rsClass.getInt("class_number");
                            classes.add(classNumber);
                        }
                        classComboBox.setItems(classes);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            classComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    groupComboBox.getItems().clear();

                    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                        String groupQuery = "SELECT DISTINCT group_name FROM completed_schedule WHERE group_name IN (SELECT DISTINCT group_name FROM groups WHERE SUBSTRING(group_name, 1, 2) = ?) AND day_of_week = ? AND class_number = ?";
                        PreparedStatement psGroup = connection.prepareStatement(groupQuery);
                        psGroup.setString(1, directionComboBox.getValue().substring(0, 2));
                        psGroup.setString(2, dateComboBox.getValue());
                        psGroup.setInt(3, newValue);
                        ResultSet rsGroup = psGroup.executeQuery();

                        ObservableList<String> groups = FXCollections.observableArrayList();
                        while (rsGroup.next()) {
                            String group = rsGroup.getString("group_name");
                            groups.add(group);
                        }
                        groupComboBox.setItems(groups);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
//    public void fillSchedule(int id) {
//        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
//            String querySubject = "SELECT subject_id FROM completed_schedule WHERE id = ?";
//            String queryTeacher = "SELECT teacher_id FROM completed_schedule WHERE id = ?";
//            String queryAuditory = "SELECT auditory_id FROM completed_schedule WHERE id = ?";
//            PreparedStatement ps = connection.prepareStatement(querySubject);
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                String subject = rs.getString("subject_name");
//                String teacher = rs.getString("teacher_name");
//                String auditory = rs.getString("auditory_name");
//                LocalDate date = rs.getDate("date").toLocalDate();
//
//                DayOfWeek dayOfWeek = date.getDayOfWeek();
//                switch (dayOfWeek) {
//                    case MONDAY:
//                        Monday.getItems().add(subject);
//                        Monday.getItems().add(teacher);
//                        Monday.getItems().add(auditory);
//                        break;
//                    case TUESDAY:
//                        Tuesday.getItems().add(subject);
//                        Tuesday.getItems().add(teacher);
//                        Tuesday.getItems().add(auditory);
//                        break;
//                    case WEDNESDAY:
//                        Wednesday.getItems().add(subject);
//                        Wednesday.getItems().add(teacher);
//                        Wednesday.getItems().add(auditory);
//                        break;
//                    case THURSDAY:
//                        Thursday.getItems().add(subject);
//                        Thursday.getItems().add(teacher);
//                        Thursday.getItems().add(auditory);
//                        break;
//                    case FRIDAY:
//                        Friday.getItems().add(subject);
//                        Friday.getItems().add(teacher);
//                        Friday.getItems().add(auditory);
//                        break;
//                    default:
//                        break;
//                }
//            } else {
//                // Обработка, если запись в базе данных не найдена
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }

    @FXML
    void handleBackButtonAction(ActionEvent actionEvent) {
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
