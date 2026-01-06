package myProject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

import myProject.ambulance.AmbulanceServicePage;
import myProject.medicine.BuyPage;
import myProject.medicine.MedicineHomePage;
import myProject.medicine.MedicineNavigator;
import myProject.medicine.MedicineRepository;
import myProject.medicine.MedicineResultsPage;
import myProject.medicine.OrderSuccessPage;
import myProject.medicine.PaymentPage;
import myProject.medicine.SelectionRow;

public class AidAccessApp extends Application implements MedicineNavigator {
    private Stage stage;
    private BorderPane shell;
    private StackPane content;
    private MedicineRepository medicineRepository;

    private AmbulanceServicePage ambulancePage;
    private javafx.scene.Node doctorPlaceholder;
    private javafx.scene.Node landingView;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.medicineRepository = new MedicineRepository();
        this.ambulancePage = new AmbulanceServicePage();
        this.doctorPlaceholder = createDoctorPlaceholder();
        this.landingView = createLandingView();

        shell = new BorderPane();
        shell.setTop(buildNav());
        content = new StackPane();
        shell.setCenter(content);

        Scene scene = new Scene(shell, 1100, 720);
        primaryStage.setScene(scene);
        primaryStage.setTitle("AidAccess - One stop care");

        showLanding();
        primaryStage.show();
    }

    private HBox buildNav() {
        Label title = new Label("AidAccess");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button homeBtn = new Button("Home");
        Button doctorBtn = new Button("Doctor");
        Button medicineBtn = new Button("Medicines");
        Button ambulanceBtn = new Button("Ambulance");
        Button helpBtn = new Button("Help");
        homeBtn.setOnAction(e -> showLanding());
        doctorBtn.setOnAction(e -> showMediConnect());
        medicineBtn.setOnAction(e -> showMedicinesHome());
        ambulanceBtn.setOnAction(e -> showAmbulance());
        helpBtn.setOnAction(e -> ClientFX.openHelpChat());

        HBox nav = new HBox(10, title, homeBtn, doctorBtn, medicineBtn, ambulanceBtn);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        nav.getChildren().addAll(spacer, helpBtn);
        nav.setStyle("-fx-padding: 12; -fx-background-color: linear-gradient(to right, #0b8793, #1f3b4d); -fx-alignment: center-left;");
        homeBtn.setStyle(primaryButton());
        doctorBtn.setStyle(primaryButton());
        medicineBtn.setStyle(primaryButton());
        ambulanceBtn.setStyle(primaryButton());
        helpBtn.setStyle(ghostButton());
        return nav;
    }

    private String primaryButton() {
        return "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-padding: 8 14; -fx-background-radius: 8;";
    }

    private String ghostButton() {
        return "-fx-background-color: transparent; -fx-border-color: white; -fx-text-fill: white; -fx-padding: 8 12; -fx-background-radius: 8; -fx-border-radius: 8;";
    }

    private void setContent(javafx.scene.Node node) {
        content.getChildren().setAll(node);
    }

    private void showMedicinesHome() {
        setContent(new MedicineHomePage(this, medicineRepository).getView());
    }

    private void showAmbulance() {
        setContent(ambulancePage.getView());
    }

    private void showLanding() {
        setContent(landingView);
    }

    private void showMediConnect() {
        try {
            // Launch MediConnect in a new window
            com.example.mediconnect.MediConnectApp mediConnectApp = new com.example.mediconnect.MediConnectApp();
            Stage mediConnectStage = new Stage();
            mediConnectApp.start(mediConnectStage);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Fallback to placeholder if MediConnect fails to launch
            showErrorMessage("Error launching MediConnect: " + ex.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        Label headline = new Label("Error");
        headline.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #c53030;");
        Label subtitle = new Label(message);
        subtitle.setStyle("-fx-text-fill: #444;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(400);
        Button goHome = new Button("Go to Home");
        goHome.setStyle("-fx-background-color: #0b8793; -fx-text-fill: white; -fx-padding: 8 14; -fx-background-radius: 8;");
        goHome.setOnAction(e -> showLanding());

        VBox box = new VBox(12, headline, subtitle, goHome);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        setContent(box);
    }

    private void showDoctorPlaceholder() {
        setContent(doctorPlaceholder);
    }

    private javafx.scene.Node createDoctorPlaceholder() {
        Label headline = new Label("Doctor appointments coming soon");
        headline.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subtitle = new Label("Check back later or use Medicines and Ambulance in the meantime.");
        subtitle.setStyle("-fx-text-fill: #444;");
        Button goMedicines = new Button("Go to Medicines");
        goMedicines.setStyle("-fx-background-color: #0b8793; -fx-text-fill: white; -fx-padding: 8 14; -fx-background-radius: 8;");
        goMedicines.setOnAction(e -> showMedicinesHome());

        VBox box = new VBox(12, headline, subtitle, goMedicines);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private javafx.scene.Node createLandingView() {
        Label headline = new Label("AidAccess");
        headline.setFont(Font.font("Helvetica", FontWeight.BOLD, 32));
        headline.setTextFill(Color.web("#0b8793"));

        Label tagline = new Label("Choose the care you need");
        tagline.setFont(Font.font("Helvetica", 16));
        tagline.setTextFill(Color.web("#2c3e50"));

        HBox cards = new HBox(16,
                serviceCard("Doctor", "MediConnect - Doctor appointments", this::showMediConnect),
                serviceCard("Medicines", "Search, compare, and order meds", this::showMedicinesHome),
                serviceCard("Ambulance", "Request rapid dispatch", this::showAmbulance)
        );
        cards.setAlignment(Pos.CENTER);
        cards.setStyle("-fx-padding: 16;");

        VBox layout = new VBox(18, headline, tagline, cards);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #f6f9fc, #e9f3f7); -fx-padding: 28;");

        return layout;
    }

    private VBox serviceCard(String title, String subtitle, Runnable action) {
        Label t = new Label(title);
        t.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#1f3b4d"));

        Label sub = new Label(subtitle);
        sub.setFont(Font.font("Helvetica", 13));
        sub.setTextFill(Color.web("#4a6070"));
        sub.setWrapText(true);
        sub.setMaxWidth(220);

        Button go = new Button("Continue");
        go.setStyle("-fx-background-color: #0b8793; -fx-text-fill: white; -fx-padding: 8 14; -fx-background-radius: 8;");
        go.setOnAction(e -> action.run());

        VBox box = new VBox(10, t, sub, go);
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle("-fx-padding: 16; -fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4);");

        Rectangle accent = new Rectangle(46, 6, Color.web("#0b8793"));
        accent.setArcWidth(6);
        accent.setArcHeight(6);
        box.getChildren().add(0, accent);

        return box;
    }

    // MedicineNavigator implementation
    @Override
    public void showHome() {
        showMedicinesHome();
    }

    @Override
    public void showResults(String medicineName) {
        setContent(new MedicineResultsPage(this, medicineRepository, medicineName).getView());
    }

    @Override
    public void showBuyPage(List<SelectionRow> selections) {
        setContent(new BuyPage(this, selections).getView());
    }

    @Override
    public void showPaymentPage(List<SelectionRow> selections) {
        setContent(new PaymentPage(this, selections).getView());
    }

    @Override
    public void showOrderSuccess() {
        setContent(new OrderSuccessPage(this).getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
