package com.example.sigmatimemanager.fxml_controller;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    TableColumn<String , String> Subject1;
    @FXML
    TableColumn<String , String> Teacher1;
    @FXML
    TableColumn<String , String> Auditory1;
    @FXML
    TableView<String> Tuesday;
    @FXML
    TableColumn<String , String> Subject2;
    @FXML
    TableColumn<String , String> Teacher2;
    @FXML
    TableColumn<String , String> Auditory2;
    @FXML
    TableView<String> Wednesday;
    @FXML
    TableColumn<String , String>  Subject3;
    @FXML
    TableColumn<String , String> Teacher3;
    @FXML
    TableColumn<String , String> Auditory3;
    @FXML
    TableView<String> Thursday;
    @FXML
    TableColumn<String , String> Subject4;
    @FXML
    TableColumn<String , String> Teacher4;
    @FXML
    TableColumn<String , String> Auditory4;
    @FXML
    TableView<String> Friday;
    @FXML
    TableColumn<String , String> Subject5;
    @FXML
    TableColumn<String , String>Teacher5;
    @FXML
    TableColumn<String , String> Auditory5;
    Integer id;
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

                        groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableGroup, oldGroup, newGroup) -> {
                            if (newGroup != null) {
                                try {
                                    getScheduleId();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    void getScheduleId() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        String groupBox = groupComboBox.getValue();
        String dateBox = dateComboBox.getValue();
        Integer classBox = classComboBox.getValue();
        String query = "SELECT id FROM completed_schedule WHERE group_name = ? AND day_of_week = ? AND class_number = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, groupBox);
        ps.setString(2, dateBox);
        ps.setInt(3, classBox);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
            System.out.println(id);
        }
        if (id!=null) {
            fillSchedule(id,"Monday","Tuesday","Wednesday","Thursday","Friday");
        }
    }
    public void fillDaySchedule(String dayOfWeek, int scheduleId) {
        final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
        final String DATABASE_USER = "postgres";
        final String DATABASE_PASSWORD = "1234";

        // Запрос для получения пар для выбранной группы и выбранного дня
        String query = """
    SELECT s.subject_name, t.full_name as teacher_name, 
           a.building || '.' || a.room_number as auditory_name
    FROM completed_schedule cs
    JOIN subject s ON cs.subject_id = s.id
    JOIN teacher t ON cs.teacher_id = t.id
    JOIN auditory a ON cs.auditory_id = a.id
    WHERE cs.group_name = (
        SELECT group_name FROM completed_schedule WHERE id = ?
    )
    AND cs.day_of_week = ?
    """;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, scheduleId);
            ps.setString(2, dayOfWeek);
            ResultSet rs = ps.executeQuery();

            // Заполняем таблицу данными из результата запроса для выбранного дня недели
            ObservableList<String> entries = FXCollections.observableArrayList();
            while (rs.next()) {
                String subject = rs.getString("subject_name");
                String teacher = rs.getString("teacher_name");
                String auditory = rs.getString("auditory_name");
                String entry = String.format("%s, %s, %s", subject, teacher, auditory);
                entries.add(entry);
                System.out.println("Number of entries for " + dayOfWeek + " " + entries.size());
            }
            System.out.println("Day: " + dayOfWeek + ", Entries: " + entries); // Отладочный вывод
            TableView<String> table = getTableViewByDay(dayOfWeek);
            if (table != null) {
                if (!entries.isEmpty()) {
                    switch (dayOfWeek) {
                        case "Monday":
                            setCellValueFactoriesForMonday();
                            break;
                        case "Tuesday":
                            setCellValueFactoriesForTuesday();
                            break;
                        case "Wednesday":
                            setCellValueFactoriesForWednesday();
                            break;
                        case "Thursday":
                            setCellValueFactoriesForThursday();
                            break;
                        case "Friday":
                            setCellValueFactoriesForFriday();
                            break;
                        default:
                            System.out.println("Error in switch case");

                    }
                    table.setItems(entries);
                    System.out.println(entries.size());
                } else {
                    entries.add("Окно");
                    table.setItems(entries);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public void fillSchedule(int scheduleId, String... daysOfWeek) {
        for (String day : daysOfWeek) {
            System.out.println("day of week: " + daysOfWeek);
            fillDaySchedule(day, scheduleId);
        }
    }

    // Метод для получения таблицы по дню недели
    private TableView<String> getTableViewByDay(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Monday":
                return Monday;
            case "Tuesday":
                return Tuesday;
            case "Wednesday":
                return Wednesday;
            case "Thursday":
                return Thursday;
            case "Friday":
                return Friday;
            default:
                return null;
        }
    }
    private void setCellValueFactoriesForDay(TableColumn<String, String> subjectColumn, TableColumn<String, String> teacherColumn, TableColumn<String, String> auditoryColumn) {
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        auditoryColumn.setCellValueFactory(new PropertyValueFactory<>("auditory"));
    }

    private void setCellValueFactoriesForMonday() {
        setCellValueFactoriesForDay(Subject1, Teacher1, Auditory1);
    }

    private void setCellValueFactoriesForTuesday() {
        setCellValueFactoriesForDay(Subject2, Teacher2, Auditory2);
    }
    private void setCellValueFactoriesForWednesday() {
        setCellValueFactoriesForDay(Subject3, Teacher3, Auditory3);
    }
    private void setCellValueFactoriesForThursday() {
        setCellValueFactoriesForDay(Subject4, Teacher4, Auditory4);
    }
    private void setCellValueFactoriesForFriday() {
        setCellValueFactoriesForDay(Subject5, Teacher5, Auditory5);
    }

//    public void fillSchedule(int groupId) {
//        final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
//        final String DATABASE_USER = "postgres";
//        final String DATABASE_PASSWORD = "1234";
//
//        String query = """
//        SELECT cs.day_of_week, s.subject_name, t.full_name as teacher_name,
//               a.building || '.' || a.room_number as auditory_name
//        FROM completed_schedule cs
//        JOIN subject s ON cs.subject_id = s.id
//        JOIN teacher t ON cs.teacher_id = t.id
//        JOIN auditory a ON cs.auditory_id = a.id
//        WHERE cs.group_id = ?
//    """;
//
//        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
//             PreparedStatement ps = connection.prepareStatement(query)) {
//            ps.setInt(1, groupId);
//            ResultSet rs = ps.executeQuery();
//
//            ObservableList<String> mondayList = FXCollections.observableArrayList();
//            ObservableList<String> tuesdayList = FXCollections.observableArrayList();
//            ObservableList<String> wednesdayList = FXCollections.observableArrayList();
//            ObservableList<String> thursdayList = FXCollections.observableArrayList();
//            ObservableList<String> fridayList = FXCollections.observableArrayList();
//
//            while (rs.next()) {
//                String dayOfWeek = rs.getString("day_of_week");
//                String subject = rs.getString("subject_name");
//                String teacher = rs.getString("teacher_name");
//                String auditory = rs.getString("auditory_name");
//                String entry = String.format("%s, %s, %s", subject, teacher, auditory);
//
//                System.out.println("Day: " + dayOfWeek + " Entry: " + entry); // Debug output
//
//                switch (dayOfWeek) {
//                    case "Monday":
//                        mondayList.add(entry);
//                        break;
//                    case "Tuesday":
//                        tuesdayList.add(entry);
//                        break;
//                    case "Wednesday":
//                        wednesdayList.add(entry);
//                        break;
//                    case "Thursday":
//                        thursdayList.add(entry);
//                        break;
//                    case "Friday":
//                        fridayList.add(entry);
//                        break;
//                }
//            }
//
//            Monday.setItems(mondayList);
//            Tuesday.setItems(tuesdayList);
//            Wednesday.setItems(wednesdayList);
//            Thursday.setItems(thursdayList);
//            Friday.setItems(fridayList);
//
//            Subject1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[0]));
//            Teacher1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[1]));
//            Auditory1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[2]));
//
//            Subject2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[0]));
//            Teacher2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[1]));
//            Auditory2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[2]));
//
//            Subject3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[0]));
//            Teacher3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[1]));
//            Auditory3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[2]));
//
//            Subject4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[0]));
//            Teacher4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[1]));
//            Auditory4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[2]));
//
//            Subject5.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[0]));
//            Teacher5.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[1]));
//            Auditory5.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().split(", ")[2]));
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }

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
