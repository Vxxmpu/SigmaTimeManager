package com.example.sigmatimemanager.dbController;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateDBProblemChecker {
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    private final String DATABASE_USER = "postgres";
    private final String DATABASE_PASSWORD = "1234";
    private Integer class_number;
    private Integer auditory_id = 0;
    private Integer group_id = 0;
    private Integer subject_id = 0;
    private Integer teacher_id = 0;
    private String date;

    public void SetValues(String groupName, Integer class_number, String subjectName, String teacherName, Integer floor, String building, Integer roomNumber, String date) {
//        String[] parts = auditory_number.split("\\.");
//        String building = parts[0];
//        int roomNumber = Integer.parseInt(parts[1]);
//        int floor = Integer.parseInt(parts[2]);
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            this.class_number = class_number;
            this.date = date;
            String sql = "SELECT * FROM auditory WHERE building = ? AND room_number = ? AND floor = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, building);
            pst.setInt(2, roomNumber);
            pst.setInt(3, floor);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                auditory_id = rs.getInt("id");
            }
            String sql2 = "SELECT * FROM groups WHERE group_name = ?";
            PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setString(1, groupName);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                group_id = rs2.getInt("id");
            }
            String sql3 = "SELECT * FROM subject WHERE subject_name = ?";
            PreparedStatement pst3 = con.prepareStatement(sql3);
            pst3.setString(1, subjectName);
            ResultSet rs3 = pst3.executeQuery();
            if (rs3.next()) {
                subject_id = rs3.getInt("id");
            }
            String sql4 = "SELECT * FROM teacher WHERE full_name = ?";
            PreparedStatement pst4 = con.prepareStatement(sql4);
            pst4.setString(1, teacherName);
            ResultSet rs4 = pst4.executeQuery();
            if (rs4.next()) {
                teacher_id = rs4.getInt("id");
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(CreateScheduleDBController.class.getName());

            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public String isGroupContainsInDataBase() {
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {

            String query1 = "SELECT * FROM completed_schedule WHERE group_id = ? AND class_number = ? AND day_of_week = ?";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setInt(1, group_id);
            stmt1.setInt(2, class_number);
            stmt1.setString(3, date);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                return "Группа уже имеет запись в расписании на эту дату.";
            }

            String query2 = "SELECT * FROM completed_schedule WHERE teacher_id = ? AND day_of_week = ? AND class_number = ?";
            PreparedStatement stmt2 = con.prepareStatement(query2);
            stmt2.setInt(1, teacher_id);
            stmt2.setString(2, date);
            stmt2.setInt(3, class_number);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                return "Преподаватель уже занят в это время.";
            }

            String query3 = "SELECT * FROM completed_schedule WHERE auditory_id = ? AND day_of_week = ? AND class_number = ?";
            PreparedStatement stmt3 = con.prepareStatement(query3);
            stmt3.setInt(1, auditory_id);
            stmt3.setString(2, date);
            stmt3.setInt(3, class_number);
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) {
                return "Аудитория уже занята в это время.";
            }
            return "Все нормально!";
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(CreateScheduleDBProblemChecker.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public void updateClassNumber(Integer scheduleId, Integer classNumber) throws SQLException {
        String query = "UPDATE completed_schedule SET class_number = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,classNumber);
            ps.setInt(2,scheduleId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("class number changed successfully");
            }
        }
    }

    // МЕТОДЫ ДЛЯ ПРОВЕРКИ
    ////////////////////////////////////////////
    public Integer getClassNumber(Integer id) throws SQLException {
        int class_number =0;
        String query = "SELECT class_number FROM completed_schedule WHERE id = ?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            class_number = rs.getInt("class_number");
        }
        return class_number;
    }
    public String getDate(Integer id) throws SQLException {
        String date = null;
        String query = "SELECT day_of_week FROM completed_schedule WHERE id = ?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            date = rs.getString("day_of_week");
        }
        return date;
    }
    public String getSubject(Integer id)throws SQLException{
        String subject = null;
        int subject_id = 0;
        String query = "SELECT subject_id FROM completed_schedule WHERE id=?";
        String query_fin = "SELECT subject_name FROM subject where id=?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            subject_id = rs.getInt("subject_id");
        }
        PreparedStatement pst2 = con.prepareStatement(query_fin);
        pst2.setInt(1,subject_id);
        rs = pst2.executeQuery();
        if (rs.next()){
            subject = rs.getString("subject_name");
        }
        return subject;
    }

    public String getTeacher(Integer id) throws SQLException {
        String teacher = null;
        int teacher_id = 0;
        String query = "SELECT teacher_id FROM completed_schedule WHERE id=?";
        String query_fin = "SELECT full_name FROM teacher where id=?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            teacher_id = rs.getInt("teacher_id");
        }
        PreparedStatement pst2 = con.prepareStatement(query_fin);
        pst2.setInt(1,teacher_id);
        rs = pst2.executeQuery();
        if (rs.next()){
            teacher = rs.getString("full_name");
        }
        return teacher;
    }
    public String getAuditoryBuilding(Integer id)throws SQLException{
        String auditoryBuilding = null;
        int auditory_id = 0;
        String query = "SELECT auditory_id FROM completed_schedule WHERE id=?";
        String query_fin = "SELECT building FROM teacher where id=?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            auditory_id = rs.getInt("id");
        }
        PreparedStatement pst2 = con.prepareStatement(query_fin);
        pst2.setInt(1,auditory_id);
        rs = pst2.executeQuery();
        if (rs.next()){
            auditoryBuilding = rs.getString("building");
        }

        return auditoryBuilding;
    }
    public Integer getAuditoryFloor(Integer id)throws SQLException{
        Integer auditoryFloor = null;
        int auditory_id = 0;
        String query = "SELECT auditory_id FROM completed_schedule WHERE id=?";
        String query_fin = "SELECT floor FROM teacher where id=?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            auditory_id = rs.getInt("id");
        }
        PreparedStatement pst2 = con.prepareStatement(query_fin);
        pst2.setInt(1,auditory_id);
        rs = pst2.executeQuery();
        if (rs.next()){
            auditoryFloor = rs.getInt("floor");
        }

        return auditoryFloor;
    }
    public Integer getAuditoryRoom(Integer id)throws SQLException{
        Integer auditoryRoom = null;
        int auditory_id = 0;
        String query = "SELECT auditory_id FROM completed_schedule WHERE id=?";
        String query_fin = "SELECT room_number FROM teacher where id=?";
        Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            auditory_id = rs.getInt("id");
        }
        PreparedStatement pst2 = con.prepareStatement(query_fin);
        pst2.setInt(1,auditory_id);
        rs = pst2.executeQuery();
        if (rs.next()){
            auditoryRoom = rs.getInt("room_number");
        }

        return auditoryRoom;
    }
    public void updateSubjectId(Integer scheduleId, String subjectName) throws SQLException {
        String query = "UPDATE completed_schedule SET subject_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int subject_id = 0;
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {

            String sql3 = "SELECT * FROM subject WHERE subject_name = ?";
            PreparedStatement pst3 = con.prepareStatement(sql3);
            pst3.setString(1, subjectName);
            ResultSet rs3 = pst3.executeQuery();
            if (rs3.next()) {
                subject_id = rs3.getInt("id");
            }
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,subject_id);
            ps.setInt(2,scheduleId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("subject changed successfully");
            }
        }

    }

    public void updateTeacherId(Integer scheduleId,String teacherName) throws SQLException {
        String query = "UPDATE completed_schedule SET teacher_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int teacher_id = 0;
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {

            String sql4 = "SELECT * FROM teacher WHERE full_name = ?";
            PreparedStatement pst4 = con.prepareStatement(sql4);
            pst4.setString(1, teacherName);
            ResultSet rs4 = pst4.executeQuery();
            if (rs4.next()) {
                teacher_id = rs4.getInt("id");
            }
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,teacher_id);
            ps.setInt(2,scheduleId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("teacher changed successfully");
            }
        }
    }

    public void updateAuditoryId(Integer scheduleId, String building,Integer roomNumber, Integer floor) throws SQLException {
        String query = "UPDATE completed_schedule SET auditory_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int auditory_id = 0;

        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {

            String sql = "SELECT * FROM auditory WHERE building = ? AND room_number = ? AND floor = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, building);
            pst.setInt(2, roomNumber);
            pst.setInt(3, floor);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                auditory_id = rs.getInt("id");
            }
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,auditory_id);
            ps.setInt(2,scheduleId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("auditory changed successfully");
            }
        }



    }

    public void updateDate(Integer scheduleId, String date) throws SQLException {
        String query = "UPDATE completed_schedule SET day_of_week = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, date);
            ps.setInt(2, scheduleId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("date changed successfully");
            }
        }

    }
    //короч сюда во-первых надо будет сделать методу что бы он принимал по 2 значения это типо то что comboBox и choiceBox(либо же легче будет на момент подбора группы для изменения взять уже айди
//расписания что бы здесь легче было его заменить ведь id всегда уникален
//    public void updateInDatabase(Integer schedule_id, Integer class_number, String subjectName, String teacherName, Integer floor, String building, Integer roomNumber, LocalDate date) throws SQLException {
//        String query = "UPDATE completed_schedule SET class_number = ?,updated_at = CURRENT_TIMESTAMP, subject_id = ?, teacher_id = ?, auditory_id = ?, date = ? WHERE id = ?";
//
////        String[] parts = auditory_number.split("\\.");
////
////        String building = parts[0];
////        int roomNumber = Integer.parseInt(parts[1]);
////        int floor = Integer.parseInt(parts[2]);
//        int auditory_id = 0;
//        int subject_id = 0;
//        int teacher_id = 0;
//        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
//
//            String sql = "SELECT * FROM auditory WHERE building = ? AND room_number = ? AND floor = ?";
//            PreparedStatement pst = con.prepareStatement(sql);
//            pst.setString(1, building);
//            pst.setInt(2, roomNumber);
//            pst.setInt(3, floor);
//            ResultSet rs = pst.executeQuery();
//            if (rs.next()) {
//                auditory_id = rs.getInt("id");
//            }
//
//            String sql3 = "SELECT * FROM subject WHERE subject_name = ?";
//            PreparedStatement pst3 = con.prepareStatement(sql3);
//            pst3.setString(1, subjectName);
//            ResultSet rs3 = pst3.executeQuery();
//            if (rs3.next()) {
//                subject_id = rs3.getInt("id");
//            }
//
//            String sql4 = "SELECT * FROM teacher WHERE full_name = ?";
//            PreparedStatement pst4 = con.prepareStatement(sql4);
//            pst4.setString(1, teacherName);
//            ResultSet rs4 = pst4.executeQuery();
//            if (rs4.next()) {
//                teacher_id = rs4.getInt("id");
//            }
//            PreparedStatement ps = con.prepareStatement(query);
//            ps.setInt(1, class_number);
//            ps.setInt(2, subject_id);
//            ps.setInt(3, teacher_id);
//            ps.setInt(4, auditory_id);
//            ps.setDate(5, Date.valueOf(date));
//            ps.setInt(6, schedule_id);
//            int rowsUpdated = ps.executeUpdate();
//            if (rowsUpdated > 0) {
//                System.out.println("Данные успешно обновлены.");
//            }
//
//        } catch (SQLException ex) {
//            Logger lgr = Logger.getLogger(UpdateDBProblemChecker.class.getName());
//
//            lgr.log(Level.SEVERE, ex.getMessage(), ex);
//        }
//
//    }
}
