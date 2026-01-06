package myProject.medicine;

import myProject.ClientFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
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

public class BuyPage {
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

    public BuyPage(MedicineNavigator app, List<SelectionRow> selections) {
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

        Label icon = new Label("🛒");
        icon.setFont(Font.font("Segoe UI Emoji", 28));

        Label title = new Label("Order Summary");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(PRIMARY_COLOR));

        Label subtitle = new Label("Review your items before checkout");
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

        // ===== ORDER ITEMS =====
        VBox itemsContainer = new VBox(12);
        double subtotal = 0;
        int totalItems = 0;

        for (SelectionRow r : selections) {
            Medicine m = r.getMedicine();
            double lineTotal = r.getQuantity() * m.getPrice();
            subtotal += lineTotal;
            totalItems += r.getQuantity();

            // Item card
            Label itemIcon = new Label("💊");
            itemIcon.setFont(Font.font("Segoe UI Emoji", 20));

            Label itemName = new Label(m.getName());
            itemName.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
            itemName.setStyle("-fx-text-fill: #000000;"); // BLACK text

            Label itemDetails = new Label(m.getShop() + " • " + m.getLocation() + " • " + m.getBrand());
            itemDetails.setFont(Font.font("Segoe UI", 12));
            itemDetails.setStyle("-fx-text-fill: #374151;"); // Dark gray text

            VBox itemInfo = new VBox(2, itemName, itemDetails);
            HBox.setHgrow(itemInfo, Priority.ALWAYS);

            Label qtyLabel = new Label("×" + r.getQuantity());
            qtyLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            qtyLabel.setStyle("-fx-background-color: #e0f2fe; -fx-padding: 4 10; -fx-background-radius: 12; -fx-text-fill: #1e3a5f;");

            Label priceLabel = new Label("৳ " + String.format("%.2f", lineTotal));
            priceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            priceLabel.setStyle("-fx-text-fill: #047857;"); // Dark green text
            priceLabel.setMinWidth(100);
            priceLabel.setAlignment(Pos.CENTER_RIGHT);

            HBox itemRow = new HBox(12, itemIcon, itemInfo, qtyLabel, priceLabel);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setPadding(new Insets(14, 16, 14, 16));
            itemRow.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #d1d5db; -fx-border-radius: 12; -fx-border-width: 1;");

            DropShadow itemShadow = new DropShadow();
            itemShadow.setColor(Color.rgb(0, 0, 0, 0.1));
            itemShadow.setRadius(12);
            itemShadow.setOffsetY(4);
            itemRow.setEffect(itemShadow);

            itemsContainer.getChildren().add(itemRow);
        }

        ScrollPane scrollPane = new ScrollPane(itemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(0));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ===== PRICE SUMMARY CARD =====
        double delivery = 50.0;
        double total = subtotal + delivery;

        Label summaryTitle = new Label("💰 Price Breakdown");
        summaryTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        summaryTitle.setTextFill(Color.web(TEXT_PRIMARY));

        HBox subtotalRow = createPriceRow("Subtotal (" + totalItems + " items)", subtotal, false);
        HBox deliveryRow = createPriceRow("🚚 Delivery Charge", delivery, false);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e5e7eb;");

        HBox totalRow = createPriceRow("Total Amount", total, true);

        VBox summaryCard = new VBox(12, summaryTitle, subtotalRow, deliveryRow, sep, totalRow);
        summaryCard.setPadding(new Insets(20));
        summaryCard.setStyle("-fx-background-color: white; -fx-background-radius: 16;");

        DropShadow summaryShadow = new DropShadow();
        summaryShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        summaryShadow.setRadius(12);
        summaryShadow.setOffsetY(4);
        summaryCard.setEffect(summaryShadow);

        // ===== MAIN CONTENT =====
        Label itemsTitle = new Label("📦 Your Items (" + selections.size() + ")");
        itemsTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        itemsTitle.setTextFill(Color.web(TEXT_PRIMARY));

        VBox mainContent = new VBox(16, itemsTitle, scrollPane, summaryCard);
        mainContent.setPadding(new Insets(20, 24, 16, 24));
        root.setCenter(mainContent);

        // ===== BOTTOM ACTION BAR =====
        Label secureLabel = new Label("🔒 Secure checkout • 100% safe payment");
        secureLabel.setFont(Font.font("Segoe UI", 12));
        secureLabel.setTextFill(Color.web(TEXT_SECONDARY));

        Button placeOrderBtn = new Button("💳 Proceed to Payment");
        placeOrderBtn.setStyle(primaryButtonStyle());
        placeOrderBtn.setMinHeight(48);
        placeOrderBtn.setMinWidth(200);
        placeOrderBtn.setOnMouseEntered(e -> placeOrderBtn.setStyle(primaryButtonHoverStyle()));
        placeOrderBtn.setOnMouseExited(e -> placeOrderBtn.setStyle(primaryButtonStyle()));

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        HBox bottom = new HBox(16, secureLabel, bottomSpacer, placeOrderBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 24, 20, 24));
        bottom.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1 0 0 0;");
        root.setBottom(bottom);

        // ===== EVENT HANDLERS =====
        backBtn.setOnAction(e -> app.showHome());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());
        placeOrderBtn.setOnAction(e -> app.showPaymentPage(selections));

        return root;
    }

    private HBox createPriceRow(String label, double amount, boolean isTotal) {
        Label leftLabel = new Label(label);
        leftLabel.setFont(Font.font("Segoe UI", isTotal ? FontWeight.BOLD : FontWeight.NORMAL, isTotal ? 15 : 13));
        leftLabel.setTextFill(Color.web(TEXT_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rightLabel = new Label("৳ " + String.format("%.2f", amount));
        rightLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, isTotal ? 18 : 14));
        rightLabel.setTextFill(Color.web(isTotal ? SUCCESS_COLOR : TEXT_PRIMARY));

        HBox row = new HBox(8, leftLabel, spacer, rightLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
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
}
