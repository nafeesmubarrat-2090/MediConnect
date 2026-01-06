package com.example.mediconnect;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class PatientHomeController {
    @FXML private ComboBox<String> sectorField;
    @FXML private ComboBox<String> locationField;
    @FXML private ComboBox<String> onlineField;
    @FXML private VBox doctorCardsContainer;
    @FXML private Label resultCountLabel;

    private final DoctorDAO doctorDAO = new DoctorDAO();

    @FXML
    public void initialize() {
        // Populate online consultation dropdown
        if (onlineField != null) {
            onlineField.setItems(FXCollections.observableArrayList("Any", "Yes", "No"));
            onlineField.setValue("Any");
        }

        // Populate sector and location dropdowns with unique values from database
        populateSectorAndLocationDropdowns();

        loadAllDoctors();
    }

    /**
     * Dynamically load unique sectors and locations from the database
     */
    private void populateSectorAndLocationDropdowns() {
        // Get unique sectors from database
        List<String> sectors = doctorDAO.getUniqueSectors();
        sectors.add(0, "Any"); // Add "Any" option at the beginning

        // Get unique locations from database
        List<String> locations = doctorDAO.getUniqueLocations();
        locations.add(0, "Any"); // Add "Any" option at the beginning

        // Populate sector ComboBox
        if (sectorField != null) {
            sectorField.setItems(FXCollections.observableArrayList(sectors));
            sectorField.setValue("Any");
            sectorField.setPromptText("Any");
        }

        // Populate location ComboBox
        if (locationField != null) {
            locationField.setItems(FXCollections.observableArrayList(locations));
            locationField.setValue("Any");
            locationField.setPromptText("Any");
        }
    }

    private void loadAllDoctors() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        displayDoctorCards(doctors);
        if (resultCountLabel != null) {
            resultCountLabel.setText(doctors.size() + " doctor(s) found");
        }
    }

    private void displayDoctorCards(List<Doctor> doctors) {
        doctorCardsContainer.getChildren().clear();

        for (Doctor doctor : doctors) {
            VBox card = createDoctorCard(doctor);
            doctorCardsContainer.getChildren().add(card);
        }
    }

    private VBox createDoctorCard(Doctor doctor) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; " +
                     "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setMaxWidth(Double.MAX_VALUE);

        // Doctor name and sector
        Label nameLabel = new Label(doctor.getName());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #2d3748;");

        Label sectorLabel = new Label(doctor.getSector());
        sectorLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #667eea;");

        // Location and chamber info
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label locationLabel = new Label("📍 " + doctor.getLocation());
        locationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4a5568;");

        Label chamberLabel = new Label("🏥 " + doctor.getChamber());
        chamberLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4a5568;");

        Label feesLabel = new Label("৳ " + doctor.getFees());
        feesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #48bb78;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        infoBox.getChildren().addAll(locationLabel, chamberLabel, spacer, feesLabel);

        // View Profile Button
        Button viewButton = new Button("View Profile");
        viewButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                           "-fx-font-weight: 600; -fx-background-radius: 8; " +
                           "-fx-padding: 10 24; -fx-cursor: hand;");
        viewButton.setOnAction(e -> openDoctorDetail(doctor));

        // Hover effect
        viewButton.setOnMouseEntered(e ->
            viewButton.setStyle("-fx-background-color: #5568d3; -fx-text-fill: white; " +
                               "-fx-font-weight: 600; -fx-background-radius: 8; " +
                               "-fx-padding: 10 24; -fx-cursor: hand;"));
        viewButton.setOnMouseExited(e ->
            viewButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                               "-fx-font-weight: 600; -fx-background-radius: 8; " +
                               "-fx-padding: 10 24; -fx-cursor: hand;"));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(viewButton);

        card.getChildren().addAll(nameLabel, sectorLabel, infoBox, buttonBox);

        // Card hover effect
        card.setOnMouseEntered(e ->
            card.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 12; -fx-padding: 20; " +
                         "-fx-border-color: #667eea; -fx-border-radius: 12; -fx-border-width: 2; " +
                         "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 12, 0, 0, 4); -fx-cursor: hand;"));
        card.setOnMouseExited(e ->
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; " +
                         "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"));

        return card;
    }

    private void openDoctorDetail(Doctor doctor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/doctor_detail.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());

            DoctorDetailController controller = loader.getController();
            controller.setDoctor(doctor);

            Stage stage = (Stage) doctorCardsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Doctor Profile - " + doctor.getName());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to open doctor profile").showAndWait();
        }
    }

    @FXML
    private void handleSearch() {
        String sector = sectorField.getValue();
        String location = locationField.getValue();
        String online = onlineField.getValue();

        // Treat "Any" as no filter (null)
        if ("Any".equals(sector)) {
            sector = null;
        }
        if ("Any".equals(location)) {
            location = null;
        }
        if ("Any".equals(online)) {
            online = null;
        }

        List<Doctor> doctors = doctorDAO.searchDoctors(sector, location, online);
        displayDoctorCards(doctors);
        if (resultCountLabel != null) {
            resultCountLabel.setText(doctors.size() + " doctor(s) match your filters");
        }
    }

    @FXML
    private void handleViewAll() {
        sectorField.setValue("Any");
        locationField.setValue("Any");
        onlineField.setValue("Any");
        loadAllDoctors();
    }

    @FXML
    private void goBackToLanding() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediconnect/landing.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/mediconnect/style.css").toExternalForm());
            Stage stage = (Stage) doctorCardsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("AidAccess - Patient Portal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
