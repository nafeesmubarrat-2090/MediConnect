package myProject.ambulance;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import myProject.ClientFX;

public class AmbulanceServicePage {
    // Professional color scheme
    private static final String PRIMARY_COLOR = "#dc2626";
    private static final String PRIMARY_DARK = "#b91c1c";
    private static final String SECONDARY_COLOR = "#1e3a5f";
    private static final String TEXT_PRIMARY = "#1f2937";
    private static final String TEXT_SECONDARY = "#6b7280";
    private static final String SUCCESS_COLOR = "#059669";
    private static final String WARNING_COLOR = "#d97706";
    private static final String BG_GRADIENT = "-fx-background-color: linear-gradient(to bottom right, #fef2f2, #fee2e2, #fecaca);";

    private final List<AmbulanceInfo> allProviders = seed();
    private final List<Hospital> allHospitals = seedHospitals();
    private final List<BookingHistory> bookingHistory = new ArrayList<>();
    private File selectedMedicalFile = null;
    private Label trackingStatusLabel;
    private Circle trackingIndicator;
    private boolean isEnglish = true;
    private String currentLocation = "23.8103° N, 90.4125° E";
    private CheckBox insuranceCheckBox;
    private String selectedCity = "All Cities";

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle(BG_GRADIENT);

        // ===== PROFESSIONAL HEADER =====
        Label icon = new Label("🚑");
        icon.setFont(Font.font("Segoe UI Emoji", 32));

        Label title = new Label(getText("Emergency Ambulance Services", "জরুরি অ্যাম্বুলেন্স সেবা"));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        Label subtitle = new Label(getText("24/7 Emergency Response • Fast & Reliable", "২৪/৭ জরুরি সেবা • দ্রুত ও নির্ভরযোগ্য"));
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");

        VBox titleBox = new VBox(2, title, subtitle);
        HBox headerLeft = new HBox(12, icon, titleBox);
        headerLeft.setAlignment(Pos.CENTER_LEFT);

        // SOS Panic Button
        Button panicBtn = new Button("🚨 SOS EMERGENCY");
        panicBtn.setStyle(panicButtonStyle());
        panicBtn.setOnMouseEntered(e -> panicBtn.setStyle(panicButtonHoverStyle()));
        panicBtn.setOnMouseExited(e -> panicBtn.setStyle(panicButtonStyle()));
        panicBtn.setOnAction(e -> handlePanicButton());

        // Language Toggle
        Button langBtn = new Button(isEnglish ? "বাংলা" : "English");
        langBtn.setStyle(secondaryButtonStyle());
        langBtn.setOnMouseEntered(e -> langBtn.setStyle(secondaryButtonHoverStyle()));
        langBtn.setOnMouseExited(e -> langBtn.setStyle(secondaryButtonStyle()));
        langBtn.setOnAction(e -> {
            isEnglish = !isEnglish;
            root.setCenter(createTabPane());
            langBtn.setText(isEnglish ? "বাংলা" : "English");
            title.setText(getText("Emergency Ambulance Services", "জরুরি অ্যাম্বুলেন্স সেবা"));
            subtitle.setText(getText("24/7 Emergency Response • Fast & Reliable", "২৪/৭ জরুরি সেবা • দ্রুত ও নির্ভরযোগ্য"));
        });

        Button helpBtn = new Button("🆘 " + getText("Help", "সাহায্য"));
        helpBtn.setStyle(helpButtonStyle());
        helpBtn.setOnMouseEntered(e -> helpBtn.setStyle(helpButtonHoverStyle()));
        helpBtn.setOnMouseExited(e -> helpBtn.setStyle(helpButtonStyle()));
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(12, headerLeft, headerSpacer, panicBtn, langBtn, helpBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #fecaca; -fx-border-width: 0 0 2 0;");

        DropShadow headerShadow = new DropShadow();
        headerShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        headerShadow.setRadius(10);
        headerShadow.setOffsetY(2);
        header.setEffect(headerShadow);

        root.setTop(header);
        root.setCenter(createTabPane());

        return root;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");
        tabPane.getTabs().addAll(
            createBookingTab(),
            createNearbyHospitalsTab(),
            createMapLocationTab(),
            createBloodBankTab(),
            createEmergencyContactsTab(),
            createBookingHistoryTab()
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Style the tabs
        tabPane.getStyleClass().add("professional-tabs");

        return tabPane;
    }

    // ===== STYLE METHODS =====
    private String panicButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, #dc2626, #b91c1c);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 8; -fx-padding: 12 24; -fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(220, 38, 38, 0.4), 8, 0, 0, 2);";
    }

    private String panicButtonHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #b91c1c, #991b1b);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 8; -fx-padding: 12 24; -fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(185, 28, 28, 0.6), 12, 0, 0, 3);";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, " + PRIMARY_COLOR + ", " + PRIMARY_DARK + ");" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;";
    }

    private String primaryButtonHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, " + PRIMARY_DARK + ", #991b1b);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;";
    }

    private String secondaryButtonStyle() {
        return "-fx-background-color: white; -fx-text-fill: " + TEXT_PRIMARY + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 12px;" +
               "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #d1d5db;" +
               "-fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String secondaryButtonHoverStyle() {
        return "-fx-background-color: #f9fafb; -fx-text-fill: " + TEXT_PRIMARY + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 12px;" +
               "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #9ca3af;" +
               "-fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String helpButtonStyle() {
        return "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-font-weight: bold;" +
               "-fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String helpButtonHoverStyle() {
        return "-fx-background-color: #fde68a; -fx-text-fill: #78350f;" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-font-weight: bold;" +
               "-fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String cardStyle() {
        return "-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16;" +
               "-fx-border-color: #fee2e2; -fx-border-width: 1;";
    }

    private String inputFieldStyle() {
        return "-fx-background-color: #fafafa; -fx-background-radius: 8; -fx-border-radius: 8;" +
               "-fx-border-color: #e5e7eb; -fx-padding: 8 12; -fx-font-family: 'Segoe UI';";
    }

    private Tab createBookingTab() {
        Tab tab = new Tab(getText("Book Ambulance", "অ্যাম্বুলেন্স বুক করুন"));
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(10));

        // Left side - Ambulance list with enhanced info
        VBox leftSide = new VBox(10);

        // Filter controls
        HBox filterBox = new HBox(10);
        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.getItems().addAll(
            getText("Sort by ETA", "সময় অনুসারে"),
            getText("Sort by Cost", "খরচ অনুসারে"),
            getText("Available Only", "শুধু উপলব্ধ")
        );
        sortBy.setPromptText(getText("Filter", "ফিল্টার"));

        CheckBox advancedOnly = new CheckBox(getText("Advanced Life Support", "উন্নত সেবা"));
        filterBox.getChildren().addAll(sortBy, advancedOnly);

        ListView<AmbulanceInfo> list = new ListView<>();
        // Initially show all providers
        list.getItems().addAll(allProviders);
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AmbulanceInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cell = new VBox(5);
                    cell.setPadding(new Insets(5));

                    HBox headerBox = new HBox(10);
                    Label nameLabel = new Label(item.name);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Rating stars
                    Label ratingLabel = new Label("⭐".repeat(item.rating) + " (" + item.rating + ".0)");
                    ratingLabel.setStyle("-fx-font-size: 11px;");
                    headerBox.getChildren().addAll(nameLabel, spacer, ratingLabel);

                    Label infoLabel = new Label("📍 " + item.city + " • ⏱ ETA: " + item.baseEtaMinutes + " mins • 💰 ৳" + item.baseCost);
                    Label equipLabel = new Label("🏥 Equipment: " + String.join(", ", item.equipment));
                    equipLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                    HBox statusBox = new HBox(15);
                    Label availLabel = new Label(item.available ? "✓ Available" : "✗ Busy");
                    availLabel.setStyle(item.available ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: red;");

                    // Direct call button
                    Button callBtn = new Button("📞 Call Now");
                    callBtn.setStyle("-fx-font-size: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
                    callBtn.setOnAction(ev ->
                        showAlert(AmbulanceServicePage.this.getText("Calling", "কল করা হচ্ছে"),
                                 AmbulanceServicePage.this.getText("Calling driver ", "ড্রাইভারকে কল করা হচ্ছে ") + item.driverName + " at " + item.phone,
                                 Alert.AlertType.INFORMATION)
                    );

                    statusBox.getChildren().addAll(availLabel, callBtn);

                    cell.getChildren().addAll(headerBox, infoLabel, equipLabel, statusBox);
                    setGraphic(cell);
                    setText(null);
                }
            }
        });
        list.getSelectionModel().selectFirst();

        Label listTitle = new Label(getText("Available Ambulances", "উপলব্ধ অ্যাম্বুলেন্স"));
        listTitle.setStyle("-fx-font-weight: bold;");
        leftSide.getChildren().addAll(listTitle, filterBox, list);
        VBox.setVgrow(list, Priority.ALWAYS);

        // Right side - Enhanced booking form
        VBox right = new VBox(12);
        right.setPadding(new Insets(10));
        right.setAlignment(Pos.TOP_LEFT);

        Label formTitle = new Label(getText("Booking Details", "বুকিং বিবরণ"));
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);

        TextField name = new TextField();
        name.setPromptText(getText("Patient name", "রোগীর নাম"));
        TextField phone = new TextField();
        phone.setPromptText(getText("Contact number", "যোগাযোগ নম্বর"));

        // City selection ComboBox
        ComboBox<String> cityComboBox = new ComboBox<>();
        cityComboBox.getItems().addAll("All Cities", "Dhaka", "Chittagong", "Rajshahi", "Sylhet", "Khulna");
        cityComboBox.setValue("All Cities");
        cityComboBox.setPromptText(getText("Select City", "শহর নির্বাচন করুন"));

        TextField destination = new TextField();
        destination.setPromptText(getText("Destination hospital", "গন্তব্য হাসপাতাল"));

        ComboBox<String> priority = new ComboBox<>();
        priority.getItems().addAll("Critical", "High", "Normal");
        priority.getSelectionModel().selectFirst();

        // Update list cell factory to show adjusted values based on priority
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AmbulanceInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cell = new VBox(5);
                    cell.setPadding(new Insets(5));

                    HBox headerBox = new HBox(10);
                    Label nameLabel = new Label(item.name);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Rating stars
                    Label ratingLabel = new Label("⭐".repeat(item.rating) + " (" + item.rating + ".0)");
                    ratingLabel.setStyle("-fx-font-size: 11px;");
                    headerBox.getChildren().addAll(nameLabel, spacer, ratingLabel);

                    // Get current urgency level
                    String urgency = priority.getValue() != null ? priority.getValue() : "Normal";
                    int adjustedETA = item.getAdjustedETA(urgency);
                    int adjustedCost = item.getAdjustedCost(urgency);

                    Label infoLabel = new Label("📍 " + item.city + " • ⏱ ETA: " + adjustedETA + " mins • 💰 ৳" + adjustedCost);
                    Label equipLabel = new Label("🏥 Equipment: " + String.join(", ", item.equipment));
                    equipLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                    HBox statusBox = new HBox(15);
                    Label availLabel = new Label(item.available ? "✓ Available" : "✗ Busy");
                    availLabel.setStyle(item.available ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: red;");

                    // Direct call button
                    Button callBtn = new Button("📞 Call Now");
                    callBtn.setStyle("-fx-font-size: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
                    callBtn.setOnAction(ev ->
                        showAlert(AmbulanceServicePage.this.getText("Calling", "কল করা হচ্ছে"),
                                 AmbulanceServicePage.this.getText("Calling driver ", "ড্রাইভারকে কল করা হচ্ছে ") + item.driverName + " at " + item.phone,
                                 Alert.AlertType.INFORMATION)
                    );

                    statusBox.getChildren().addAll(availLabel, callBtn);

                    cell.getChildren().addAll(headerBox, infoLabel, equipLabel, statusBox);
                    setGraphic(cell);
                    setText(null);
                }
            }
        });
        list.getSelectionModel().selectFirst();

        TextArea notes = new TextArea();
        notes.setPromptText(getText("Patient condition, landmarks, special instructions", "রোগীর অবস্থা, নির্দেশনা"));
        notes.setPrefRowCount(2);

        // Patient condition quick select
        HBox conditionBox = new HBox(5);
        Label condLabel = new Label(getText("Quick Condition:", "দ্রুত অবস্থা:"));
        Button heartBtn = new Button("❤️ Heart");
        Button accidentBtn = new Button("🚗 Accident");
        Button strokeBtn = new Button("🧠 Stroke");
        Button breathBtn = new Button("🫁 Breathing");

        heartBtn.setOnAction(e -> notes.setText("Suspected heart attack - requires cardiac monitoring"));
        accidentBtn.setOnAction(e -> notes.setText("Road accident - possible fractures/trauma"));
        strokeBtn.setOnAction(e -> notes.setText("Suspected stroke - urgent neurological care needed"));
        breathBtn.setOnAction(e -> notes.setText("Breathing difficulty - oxygen support required"));

        conditionBox.getChildren().addAll(condLabel, heartBtn, accidentBtn, strokeBtn, breathBtn);

        form.addRow(0, new Label(getText("Patient Name:", "রোগীর নাম:")), name);
        form.addRow(1, new Label(getText("Phone:", "ফোন:")), phone);
        form.addRow(2, new Label(getText("City:", "শহর:")), cityComboBox);
        form.addRow(3, new Label(getText("Destination:", "গন্তব্য:")), destination);
        form.addRow(4, new Label(getText("Urgency:", "জরুরিত্ব:")), priority);
        form.addRow(5, new Label(getText("Notes:", "নোট:")), notes);

        // Add city change listener to filter ambulances and hospitals
        cityComboBox.valueProperty().addListener((_, _, newCity) -> {
            selectedCity = newCity;
            filterAmbulances(list, newCity);
            // Update cost and ETA display
            if (list.getItems().size() > 0) {
                list.getSelectionModel().selectFirst();
            }
        });

        // Insurance verification
        insuranceCheckBox = new CheckBox(getText("I have health insurance", "আমার স্বাস্থ্য বীমা আছে"));
        TextField insuranceId = new TextField();
        insuranceId.setPromptText(getText("Insurance ID", "বীমা আইডি"));
        insuranceId.setDisable(true);
        insuranceCheckBox.setOnAction(e -> insuranceId.setDisable(!insuranceCheckBox.isSelected()));

        VBox insuranceBox = new VBox(5, insuranceCheckBox, insuranceId);

        // Medical file upload
        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER_LEFT);
        Label fileLabel = new Label(getText("Medical Records:", "মেডিকেল রেকর্ড:"));
        Button uploadBtn = new Button(getText("Upload File", "ফাইল আপলোড"));
        Label fileNameLabel = new Label(getText("No file selected", "কোন ফাইল নির্বাচিত নয়"));
        fileNameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(getText("Select Medical Records", "মেডিকেল রেকর্ড নির্বাচন করুন"));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png")
            );
            selectedMedicalFile = fileChooser.showOpenDialog(null);
            if (selectedMedicalFile != null) {
                fileNameLabel.setText(selectedMedicalFile.getName());
            }
        });
        fileBox.getChildren().addAll(fileLabel, uploadBtn, fileNameLabel);

        // Share location with family
        CheckBox shareLocation = new CheckBox(getText("Share live location with family", "পরিবারের সাথে লোকেশন শেয়ার করুন"));
        TextField familyContact = new TextField();
        familyContact.setPromptText(getText("Family contact number", "পরিবারের নম্বর"));
        familyContact.setDisable(true);
        shareLocation.setOnAction(e -> familyContact.setDisable(!shareLocation.isSelected()));

        // Cost estimator with breakdown
        VBox costBox = new VBox(5);
        Label costLabel = new Label(getText("Estimated Cost: ৳0", "আনুমানিক খরচ: ৳০"));
        costLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3; -fx-font-size: 16px;");
        Label etaLabel = new Label(getText("Estimated Time: -- mins", "আনুমানিক সময়: -- মিনিট"));
        etaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800; -fx-font-size: 14px;");
        Label costBreakdown = new Label(getText("Base fare + Distance charge + Equipment fee", "মূল্য + দূরত্ব + সরঞ্জাম"));
        costBreakdown.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        costBox.getChildren().addAll(costLabel, etaLabel, costBreakdown);

        // Update cost and ETA when ambulance or urgency changes
        Runnable updateCostAndETA = () -> {
            AmbulanceInfo sel = list.getSelectionModel().getSelectedItem();
            String urgency = priority.getValue();
            if (sel != null && urgency != null) {
                int adjustedCost = sel.getAdjustedCost(urgency);
                int adjustedETA = sel.getAdjustedETA(urgency);
                costLabel.setText(getText("Estimated Cost: ৳", "আনুমানিক খরচ: ৳") + adjustedCost);
                etaLabel.setText(getText("Estimated Time: ", "আনুমানিক সময়: ") + adjustedETA + getText(" mins", " মিনিট"));

                // Update urgency indicator color
                if ("Critical".equals(urgency)) {
                    costLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #f44336; -fx-font-size: 16px;");
                    etaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #f44336; -fx-font-size: 14px;");
                } else if ("High".equals(urgency)) {
                    costLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800; -fx-font-size: 16px;");
                    etaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800; -fx-font-size: 14px;");
                } else {
                    costLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 16px;");
                    etaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 14px;");
                }

                // Refresh the list to update displayed values
                list.refresh();
            }
        };

        list.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> updateCostAndETA.run());
        priority.valueProperty().addListener((_, _, _) -> updateCostAndETA.run());

        // Initial update
        updateCostAndETA.run();

        // Tracking status
        HBox trackingBox = new HBox(10);
        trackingBox.setAlignment(Pos.CENTER_LEFT);
        trackingIndicator = new Circle(8);
        trackingIndicator.setFill(Color.GRAY);
        trackingStatusLabel = new Label(getText("Not tracking", "ট্র্যাক করা হচ্ছে না"));
        trackingStatusLabel.setStyle("-fx-font-size: 11px;");
        trackingBox.getChildren().addAll(trackingIndicator, trackingStatusLabel);

        // Payment method selection
        HBox paymentBox = new HBox(10);
        Label payLabel = new Label(getText("Payment:", "পেমেন্ট:"));
        ComboBox<String> paymentMethod = new ComboBox<>();
        paymentMethod.getItems().addAll(
            getText("Cash on Arrival", "ক্যাশ"),
            "bKash", "Nagad", "Rocket",
            getText("Credit/Debit Card", "কার্ড"),
            getText("Insurance Direct Billing", "বীমা")
        );
        paymentMethod.getSelectionModel().selectFirst();
        paymentBox.getChildren().addAll(payLabel, paymentMethod);

        Button request = new Button(getText("🚨 REQUEST AMBULANCE NOW", "🚨 এখনই অ্যাম্বুলেন্স ডাকুন"));
        request.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-font-size: 14px;");
        request.setOnAction(e -> {
            AmbulanceInfo sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert(getText("Error", "ত্রুটি"), getText("Please select an ambulance", "অ্যাম্বুলেন্স নির্বাচন করুন"), Alert.AlertType.ERROR);
                return;
            }
            if (name.getText().isEmpty() || phone.getText().isEmpty() || cityComboBox.getValue() == null || cityComboBox.getValue().equals("All Cities")) {
                showAlert(getText("Error", "ত্রুটি"), getText("Please fill in all required fields and select a specific city", "সব তথ্য পূরণ করুন এবং একটি নির্দিষ্ট শহর নির্বাচন করুন"), Alert.AlertType.ERROR);
                return;
            }
            if (!sel.available) {
                showAlert(getText("Error", "ত্রুটি"), getText("Selected ambulance is currently busy", "নির্বাচিত অ্যাম্বুলেন্স ব্যস্ত"), Alert.AlertType.ERROR);
                return;
            }

            // Get adjusted values based on urgency
            String urgency = priority.getValue();
            int adjustedCost = sel.getAdjustedCost(urgency);
            int adjustedETA = sel.getAdjustedETA(urgency);

            // Create booking
            BookingHistory booking = new BookingHistory(
                LocalDateTime.now(),
                sel.name,
                name.getText(),
                cityComboBox.getValue(), // Use selected city
                destination.getText(),
                urgency,
                adjustedCost
            );
            bookingHistory.addFirst(booking);

            // Start tracking with adjusted ETA
            startTracking(sel, adjustedETA);

            // Share location if enabled
            if (shareLocation.isSelected() && !familyContact.getText().isEmpty()) {
                sendLocationToFamily(familyContact.getText(), cityComboBox.getValue());
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(getText("Ambulance Dispatched", "অ্যাম্বুলেন্স পাঠানো হয়েছে"));
            alert.setHeaderText("✓ " + sel.name + " " + getText("is on the way!", "আসছে!"));

            String urgencyInfo = "";
            if ("Critical".equals(urgency)) {
                urgencyInfo = getText("\n⚠️ CRITICAL - Priority dispatch activated!", "\n⚠️ জরুরি - অগ্রাধিকার প্রেরণ সক্রিয়!");
            } else if ("High".equals(urgency)) {
                urgencyInfo = getText("\n⚠️ HIGH - Fast track service enabled!", "\n⚠️ উচ্চ - দ্রুত সেবা সক্রিয়!");
            }

            alert.setContentText(
                getText("Booking ID: #", "বুকিং আইডি: #") + booking.bookingId + "\n" +
                getText("Urgency Level: ", "জরুরিত্ব স্তর: ") + urgency + urgencyInfo + "\n" +
                getText("ETA: ", "সময়: ") + adjustedETA + getText(" minutes", " মিনিট") + "\n" +
                getText("Driver: ", "ড্রাইভার: ") + sel.driverName + "\n" +
                getText("Contact: ", "যোগাযোগ: ") + sel.phone + "\n" +
                getText("Estimated Cost: ৳", "আনুমানিক খরচ: ৳") + adjustedCost + "\n" +
                getText("Payment: ", "পেমেন্ট: ") + paymentMethod.getValue() + "\n\n" +
                getText("Equipment: ", "সরঞ্জাম: ") + String.join(", ", sel.equipment) + "\n\n" +
                getText("Rate your experience after service completion!", "সেবা শেষে রেটিং দিন!")
            );
            alert.showAndWait();

            // Clear form
            name.clear();
            phone.clear();
            cityComboBox.setValue("All Cities");
            destination.clear();
            notes.clear();
            fileNameLabel.setText(getText("No file selected", "কোন ফাইল নির্বাচিত নয়"));
            selectedMedicalFile = null;
            insuranceCheckBox.setSelected(false);
            shareLocation.setSelected(false);
        });

        right.getChildren().addAll(
            formTitle, form, conditionBox, insuranceBox, fileBox,
            new VBox(5, shareLocation, familyContact),
            costBox, paymentBox, trackingBox, request
        );

        content.setLeft(leftSide);
        content.setRight(right);
        BorderPane.setMargin(leftSide, new Insets(0, 10, 0, 0));

        tab.setContent(content);
        return tab;
    }

    private Tab createNearbyHospitalsTab() {
        Tab tab = new Tab(getText("🏥 Nearby Hospitals", "🏥 কাছের হাসপাতাল"));
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label(getText("Hospitals Near You", "আপনার কাছের হাসপাতাল"));
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Search and filter
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText(getText("Search by name or specialty", "নাম অথবা বিশেষত্ব খুঁজুন"));
        searchField.setPrefWidth(200);

        // City filter for hospitals
        ComboBox<String> cityFilter = new ComboBox<>();
        cityFilter.getItems().addAll("All Cities", "Dhaka", "Chittagong", "Rajshahi", "Sylhet", "Khulna");
        cityFilter.setValue(selectedCity); // Use the same selected city from booking
        cityFilter.setPromptText(getText("Filter by City", "শহর অনুসারে"));

        ComboBox<String> distanceFilter = new ComboBox<>();
        distanceFilter.getItems().addAll(
            getText("Within 5 km", "৫ কিমি"),
            getText("Within 10 km", "১০ কিমি"),
            getText("Within 20 km", "২০ কিমি"),
            getText("All", "সব")
        );
        distanceFilter.getSelectionModel().selectFirst();
        searchBox.getChildren().addAll(searchField, cityFilter, distanceFilter);

        ListView<Hospital> hospitalList = new ListView<>();

        // Filter hospitals by selected city
        if ("All Cities".equals(selectedCity)) {
            hospitalList.getItems().addAll(allHospitals);
        } else {
            hospitalList.getItems().addAll(
                allHospitals.stream()
                    .filter(h -> h.city.equals(selectedCity))
                    .collect(Collectors.toList())
            );
        }

        // Add city filter change listener
        cityFilter.valueProperty().addListener((_, _, newCity) -> {
            selectedCity = newCity;
            hospitalList.getItems().clear();
            if ("All Cities".equals(newCity)) {
                hospitalList.getItems().addAll(allHospitals);
            } else {
                hospitalList.getItems().addAll(
                    allHospitals.stream()
                        .filter(h -> h.city.equals(newCity))
                        .collect(Collectors.toList())
                );
            }

            if (hospitalList.getItems().isEmpty()) {
                showAlert(getText("No Hospitals", "কোন হাসপাতাল নেই"),
                         getText("No hospitals found in " + newCity + ". Please select another city.",
                                newCity + " এ কোন হাসপাতাল পাওয়া যায়নি।"),
                         Alert.AlertType.INFORMATION);
            }
        });
        hospitalList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Hospital item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cell = new VBox(8);
                    cell.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");

                    HBox headerBox = new HBox(10);
                    Label nameLabel = new Label(item.name);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    Label ratingLabel = new Label("⭐".repeat(item.rating) + " (" + item.rating + ".0)");
                    headerBox.getChildren().addAll(nameLabel, spacer, ratingLabel);

                    Label distLabel = new Label("📍 " + item.distance + " km away • " + item.city);
                    Label specialLabel = new Label("🏥 " + AmbulanceServicePage.this.getText("Specialties: ", "বিশেষত্ব: ") + String.join(", ", item.specialties));
                    specialLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

                    HBox infoBox = new HBox(20);
                    Label bedsLabel = new Label("🛏️ " + item.availableBeds + AmbulanceServicePage.this.getText(" beds", " বেড"));
                    Label emergLabel = new Label(item.hasEmergency ? "✓ 24/7 Emergency" : "Regular hours");
                    emergLabel.setStyle(item.hasEmergency ? "-fx-text-fill: green;" : "-fx-text-fill: orange;");
                    infoBox.getChildren().addAll(bedsLabel, emergLabel);

                    HBox actionBox = new HBox(10);
                    Button callBtn = new Button("📞 " + AmbulanceServicePage.this.getText("Call", "কল"));
                    callBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    callBtn.setOnAction(e -> showAlert(AmbulanceServicePage.this.getText("Calling", "কল"), "Calling " + item.phone, Alert.AlertType.INFORMATION));

                    Button dirBtn = new Button("🗺️ " + AmbulanceServicePage.this.getText("Directions", "দিক"));
                    dirBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    dirBtn.setOnAction(e -> showDirections(item.name, item.address));

                    Button selectBtn = new Button(AmbulanceServicePage.this.getText("Select as Destination", "গন্তব্য হিসাবে"));
                    selectBtn.setOnAction(e -> showAlert(AmbulanceServicePage.this.getText("Selected", "নির্বাচিত"), item.name + AmbulanceServicePage.this.getText(" set as destination", " গন্তব্য হিসাবে সেট"), Alert.AlertType.INFORMATION));

                    actionBox.getChildren().addAll(callBtn, dirBtn, selectBtn);

                    cell.getChildren().addAll(headerBox, distLabel, specialLabel, infoBox, actionBox);
                    setGraphic(cell);
                    setText(null);
                }
            }
        });

        VBox.setVgrow(hospitalList, Priority.ALWAYS);
        content.getChildren().addAll(title, searchBox, hospitalList);

        tab.setContent(content);
        return tab;
    }

    private Tab createMapLocationTab() {
        Tab tab = new Tab(getText("🗺️ Map & Location", "🗺️ ম্যাপ ও লোকেশন"));
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label(getText("Live Location Tracking", "লাইভ লোকেশন ট্র্যাকিং"));
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Current location display
        VBox locationBox = new VBox(10);
        locationBox.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2; -fx-padding: 15; -fx-background-color: #E3F2FD;");
        Label currentLocLabel = new Label(getText("📍 Your Current Location:", "📍 আপনার বর্তমান অবস্থান:"));
        currentLocLabel.setStyle("-fx-font-weight: bold;");
        Label coordsLabel = new Label(currentLocation);
        coordsLabel.setStyle("-fx-font-size: 14px;");

        Button refreshLoc = new Button(getText("🔄 Refresh Location", "🔄 লোকেশন আপডেট"));
        refreshLoc.setOnAction(e -> {
            // Simulate location update
            currentLocation = "23." + (8000 + (int)(Math.random() * 200)) + "° N, 90." + (4000 + (int)(Math.random() * 200)) + "° E";
            coordsLabel.setText(currentLocation);
            showAlert(getText("Location Updated", "লোকেশন আপডেট"), getText("Your location has been refreshed", "আপনার অবস্থান আপডেট হয়েছে"), Alert.AlertType.INFORMATION);
        });

        Button shareLoc = new Button(getText("📤 Share Location", "📤 লোকেশন শেয়ার"));
        shareLoc.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipContent = new ClipboardContent();
            clipContent.putString("My Location: " + currentLocation);
            clipboard.setContent(clipContent);
            showAlert(getText("Location Copied", "কপি হয়েছে"), getText("Location copied to clipboard", "লোকেশন কপি হয়েছে"), Alert.AlertType.INFORMATION);
        });

        HBox locButtons = new HBox(10, refreshLoc, shareLoc);
        locationBox.getChildren().addAll(currentLocLabel, coordsLabel, locButtons);

        // Map placeholder (in real app, would use Google Maps API or similar)
        VBox mapPlaceholder = new VBox();
        mapPlaceholder.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: #f5f5f5;");
        mapPlaceholder.setPrefHeight(300);
        mapPlaceholder.setAlignment(Pos.CENTER);

        Label mapLabel = new Label("🗺️\n" + getText("Interactive Map View", "ইন্টারঅ্যাক্টিভ ম্যাপ") + "\n" +
                                  getText("(Showing your location and nearby services)", "(আপনার অবস্থান এবং কাছের সেবা)"));
        mapLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-text-alignment: center;");
        mapPlaceholder.getChildren().add(mapLabel);

        // Nearby services summary
        GridPane nearbyGrid = new GridPane();
        nearbyGrid.setHgap(15);
        nearbyGrid.setVgap(10);
        nearbyGrid.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label nearbyTitle = new Label(getText("📍 Nearby Services", "📍 কাছের সেবা"));
        nearbyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nearbyGrid.add(nearbyTitle, 0, 0, 2, 1);

        nearbyGrid.add(new Label("🚑 " + getText("Ambulances:", "অ্যাম্বুলেন্স:")), 0, 1);
        nearbyGrid.add(new Label(allProviders.size() + getText(" available", " উপলব্ধ")), 1, 1);

        nearbyGrid.add(new Label("🏥 " + getText("Hospitals:", "হাসপাতাল:")), 0, 2);
        nearbyGrid.add(new Label(allHospitals.size() + getText(" within 10km", " ১০ কিমির মধ্যে")), 1, 2);

        nearbyGrid.add(new Label("💊 " + getText("Pharmacies:", "ফার্মেসি:")), 0, 3);
        nearbyGrid.add(new Label("12 " + getText("nearby", "কাছে")), 1, 3);

        nearbyGrid.add(new Label("🩸 " + getText("Blood Banks:", "রক্তব্যাংক:")), 0, 4);
        nearbyGrid.add(new Label("8 " + getText("nearby", "কাছে")), 1, 4);

        content.getChildren().addAll(title, locationBox, mapPlaceholder, nearbyGrid);

        tab.setContent(content);
        return tab;
    }

    private Tab createBloodBankTab() {
        Tab tab = new Tab(getText("🩸 Blood Bank", "🩸 রক্তব্যাংক"));
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label(getText("Blood Availability", "রক্তের প্রাপ্যতা"));
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Blood type selector
        HBox bloodTypeBox = new HBox(10);
        Label selectLabel = new Label(getText("Select Blood Type:", "রক্তের গ্রুপ নির্বাচন করুন:"));
        selectLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<String> bloodType = new ComboBox<>();
        bloodType.getItems().addAll("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");
        bloodType.setPromptText(getText("Choose", "নির্বাচন করুন"));

        Button searchBtn = new Button(getText("🔍 Search", "🔍 খুঁজুন"));
        searchBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        bloodTypeBox.getChildren().addAll(selectLabel, bloodType, searchBtn);

        // Blood bank grid
        GridPane bloodGrid = new GridPane();
        bloodGrid.setHgap(20);
        bloodGrid.setVgap(15);
        bloodGrid.setStyle("-fx-padding: 20; -fx-border-color: #ddd; -fx-border-width: 1;");

        String[] types = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        int[] units = {45, 12, 38, 8, 52, 15, 22, 6};

        for (int i = 0; i < types.length; i++) {
            VBox typeBox = new VBox(5);
            typeBox.setAlignment(Pos.CENTER);
            typeBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: " +
                           (units[i] > 20 ? "#E8F5E9" : units[i] > 10 ? "#FFF9C4" : "#FFEBEE"));

            Label typeLabel = new Label(types[i]);
            typeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            Label unitsLabel = new Label(units[i] + getText(" units", " ইউনিট"));
            Label statusLabel = new Label(units[i] > 20 ? getText("✓ Available", "✓ উপলব্ধ") :
                                        units[i] > 10 ? getText("⚠ Limited", "⚠ সীমিত") :
                                        getText("✗ Low Stock", "✗ কম মজুদ"));
            statusLabel.setStyle("-fx-font-weight: bold;");

            typeBox.getChildren().addAll(typeLabel, unitsLabel, statusLabel);
            bloodGrid.add(typeBox, i % 4, i / 4);
        }

        // Blood banks list
        Label banksTitle = new Label(getText("🏥 Blood Banks Nearby", "🏥 কাছের রক্তব্যাংক"));
        banksTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox banksList = new VBox(10);
        String[] bankNames = {
            "Dhaka Medical College Blood Bank",
            "Red Crescent Blood Bank",
            "Quantum Foundation Blood Bank",
            "Badhan Blood Donation Organization"
        };
        String[] bankPhones = {"01800111222", "01800111333", "01800111444", "01800111555"};

        for (int i = 0; i < bankNames.length; i++) {
            final int index = i;
            HBox bankBox = new HBox(15);
            bankBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10;");
            bankBox.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(3);
            Label nameLabel = new Label(bankNames[i]);
            nameLabel.setStyle("-fx-font-weight: bold;");
            Label phoneLabel = new Label("📞 " + bankPhones[i]);
            info.getChildren().addAll(nameLabel, phoneLabel);
            HBox.setHgrow(info, Priority.ALWAYS);

            Button callBtn = new Button(getText("Call", "কল"));
            callBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            callBtn.setOnAction(e -> showAlert(getText("Calling", "কল"), "Calling " + bankPhones[index], Alert.AlertType.INFORMATION));

            Button requestBtn = new Button(getText("Request Blood", "রক্ত চাই"));
            requestBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            requestBtn.setOnAction(e -> showAlert(getText("Request Sent", "অনুরোধ পাঠানো হয়েছে"),
                                                 getText("Blood request sent to ", "রক্তের অনুরোধ পাঠানো হয়েছে ") + bankNames[index],
                                                 Alert.AlertType.INFORMATION));

            bankBox.getChildren().addAll(info, callBtn, requestBtn);
            banksList.getChildren().add(bankBox);
        }

        ScrollPane scrollPane = new ScrollPane(banksList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);

        content.getChildren().addAll(title, bloodTypeBox, bloodGrid, banksTitle, scrollPane);

        tab.setContent(content);
        return tab;
    }

    private Tab createEmergencyContactsTab() {
        Tab tab = new Tab(getText("📞 Emergency Contacts", "📞 জরুরি যোগাযোগ"));
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label(getText("Emergency Hotlines", "জরুরি হটলাইন"));
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        List<EmergencyContact> contacts = List.of(
            new EmergencyContact(getText("National Emergency", "জাতীয় জরুরি"), "999", getText("24/7 Emergency Response", "২৪/৭ জরুরি সেবা")),
            new EmergencyContact(getText("Ambulance Service", "অ্যাম্বুলেন্স সেবা"), "199", getText("Quick Ambulance Dispatch", "দ্রুত অ্যাম্বুলেন্স")),
            new EmergencyContact(getText("Fire Service", "ফায়ার সার্ভিস"), "9555555", getText("Fire & Rescue", "আগুন ও উদ্ধার")),
            new EmergencyContact(getText("Police Emergency", "পুলিশ জরুরি"), "100", getText("Police Assistance", "পুলিশ সহায়তা")),
            new EmergencyContact(getText("Poison Control", "বিষ নিয়ন্ত্রণ"), "16263", getText("Poison & Toxin Emergency", "বিষ জরুরি সেবা")),
            new EmergencyContact(getText("Women & Children", "মহিলা ও শিশু"), "109", getText("Women & Children Helpline", "মহিলা ও শিশু হেল্পলাইন"))
        );

        VBox contactList = new VBox(10);
        for (EmergencyContact contact : contacts) {
            HBox contactBox = new HBox(15);
            contactBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f9f9f9;");
            contactBox.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(5);
            Label nameLabel = new Label(contact.name);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label descLabel = new Label(contact.description);
            descLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
            info.getChildren().addAll(nameLabel, descLabel);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label phoneLabel = new Label(contact.phone);
            phoneLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

            Button callBtn = new Button("📞 " + getText("Call", "কল"));
            callBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            callBtn.setOnAction(e -> {
                showAlert(getText("Calling", "কল করা হচ্ছে"), getText("Dialing ", "ডায়াল করা হচ্ছে ") + contact.phone + "...", Alert.AlertType.INFORMATION);
            });

            contactBox.getChildren().addAll(info, phoneLabel, callBtn);
            contactList.getChildren().add(contactBox);
        }

        ScrollPane scrollPane = new ScrollPane(contactList);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(title, scrollPane);
        tab.setContent(content);
        return tab;
    }

    private Tab createBookingHistoryTab() {
        Tab tab = new Tab(getText("📋 Booking History", "📋 বুকিং ইতিহাস"));
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label(getText("Past Bookings", "পূর্ববর্তী বুকিং"));
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<BookingHistory> historyList = new ListView<>();
        historyList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BookingHistory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cell = new VBox(8);
                    cell.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

                    Label idLabel = new Label(AmbulanceServicePage.this.getText("Booking #", "বুকিং #") + item.bookingId + " - " + item.timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                    idLabel.setStyle("-fx-font-weight: bold;");
                    Label providerLabel = new Label(AmbulanceServicePage.this.getText("Provider: ", "প্রদানকারী: ") + item.provider);
                    Label patientLabel = new Label(AmbulanceServicePage.this.getText("Patient: ", "রোগী: ") + item.patientName);
                    Label routeLabel = new Label(AmbulanceServicePage.this.getText("Route: ", "রুট: ") + item.pickup + " → " + item.destination);
                    Label urgencyLabel = new Label(AmbulanceServicePage.this.getText("Urgency: ", "জরুরিত্ব: ") + item.urgency + " | " + AmbulanceServicePage.this.getText("Cost: ৳", "খরচ: ৳") + item.cost);
                    urgencyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");

                    // Rating section
                    HBox ratingBox = new HBox(10);
                    ratingBox.setAlignment(Pos.CENTER_LEFT);
                    if (item.rating == 0) {
                        Label rateLabel = new Label(AmbulanceServicePage.this.getText("Rate this service:", "এই সেবা রেটিং দিন:"));
                        Button[] stars = new Button[5];
                        for (int i = 0; i < 5; i++) {
                            final int rating = i + 1;
                            stars[i] = new Button("⭐");
                            stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
                            stars[i].setOnAction(e -> {
                                item.rating = rating;
                                showAlert(AmbulanceServicePage.this.getText("Thank You!", "ধন্যবাদ!"),
                                         AmbulanceServicePage.this.getText("You rated this service ", "আপনি এই সেবা রেটিং দিয়েছেন ") + rating + "/5",
                                         Alert.AlertType.INFORMATION);
                                historyList.refresh();
                            });
                        }
                        ratingBox.getChildren().add(rateLabel);
                        ratingBox.getChildren().addAll(stars);
                    } else {
                        Label ratedLabel = new Label(AmbulanceServicePage.this.getText("Your Rating: ", "আপনার রেটিং: ") + "⭐".repeat(item.rating) + " (" + item.rating + "/5)");
                        ratedLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                        ratingBox.getChildren().add(ratedLabel);
                    }

                    Button rebookBtn = new Button(AmbulanceServicePage.this.getText("📋 Rebook", "📋 পুনরায় বুক"));
                    rebookBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    rebookBtn.setOnAction(e -> showAlert(AmbulanceServicePage.this.getText("Rebooking", "পুনরায় বুকিং"),
                                                        AmbulanceServicePage.this.getText("Previous booking details loaded", "পূর্ববর্তী বুকিং তথ্য লোড হয়েছে"),
                                                        Alert.AlertType.INFORMATION));

                    HBox actionBox = new HBox(10, ratingBox);
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    actionBox.getChildren().addAll(spacer, rebookBtn);

                    cell.getChildren().addAll(idLabel, providerLabel, patientLabel, routeLabel, urgencyLabel, actionBox);
                    setGraphic(cell);
                    setText(null);
                }
            }
        });

        historyList.getItems().addAll(bookingHistory);
        VBox.setVgrow(historyList, Priority.ALWAYS);

        if (bookingHistory.isEmpty()) {
            Label emptyLabel = new Label(getText("No booking history yet", "এখনো কোন বুকিং ইতিহাস নেই"));
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            content.getChildren().addAll(title, emptyLabel);
        } else {
            content.getChildren().addAll(title, historyList);
        }

        tab.setContent(content);
        return tab;
    }

    private void filterAmbulances(ListView<AmbulanceInfo> list, String city) {
        list.getItems().clear();
        if ("All Cities".equals(city)) {
            list.getItems().addAll(allProviders);
        } else {
            list.getItems().addAll(
                allProviders.stream()
                    .filter(amb -> amb.city.equals(city))
                    .collect(Collectors.toList())
            );
        }

        // Show message if no ambulances found
        if (list.getItems().isEmpty()) {
            showAlert(getText("No Ambulances", "কোন অ্যাম্বুলেন্স নেই"),
                     getText("No ambulances available in " + city + ". Please select another city.",
                            city + " এ কোন অ্যাম্বুলেন্স পাওয়া যায়নি। অন্য শহর নির্বাচন করুন।"),
                     Alert.AlertType.INFORMATION);
        }
    }

    private void startTracking(AmbulanceInfo ambulance, int adjustedETA) {
        trackingIndicator.setFill(Color.GREEN);
        String[] statusesEn = {
            "Ambulance dispatched...",
            "Driver contacted...",
            "En route to pickup location...",
            "Arriving in " + adjustedETA + " minutes..."
        };
        String[] statusesBn = {
            "অ্যাম্বুলেন্স পাঠানো হয়েছে...",
            "ড্রাইভারের সাথে যোগাযোগ হয়েছে...",
            "পিকআপ লোকেশনের দিকে আসছে...",
            adjustedETA + " মিনিটে পৌঁছাবে..."
        };

        String[] statuses = isEnglish ? statusesEn : statusesBn;

        Timeline timeline = new Timeline();
        for (int i = 0; i < statuses.length; i++) {
            final int index = i;
            timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(i * 2), e -> {
                    trackingStatusLabel.setText(statuses[index]);
                    trackingIndicator.setFill(index % 2 == 0 ? Color.GREEN : Color.LIMEGREEN);
                })
            );
        }
        timeline.play();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getText(String english, String bangla) {
        return isEnglish ? english : bangla;
    }

    private void handlePanicButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getText("🚨 EMERGENCY SOS", "🚨 জরুরি SOS"));
        alert.setHeaderText(getText("PANIC BUTTON ACTIVATED!", "প্যানিক বাটন সক্রিয়!"));
        alert.setContentText(
            getText(
                "This will:\n" +
                "• Call emergency services (999)\n" +
                "• Share your location\n" +
                "• Alert emergency contacts\n" +
                "• Dispatch nearest ambulance\n\n" +
                "Your location: " + currentLocation + "\n\n" +
                "Proceed?",
                "এটি করবে:\n" +
                "• জরুরি সেবায় কল (999)\n" +
                "• আপনার লোকেশন শেয়ার\n" +
                "• জরুরি যোগাযোগে সতর্কতা\n" +
                "• নিকটতম অ্যাম্বুলেন্স পাঠানো\n\n" +
                "আপনার অবস্থান: " + currentLocation + "\n\n" +
                "এগিয়ে যান?"
            )
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert(
                    getText("🚨 SOS SENT", "🚨 SOS পাঠানো হয়েছে"),
                    getText(
                        "Emergency services have been notified!\n" +
                        "Ambulance dispatching...\n" +
                        "Stay calm, help is on the way!",
                        "জরুরি সেবা অবহিত করা হয়েছে!\n" +
                        "অ্যাম্বুলেন্স পাঠানো হচ্ছে...\n" +
                        "শান্ত থাকুন, সাহায্য আসছে!"
                    ),
                    Alert.AlertType.INFORMATION
                );
            }
        });
    }

    private void sendLocationToFamily(String contactNumber, String location) {
        // Simulate sending location with actual location parameter
        showAlert(
            getText("Location Shared", "লোকেশন শেয়ার"),
            getText(
                "Live location shared with " + contactNumber + "\n" +
                "Location: " + location + "\n" +
                "They can track your ambulance in real-time.",
                "লাইভ লোকেশন শেয়ার হয়েছে " + contactNumber + " এর সাথে\n" +
                "অবস্থান: " + location + "\n" +
                "তারা রিয়েল-টাইমে ট্র্যাক করতে পারবেন।"
            ),
            Alert.AlertType.INFORMATION
        );
    }

    private void showDirections(String hospitalName, String address) {
        showAlert(
            getText("Directions", "দিক নির্দেশনা"),
            getText(
                "Opening directions to:\n" + hospitalName + "\n" + address + "\n\n" +
                "(Would open in Google Maps/Navigation app)",
                "দিক নির্দেশনা খোলা হচ্ছে:\n" + hospitalName + "\n" + address + "\n\n" +
                "(গুগল ম্যাপ/নেভিগেশন অ্যাপে খুলবে)"
            ),
            Alert.AlertType.INFORMATION
        );
    }

    private List<AmbulanceInfo> seed() {
        List<AmbulanceInfo> list = new ArrayList<>();
        list.add(new AmbulanceInfo("LifeLine Express", "Dhaka", 12, "Hasan", "01800000001",
            List.of("Oxygen", "Defibrillator", "Stretcher", "First Aid"), true, 800, 5));
        list.add(new AmbulanceInfo("RapidAid Advanced", "Chittagong", 18, "Rahman", "01800000002",
            List.of("ICU Setup", "Ventilator", "Cardiac Monitor", "Oxygen"), true, 1500, 5));
        list.add(new AmbulanceInfo("CareMove", "Rajshahi", 25, "Nabila", "01800000003",
            List.of("Basic Life Support", "Stretcher", "Oxygen"), false, 600, 4));
        list.add(new AmbulanceInfo("MediSpeed Emergency", "Dhaka", 8, "Karim", "01800000004",
            List.of("Advanced Life Support", "Defibrillator", "IV Setup", "Oxygen"), true, 1200, 5));
        list.add(new AmbulanceInfo("QuickResponse 24/7", "Sylhet", 15, "Fatima", "01800000005",
            List.of("Oxygen", "First Aid", "Wheelchair"), true, 700, 4));
        list.add(new AmbulanceInfo("Critical Care Ambulance", "Dhaka", 10, "Rahim", "01800000006",
            List.of("ICU Setup", "Ventilator", "Cardiac Monitor", "Defibrillator", "IV Setup"), true, 1800, 5));
        list.add(new AmbulanceInfo("City Emergency Services", "Khulna", 20, "Sadia", "01800000007",
            List.of("Basic Life Support", "Oxygen", "Stretcher"), true, 650, 4));
        return list;
    }

    private List<Hospital> seedHospitals() {
        List<Hospital> list = new ArrayList<>();
        list.add(new Hospital("Dhaka Medical College Hospital", "Dhaka", 2.5,
            List.of("General Medicine", "Surgery", "Emergency Care", "ICU"),
            "Bakshibazar, Dhaka", "02-9673036", 150, true, 5));
        list.add(new Hospital("Square Hospital", "Dhaka", 3.8,
            List.of("Cardiology", "Neurology", "Oncology", "ICU"),
            "18/F, Bir Uttam Qazi Nuruzzaman Sarak", "09666-710678", 80, true, 5));
        list.add(new Hospital("United Hospital", "Dhaka", 4.2,
            List.of("Cardiology", "Orthopedics", "Pediatrics", "Emergency"),
            "Gulshan, Dhaka", "09666-710666", 95, true, 5));
        list.add(new Hospital("Apollo Hospitals Dhaka", "Dhaka", 5.5,
            List.of("Cardiology", "Neurology", "Oncology", "Critical Care"),
            "Plot# 81, Block# E, Bashundhara R/A", "10678", 120, true, 5));
        list.add(new Hospital("Bangabandhu Sheikh Mujib Medical University", "Dhaka", 3.2,
            List.of("All Specialties", "Research", "Teaching Hospital"),
            "Shahbagh, Dhaka", "02-9664144", 200, true, 4));
        list.add(new Hospital("Lab Aid Hospital", "Dhaka", 6.8,
            List.of("General Surgery", "Medicine", "Diagnostics"),
            "Dhanmondi, Dhaka", "09666-710704", 60, true, 4));
        list.add(new Hospital("Holy Family Red Crescent Hospital", "Dhaka", 7.2,
            List.of("Maternity", "Pediatrics", "Surgery"),
            "Eskaton, Dhaka", "02-9360660", 70, true, 4));
        list.add(new Hospital("Popular Medical College Hospital", "Dhaka", 8.5,
            List.of("General Medicine", "Surgery", "ICU"),
            "Dhanmondi, Dhaka", "09666710939", 55, true, 4));
        return list;
    }

    private record AmbulanceInfo(String name, String city, int baseEtaMinutes, String driverName,
                                 String phone, List<String> equipment, boolean available, int baseCost, int rating) {

        // Calculate adjusted ETA based on urgency (Critical gets faster service)
        public int getAdjustedETA(String urgency) {
            return switch (urgency) {
                case "Critical" -> (int) (baseEtaMinutes * 0.6);  // 40% faster
                case "High" -> (int) (baseEtaMinutes * 0.8);      // 20% faster
                default -> baseEtaMinutes;                         // Normal speed
            };
        }

        // Calculate adjusted cost based on urgency (Critical costs more)
        public int getAdjustedCost(String urgency) {
            return switch (urgency) {
                case "Critical" -> (int) (baseCost * 1.5);  // 50% more expensive
                case "High" -> (int) (baseCost * 1.25);     // 25% more expensive
                default -> baseCost;                        // Base price
            };
        }
    }

    private record EmergencyContact(String name, String phone, String description) { }

    private record Hospital(String name, String city, double distance, List<String> specialties,
                           String address, String phone, int availableBeds, boolean hasEmergency, int rating) { }

    private static class BookingHistory {
        private static int counter = 1000;
        final int bookingId;
        final LocalDateTime timestamp;
        final String provider;
        final String patientName;
        final String pickup;
        final String destination;
        final String urgency;
        final int cost;
        int rating; // 0 = not rated, 1-5 = star rating

        BookingHistory(LocalDateTime timestamp, String provider, String patientName,
                      String pickup, String destination, String urgency, int cost) {
            this.bookingId = counter++;
            this.timestamp = timestamp;
            this.provider = provider;
            this.patientName = patientName;
            this.pickup = pickup;
            this.destination = destination;
            this.urgency = urgency;
            this.cost = cost;
            this.rating = 0;
        }
    }
}

