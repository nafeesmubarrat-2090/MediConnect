package myProject.medicine;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SelectionRow {
    private final Medicine medicine;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);

    public SelectionRow(Medicine medicine) {
        this.medicine = medicine;
    }

    public Medicine getMedicine() { return medicine; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean v) { selected.set(v); }
    public BooleanProperty selectedProperty() { return selected; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int q) { quantity.set(q); }
    public IntegerProperty quantityProperty() { return quantity; }
}
