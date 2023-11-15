module com.example.socialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.xml.crypto;

    opens com.example.socialnetworkgui to javafx.fxml;
    exports com.example.socialnetworkgui;
}