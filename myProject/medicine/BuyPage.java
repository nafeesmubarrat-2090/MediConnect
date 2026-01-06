package myProject.medicine;

import myProject.ClientFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

public class BuyPage {
    private final MedicineNavigator app;
    private final List<SelectionRow> selections;

    public BuyPage(MedicineNavigator app, List<SelectionRow> selections) {
        this.app = app; this.selections = selections;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f7fafc, #e8f1f6);");

        Button helpBtn = new Button("Help");
        helpBtn.setStyle(ghost());
        Label title = new Label("Review your selection");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#0b8793"));
        HBox top = new HBox(10, title);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        top.getChildren().addAll(spacer, helpBtn);
        top.setPadding(new Insets(16, 18, 10, 18));
        root.setTop(top);

        TextArea summary = new TextArea();
        summary.setEditable(false);
        summary.setWrapText(true);
        summary.setStyle("-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: transparent; -fx-font-size: 13px;");
        StringBuilder sb = new StringBuilder();
        double subtotal = 0;
        for (SelectionRow r : selections) {
            Medicine m = r.getMedicine();
            double line = r.getQuantity() * m.getPrice();
            subtotal += line;
            sb.append(r.getQuantity()).append(" x ")
              .append(m.getName()).append(" (")
              .append(m.getDosageForm()).append(", ")
              .append(m.getStrength()).append(") by ")
              .append(m.getBrand()).append(" -> Tk ")
              .append(String.format("%.2f", line)).append("\n");
        }
        double delivery = 10.0; // Tk 10 delivery
        double total = subtotal + delivery;
        sb.append("\nSubtotal: Tk ").append(String.format("%.2f", subtotal))
          .append("\nDelivery Charge: Tk ").append(String.format("%.2f", delivery))
          .append("\nTotal: Tk ").append(String.format("%.2f", total));
        summary.setText(sb.toString());

        VBox card = new VBox(10, summary);
        card.setPadding(new Insets(12));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));
        card.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);");
        BorderPane.setMargin(card, new Insets(8, 18, 14, 18));
        root.setCenter(card);

        Button back = new Button("Back");
        Button placeOrder = new Button("Place Order");
        back.setStyle(ghost());
        placeOrder.setStyle(primary());

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10, 18, 14, 18));
        bottom.setAlignment(Pos.CENTER);
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        bottom.getChildren().addAll(back, leftSpacer, placeOrder, rightSpacer);
        root.setBottom(bottom);

        back.setOnAction(e -> app.showHome());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());
        placeOrder.setOnAction(e -> app.showPaymentPage(selections));

        return root;
    }

    private String primary() {
        return "-fx-background-color: linear-gradient(to right, #0b8793, #1f3b4d); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 14;";
    }

    private String ghost() {
        return "-fx-background-color: transparent; -fx-border-color: #1f3b4d; -fx-text-fill: #1f3b4d; -fx-border-radius: 10; -fx-padding: 7 12;";
    }
}
