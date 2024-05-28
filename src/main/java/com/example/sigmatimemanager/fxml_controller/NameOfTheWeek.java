package com.example.sigmatimemanager.fxml_controller;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class NameOfTheWeek {
    public static DayOfWeek getNameOfTheWeek(LocalDate date) {
        return date.getDayOfWeek();
    }
}
