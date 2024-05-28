package com.example.sigmatimemanager.fxml_controller;

import com.example.sigmatimemanager.dbController.CreateScheduleDBProblemChecker;
import com.example.sigmatimemanager.dbController.UpdateDBProblemChecker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class UpdateScheduleController {
    private Connection con;
    @FXML
    AnchorPane MainPane;
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
    RadioButton radioLock2;
    @FXML
    RadioButton radioLock3;
    @FXML
    RadioButton radioLock4;
    @FXML
    RadioButton radioLock5;
    @FXML
    RadioButton radioLock6;



    final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    final String DATABASE_USER = "postgres";
    final String DATABASE_PASSWORD = "1234";

    public void initialize() throws SQLException {
        fillChoices();
        fillRecentGroupsView();
        checkButton.setOnAction(event -> {
            checkProblems();
        });
        fillUpdateChoises();
        confirmButton.setOnAction(event -> {
            try {
                handleConfirmButtonAction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void radioButtons(){
        UpdateDBProblemChecker updateDBProblemChecker = new UpdateDBProblemChecker();
        Integer completed_id = 0;
        String groupBox = groupComboBox.getValue();
        String dateBox = dateComboBox.getValue();
        Integer classBox = classComboBox.getValue();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String query = "SELECT id FROM completed_schedule WHERE group_name = ? AND day_of_week = ? AND class_number = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, groupBox);
            ps.setString(2, dateBox);
            ps.setInt(3, classBox);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                completed_id = rs.getInt("id");
                System.out.println("completed_schedule id: " + completed_id);
            } else {
                System.out.println("Schedule not found 1.");
            }
        }catch (SQLException ex) {
            System.out.println("Error 2.");
            ex.printStackTrace();
        }
        Integer finalCompleted_id = completed_id;
        radioLock2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {

                try {
                    classChoiceBox.setValue(updateDBProblemChecker.getClassNumber(finalCompleted_id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                classChoiceBox.setDisable(true);

            } else {
                classChoiceBox.setValue(null);
                classChoiceBox.setDisable(false);
            }
        });
        radioLock3.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    dayOfWeek.setValue(updateDBProblemChecker.getDate(finalCompleted_id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                dayOfWeek.setDisable(true);
            } else {
                dayOfWeek.setValue(null);
                dayOfWeek.setDisable(false);
            }
        });
        radioLock4.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    subjectChoiceBox.setValue(updateDBProblemChecker.getSubject(finalCompleted_id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                subjectChoiceBox.setDisable(true);
            } else {
                subjectChoiceBox.setValue(null);
                subjectChoiceBox.setDisable(false);
            }
        });
        radioLock5.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    teacherChoiceBox.setValue(updateDBProblemChecker.getTeacher(finalCompleted_id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                teacherChoiceBox.setDisable(true);
            } else {
                teacherChoiceBox.setValue(null);
                teacherChoiceBox.setDisable(false);
            }
        });
        radioLock6.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    auditoryBuildingChoiceBox.setValue(updateDBProblemChecker.getAuditoryBuilding(finalCompleted_id));
                    auditoryFloorChoiceBox.setValue(updateDBProblemChecker.getAuditoryFloor(finalCompleted_id));
                    auditoryRoomChoiceBox.setValue(updateDBProblemChecker.getAuditoryRoom(finalCompleted_id));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                auditoryBuildingChoiceBox.setDisable(true);
                auditoryFloorChoiceBox.setDisable(true);
                auditoryRoomChoiceBox.setDisable(true);
            } else {
                auditoryBuildingChoiceBox.setValue(null);
                auditoryFloorChoiceBox.setValue(null);
                auditoryRoomChoiceBox.setValue(null);

                auditoryBuildingChoiceBox.setDisable(false);
                auditoryFloorChoiceBox.setDisable(false);
                auditoryRoomChoiceBox.setDisable(false);
            }
        });
    }
    public void fillChoices() throws SQLException {
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
    public void fillUpdateChoises(){
        ObservableList<Integer> Class = FXCollections.observableArrayList();
        ObservableList<String> Subject = FXCollections.observableArrayList();
        ObservableList<String> Teacher = FXCollections.observableArrayList();
        ObservableList<String> AuditoryBuilding = FXCollections.observableArrayList();
        ObservableList<Integer> AuditoryFloor = FXCollections.observableArrayList();
        ObservableList<Integer> AuditoryRoom = FXCollections.observableArrayList();
        final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
        final String DATABASE_USER = "postgres";
        final String DATABASE_PASSWORD = "1234";
        groupComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                radioButtons();
                classChoiceBox.getItems().clear();
                subjectChoiceBox.getItems().clear();
                teacherChoiceBox.getItems().clear();
                auditoryBuildingChoiceBox.getItems().clear();
                auditoryFloorChoiceBox.getItems().clear();
                auditoryRoomChoiceBox.getItems().clear();
                try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
                     Statement stmt = con.createStatement();) {
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

                    classChoiceBox.setItems(Class);
                    subjectChoiceBox.setItems(Subject);
                    teacherChoiceBox.setItems(Teacher);
                    auditoryBuildingChoiceBox.setItems(AuditoryBuilding);
                    auditoryBuildingChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
                        if (newValue1 != null) {
                            try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                                String floorCheckSQL = "SELECT DISTINCT floor FROM auditory WHERE building LIKE ?";
                                PreparedStatement ps = connection.prepareStatement(floorCheckSQL);
                                ps.setString(1, newValue1);
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

                    auditoryFloorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
                        if (newValue1 != null) {
                            try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                                String roomCheckSQL = "SELECT DISTINCT room_number FROM auditory WHERE building LIKE ? AND floor = ?";
                                PreparedStatement ps = connection.prepareStatement(roomCheckSQL);
                                ps.setString(1, auditoryBuildingChoiceBox.getValue());
                                ps.setInt(2, newValue1);
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

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

    });

    }

    private void checkProblems() {
        UpdateDBProblemChecker checker = new UpdateDBProblemChecker();
        checker.SetValues(groupComboBox.getValue(), classChoiceBox.getValue(),
                subjectChoiceBox.getValue(), teacherChoiceBox.getValue(),
                auditoryFloorChoiceBox.getValue(),auditoryBuildingChoiceBox.getValue(),
                auditoryRoomChoiceBox.getValue(), dayOfWeek.getValue());

        String problem = checker.isGroupContainsInDataBase();

        if (problem != null) {
            switch (problem) {
                case "Группа уже имеет запись в расписании на эту дату.":
                    problem3.setText(problem);
                    break;
                case "Преподаватель уже занят в это время.":
                    problem5.setText(problem);
                    break;
                case "Аудитория уже занята в это время.":
                    problem2.setText(problem);
                    break;
                case "Все нормально!":
                    problem2.setText(problem);
                    break;
            }
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
    @FXML
    TableView<GroupData> recentGroupsView;
    @FXML
    TableColumn<GroupData, String> groupCol;
    @FXML
    TableColumn<GroupData, Integer> courseCol;
    @FXML
    TableColumn<GroupData, LocalDate> dateCol;
    public void fillRecentGroupsView(){
        ObservableList<GroupData> data = FXCollections.observableArrayList();

        final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
        final String DATABASE_USER = "postgres";
        final String DATABASE_PASSWORD = "1234";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT completed_schedule.group_name, groups.course, completed_schedule.updated_at " +
                     "FROM completed_schedule INNER JOIN groups ON completed_schedule.group_id = groups.id")) {

            while (rs.next()) {
                String groupName = rs.getString("group_name");
                int course = rs.getInt("course");
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

                GroupData item = new GroupData(groupName, course, updatedAt);
                data.add(item);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        groupCol.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        recentGroupsView.setItems(data);
    }

    @FXML
    void handleConfirmButtonAction() throws SQLException {
        Integer class_number = classChoiceBox.getValue();
        String subjectName = subjectChoiceBox.getValue();
        String teacherName = teacherChoiceBox.getValue();
        String auditoryBuilding = auditoryBuildingChoiceBox.getValue();
        Integer auditoryRoom = auditoryRoomChoiceBox.getValue();
        Integer auditoryFloor = auditoryFloorChoiceBox.getValue();
        String date = dayOfWeek.getValue();

        Integer chooseCourseBox = ChooseCourse.getValue();
        String groupBox = groupComboBox.getValue();
        String dateBox = dateComboBox.getValue();
        Integer classBox = classComboBox.getValue();

        Boolean radioButton2 = radioLock2.isSelected();
        Boolean radioButton3 = radioLock3.isSelected();
        Boolean radioButton4 = radioLock4.isSelected();
        Boolean radioButton5 = radioLock5.isSelected();
        Boolean radioButton6 = radioLock6.isSelected();

        Integer id = 0;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String query = "SELECT id FROM completed_schedule WHERE group_name = ? AND day_of_week = ? AND class_number = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, groupBox);
            ps.setString(2, dateBox);
            ps.setInt(3, classBox);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
                System.out.println("completed_schedule id: " + id);
            } else {
                System.out.println("Schedule not found 1.");
            }
        }catch (SQLException ex) {
            System.out.println("Error 2.");
            ex.printStackTrace();
        }
        try {
            UpdateDBProblemChecker dbProblemChecker = new UpdateDBProblemChecker();
            if (!radioButton2){
                dbProblemChecker.updateClassNumber(id,class_number);
            }
            if (!radioButton3){
                dbProblemChecker.updateDate(id,dateBox);
            }
            if (!radioButton4){
                dbProblemChecker.updateSubjectId(id,subjectName);
            }
            if (!radioButton5){
                dbProblemChecker.updateTeacherId(id,teacherName);
            }
            if (!radioButton6){
                dbProblemChecker.updateAuditoryId(id,auditoryBuilding,auditoryRoom,auditoryFloor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
