module com.example.socialprojectgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.socialprojectgui to javafx.fxml;
    exports com.example.socialprojectgui;
    exports com.example.socialprojectgui.controller;
    opens com.example.socialprojectgui.controller to javafx.fxml;
    opens  com.example.socialprojectgui.domain to javafx.base;
    exports com.example.socialprojectgui.domain.validators;
    exports com.example.socialprojectgui.domain;
}