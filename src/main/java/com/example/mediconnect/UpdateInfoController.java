package com.example.mediconnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class UpdateInfoController {
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField sectorField;
    @FXML private TextField chamberField;
    @FXML private TextField locationField;
    @FXML private TextField qualificationsField;
    @FXML private TextField experienceField;
    @FXML private TextField ageField;
    @FXML private TextField emailField;
    @FXML private VBox daysContainer;
    @FXML private ComboBox<String> genderField;
    @FXML private TextField feesField;
    @FXML private TextField hospitalAffiliationsField;
    @FXML private ComboBox<String> onlineConsultationField;
    @FXML private ComboBox<Integer> consultationDurationField;
    @FXML private PasswordField passwordField;

    private Doctor currentDoctor;

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

        void setSelected(boolean selected) {
            checkBox.setSelected(selected);
            startTime.setDisable(!selected);
            endTime.setDisable(!selected);
        }

        void setTimes(String start, String end) {
            startTime.setValue(start);
            endTime.setValue(end);
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

    public void setDoctor(Doctor doctor) {
        this.currentDoctor = doctor;
        nameField.setText(doctor.getName());
        phoneField.setText(doctor.getPhone());
        sectorField.setText(doctor.getSector());
        chamberField.setText(doctor.getChamber());
        locationField.setText(doctor.getLocation());
        qualificationsField.setText(doctor.getQualifications());
        experienceField.setText(doctor.getExperience());
        ageField.setText(doctor.getAge());
        emailField.setText(doctor.getEmail());
        genderField.setValue(doctor.getGender());
        feesField.setText(doctor.getFees());
        hospitalAffiliationsField.setText(doctor.getHospitalAffiliations());
        onlineConsultationField.setValue(doctor.getOnlineConsultation());
        consultationDurationField.setValue(doctor.getConsultationDuration());
        passwordField.setText(doctor.getPassword());

        // Parse consultation hours (format: "Mon:09:00-17:00 Tue:10:00-14:00")
        String consultationHours = doctor.getConsultationHours();
        if (consultationHours != null && !consultationHours.isEmpty()) {
            // Check if it's the new format (contains day prefix like "Mon:")
            if (consultationHours.contains(":") && (consultationHours.contains("Mon") ||
                consultationHours.contains("Tue") || consultationHours.contains("Wed"))) {
                // New per-day format
                String[] dayEntries = consultationHours.split("\\s+");
                for (String entry : dayEntries) {
                    if (entry.contains(":")) {
                        int firstColon = entry.indexOf(':');
                        String day = entry.substring(0, firstColon);
                        String times = entry.substring(firstColon + 1);

                        if (times.contains("-")) {
                            String[] timeParts = times.split("-");
                            if (timeParts.length == 2 && dayRows.containsKey(day)) {
                                DayRow row = dayRows.get(day);
                                row.setSelected(true);
                                row.setTimes(timeParts[0].trim(), timeParts[1].trim());
                            }
                        }
                    }
                }
            } else {
                // Old format (single time range like "09:00 - 17:00")
                // Apply same time to all selected days
                String[] timeParts = consultationHours.split("-");
                String startTime = timeParts.length > 0 ? timeParts[0].trim() : null;
                String endTime = timeParts.length > 1 ? timeParts[1].trim() : null;

                // Also parse available days from doctor
                String availableDays = doctor.getAvailableDays();
                if (availableDays != null && !availableDays.isEmpty()) {
                    String[] days = availableDays.split("\\s+");
                    for (String day : days) {
                        if (dayRows.containsKey(day)) {
                            DayRow row = dayRows.get(day);
                            row.setSelected(true);
                            if (startTime != null && endTime != null) {
                                row.setTimes(startTime, endTime);
                            }
                        }
                    }
                }
            }
        } else {
            // If no consultation hours, just pre-select available days
            String availableDays = doctor.getAvailableDays();
            if (availableDays != null && !availableDays.isEmpty()) {
                String[] days = availableDays.split("\\s+");
                for (String day : days) {
                    if (dayRows.containsKey(day)) {
                        dayRows.get(day).setSelected(true);
                    }
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        // Validate fields
        if (nameField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            sectorField.getText().trim().isEmpty() ||
            chamberField.getText().trim().isEmpty() ||
            locationField.getText().trim().isEmpty() ||
            qualificationsField.getText().trim().isEmpty() ||
            experienceField.getText().trim().isEmpty() ||
            ageField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            genderField.getValue() == null ||
            feesField.getText().trim().isEmpty() ||
            hospitalAffiliationsField.getText().trim().isEmpty() ||
            onlineConsultationField.getValue() == null ||
            consultationDurationField.getValue() == null ||
            passwordField.getText().trim().isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        // Build consultation hours string (format: "Mon:09:00-17:00 Tue:10:00-14:00")
        StringBuilder consultationHoursBuilder = new StringBuilder();
        StringBuilder availableDaysBuilder = new StringBuilder();
        boolean hasSelectedDays = false;

        for (DayRow row : dayRows.values()) {
            if (row.isSelected()) {
                if (!row.hasValidTimes()) {
                    showAlert("Error", "Please set consultation hours for " + row.getDay());
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

        if (!hasSelectedDays) {
            showAlert("Error", "Please select at least one day and set consultation hours!");
            return;
        }

        String consultationHours = consultationHoursBuilder.toString();
        String availableDays = availableDaysBuilder.toString();

        // Create updated Doctor object
        Doctor updatedDoctor = new Doctor(
            currentDoctor.getUsername(),
            passwordField.getText().trim(),
            nameField.getText().trim(),
            sectorField.getText().trim(),
            qualificationsField.getText().trim(),
            experienceField.getText().trim(),
            ageField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            chamberField.getText().trim(),
            locationField.getText().trim(),
            consultationHours,
            availableDays,
            genderField.getValue(),
            feesField.getText().trim(),
            hospitalAffiliationsField.getText().trim(),
            onlineConsultationField.getValue(),
            consultationDurationField.getValue()
        );

        // Update in database
        DoctorDAO dao = new DoctorDAO();
        if (dao.updateDoctor(updatedDoctor)) {
            currentDoctor = updatedDoctor;
            showAlert("Success", "Profile updated successfully!");
            handleGoBack();
        } else {
            showAlert("Error", "Failed to update profile!");
        }
    }

    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/Dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 600, 450);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());

            // Pass the current doctor back to the dashboard
            DoctorDashboard controller = loader.getController();
            controller.setDoctor(currentDoctor);

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Doctor Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
