package com.example.mediconnect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DoctorDashboard {
    @FXML private Label nameLabel;
    @FXML private Label sectorLabel;
    @FXML private Label qualificationsLabel;
    @FXML private Label experienceLabel;
    @FXML private Label ageLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label chamberLabel;
    @FXML private Label locationLabel;
    @FXML private VBox scheduleContainer;
    @FXML private Label genderLabel;
    @FXML private Label feesLabel;
    @FXML private Label hospitalAffiliationsLabel;
    @FXML private Label onlineConsultationLabel;
    @FXML private Label consultationDurationLabel;
    @FXML private VBox timeSlotContainer;

    private Doctor doctor;
    private ReservationDAO reservationDAO;

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        this.reservationDAO = new ReservationDAO();

        this.nameLabel.setText(doctor.getName());
        this.sectorLabel.setText(doctor.getSector());
        this.qualificationsLabel.setText(doctor.getQualifications());
        this.experienceLabel.setText(doctor.getExperience());
        if (this.ageLabel != null) this.ageLabel.setText(doctor.getAge());
        this.phoneLabel.setText(doctor.getPhone());
        this.emailLabel.setText(doctor.getEmail());
        this.chamberLabel.setText(doctor.getChamber());
        this.locationLabel.setText(doctor.getLocation());

        // Display schedule (per-day consultation hours)
        if (this.scheduleContainer != null) {
            this.scheduleContainer.getChildren().clear();
            String consultationHours = doctor.getConsultationHours();
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
                            scheduleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a5568;");
                            this.scheduleContainer.getChildren().add(scheduleLabel);
                        }
                    }
                } else {
                    // Old format - show as single entry with available days
                    String availableDays = doctor.getAvailableDays();
                    Label scheduleLabel = new Label(availableDays + ": " + consultationHours);
                    scheduleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a5568;");
                    this.scheduleContainer.getChildren().add(scheduleLabel);
                }
            }
        }

        this.genderLabel.setText(doctor.getGender());
        this.feesLabel.setText(doctor.getFees());
        this.hospitalAffiliationsLabel.setText(doctor.getHospitalAffiliations());
        this.onlineConsultationLabel.setText(doctor.getOnlineConsultation());
        if (this.consultationDurationLabel != null) {
            this.consultationDurationLabel.setText(doctor.getConsultationDuration() + " minutes");
        }

        // Display time slots with reservations
        displayTimeSlots();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);
            Stage stage = (Stage) nameLabel.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Doctor Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/UpdateInfo.fxml"));
            Scene scene = new Scene(loader.load(), 650, 720);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            UpdateInfoController controller = loader.getController();
            controller.setDoctor(doctor);
            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Update Profile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayTimeSlots() {
        if (doctor == null || timeSlotContainer == null) {
            System.out.println("DEBUG: Doctor is null: " + (doctor == null) + ", Container is null: " + (timeSlotContainer == null));
            return;
        }

        timeSlotContainer.getChildren().clear();

        // Clean up past reservations
        reservationDAO.deletePastReservations();

        // Parse consultation hours (e.g., "09:00-17:00" or "0900-1700")
        String hoursStr = doctor.getConsultationHours();
        System.out.println("DEBUG Dashboard: Consultation hours: " + hoursStr);
        LocalTime startTime, endTime;

        try {
            // Handle per-day format
            if (hoursStr.contains("Mon") || hoursStr.contains("Tue") || hoursStr.contains("Wed")) {
                // Use first day's hours as default
                String[] entries = hoursStr.split("\\s+");
                if (entries.length > 0 && entries[0].contains(":")) {
                    int firstColon = entries[0].indexOf(':');
                    hoursStr = entries[0].substring(firstColon + 1);
                }
            }

            String[] times = hoursStr.split("-");
            startTime = parseTime(times[0].trim());
            endTime = parseTime(times[1].trim());
            System.out.println("DEBUG Dashboard: Parsed times - Start: " + startTime + ", End: " + endTime);
        } catch (Exception e) {
            System.err.println("ERROR parsing consultation hours: " + e.getMessage());
            e.printStackTrace();
            Label errorLabel = new Label("Invalid consultation hours format: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            timeSlotContainer.getChildren().add(errorLabel);
            return;
        }

        int duration = doctor.getConsultationDuration();
        System.out.println("DEBUG Dashboard: Consultation duration: " + duration + " minutes");

        String availableDaysStr = doctor.getAvailableDays();
        // Split by both comma and space to handle different formats
        String[] availableDays = availableDaysStr != null ? availableDaysStr.split("[,\\s]+") : new String[0];
        System.out.println("DEBUG Dashboard: Available days: " + String.join(", ", availableDays));

        // Check if we should show all days
        boolean showAllDays = availableDays.length == 0 ||
                              availableDaysStr == null ||
                              availableDaysStr.trim().isEmpty() ||
                              availableDaysStr.toLowerCase().contains("all") ||
                              availableDaysStr.toLowerCase().contains("everyday") ||
                              availableDaysStr.toLowerCase().contains("daily");

        if (showAllDays) {
            System.out.println("DEBUG Dashboard: Showing all days (no restrictions)");
        }

        // Add header
        Label headerLabel = new Label("📅 Upcoming Reservations");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        headerLabel.setPadding(new Insets(10, 0, 10, 0));
        timeSlotContainer.getChildren().add(headerLabel);

        // Create time slots for next 7 days
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate date = today.plusDays(dayOffset);
            String dayOfWeek = date.getDayOfWeek().toString(); // e.g., "WEDNESDAY"

            System.out.println("DEBUG Dashboard: Checking date: " + date + " (" + dayOfWeek + ")");

            // Check if doctor is available on this day
            boolean isDayAvailable = showAllDays; // If showing all days, start with true

            if (!showAllDays) {
                // Check against specific available days
                for (String availableDay : availableDays) {
                    String trimmedDay = availableDay.trim().toLowerCase();
                    String checkDay = dayOfWeek.toLowerCase();

                    // Check if day matches (multiple matching strategies)
                    // 1. Exact match: "monday" == "monday"
                    // 2. Abbreviated match: "mon" matches "monday"
                    // 3. Start match: "monday" starts with "mon"
                    if (checkDay.equals(trimmedDay) ||
                        checkDay.startsWith(trimmedDay) ||
                        trimmedDay.startsWith(checkDay.substring(0, Math.min(3, checkDay.length()))) ||
                        (trimmedDay.length() >= 3 && checkDay.startsWith(trimmedDay.substring(0, 3)))) {
                        isDayAvailable = true;
                        System.out.println("DEBUG Dashboard: Day matches! Available day: " + availableDay + " matched with " + dayOfWeek);
                        break;
                    }
                }
            } else {
                System.out.println("DEBUG Dashboard: Day included (showing all days)");
            }

            if (!isDayAvailable) {
                System.out.println("DEBUG Dashboard: Day not available, skipping " + dayOfWeek);
                continue;
            }

            // Create header for the day
            Label dayLabel = new Label(date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")));
            dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
            dayLabel.setPadding(new Insets(10, 0, 5, 0));
            timeSlotContainer.getChildren().add(dayLabel);

            // Create time slots grid for this day
            GridPane slotsGrid = new GridPane();
            slotsGrid.setHgap(10);
            slotsGrid.setVgap(10);
            slotsGrid.setPadding(new Insets(5, 0, 15, 0));

            LocalTime currentSlotTime = startTime;
            int col = 0;
            int row = 0;

            while (currentSlotTime.isBefore(endTime)) {
                LocalTime slotEndTime = currentSlotTime.plusMinutes(duration);
                if (slotEndTime.isAfter(endTime)) break;

                LocalDateTime slotDateTime = LocalDateTime.of(date, currentSlotTime);

                // Skip if slot is in the past
                if (slotDateTime.isAfter(now)) {
                    Button slotButton = createTimeSlotButton(date, currentSlotTime, slotEndTime, dayOfWeek);
                    slotsGrid.add(slotButton, col, row);

                    col++;
                    if (col >= 4) {
                        col = 0;
                        row++;
                    }
                }

                currentSlotTime = slotEndTime;
            }

            if (slotsGrid.getChildren().size() > 0) {
                timeSlotContainer.getChildren().add(slotsGrid);
            }
        }

        // Show message if no upcoming reservations
        if (timeSlotContainer.getChildren().size() == 1) { // Only header
            Label noSlotsLabel = new Label("No upcoming appointments. Patients can book from your available time slots.");
            noSlotsLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");
            timeSlotContainer.getChildren().add(noSlotsLabel);
        }

        System.out.println("DEBUG Dashboard: Total children in container: " + timeSlotContainer.getChildren().size());
    }

    private LocalTime parseTime(String timeStr) {
        // Remove any colons and parse as HHMM format
        timeStr = timeStr.replace(":", "");
        if (timeStr.length() == 4) {
            int hours = Integer.parseInt(timeStr.substring(0, 2));
            int minutes = Integer.parseInt(timeStr.substring(2, 4));
            return LocalTime.of(hours, minutes);
        } else if (timeStr.length() == 3) {
            int hours = Integer.parseInt(timeStr.substring(0, 1));
            int minutes = Integer.parseInt(timeStr.substring(1, 3));
            return LocalTime.of(hours, minutes);
        }
        throw new IllegalArgumentException("Invalid time format: " + timeStr);
    }

    private Button createTimeSlotButton(LocalDate date, LocalTime startTime, LocalTime endTime, String dayOfWeek) {
        Button button = new Button();

        String timeText = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) +
                         " - " + endTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Check if slot is booked
        Reservation reservation = reservationDAO.getReservationBySlot(doctor.getId(), date, startTime);

        if (reservation != null) {
            button.setText(timeText + "\n✓ BOOKED");
            button.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                          "-fx-font-size: 11px; -fx-min-width: 120px; -fx-min-height: 50px; " +
                          "-fx-cursor: hand; -fx-background-radius: 5px;");
            button.setOnAction(e -> showPatientInfo(reservation));
        } else {
            button.setText(timeText + "\nAvailable");
            button.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                          "-fx-font-size: 11px; -fx-min-width: 120px; -fx-min-height: 50px; " +
                          "-fx-opacity: 0.6; -fx-background-radius: 5px;");
            button.setDisable(true);
        }

        return button;
    }

    private void showPatientInfo(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Patient Information");
        alert.setHeaderText("Reservation Details");

        StringBuilder content = new StringBuilder();
        content.append("Patient Name: ").append(reservation.getPatientName()).append("\n");
        content.append("Age: ").append(reservation.getPatientAge()).append("\n");
        content.append("Gender: ").append(reservation.getPatientGender()).append("\n");
        content.append("Phone: ").append(reservation.getPatientPhone()).append("\n");

        if (reservation.getPatientEmail() != null && !reservation.getPatientEmail().isEmpty()) {
            content.append("Email: ").append(reservation.getPatientEmail()).append("\n");
        }

        content.append("\nDate: ").append(reservation.getReservationDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy"))).append("\n");
        content.append("Time: ").append(reservation.getStartTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"))).append(" - ")
                .append(reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");

        if (reservation.getPastComplexities() != null && !reservation.getPastComplexities().isEmpty()) {
            content.append("\nPast Complexities:\n").append(reservation.getPastComplexities());
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

}
