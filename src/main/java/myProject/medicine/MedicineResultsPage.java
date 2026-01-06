package myProject.medicine;

import myProject.ClientFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MedicineResultsPage {
    private final MedicineNavigator app;
    private final MedicineRepository repo;
    private final String medicineName;

    private final TableView<SelectionRow> table = new TableView<>();
    private final ObservableList<SelectionRow> rows = FXCollections.observableArrayList();
    private Label resultsCount;

    // Professional color scheme
    private static final String PRIMARY_COLOR = "#0d9488";
    private static final String PRIMARY_DARK = "#0f766e";
    private static final String SECONDARY_COLOR = "#1e3a5f";
    private static final String TEXT_PRIMARY = "#1f2937";
    private static final String TEXT_SECONDARY = "#6b7280";
    private static final String SUCCESS_COLOR = "#059669";
    private static final String BG_GRADIENT = "-fx-background-color: linear-gradient(to bottom right, #f0fdfa, #e0f2fe, #f0f9ff);";

    public MedicineResultsPage(MedicineNavigator app, MedicineRepository repo, String medicineName) {
        this.app = app; this.repo = repo; this.medicineName = medicineName;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle(BG_GRADIENT);

        // ===== HEADER SECTION =====
        Button backBtn = new Button("← Back");
        backBtn.setStyle(backButtonStyle());
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backButtonHoverStyle()));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(backButtonStyle()));

        Label icon = new Label("🔬");
        icon.setFont(Font.font("Segoe UI Emoji", 28));

        Label title = new Label("Search Results");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(PRIMARY_COLOR));

        Label searchQuery = new Label("\"" + medicineName + "\"");
        searchQuery.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        searchQuery.setTextFill(Color.web(SECONDARY_COLOR));
        searchQuery.setStyle("-fx-background-color: #e0f2fe; -fx-padding: 4 12; -fx-background-radius: 12;");

        resultsCount = new Label();
        resultsCount.setFont(Font.font("Segoe UI", 12));
        resultsCount.setTextFill(Color.web(TEXT_SECONDARY));

        VBox titleBox = new VBox(4,
            new HBox(8, title, searchQuery),
            resultsCount
        );

        HBox leftHeader = new HBox(12, backBtn, icon, titleBox);
        leftHeader.setAlignment(Pos.CENTER_LEFT);

        Button filterBtn = new Button("🔧 Filters");
        filterBtn.setStyle(filterButtonStyle());
        filterBtn.setOnMouseEntered(e -> filterBtn.setStyle(filterButtonHoverStyle()));
        filterBtn.setOnMouseExited(e -> filterBtn.setStyle(filterButtonStyle()));

        Button helpBtn = new Button("🆘 Help");
        helpBtn.setStyle(helpButtonStyle());
        helpBtn.setOnMouseEntered(e -> helpBtn.setStyle(helpButtonHoverStyle()));
        helpBtn.setOnMouseExited(e -> helpBtn.setStyle(helpButtonStyle()));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(12, leftHeader, headerSpacer, filterBtn, helpBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 1 0;");

        root.setTop(header);

        // ===== TABLE SECTION =====
        Label tableTitle = new Label("📋 Available Options");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        tableTitle.setTextFill(Color.web(TEXT_PRIMARY));

        Label tableSubtitle = new Label("Select medicines to add to your cart. Use checkboxes or click rows to select.");
        tableSubtitle.setFont(Font.font("Segoe UI", 12));
        tableSubtitle.setTextFill(Color.web(TEXT_SECONDARY));

        VBox tableHeader = new VBox(4, tableTitle, tableSubtitle);
        tableHeader.setPadding(new Insets(0, 0, 16, 0));

        // Configure table columns
        TableColumn<SelectionRow, Boolean> selCol = new TableColumn<>("✓");
        selCol.setCellValueFactory(cd -> cd.getValue().selectedProperty());
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setPrefWidth(50);
        selCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SelectionRow, String> nameCol = new TableColumn<>("Medicine Name");
        nameCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getName()));
        nameCol.setPrefWidth(180);

        TableColumn<SelectionRow, String> formCol = new TableColumn<>("Dosage Form");
        formCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getShop()));

        TableColumn<SelectionRow, String> strengthCol = new TableColumn<>("Strength");
        strengthCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getLocation()));

        TableColumn<SelectionRow, String> brandCol = new TableColumn<>("Manufacturer");
        brandCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getBrand()));

        TableColumn<SelectionRow, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.format("৳ %.2f", cd.getValue().getMedicine().getPrice())));
        priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<SelectionRow, Number> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(cd -> cd.getValue().quantityProperty());
        qtyCol.setCellFactory(col -> new QtyCell());
        qtyCol.setPrefWidth(100);

        table.getColumns().addAll(selCol, nameCol, formCol, strengthCol, brandCol, priceCol, qtyCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setStyle(tableStyle());
        table.setEditable(true);
        selCol.setEditable(true);

        table.setRowFactory(tv -> {
            TableRow<SelectionRow> row = new TableRow<>() {
                @Override
                protected void updateItem(SelectionRow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && item.isSelected()) {
                        setStyle("-fx-background-color: #d1fae5;");
                    } else {
                        setStyle("");
                    }
                }
            };
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 1) {
                    SelectionRow r = row.getItem();
                    r.setSelected(!r.isSelected());
                    table.refresh();
                }
            });
            return row;
        });

        VBox.setVgrow(table, Priority.ALWAYS);

        VBox card = new VBox(8, tableHeader, table);
        card.setPadding(new Insets(20));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(16), Insets.EMPTY)));

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        cardShadow.setRadius(20);
        cardShadow.setOffsetY(4);
        card.setEffect(cardShadow);

        BorderPane.setMargin(card, new Insets(20, 24, 16, 24));
        root.setCenter(card);

        // ===== BOTTOM ACTION BAR =====
        Label cartInfo = new Label("🛒 Select items and proceed to checkout");
        cartInfo.setFont(Font.font("Segoe UI", 13));
        cartInfo.setTextFill(Color.web(TEXT_SECONDARY));

        Button buyBtn = new Button("🛍 Proceed to Buy");
        buyBtn.setStyle(buyButtonStyle());
        buyBtn.setMinHeight(44);
        buyBtn.setMinWidth(180);
        buyBtn.setOnMouseEntered(e -> buyBtn.setStyle(buyButtonHoverStyle()));
        buyBtn.setOnMouseExited(e -> buyBtn.setStyle(buyButtonStyle()));

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        HBox bottom = new HBox(16, cartInfo, bottomSpacer, buyBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 24, 20, 24));
        bottom.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1 0 0 0;");
        root.setBottom(bottom);

        // ===== LOAD DATA & EVENTS =====
        rebuildRows(null, null, null);
        table.setItems(rows);

        backBtn.setOnAction(e -> app.showHome());
        filterBtn.setOnAction(e -> showFilterDialog());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());

        buyBtn.setOnAction(e -> {
            List<SelectionRow> selected = rows.stream()
                    .filter(r -> r.isSelected() && r.getQuantity() > 0)
                    .collect(Collectors.toList());
            if (selected.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Items Selected");
                alert.setHeaderText("Please select at least one item");
                alert.setContentText("Use the checkboxes or click on rows to select medicines for purchase.");
                alert.showAndWait();
                return;
            }
            app.showBuyPage(selected);
        });

        return root;
    }

    private void rebuildRows(String location, String shop, String brand) {
        rows.clear();
        String q = medicineName;
        repo.all().stream()
                .filter(m -> nameMatches(m.getName(), q))
                .filter(m -> location == null || Objects.equals(m.getLocation(), location))
                .filter(m -> shop == null || Objects.equals(m.getShop(), shop))
                .filter(m -> brand == null || Objects.equals(m.getBrand(), brand))
                .forEach(m -> rows.add(new SelectionRow(m)));

        resultsCount.setText("📊 Found " + rows.size() + " matching options");
    }

    private boolean nameMatches(String candidate, String query) {
        String c = normalize(candidate);
        String q = normalize(query);
        if (c.equalsIgnoreCase(q)) return true;
        return c.contains(q.toLowerCase()) || q.contains(c.toLowerCase());
    }

    private String normalize(String s) {
        if (s == null) return "";
        String normalized = s
            .replace('\u00A0', ' ')
            .replaceAll("[\\u2010-\\u2015\\u2212]", "-");
        normalized = normalized.trim().replaceAll("\\s+", " ");
        return normalized.toLowerCase();
    }

    private void showFilterDialog() {
        Dialog<FilterResult> dlg = new Dialog<>();
        dlg.setTitle("🔧 Filter Options");
        dlg.setHeaderText("Narrow down your search results");

        ButtonType apply = new ButtonType("Apply Filters", ButtonBar.ButtonData.OK_DONE);
        ButtonType reset = new ButtonType("Reset All", ButtonBar.ButtonData.LEFT);
        dlg.getDialogPane().getButtonTypes().addAll(apply, reset, ButtonType.CANCEL);

        Set<String> strengths = repo.distinctLocations();
        Set<String> forms = repo.distinctShops();
        Set<String> brands = repo.distinctBrands();

        ComboBox<String> strengthBox = new ComboBox<>();
        strengthBox.getItems().add("All Strengths");
        strengthBox.getItems().addAll(strengths);
        strengthBox.getSelectionModel().selectFirst();
        strengthBox.setMinWidth(250);

        ComboBox<String> formBox = new ComboBox<>();
        formBox.getItems().add("All Dosage Forms");
        formBox.getItems().addAll(forms);
        formBox.getSelectionModel().selectFirst();
        formBox.setMinWidth(250);

        ComboBox<String> brandBox = new ComboBox<>();
        brandBox.getItems().add("All Manufacturers");
        brandBox.getItems().addAll(brands);
        brandBox.getSelectionModel().selectFirst();
        brandBox.setMinWidth(250);

        GridPane gp = new GridPane();
        gp.setHgap(16);
        gp.setVgap(16);
        gp.setPadding(new Insets(20));

        gp.addRow(0, createFilterLabel("💪 Strength:"), strengthBox);
        gp.addRow(1, createFilterLabel("💊 Dosage Form:"), formBox);
        gp.addRow(2, createFilterLabel("🏭 Manufacturer:"), brandBox);

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().setMinWidth(450);

        dlg.setResultConverter(bt -> {
            if (bt == apply) return new FilterResult(
                val(strengthBox, "All Strengths"),
                val(formBox, "All Dosage Forms"),
                val(brandBox, "All Manufacturers")
            );
            if (bt == reset) return new FilterResult(null, null, null);
            return null;
        });

        FilterResult r = dlg.showAndWait().orElse(null);
        if (r != null) {
            rebuildRows(r.location, r.shop, r.brand);
        }
    }

    private Label createFilterLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web(TEXT_PRIMARY));
        return label;
    }

    private static String val(ComboBox<String> cb, String allValue) {
        String v = cb.getValue();
        return (v == null || v.equals(allValue)) ? null : v;
    }

    private record FilterResult(String location, String shop, String brand) { }

    // ===== STYLE METHODS =====
    private String tableStyle() {
        return "-fx-background-color: transparent; -fx-padding: 0;" +
               "-fx-table-cell-border-color: #f3f4f6;";
    }

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

    private String filterButtonStyle() {
        return "-fx-background-color: white; -fx-text-fill: " + SECONDARY_COLOR + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;" +
               "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #d1d5db;" +
               "-fx-padding: 8 16; -fx-cursor: hand;";
    }

    private String filterButtonHoverStyle() {
        return "-fx-background-color: #f9fafb; -fx-text-fill: " + SECONDARY_COLOR + ";" +
               "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;" +
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

    private String buyButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, " + SUCCESS_COLOR + ", #047857);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 10; -fx-padding: 12 24; -fx-cursor: hand;";
    }

    private String buyButtonHoverStyle() {
        return "-fx-background-color: linear-gradient(to right, #047857, #065f46);" +
               "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 14px;" +
               "-fx-background-radius: 10; -fx-padding: 12 24; -fx-cursor: hand;";
    }

    // ===== QUANTITY CELL =====
    private static class QtyCell extends TableCell<SelectionRow, Number> {
        private final javafx.scene.control.Spinner<Integer> spinner = new javafx.scene.control.Spinner<>(1, 99, 1);
        private SelectionRow current;

        public QtyCell() {
            spinner.setEditable(false);
            spinner.setStyle("-fx-font-family: 'Segoe UI';");
            spinner.setPrefWidth(80);
            spinner.valueProperty().addListener((obs, o, n) -> {
                if (current != null) current.setQuantity(n);
            });
        }

        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                current = null;
            } else {
                current = getTableView().getItems().get(getIndex());
                spinner.getValueFactory().setValue(current.getQuantity());
                setGraphic(spinner);
            }
        }
    }
}
