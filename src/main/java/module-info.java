module com.example.socialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.xml.crypto;


    opens com.example.socialnetworkgui to javafx.fxml, javafx.base;
    opens com.example.socialnetworkgui.controller to javafx.fxml;
    exports com.example.socialnetworkgui;
    exports com.example.socialnetworkgui.controller;

    opens com.example.socialnetworkgui.domain to javafx.base;
    exports com.example.socialnetworkgui.domain;

    opens com.example.socialnetworkgui.exceptions;
    exports com.example.socialnetworkgui.exceptions;

    opens com.example.socialnetworkgui.DTO to javafx.base;
    exports com.example.socialnetworkgui.DTO;


}