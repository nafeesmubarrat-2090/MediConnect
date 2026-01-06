package myProject.medicine;

import myProject.ClientFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.DropShadow;

public class OrderSuccessPage {
    private final MedicineNavigator app;

    // Professional color scheme
    private static final String PRIMARY_COLOR = "#0d9488";
    private static final String TEXT_PRIMARY = "#1f2937";
    private static final String TEXT_SECONDARY = "#6b7280";
    private static final String SUCCESS_COLOR = "#059669";
    private static final String BG_GRADIENT = "-fx-background-color: linear-gradient(to bottom right, #ecfdf5, #d1fae5, #a7f3d0);";

    public OrderSuccessPage(MedicineNavigator app) { this.app = app; }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle(BG_GRADIENT);

        // ===== HEADER =====
        Button helpBtn = new Button("🆘 Help");
        helpBtn.setStyle(helpButtonStyle());
        helpBtn.setOnMouseEntered(e -> helpBtn.setStyle(helpButtonHoverStyle()));
        helpBtn.setOnMouseExited(e -> helpBtn.setStyle(helpButtonStyle()));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(headerSpacer, helpBtn);
        header.setPadding(new Insets(20, 24, 10, 24));
        root.setTop(header);

        // ===== SUCCESS CONTENT =====
        Label checkIcon = new Label("✓");
        checkIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        checkIcon.setTextFill(Color.WHITE);
        checkIcon.setStyle("-fx-background-color: " + SUCCESS_COLOR + "; -fx-background-radius: 50; -fx-padding: 20 28;");

        DropShadow iconShadow = new DropShadow();
        iconShadow.setColor(Color.rgb(5, 150, 105, 0.4));
        iconShadow.setRadius(20);
        iconShadow.setOffsetY(6);
        checkIcon.setEffect(iconShadow);

        Label celebrateIcon = new Label("🎉");
        celebrateIcon.setFont(Font.font("Segoe UI Emoji", 40));

        Label successTitle = new Label("Order Placed Successfully!");
        successTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        successTitle.setTextFill(Color.web(SUCCESS_COLOR));

        Label orderNumber = new Label("Order #" + System.currentTimeMillis() % 100000);
        orderNumber.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        orderNumber.setTextFill(Color.web(TEXT_SECONDARY));
        orderNumber.setStyle("-fx-background-color: white; -fx-padding: 6 16; -fx-background-radius: 20;");

        Label message = new Label("Thank you for your order! We're preparing your medicines\nand you'll receive a call when it's out for delivery.");
        message.setFont(Font.font("Segoe UI", 14));
        message.setTextFill(Color.web(TEXT_PRIMARY));
        message.setTextAlignment(TextAlignment.CENTER);
        message.setWrapText(true);

        // Timeline steps
        VBox timeline = createTimeline();

        VBox successContent = new VBox(20, checkIcon, celebrateIcon, successTitle, orderNumber, message, timeline);
        successContent.setAlignment(Pos.CENTER);
        successContent.setPadding(new Insets(40));
        successContent.setMaxWidth(500);

        VBox card = new VBox(successContent);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(550);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 24;");

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        cardShadow.setRadius(30);
        cardShadow.setOffsetY(8);
        card.setEffect(cardShadow);

        VBox centerWrapper = new VBox(card);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(20, 24, 20, 24));
        root.setCenter(centerWrapper);

        // ===== BOTTOM ACTIONS =====
        Button homeBtn = new Button("🏠 Back to Medicine Home");
        homeBtn.setStyle(primaryButtonStyle());
        homeBtn.setMinHeight(48);
        homeBtn.setMinWidth(240);
        homeBtn.setOnMouseEntered(e -> homeBtn.setStyle(primaryButtonHoverStyle()));
        homeBtn.setOnMouseExited(e -> homeBtn.setStyle(primaryButtonStyle()));

        Button trackBtn = new Button("📦 Track Order");
        trackBtn.setStyle(secondaryButtonStyle());
        trackBtn.setMinHeight(48);
        trackBtn.setOnMouseEntered(e -> trackBtn.setStyle(secondaryButtonHoverStyle()));
        trackBtn.setOnMouseExited(e -> trackBtn.setStyle(secondaryButtonStyle()));

        HBox bottom = new HBox(16, trackBtn, homeBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 24, 30, 24));
        root.setBottom(bottom);

        // ===== EVENT HANDLERS =====
        homeBtn.setOnAction(e -> app.showHome());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());
        trackBtn.setOnAction(e -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Order Tracking");
            alert.setHeaderText("Your order is being processed");
            alert.setContentText("You will receive an SMS with tracking details shortly.\n\nEstimated delivery: 2-3 business days");
            alert.showAndWait();
        });

        return root;
    }

    private VBox createTimeline() {
        VBox timeline = new VBox(8);
        timeline.setAlignment(Pos.CENTER_LEFT);
        timeline.setPadding(new Insets(20, 0, 0, 0));

        String[] steps = {"✓ Order Confirmed", "⏳ Preparing Your Items", "🚚 Out for Delivery", "📍 Delivered"};
        String[] times = {"Just now", "In progress", "Expected", "Pending"};

        for (int i = 0; i < steps.length; i++) {
            boolean isPast = i == 0;

            Label stepLabel = new Label(steps[i]);
            stepLabel.setFont(Font.font("Segoe UI", isPast ? FontWeight.SEMI_BOLD : FontWeight.NORMAL, 13));
            stepLabel.setTextFill(Color.web(isPast ? SUCCESS_COLOR : TEXT_SECONDARY));

            Label timeLabel = new Label(times[i]);
            timeLabel.setFont(Font.font("Segoe UI", 11));
            timeLabel.setTextFill(Color.web(TEXT_SECONDARY));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox stepRow = new HBox(8, stepLabel, spacer, timeLabel);
            stepRow.setAlignment(Pos.CENTER_LEFT);
            stepRow.setPadding(new Insets(8, 16, 8, 16));
            stepRow.setStyle("-fx-background-color: " + (isPast ? "#ecfdf5" : "#f9fafb") + "; -fx-background-radius: 8;");

            timeline.getChildren().add(stepRow);
        }
        return timeline;
    }

    private String helpButtonStyle() {
        return "-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String helpButtonHoverStyle() {
        return "-fx-background-color: #fde68a; -fx-text-fill: #78350f; -fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, " + SUCCESS_COLOR + ", #047857); -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 12; -fx-padding: 14 28; -fx-cursor: hand;";
    }

    private String primaryButtonHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #047857, #065f46); -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 12; -fx-padding: 14 28; -fx-cursor: hand;";
    }

    private String secondaryButtonStyle() {
        return "-fx-background-color: white; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #d1d5db; -fx-padding: 14 24; -fx-cursor: hand;";
    }

    private String secondaryButtonHoverStyle() {
        return "-fx-background-color: #f9fafb; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #9ca3af; -fx-padding: 14 24; -fx-cursor: hand;";
    }
}
