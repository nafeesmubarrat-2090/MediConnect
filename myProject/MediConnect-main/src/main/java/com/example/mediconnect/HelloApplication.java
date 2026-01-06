package com.example.mediconnect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database on startup
        DatabaseManager.initializeDatabase();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/landing.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        // apply shared stylesheet
        scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());

        stage.setTitle("AidAccess - Patient Portal");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Close database connection on application shutdown
        DatabaseManager.close();
    }
}
