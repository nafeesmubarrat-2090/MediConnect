package myProject.medicine;

import myProject.ClientFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

public class MedicineHomePage {
    private final MedicineNavigator app;
    private final MedicineRepository repo;

    public MedicineHomePage(MedicineNavigator app, MedicineRepository repo) {
        this.app = app;
        this.repo = repo;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f7fafc, #e8f1f6);");

        Label title = new Label("Find medicines fast");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#0b8793"));

        Label subtitle = new Label("Search by name, then filter by location, shop, or brand.");
        subtitle.setFont(Font.font("Helvetica", 13));
        subtitle.setTextFill(Color.web("#4a6070"));

        TextField search = new TextField();
        search.setPromptText("Search medicine by name");
        search.setMinHeight(36);
        search.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #c7d6e2; -fx-padding: 0 10;");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(primary());
        Button filterBtn = new Button("Filter");
        filterBtn.setStyle(ghost());
        Button helpBtn = new Button("Help");
        helpBtn.setStyle(ghost());

        HBox searchRow = new HBox(10, search, searchBtn, filterBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(search, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(10, new VBox(4, title, subtitle), spacer, helpBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(16, 18, 12, 18));

        VBox topWrap = new VBox(12, top, searchRow);
        topWrap.setPadding(new Insets(10, 18, 10, 18));
        topWrap.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setTop(topWrap);

        ObservableList<String> names = FXCollections.observableArrayList(repo.distinctNames());
        ListView<String> list = new ListView<>(names);
        list.setStyle("-fx-background-insets: 0; -fx-padding: 6;");

        VBox card = new VBox(list);
        card.setPadding(new Insets(12));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));
        card.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);");
        card.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(card, new Insets(6, 18, 18, 18));
        root.setCenter(card);

        Runnable doSearch = () -> {
            String query = search.getText() == null ? "" : search.getText().trim();
            if (query.isEmpty()) return;
            app.showResults(query);
        };
        searchBtn.setOnAction(e -> doSearch.run());
        search.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) doSearch.run(); });
        list.setOnMouseClicked(e -> {
            String sel = list.getSelectionModel().getSelectedItem();
            if (sel != null) app.showResults(sel);
        });

        filterBtn.setOnAction(e -> showHomeFilterDialog(list));
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());

        return root;
    }

    private void showHomeFilterDialog(ListView<String> list) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Filter (Home)");
        ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(close);

        TextField startsWith = new TextField();
        startsWith.setPromptText("Name starts with (optional)");
        VBox box = new VBox(10, new Label("Quick filter:"), startsWith);
        box.setPadding(new Insets(10));
        dlg.getDialogPane().setContent(box);

        Node ok = dlg.getDialogPane().lookupButton(close);
        ok.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            String p = startsWith.getText() == null ? "" : startsWith.getText().trim().toLowerCase();
            list.getItems().setAll(repo.distinctNames().stream()
                    .filter(n -> p.isEmpty() || n.toLowerCase().startsWith(p))
                    .toList());
        });

        dlg.showAndWait();
    }

    private String primary() {
        return "-fx-background-color: linear-gradient(to right, #0b8793, #1f3b4d); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 14;";
    }

    private String ghost() {
        return "-fx-background-color: transparent; -fx-border-color: #1f3b4d; -fx-text-fill: #1f3b4d; -fx-border-radius: 10; -fx-padding: 7 12;";
    }
}
