package com.example.sigmatimemanager.dbController;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateScheduleDBProblemChecker {
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    private final String DATABASE_USER = "postgres";
    private final String DATABASE_PASSWORD = "1234";
    private Integer class_number;
    private Integer auditory_id = 0;
    private Integer group_id = 0;
    private Integer subject_id = 0;
    private Integer teacher_id = 0;
    private String date;

    public void SetValues(String groupName, Integer class_number, String subjectName, String teacherName,Integer floor,String building,Integer roomNumber, String date){
//        String[] parts = auditory_number.split("\\.");
//        String building = parts[0];
//        int roomNumber = Integer.parseInt(parts[1]);
//        int floor = Integer.parseInt(parts[2]);
        try(Connection con = DriverManager.getConnection(DATABASE_URL,DATABASE_USER,DATABASE_PASSWORD)) {
            this.class_number= class_number;
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
            pst2.setString(1,groupName);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                group_id = rs2.getInt("id");
            }
            String sql3 = "SELECT * FROM subject WHERE subject_name = ?";
            PreparedStatement pst3 = con.prepareStatement(sql3);
            pst3.setString(1,subjectName);
            ResultSet rs3 = pst3.executeQuery();
            if (rs3.next()) {
                subject_id = rs3.getInt("id");
            }
            String sql4 = "SELECT * FROM teacher WHERE full_name = ?";
            PreparedStatement pst4 = con.prepareStatement(sql4);
            pst4.setString(1,teacherName);
            ResultSet rs4 = pst4.executeQuery();
            if (rs4.next()) {
                teacher_id = rs4.getInt("id");
            }

    } catch (SQLException ex) {
            Logger lgr  = Logger.getLogger(CreateScheduleDBController.class.getName());

            lgr.log(Level.SEVERE, ex.getMessage(),ex);
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
            stmt2.setInt(3,class_number);
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

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(CreateScheduleDBProblemChecker.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
