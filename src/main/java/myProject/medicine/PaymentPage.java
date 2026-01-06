package myProject.medicine;

import myProject.ClientFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
import javafx.scene.effect.DropShadow;

import java.util.List;

public class PaymentPage {
    private final MedicineNavigator app;
    private final List<SelectionRow> selections;

    // Professional color scheme
    private static final String PRIMARY_COLOR = "#0d9488";
    private static final String PRIMARY_DARK = "#0f766e";
    private static final String SECONDARY_COLOR = "#1e3a5f";
    private static final String TEXT_PRIMARY = "#1f2937";
    private static final String TEXT_SECONDARY = "#6b7280";
    private static final String SUCCESS_COLOR = "#059669";
    private static final String BG_GRADIENT = "-fx-background-color: linear-gradient(to bottom right, #f0fdfa, #e0f2fe, #f0f9ff);";

    public PaymentPage(MedicineNavigator app, List<SelectionRow> selections) {
        this.app = app; this.selections = selections;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle(BG_GRADIENT);

        // ===== HEADER =====
        Button backBtn = new Button("← Back");
        backBtn.setStyle(backButtonStyle());
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backButtonHoverStyle()));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(backButtonStyle()));

        Label icon = new Label("💳");
        icon.setFont(Font.font("Segoe UI Emoji", 28));

        Label title = new Label("Payment Gateway");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(PRIMARY_COLOR));

        Label subtitle = new Label("Choose your preferred payment method");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web(TEXT_SECONDARY));

        VBox titleBox = new VBox(2, title, subtitle);
        HBox leftHeader = new HBox(12, backBtn, icon, titleBox);
        leftHeader.setAlignment(Pos.CENTER_LEFT);

        Button helpBtn = new Button("🆘 Help");
        helpBtn.setStyle(helpButtonStyle());
        helpBtn.setOnMouseEntered(e -> helpBtn.setStyle(helpButtonHoverStyle()));
        helpBtn.setOnMouseExited(e -> helpBtn.setStyle(helpButtonStyle()));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(12, leftHeader, headerSpacer, helpBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 1 0;");
        root.setTop(header);

        // ===== PAYMENT OPTIONS =====
        ToggleGroup group = new ToggleGroup();

        // Cash on Delivery Option
        VBox codOption = createPaymentOption(
            "🚚", "Cash on Delivery",
            "Pay when you receive your order",
            "No additional charges",
            group
        );
        RadioButton codRadio = (RadioButton) codOption.getUserData();

        // Mobile Banking Option
        VBox mobileOption = createPaymentOption(
            "📱", "Mobile Banking",
            "bKash, Nagad, Rocket",
            "Instant payment confirmation",
            group
        );
        RadioButton mobileRadio = (RadioButton) mobileOption.getUserData();

        // Card Payment Option
        VBox cardOption = createPaymentOption(
            "💳", "Credit/Debit Card",
            "Visa, Mastercard, AMEX",
            "Secure 3D payment",
            group
        );
        RadioButton cardRadio = (RadioButton) cardOption.getUserData();

        Label paymentTitle = new Label("🔐 Select Payment Method");
        paymentTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        paymentTitle.setTextFill(Color.web(TEXT_PRIMARY));

        VBox optionsContainer = new VBox(12, paymentTitle, codOption, mobileOption, cardOption);
        optionsContainer.setPadding(new Insets(20));
        optionsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 16;");

        DropShadow optionsShadow = new DropShadow();
        optionsShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        optionsShadow.setRadius(16);
        optionsShadow.setOffsetY(4);
        optionsContainer.setEffect(optionsShadow);

        // ===== ORDER SUMMARY =====
        double subtotal = 0;
        int totalItems = 0;
        for (SelectionRow r : selections) {
            subtotal += r.getQuantity() * r.getMedicine().getPrice();
            totalItems += r.getQuantity();
        }
        double delivery = 50.0;
        double total = subtotal + delivery;

        Label summaryTitle = new Label("📋 Order Summary");
        summaryTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        summaryTitle.setTextFill(Color.web(TEXT_PRIMARY));

        HBox itemsRow = createSummaryRow("Items (" + totalItems + ")", "৳ " + String.format("%.2f", subtotal));
        HBox deliveryRow = createSummaryRow("Delivery", "৳ " + String.format("%.2f", delivery));

        HBox totalRow = createSummaryRow("Total", "৳ " + String.format("%.2f", total));
        Label totalLabel = (Label) totalRow.getChildren().get(totalRow.getChildren().size() - 1);
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        totalLabel.setTextFill(Color.web(SUCCESS_COLOR));

        VBox summaryCard = new VBox(10, summaryTitle, itemsRow, deliveryRow, totalRow);
        summaryCard.setPadding(new Insets(20));
        summaryCard.setStyle("-fx-background-color: #f0fdf4; -fx-background-radius: 16; -fx-border-color: #bbf7d0; -fx-border-radius: 16;");

        VBox mainContent = new VBox(20, optionsContainer, summaryCard);
        mainContent.setPadding(new Insets(24));
        root.setCenter(mainContent);

        // ===== BOTTOM ACTION BAR =====
        Label secureLabel = new Label("🔒 Your payment is 100% secure");
        secureLabel.setFont(Font.font("Segoe UI", 12));
        secureLabel.setTextFill(Color.web(TEXT_SECONDARY));

        Button confirmBtn = new Button("✓ Confirm & Place Order");
        confirmBtn.setStyle(disabledButtonStyle());
        confirmBtn.setMinHeight(48);
        confirmBtn.setMinWidth(220);
        confirmBtn.setDisable(true);

        // Enable button when payment method selected
        group.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n != null) {
                confirmBtn.setDisable(false);
                confirmBtn.setStyle(primaryButtonStyle());
                confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle(primaryButtonHoverStyle()));
                confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle(primaryButtonStyle()));
            }
        });

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        HBox bottom = new HBox(16, secureLabel, bottomSpacer, confirmBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 24, 20, 24));
        bottom.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1 0 0 0;");
        root.setBottom(bottom);

        // ===== EVENT HANDLERS =====
        backBtn.setOnAction(e -> app.showBuyPage(selections));
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());

        confirmBtn.setOnAction(e -> {
            String paymentMethod = "Unknown";
            if (codRadio.isSelected()) paymentMethod = "Cash on Delivery";
            else if (mobileRadio.isSelected()) paymentMethod = "Mobile Banking";
            else if (cardRadio.isSelected()) paymentMethod = "Credit/Debit Card";

            try {
                OrderLogger.logOrder(selections, paymentMethod);
                app.showOrderSuccess();
            } catch (OrderException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Order Logging Failed");
                alert.setHeaderText("Your order was placed but logging failed");
                alert.setContentText(ex.getMessage() + "\n\nPlease contact support.");
                alert.showAndWait();
                app.showOrderSuccess();
            }
        });

        return root;
    }

    private VBox createPaymentOption(String icon, String title, String description, String badge, ToggleGroup group) {
        RadioButton radio = new RadioButton();
        radio.setToggleGroup(group);
        radio.setStyle("-fx-padding: 0;");

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", 24));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web(TEXT_PRIMARY));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", 12));
        descLabel.setTextFill(Color.web(TEXT_SECONDARY));

        VBox textBox = new VBox(2, titleLabel, descLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label badgeLabel = new Label(badge);
        badgeLabel.setFont(Font.font("Segoe UI", 11));
        badgeLabel.setTextFill(Color.web(SUCCESS_COLOR));
        badgeLabel.setStyle("-fx-background-color: #d1fae5; -fx-padding: 4 10; -fx-background-radius: 10;");

        HBox content = new HBox(12, radio, iconLabel, textBox, badgeLabel);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(16));
        content.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 12; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-cursor: hand;");

        VBox wrapper = new VBox(content);
        wrapper.setUserData(radio);

        // Click anywhere to select
        content.setOnMouseClicked(e -> radio.setSelected(true));

        // Hover and selection effects
        radio.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                content.setStyle("-fx-background-color: #ecfdf5; -fx-background-radius: 12; -fx-border-color: " + SUCCESS_COLOR + "; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
            } else {
                content.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 12; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-cursor: hand;");
            }
        });

        content.setOnMouseEntered(e -> {
            if (!radio.isSelected()) {
                content.setStyle("-fx-background-color: #f0fdf4; -fx-background-radius: 12; -fx-border-color: #86efac; -fx-border-radius: 12; -fx-cursor: hand;");
            }
        });

        content.setOnMouseExited(e -> {
            if (!radio.isSelected()) {
                content.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 12; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-cursor: hand;");
            }
        });

        return wrapper;
    }

    private HBox createSummaryRow(String label, String value) {
        Label leftLabel = new Label(label);
        leftLabel.setFont(Font.font("Segoe UI", 13));
        leftLabel.setTextFill(Color.web(TEXT_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rightLabel = new Label(value);
        rightLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        rightLabel.setTextFill(Color.web(TEXT_PRIMARY));

        return new HBox(8, leftLabel, spacer, rightLabel);
    }

    // ===== STYLE METHODS =====
    private String backButtonStyle() {
        return "-fx-background-color: #f3f4f6; -fx-text-fill: " + TEXT_PRIMARY + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: bold;" +
               "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String backButtonHoverStyle() {
        return "-fx-background-color: #e5e7eb; -fx-text-fill: " + TEXT_PRIMARY + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: bold;" +
               "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;";
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

    private String primaryButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, " + SUCCESS_COLOR + ", #047857);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 10; -fx-padding: 12 28; -fx-cursor: hand;";
    }

    private String primaryButtonHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #047857, #065f46);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 10; -fx-padding: 12 28; -fx-cursor: hand;";
    }

    private String disabledButtonStyle() {
        return "-fx-background-color: #d1d5db; -fx-text-fill: #9ca3af;" +
               "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 10; -fx-padding: 12 28;";
    }
}
