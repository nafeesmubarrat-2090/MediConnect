package com.example.mediconnect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PatientLandingController {

    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;

    @FXML
    private void initialize() {
        // Bind the ImageView size to the scene size for proper scaling
        if (backgroundImage != null && root != null) {
            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                    backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
                }
            });
        }
    }

    @FXML
    private void goDoctorLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Doctor Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFindDoctor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/patient_home.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("AidAccess • Find a Doctor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
