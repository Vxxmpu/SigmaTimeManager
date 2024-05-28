module com.example.sigmatimemanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.sigmatimemanager to javafx.fxml;
    exports com.example.sigmatimemanager;
    exports com.example.sigmatimemanager.fxml_controller;
    opens com.example.sigmatimemanager.fxml_controller to javafx.fxml;
}