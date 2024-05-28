package com.example.sigmatimemanager.dbController;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateScheduleDBController {
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Diploma";
    private final String DATABASE_USER = "postgres";
    private final String DATABASE_PASSWORD = "1234";


//    //не нужно пока что
//    public boolean groupContainsInDataBase(Object group, Object Class ){
//        try (Connection con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)){
//            String query ="SELECT group_name FROM completed_schedule where class_number = ?";
//            PreparedStatement stmt = con.prepareStatement(query);
//            stmt.setObject(1,Class);
//            ResultSet rsGroup = stmt.executeQuery();
//            while (rsGroup.next()) {
//                if (rsGroup.getString("group_name") == group){
//                    return false;
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return true;
//    }
//
    public void saveToDatabase(String groupName, Integer class_number, String subjectName, String teacherName, Integer floor,String building,Integer roomNumber, String dayOfWeek) throws SQLException{
        String query ="INSERT INTO completed_schedule(\"group_name\", \"class_number\",\"subject_id\",\"teacher_id\",\"auditory_id\",\"day_of_week\",\"group_id\") VALUES( ?, ?, ?, ?, ?, ?, ?)";
//        String[] parts = auditory_number.split("\\.");
//
//        String building = parts[0];
//        int roomNumber = Integer.parseInt(parts[1]);
//        int floor = Integer.parseInt(parts[2]);
        int auditory_id = 0;
        int group_id = 0;
        int subject_id = 0;
        int teacher_id = 0;
        try(Connection con = DriverManager.getConnection(DATABASE_URL,DATABASE_USER,DATABASE_PASSWORD)) {

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
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,groupName);
            pstmt.setInt(2,class_number);
            pstmt.setInt(3,subject_id);
            pstmt.setInt(4,teacher_id);
            pstmt.setInt(5,auditory_id);
            pstmt.setString(6, dayOfWeek);
            pstmt.setInt(7,group_id);
            pstmt.executeUpdate();
            System.out.println("Success!");
        }catch (SQLException ex){
            Logger lgr  = Logger.getLogger(CreateScheduleDBController.class.getName());

            lgr.log(Level.SEVERE, ex.getMessage(),ex);
        };
//        try(Connection con = DriverManager.getConnection(DATABASE_URL,DATABASE_USER,DATABASE_PASSWORD)) {
//            PreparedStatement pst = con.prepareStatement(query);
//
//        }catch (SQLException ex){
//            Logger lgr  = Logger.getLogger(CreateScheduleDBController.class.getName());
//
//            lgr.log(Level.SEVERE, ex.getMessage(),ex);
//        };
    }

}
