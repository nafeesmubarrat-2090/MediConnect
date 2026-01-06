package com.example.mediconnect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorDetailController {
    @FXML private Label doctorNameLabel;
    @FXML private Label doctorSectorLabel;
    @FXML private Label qualificationsLabel;
    @FXML private Label experienceLabel;
    @FXML private Label ageLabel;
    @FXML private Label genderLabel;
    @FXML private Label chamberLabel;
    @FXML private Label locationLabel;
    @FXML private Label hospitalLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label feesLabel;
    @FXML private VBox scheduleContainer;
    @FXML private Label onlineLabel;

    private Doctor currentDoctor;

    public void setDoctor(Doctor doctor) {
        this.currentDoctor = doctor;
        populateFields();
    }

    private void populateFields() {
        if (currentDoctor == null) return;

        doctorNameLabel.setText(currentDoctor.getName());
        doctorSectorLabel.setText(currentDoctor.getSector());
        qualificationsLabel.setText(currentDoctor.getQualifications());
        experienceLabel.setText(currentDoctor.getExperience() + " years");
        ageLabel.setText(currentDoctor.getAge() + " years");
        genderLabel.setText(currentDoctor.getGender());
        chamberLabel.setText(currentDoctor.getChamber());
        locationLabel.setText(currentDoctor.getLocation());
        hospitalLabel.setText(currentDoctor.getHospitalAffiliations());
        phoneLabel.setText(currentDoctor.getPhone());
        emailLabel.setText(currentDoctor.getEmail());
        feesLabel.setText("৳ " + currentDoctor.getFees());

        // Display schedule (per-day consultation hours)
        if (scheduleContainer != null) {
            scheduleContainer.getChildren().clear();
            String consultationHours = currentDoctor.getConsultationHours();
            if (consultationHours != null && !consultationHours.isEmpty()) {
                // Check if it's new format (contains day prefix like "Mon:")
                if (consultationHours.contains(":") && (consultationHours.contains("Mon") ||
                    consultationHours.contains("Tue") || consultationHours.contains("Wed"))) {
                    // New per-day format: "Mon:09:00-17:00 Tue:10:00-14:00"
                    String[] entries = consultationHours.split("\\s+");
                    for (String entry : entries) {
                        if (entry.contains(":")) {
                            int firstColon = entry.indexOf(':');
                            String day = entry.substring(0, firstColon);
                            String times = entry.substring(firstColon + 1);
                            Label scheduleLabel = new Label(day + ": " + times.replace("-", " - "));
                            scheduleLabel.setStyle("-fx-font-size:15px; -fx-text-fill:#2d3748;");
                            scheduleContainer.getChildren().add(scheduleLabel);
                        }
                    }
                } else {
                    // Old format - show as single entry with available days
                    String availableDays = currentDoctor.getAvailableDays();
                    Label scheduleLabel = new Label(availableDays + ": " + consultationHours);
                    scheduleLabel.setStyle("-fx-font-size:15px; -fx-text-fill:#2d3748;");
                    scheduleContainer.getChildren().add(scheduleLabel);
                }
            }
        }

        // Style online consultation label
        String online = currentDoctor.getOnlineConsultation();
        onlineLabel.setText(online);
        if ("Yes".equalsIgnoreCase(online)) {
            onlineLabel.setStyle("-fx-font-size:15px; -fx-text-fill:#48bb78; -fx-font-weight:600;");
        } else {
            onlineLabel.setStyle("-fx-font-size:15px; -fx-text-fill:#f56565; -fx-font-weight:600;");
        }
    }

    @FXML
    private void handleBookReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/book_reservation.fxml"));
            Scene scene = new Scene(loader.load(), 700, 650);

            if (getClass().getResource("/com/example/mediconnect/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            }

            BookReservationController controller = loader.getController();
            controller.setDoctor(currentDoctor);

            Stage stage = (Stage) doctorNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Book Reservation - " + currentDoctor.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load booking page");
            alert.setContentText("Error: " + e.getMessage() + "\n\nPlease check the console for details.");
            alert.showAndWait();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/patient_home.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) doctorNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("AidAccess - Find a Doctor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

