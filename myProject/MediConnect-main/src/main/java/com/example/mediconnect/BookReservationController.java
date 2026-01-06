package com.example.mediconnect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookReservationController {
    @FXML private Label doctorNameLabel;
    @FXML private Label doctorSectorLabel;
    @FXML private Label doctorFeesLabel;
    @FXML private VBox timeSlotsContainer;
    @FXML private VBox patientInfoForm;
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea complexitiesArea;
    @FXML private Label selectedSlotLabel;

    private Doctor doctor;
    private ReservationDAO reservationDAO;
    private LocalDate selectedDate;
    private LocalTime selectedStartTime;
    private LocalTime selectedEndTime;
    private String selectedDayOfWeek;
    private Button currentlySelectedButton;

    public BookReservationController() {
        this.reservationDAO = new ReservationDAO();
    }

    @FXML
    private void initialize() {
        // Populate gender ComboBox
        if (genderComboBox != null) {
            genderComboBox.getItems().addAll("Male", "Female", "Other");
        }
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        displayDoctorInfo();
        displayAvailableTimeSlots();
    }

    private void displayDoctorInfo() {
        if (doctor != null) {
            doctorNameLabel.setText("Dr. " + doctor.getName());
            doctorSectorLabel.setText(doctor.getSector());
            doctorFeesLabel.setText("Consultation Fee: ৳" + doctor.getFees());
        }
    }

    private void displayAvailableTimeSlots() {
        if (doctor == null || timeSlotsContainer == null) {
            System.out.println("DEBUG: Doctor is null: " + (doctor == null) + ", Container is null: " + (timeSlotsContainer == null));
            return;
        }

        timeSlotsContainer.getChildren().clear();

        // Parse consultation hours
        String hoursStr = doctor.getConsultationHours();
        System.out.println("DEBUG: Consultation hours: " + hoursStr);
        LocalTime startTime, endTime;

        try {
            // Handle per-day format
            if (hoursStr.contains("Mon") || hoursStr.contains("Tue") || hoursStr.contains("Wed")) {
                String[] entries = hoursStr.split("\\s+");
                if (entries.length > 0 && entries[0].contains(":")) {
                    int firstColon = entries[0].indexOf(':');
                    hoursStr = entries[0].substring(firstColon + 1);
                }
            }

            String[] times = hoursStr.split("-");
            startTime = parseTime(times[0].trim());
            endTime = parseTime(times[1].trim());
            System.out.println("DEBUG: Parsed times - Start: " + startTime + ", End: " + endTime);
        } catch (Exception e) {
            System.err.println("ERROR parsing consultation hours: " + e.getMessage());
            e.printStackTrace();
            Label errorLabel = new Label("Unable to load available time slots. Error: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            timeSlotsContainer.getChildren().add(errorLabel);
            return;
        }

        int duration = doctor.getConsultationDuration();
        System.out.println("DEBUG: Consultation duration: " + duration + " minutes");

        String availableDaysStr = doctor.getAvailableDays();
        // Split by both comma and space to handle different formats
        String[] availableDays = availableDaysStr != null ? availableDaysStr.split("[,\\s]+") : new String[0];
        System.out.println("DEBUG: Available days: " + String.join(", ", availableDays));

        // Check if we should show all days (if available_days is empty, null, or contains "All")
        boolean showAllDays = availableDays.length == 0 ||
                              availableDaysStr == null ||
                              availableDaysStr.trim().isEmpty() ||
                              availableDaysStr.toLowerCase().contains("all") ||
                              availableDaysStr.toLowerCase().contains("everyday") ||
                              availableDaysStr.toLowerCase().contains("daily");

        if (showAllDays) {
            System.out.println("DEBUG: Showing all days (no restrictions)");
        }

        // Create time slots for next 7 days
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate date = today.plusDays(dayOffset);
            String dayOfWeek = date.getDayOfWeek().toString(); // e.g., "WEDNESDAY"

            System.out.println("DEBUG: Checking date: " + date + " (" + dayOfWeek + ")");

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
                        System.out.println("DEBUG: Day matches! Available day: " + availableDay + " matched with " + dayOfWeek);
                        break;
                    }
                }
            } else {
                System.out.println("DEBUG: Day included (showing all days)");
            }

            if (!isDayAvailable) {
                System.out.println("DEBUG: Day not available, skipping " + dayOfWeek);
                continue;
            }

            // Create header for the day
            Label dayLabel = new Label(date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")));
            dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 10 0 5 0;");
            timeSlotsContainer.getChildren().add(dayLabel);

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

            // Show the grid even if all slots are booked (for transparency)
            if (slotsGrid.getChildren().size() > 0) {
                timeSlotsContainer.getChildren().add(slotsGrid);
            }
        }

        // If no slots were added at all, show a message
        if (timeSlotsContainer.getChildren().size() == 0) {
            Label noSlotsLabel = new Label("No available time slots for the selected doctor in the next 7 days.");
            noSlotsLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 14px;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }

        System.out.println("DEBUG: Total children in container: " + timeSlotsContainer.getChildren().size());
    }

    private LocalTime parseTime(String timeStr) {
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

        // Check if slot is available
        boolean isAvailable = reservationDAO.isSlotAvailable(doctor.getId(), date, startTime);

        if (isAvailable) {
            button.setText(timeText);
            button.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                          "-fx-font-size: 12px; -fx-min-width: 120px; -fx-min-height: 45px; " +
                          "-fx-cursor: hand; -fx-background-radius: 5px;");

            LocalDate finalDate = date;
            LocalTime finalStartTime = startTime;
            LocalTime finalEndTime = endTime;
            String finalDayOfWeek = dayOfWeek;

            button.setOnAction(e -> selectTimeSlot(button, finalDate, finalStartTime, finalEndTime, finalDayOfWeek));
        } else {
            button.setText(timeText + "\n(Booked)");
            button.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #718096; " +
                          "-fx-font-size: 11px; -fx-min-width: 120px; -fx-min-height: 45px; " +
                          "-fx-background-radius: 5px;");
            button.setDisable(true);
        }

        return button;
    }

    private void selectTimeSlot(Button clickedButton, LocalDate date, LocalTime startTime, LocalTime endTime, String dayOfWeek) {
        // Reset previously selected button to default color
        if (currentlySelectedButton != null) {
            currentlySelectedButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                          "-fx-font-size: 12px; -fx-min-width: 120px; -fx-min-height: 45px; " +
                          "-fx-cursor: hand; -fx-background-radius: 5px;");
        }

        // Change clicked button to selected color (green)
        clickedButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                          "-fx-font-size: 12px; -fx-min-width: 120px; -fx-min-height: 45px; " +
                          "-fx-cursor: hand; -fx-background-radius: 5px; " +
                          "-fx-border-color: #1e8449; -fx-border-width: 2px;");

        // Store the currently selected button
        currentlySelectedButton = clickedButton;

        this.selectedDate = date;
        this.selectedStartTime = startTime;
        this.selectedEndTime = endTime;
        this.selectedDayOfWeek = dayOfWeek;

        String slotInfo = String.format("Selected: %s at %s - %s",
                date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
                startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                endTime.format(DateTimeFormatter.ofPattern("HH:mm")));

        selectedSlotLabel.setText(slotInfo);
        patientInfoForm.setVisible(true);

        // Scroll to form
        patientInfoForm.requestFocus();
    }

    @FXML
    private void handleConfirmBooking() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your name", Alert.AlertType.WARNING);
            return;
        }

        if (ageField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your age", Alert.AlertType.WARNING);
            return;
        }

        if (genderComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select your gender", Alert.AlertType.WARNING);
            return;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your phone number", Alert.AlertType.WARNING);
            return;
        }

        // Create reservation
        Reservation reservation = new Reservation(
                doctor.getId(),
                nameField.getText().trim(),
                ageField.getText().trim(),
                genderComboBox.getValue(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                complexitiesArea.getText().trim(),
                selectedDate,
                selectedStartTime,
                selectedEndTime,
                selectedDayOfWeek
        );

        boolean success = reservationDAO.createReservation(reservation);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Confirmed");
            alert.setHeaderText("Reservation Successfully Created!");
            alert.setContentText(String.format(
                    "Your appointment with Dr. %s is confirmed for %s at %s.\n\nPlease arrive on time.",
                    doctor.getName(),
                    selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
                    selectedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            ));
            alert.showAndWait();

            // Go back to doctor detail page
            handleBack();
        } else {
            showAlert("Booking Failed", "Unable to create reservation. The slot may have been taken.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelBooking() {
        patientInfoForm.setVisible(false);
        clearForm();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/doctor_detail.fxml"));
            Scene scene = new Scene(loader.load(), 650, 700);

            // Pass the doctor object to the detail controller
            DoctorDetailController controller = loader.getController();
            controller.setDoctor(doctor);

            if (getClass().getResource("/com/example/mediconnect/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            }

            Stage stage = (Stage) doctorNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Doctor Details");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        // Reset selected button color
        if (currentlySelectedButton != null) {
            currentlySelectedButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                          "-fx-font-size: 12px; -fx-min-width: 120px; -fx-min-height: 45px; " +
                          "-fx-cursor: hand; -fx-background-radius: 5px;");
            currentlySelectedButton = null;
        }

        nameField.clear();
        ageField.clear();
        genderComboBox.setValue(null);
        phoneField.clear();
        emailField.clear();
        complexitiesArea.clear();
        selectedSlotLabel.setText("");
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

