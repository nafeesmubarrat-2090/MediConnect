package myProject.medicine;

import myProject.ClientFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    public MedicineResultsPage(MedicineNavigator app, MedicineRepository repo, String medicineName) {
        this.app = app; this.repo = repo; this.medicineName = medicineName;
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();

        Label title = new Label("Results for \"" + medicineName + "\"");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#0b8793"));

        Label subtitle = new Label("Select your preferred shop, brand, and location.");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web("#4a6070"));

        Button back = new Button("Back");
        back.setStyle(ghost());
        Button filterBtn = new Button("Filters");
        filterBtn.setStyle(primary());
        Button helpBtn = new Button("Help");
        helpBtn.setStyle(ghost());

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actions = new HBox(8, back, spacer, filterBtn, helpBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(6, title, subtitle, actions);
        top.setPadding(new Insets(14, 16, 10, 16));
        root.setTop(top);

        TableColumn<SelectionRow, Boolean> selCol = new TableColumn<>("Select");
        selCol.setCellValueFactory(cd -> cd.getValue().selectedProperty());
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setPrefWidth(70);

        TableColumn<SelectionRow, String> formCol = new TableColumn<>("Dosage Form");
        formCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getDosageForm()));

        TableColumn<SelectionRow, String> strengthCol = new TableColumn<>("Strength");
        strengthCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getStrength()));

        TableColumn<SelectionRow, String> brandCol = new TableColumn<>("Manufacturer");
        brandCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getMedicine().getBrand()));

        TableColumn<SelectionRow, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.format("Tk %.2f", cd.getValue().getMedicine().getPrice())));

        TableColumn<SelectionRow, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cd -> cd.getValue().quantityProperty());
        qtyCol.setCellFactory(col -> new QtyCell());
        qtyCol.setPrefWidth(80);

        table.getColumns().addAll(selCol, formCol, strengthCol, brandCol, priceCol, qtyCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setStyle("-fx-background-color: transparent; -fx-padding: 6;");
        table.setEditable(true);
        selCol.setEditable(true);
        table.setRowFactory(tv -> {
            TableRow<SelectionRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 1) {
                    SelectionRow r = row.getItem();
                    r.setSelected(!r.isSelected());
                    table.refresh();
                }
            });
            return row;
        });

        VBox card = new VBox(table);
        card.setPadding(new Insets(10));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(14), Insets.EMPTY)));
        card.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);");
        BorderPane.setMargin(card, new Insets(8, 16, 14, 16));
        root.setCenter(card);

        Button buy = new Button("Proceed to Buy");
        buy.setStyle(primary());
        HBox bottom = new HBox(buy);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 16, 14, 16));
        root.setBottom(bottom);

        rebuildRows(null, null, null);
        table.setItems(rows);

        back.setOnAction(e -> app.showHome());
        filterBtn.setOnAction(e -> showFilterDialog());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());

        buy.setOnAction(e -> {
            List<SelectionRow> selected = rows.stream()
                    .filter(r -> r.isSelected() && r.getQuantity() > 0)
                    .collect(Collectors.toList());
            if (selected.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Select at least one item.").showAndWait();
                return;
            }
            app.showBuyPage(selected);
        });

        return root;
    }

    private void rebuildRows(String strength, String dosageForm, String brand) {
        rows.clear();
        repo.all().stream()
                .filter(m -> m.getName().equalsIgnoreCase(medicineName))
                .filter(m -> strength == null || Objects.equals(m.getStrength(), strength))
                .filter(m -> dosageForm == null || Objects.equals(m.getDosageForm(), dosageForm))
                .filter(m -> brand == null || Objects.equals(m.getBrand(), brand))
                .forEach(m -> rows.add(new SelectionRow(m)));
    }

    private void showFilterDialog() {
        Dialog<FilterResult> dlg = new Dialog<>();
        dlg.setTitle("Filter");
        ButtonType apply = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        ButtonType reset = new ButtonType("Reset", ButtonBar.ButtonData.LEFT);
        ButtonType cancel = ButtonType.CANCEL;
        dlg.getDialogPane().getButtonTypes().addAll(apply, reset, cancel);

        Set<String> strengths = repo.distinctLocations();
        Set<String> dosageForms = repo.distinctShops();
        Set<String> brands = repo.distinctBrands();

        ComboBox<String> strengthBox = new ComboBox<>(); strengthBox.getItems().add("All"); strengthBox.getItems().addAll(strengths);
        ComboBox<String> formBox = new ComboBox<>(); formBox.getItems().add("All"); formBox.getItems().addAll(dosageForms);
        ComboBox<String> brandBox = new ComboBox<>(); brandBox.getItems().add("All"); brandBox.getItems().addAll(brands);
        strengthBox.getSelectionModel().selectFirst();
        formBox.getSelectionModel().selectFirst();
        brandBox.getSelectionModel().selectFirst();

        GridPane gp = new GridPane(); gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Strength:"), strengthBox);
        gp.addRow(1, new Label("Dosage Form:"), formBox);
        gp.addRow(2, new Label("Manufacturer:"), brandBox);
        dlg.getDialogPane().setContent(gp);

        dlg.setResultConverter(bt -> {
            if (bt == apply) return new FilterResult(val(strengthBox), val(formBox), val(brandBox));
            if (bt == reset) return new FilterResult(null, null, null);
            return null;
        });

        FilterResult r = dlg.showAndWait().orElse(null);
        if (r != null) {
            rebuildRows(r.strength, r.dosageForm, r.brand);
        }
    }

    private static String val(ComboBox<String> cb) {
        String v = cb.getValue();
        return (v == null || v.equals("All")) ? null : v;
    }

    private record FilterResult(String strength, String dosageForm, String brand) { }

    private String primary() {
        return "-fx-background-color: linear-gradient(to right, #0b8793, #1f3b4d); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 14;";
    }

    private String ghost() {
        return "-fx-background-color: transparent; -fx-border-color: #1f3b4d; -fx-text-fill: #1f3b4d; -fx-border-radius: 10; -fx-padding: 7 12;";
    }

    private static class QtyCell extends TableCell<SelectionRow, Number> {
        private final javafx.scene.control.Spinner<Integer> spinner = new javafx.scene.control.Spinner<>(1, 99, 1);
        private SelectionRow current;

        public QtyCell() {
            spinner.setEditable(false);
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
