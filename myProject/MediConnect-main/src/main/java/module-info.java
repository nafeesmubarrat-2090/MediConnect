module com.example.mediconnect {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    opens com.example.mediconnect to javafx.fxml;
    exports com.example.mediconnect;
}