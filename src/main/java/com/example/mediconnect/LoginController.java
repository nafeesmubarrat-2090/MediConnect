package com.example.mediconnect;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Doctor doctor = checkCredentials(username, password);

        if (doctor != null) {
            try {
                // Use exact resource name for the Dashboard FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/Dashboard.fxml"));
                Scene scene = new Scene(loader.load(), 600, 450);

                // apply shared stylesheet
                scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());

                DoctorDashboard controller = loader.getController();
                controller.setDoctor(doctor);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Doctor Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password!");
            alert.showAndWait();
        }
    }

    private Doctor checkCredentials(String username, String password) {
        DoctorDAO dao = new DoctorDAO();
        return dao.authenticateDoctor(username, password);
    }

    @FXML
    private void handleCreateProfile(ActionEvent event) {
        try {
            // use absolute resource path to ensure it's found
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/register.fxml"));
            Scene scene = new Scene(loader.load(), 520, 520);
            // ensure consistent styling
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Create New Doctor Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/landing.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("AidAccess - Patient Portal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
