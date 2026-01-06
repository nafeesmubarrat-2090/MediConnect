package com.example.mediconnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField sectorField;
    @FXML private TextField qualificationsField;
    @FXML private TextField experienceField;
    @FXML private TextField ageField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField chamberField;
    @FXML private TextField locationField;
    @FXML private VBox daysContainer;
    @FXML private ComboBox<String> genderField;
    @FXML private TextField feesField;
    @FXML private TextField hospitalAffiliationsField;
    @FXML private ComboBox<String> onlineConsultationField;
    @FXML private ComboBox<Integer> consultationDurationField;

    // Store day rows for accessing later
    private Map<String, DayRow> dayRows = new LinkedHashMap<>();

    private static class DayRow {
        CheckBox checkBox;
        ComboBox<String> startTime;
        ComboBox<String> endTime;
        HBox container;

        DayRow(String day) {
            this.checkBox = new CheckBox(day);
            this.checkBox.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
            this.checkBox.setPrefWidth(60);

            this.startTime = new ComboBox<>();
            this.endTime = new ComboBox<>();

            ObservableList<String> times = FXCollections.observableArrayList();
            for (int i = 0; i < 24; i++) {
                times.add(String.format("%02d:00", i));
            }
            this.startTime.setItems(times);
            this.endTime.setItems(times);
            this.startTime.setPromptText("Start");
            this.endTime.setPromptText("End");
            this.startTime.setPrefWidth(110);
            this.endTime.setPrefWidth(110);
            this.startTime.setDisable(true);
            this.endTime.setDisable(true);

            // Enable time selection when checkbox is checked
            this.checkBox.setOnAction(e -> {
                boolean selected = this.checkBox.isSelected();
                this.startTime.setDisable(!selected);
                this.endTime.setDisable(!selected);
                if (!selected) {
                    this.startTime.setValue(null);
                    this.endTime.setValue(null);
                }
            });

            Label toLabel = new Label("to");
            toLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

            this.container = new HBox(10, checkBox, startTime, toLabel, endTime);
            this.container.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5 0;");
        }

        boolean isSelected() {
            return checkBox.isSelected();
        }

        String getDay() {
            return checkBox.getText();
        }

        String getStartTime() {
            return startTime.getValue();
        }

        String getEndTime() {
            return endTime.getValue();
        }

        boolean hasValidTimes() {
            return startTime.getValue() != null && endTime.getValue() != null;
        }
    }

    @FXML
    public void initialize() {
        if (genderField != null) {
            genderField.setItems(FXCollections.observableArrayList("Male", "Female"));
        }
        if (onlineConsultationField != null) {
            onlineConsultationField.setItems(FXCollections.observableArrayList("Yes", "No"));
        }
        if (consultationDurationField != null) {
            ObservableList<Integer> durations = FXCollections.observableArrayList();
            for (int i = 10; i <= 60; i += 5) {
                durations.add(i);
            }
            consultationDurationField.setItems(durations);
        }

        // Initialize days container with checkboxes and time selectors
        if (daysContainer != null) {
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String day : days) {
                DayRow row = new DayRow(day);
                dayRows.put(day, row);
                daysContainer.getChildren().add(row.container);
            }
        }
    }

    @FXML
    protected void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String sector = sectorField.getText();
        String qualifications = qualificationsField.getText();
        String experience = experienceField.getText();
        String age = ageField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String chamber = chamberField.getText();
        String location = locationField.getText();
        String gender = genderField.getValue();
        String fees = feesField.getText();
        String hospitalAffiliations = hospitalAffiliationsField.getText();
        String onlineConsultation = onlineConsultationField.getValue();
        Integer consultationDuration = consultationDurationField.getValue();

        // Build consultation hours string (format: "Mon:09:00-17:00 Tue:10:00-14:00")
        StringBuilder consultationHoursBuilder = new StringBuilder();
        StringBuilder availableDaysBuilder = new StringBuilder();
        boolean hasSelectedDays = false;

        for (DayRow row : dayRows.values()) {
            if (row.isSelected()) {
                if (!row.hasValidTimes()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Registration Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Please set consultation hours for " + row.getDay());
                    alert.showAndWait();
                    return;
                }
                hasSelectedDays = true;
                if (consultationHoursBuilder.length() > 0) {
                    consultationHoursBuilder.append(" ");
                    availableDaysBuilder.append(" ");
                }
                consultationHoursBuilder.append(row.getDay())
                    .append(":")
                    .append(row.getStartTime())
                    .append("-")
                    .append(row.getEndTime());
                availableDaysBuilder.append(row.getDay());
            }
        }

        String consultationHours = consultationHoursBuilder.toString();
        String availableDays = availableDaysBuilder.toString();

        // Basic validation - check for empty fields
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || sector.isEmpty() ||
            qualifications.isEmpty() || experience.isEmpty() || age.isEmpty() || phone.isEmpty() ||
            email.isEmpty() || chamber.isEmpty() || location.isEmpty() || !hasSelectedDays ||
            gender == null || fees.isEmpty() || hospitalAffiliations.isEmpty() || onlineConsultation == null ||
            consultationDuration == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required! Please select at least one day and set consultation hours.");
            alert.showAndWait();
            return;
        }

        // Check for duplicate username using database
        DoctorDAO dao = new DoctorDAO();
        if (dao.usernameExists(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("Username already exists! Please choose a different username.");
            alert.showAndWait();
            return;
        }

        // Create Doctor object and register in database
        Doctor newDoctor = new Doctor(username, password, name, sector, qualifications, experience,
                                       age, phone, email, chamber, location, consultationHours,
                                       availableDays, gender, fees, hospitalAffiliations, onlineConsultation,
                                       consultationDuration);

        if (dao.registerDoctor(newDoctor)) {
            // Success - show confirmation and go to login
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Registration successful! You can now login.");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/login.fxml"));
                Scene scene = new Scene(loader.load(), 500, 400);
                scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Doctor Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to register. Please try again.");
            alert.showAndWait();
        }
    }


    @FXML
    protected void handleGoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Doctor Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
