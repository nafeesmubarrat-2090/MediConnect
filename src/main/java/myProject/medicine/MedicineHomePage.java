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
import javafx.scene.control.ListCell;
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
import javafx.scene.effect.DropShadow;

public class MedicineHomePage {
    private final MedicineNavigator app;
    private final MedicineRepository repo;

    // Professional color scheme
    private static final String PRIMARY_COLOR = "#0d9488";
    private static final String PRIMARY_DARK = "#0f766e";
    private static final String SECONDARY_COLOR = "#1e3a5f";
    private static final String TEXT_PRIMARY = "#1f2937";
    private static final String TEXT_SECONDARY = "#6b7280";
    private static final String BG_GRADIENT = "-fx-background-color: linear-gradient(to bottom right, #f0fdfa, #e0f2fe, #f0f9ff);";

    public MedicineHomePage(MedicineNavigator app, MedicineRepository repo) {
        this.app = app;
        this.repo = repo;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle(BG_GRADIENT);

        // ===== HEADER SECTION =====
        Label icon = new Label("💊");
        icon.setFont(Font.font("Segoe UI Emoji", 32));

        Label title = new Label("Medicine Finder");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY_COLOR));

        Label subtitle = new Label("Search from 37,000+ medicines • Compare prices • Order online");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web(TEXT_SECONDARY));

        VBox titleBox = new VBox(2, title, subtitle);
        HBox headerContent = new HBox(12, icon, titleBox);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        Button helpBtn = new Button("🆘 Help");
        helpBtn.setStyle(helpButtonStyle());
        helpBtn.setOnMouseEntered(e -> helpBtn.setStyle(helpButtonHoverStyle()));
        helpBtn.setOnMouseExited(e -> helpBtn.setStyle(helpButtonStyle()));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(10, headerContent, headerSpacer, helpBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 1 0;");

        // ===== SEARCH SECTION =====
        Label searchIcon = new Label("🔍");
        searchIcon.setFont(Font.font("Segoe UI Emoji", 16));

        TextField search = new TextField();
        search.setPromptText("Search medicine by name (e.g., Paracetamol, Napa, Amoxicillin)");
        search.setFont(Font.font("Segoe UI", 14));
        search.setMinHeight(44);
        search.setStyle(searchFieldStyle());
        HBox.setHgrow(search, Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(primaryButtonStyle());
        searchBtn.setMinHeight(44);
        searchBtn.setMinWidth(100);
        searchBtn.setOnMouseEntered(e -> searchBtn.setStyle(primaryButtonHoverStyle()));
        searchBtn.setOnMouseExited(e -> searchBtn.setStyle(primaryButtonStyle()));

        Button filterBtn = new Button("⚙ Filter");
        filterBtn.setStyle(secondaryButtonStyle());
        filterBtn.setMinHeight(44);
        filterBtn.setOnMouseEntered(e -> filterBtn.setStyle(secondaryButtonHoverStyle()));
        filterBtn.setOnMouseExited(e -> filterBtn.setStyle(secondaryButtonStyle()));

        HBox searchRow = new HBox(12, searchIcon, search, searchBtn, filterBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.setPadding(new Insets(20, 24, 16, 24));
        searchRow.setStyle("-fx-background-color: white;");

        // Add shadow to search section
        DropShadow searchShadow = new DropShadow();
        searchShadow.setColor(Color.rgb(0, 0, 0, 0.08));
        searchShadow.setRadius(8);
        searchShadow.setOffsetY(2);
        searchRow.setEffect(searchShadow);

        VBox topSection = new VBox(0, header, searchRow);
        root.setTop(topSection);

        // ===== MEDICINE LIST SECTION =====
        Label listTitle = new Label("📋 All Available Medicines");
        listTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        listTitle.setTextFill(Color.web(TEXT_PRIMARY));

        Label listSubtitle = new Label("Click on any medicine to view details and purchase options");
        listSubtitle.setFont(Font.font("Segoe UI", 12));
        listSubtitle.setTextFill(Color.web(TEXT_SECONDARY));

        VBox listHeader = new VBox(4, listTitle, listSubtitle);
        listHeader.setPadding(new Insets(0, 0, 12, 0));

        ObservableList<String> names = FXCollections.observableArrayList(repo.distinctNames());
        ListView<String> list = new ListView<>(names);
        list.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        list.setCellFactory(lv -> new MedicineListCell());
        VBox.setVgrow(list, Priority.ALWAYS);

        // Stats bar
        Label statsLabel = new Label("📊 Total: " + names.size() + " medicines available");
        statsLabel.setFont(Font.font("Segoe UI", 12));
        statsLabel.setTextFill(Color.web(TEXT_SECONDARY));
        statsLabel.setPadding(new Insets(12, 0, 0, 0));

        VBox card = new VBox(8, listHeader, list, statsLabel);
        card.setPadding(new Insets(20));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(16), Insets.EMPTY)));

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        cardShadow.setRadius(20);
        cardShadow.setOffsetY(4);
        card.setEffect(cardShadow);

        BorderPane.setMargin(card, new Insets(20, 24, 24, 24));
        root.setCenter(card);

        // ===== EVENT HANDLERS =====
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

    // Custom list cell for professional medicine list
    private static class MedicineListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                Label nameLabel = new Label("💊 " + item);
                nameLabel.setFont(Font.font("Segoe UI", 14));
                nameLabel.setTextFill(Color.web(TEXT_PRIMARY));

                Label arrowLabel = new Label("→");
                arrowLabel.setFont(Font.font("Segoe UI", 14));
                arrowLabel.setTextFill(Color.web(TEXT_SECONDARY));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox content = new HBox(8, nameLabel, spacer, arrowLabel);
                content.setAlignment(Pos.CENTER_LEFT);
                content.setPadding(new Insets(12, 16, 12, 16));

                setGraphic(content);
                setText(null);
                setStyle("-fx-background-color: transparent; -fx-padding: 2 0;");

                // Hover effect
                setOnMouseEntered(e -> setStyle("-fx-background-color: #f0fdfa; -fx-background-radius: 8; -fx-padding: 2 0;"));
                setOnMouseExited(e -> setStyle("-fx-background-color: transparent; -fx-padding: 2 0;"));
            }
        }
    }

    private void showHomeFilterDialog(ListView<String> list) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("🔍 Quick Filter");
        dlg.setHeaderText("Filter medicines by name");
        ButtonType close = new ButtonType("Apply Filter", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(close, ButtonType.CANCEL);

        TextField startsWith = new TextField();
        startsWith.setPromptText("Enter starting letters...");
        startsWith.setStyle(searchFieldStyle());
        startsWith.setMinHeight(40);

        Label hint = new Label("💡 Tip: Enter 'A' to show all medicines starting with A");
        hint.setFont(Font.font("Segoe UI", 11));
        hint.setTextFill(Color.web(TEXT_SECONDARY));

        VBox box = new VBox(12, new Label("Medicine name starts with:"), startsWith, hint);
        box.setPadding(new Insets(20));
        dlg.getDialogPane().setContent(box);
        dlg.getDialogPane().setMinWidth(400);

        Node ok = dlg.getDialogPane().lookupButton(close);
        ok.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            String p = startsWith.getText() == null ? "" : startsWith.getText().trim().toLowerCase();
            list.getItems().setAll(repo.distinctNames().stream()
                    .filter(n -> p.isEmpty() || n.toLowerCase().startsWith(p))
                    .toList());
        });

        dlg.showAndWait();
    }

    // ===== STYLE METHODS =====
    private String primaryButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, " + PRIMARY_COLOR + ", " + PRIMARY_DARK + ");" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;";
    }

    private String primaryButtonHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, " + PRIMARY_DARK + ", #064e3b);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;";
    }

    private String secondaryButtonStyle() {
        return "-fx-background-color: white; -fx-text-fill: " + SECONDARY_COLOR + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #d1d5db;" +
               "-fx-padding: 10 16; -fx-cursor: hand;";
    }

    private String secondaryButtonHoverStyle() {
        return "-fx-background-color: #f9fafb; -fx-text-fill: " + SECONDARY_COLOR + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #9ca3af;" +
               "-fx-padding: 10 16; -fx-cursor: hand;";
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

    private String searchFieldStyle() {
        return "-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-border-radius: 8;" +
               "-fx-border-color: #e5e7eb; -fx-padding: 0 14; -fx-font-family: 'Segoe UI';";
    }
}
