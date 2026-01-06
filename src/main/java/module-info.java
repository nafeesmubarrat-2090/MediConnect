module com.example.mediconnect {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Export packages
    exports com.example.mediconnect;
    exports myProject;
    exports myProject.medicine;
    exports myProject.ambulance;

    // Open packages to JavaFX
    opens com.example.mediconnect to javafx.fxml, javafx.graphics;
    opens myProject to javafx.fxml, javafx.graphics;
    opens myProject.medicine to javafx.fxml, javafx.graphics;
    opens myProject.ambulance to javafx.fxml, javafx.graphics;
}