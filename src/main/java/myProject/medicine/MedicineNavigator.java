package myProject.medicine;

import java.util.List;

public interface MedicineNavigator {
    void showHome();
    void showResults(String medicineName);
    void showBuyPage(List<SelectionRow> selections);
    void showPaymentPage(List<SelectionRow> selections);
    void showOrderSuccess();
}
