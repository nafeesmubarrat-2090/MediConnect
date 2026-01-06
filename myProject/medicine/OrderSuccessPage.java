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

public class OrderSuccessPage {
    private final MedicineNavigator app;
    public OrderSuccessPage(MedicineNavigator app) { this.app = app; }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f7fafc, #e8f1f6);");

        Button helpBtn = new Button("Help");
        helpBtn.setStyle(ghost());
        Label heading = new Label("Order Status");
        heading.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        heading.setTextFill(Color.web("#0b8793"));
        HBox top = new HBox(10, heading);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        top.getChildren().addAll(spacer, helpBtn);
        top.setPadding(new Insets(16, 18, 10, 18));
        root.setTop(top);

        Label success = new Label("Your order has been placed successfully!");
        success.setFont(Font.font("Helvetica", FontWeight.BOLD, 17));
        success.setTextFill(Color.web("#1f3b4d"));

        Label sub = new Label("We’re preparing your items. You’ll receive a call when it’s out for delivery.");
        sub.setFont(Font.font("Helvetica", 13));
        sub.setTextFill(Color.web("#4a6070"));
        sub.setWrapText(true);

        VBox card = new VBox(8, success, sub);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));
        card.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);");
        BorderPane.setAlignment(card, Pos.CENTER);
        BorderPane.setMargin(card, new Insets(10, 18, 14, 18));
        root.setCenter(card);

        Button home = new Button("Back to Home");
        home.setStyle(primary());
        HBox bottom = new HBox(home);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 18, 16, 18));
        root.setBottom(bottom);

        home.setOnAction(e -> app.showHome());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());
        return root;
    }

    private String primary() {
        return "-fx-background-color: linear-gradient(to right, #0b8793, #1f3b4d); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 14;";
    }

    private String ghost() {
        return "-fx-background-color: transparent; -fx-border-color: #1f3b4d; -fx-text-fill: #1f3b4d; -fx-border-radius: 10; -fx-padding: 7 12;";
    }
}
