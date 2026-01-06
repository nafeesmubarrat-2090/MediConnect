package myProject.medicine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class MedicineApp extends Application implements MedicineNavigator {
    private Stage stage;
    private Scene scene;
    private MedicineRepository repo;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.repo = new MedicineRepository();

        scene = new Scene(new MedicineHomePage(this, repo).getView(), 900, 600);
        stage.setTitle("AidAccess - Medicines");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void showHome() {
        scene.setRoot(new MedicineHomePage(this, repo).getView());
    }

    @Override
    public void showResults(String medicineName) {
        scene.setRoot(new MedicineResultsPage(this, repo, medicineName).getView());
    }

    @Override
    public void showBuyPage(List<SelectionRow> selections) {
        scene.setRoot(new BuyPage(this, selections).getView());
    }

    @Override
    public void showPaymentPage(List<SelectionRow> selections) {
        scene.setRoot(new PaymentPage(this, selections).getView());
    }

    @Override
    public void showOrderSuccess() {
        scene.setRoot(new OrderSuccessPage(this).getView());
    }

    public static void main(String[] args) { launch(args); }
}
