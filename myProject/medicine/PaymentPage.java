package myProject.medicine;

import myProject.ClientFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.util.List;

public class PaymentPage {
    private final MedicineNavigator app;
    private final List<SelectionRow> selections;

    public PaymentPage(MedicineNavigator app, List<SelectionRow> selections) {
        this.app = app; this.selections = selections;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f7fafc, #e8f1f6);");

        Button helpBtn = new Button("Help");
        helpBtn.setStyle(ghost());
        Label title = new Label("Payment Gateway");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#0b8793"));
        HBox top = new HBox(10, title);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        top.getChildren().addAll(spacer, helpBtn);
        top.setPadding(new Insets(16, 18, 10, 18));
        root.setTop(top);

        ToggleGroup group = new ToggleGroup();
        RadioButton cod = new RadioButton("Cash on Delivery"); cod.setToggleGroup(group);
        RadioButton mobile = new RadioButton("Mobile Banking"); mobile.setToggleGroup(group);

        Label lead = new Label("Select a payment method:");
        lead.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        lead.setTextFill(Color.web("#1f3b4d"));

        VBox options = new VBox(12, lead, cod, mobile);
        options.setPadding(new Insets(14));
        options.setSpacing(10);
        options.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));
        options.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);");
        BorderPane.setMargin(options, new Insets(10, 18, 14, 18));
        root.setCenter(options);

        Button back = new Button("Back");
        Button confirm = new Button("Confirm & Place Order");
        confirm.setDisable(true);
        back.setStyle(ghost());
        confirm.setStyle(primary());

        group.selectedToggleProperty().addListener((obs, o, n) -> confirm.setDisable(n == null));

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 18, 14, 18));
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        bottom.getChildren().addAll(back, leftSpacer, confirm, rightSpacer);
        root.setBottom(bottom);

        back.setOnAction(e -> app.showBuyPage(selections));
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());
        confirm.setOnAction(e -> {
            String paymentMethod = cod.isSelected() ? "Cash on Delivery" : "Mobile Banking";
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

    private String primary() {
        return "-fx-background-color: linear-gradient(to right, #0b8793, #1f3b4d); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 14;";
    }

    private String ghost() {
        return "-fx-background-color: transparent; -fx-border-color: #1f3b4d; -fx-text-fill: #1f3b4d; -fx-border-radius: 10; -fx-padding: 7 12;";
    }
}
