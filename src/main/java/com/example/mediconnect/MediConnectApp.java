package com.example.mediconnect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main application class for MediConnect - Doctor Appointment System
 * Loads the original MediConnect landing page directly
 */
public class MediConnectApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database on startup
            DatabaseManager.initializeDatabase();

            // Load the original MediConnect landing page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/landing.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            // Apply shared stylesheet
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());

            primaryStage.setTitle("AidAccess - Patient Portal");

            // Load icon if available
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            } catch (Exception e) {
                // Icon not found, continue without it
            }

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError(primaryStage, "Error loading MediConnect", e.getMessage());
        }
    }

    @Override
    public void stop() {
        // Close database connection on application shutdown
        DatabaseManager.close();
    }

    private void showError(Stage stage, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Error: " + message + "\n\nMake sure MySQL database is running and configured correctly.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
