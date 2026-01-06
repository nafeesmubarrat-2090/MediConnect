package myProject.medicine;

public class Medicine {
    private final String name;
    private final String dosageForm;
    private final String brand;       // manufacturer
    private final String strength;
    private final double price;
    private final String url;

    public Medicine(String name, String dosageForm, String brand, String strength, double price, String url) {
        this.name = name;
        this.dosageForm = dosageForm;
        this.brand = brand;
        this.strength = strength;
        this.price = price;
        this.url = url;
    }

    public String getName() { return name; }
    public String getDosageForm() { return dosageForm; }
    public String getBrand() { return brand; }
    public String getStrength() { return strength; }
    public double getPrice() { return price; }
    public String getUrl() { return url; }

    // For backward compatibility with existing code
    public String getShop() { return dosageForm; }
    public String getLocation() { return strength; }

    @Override
    public String toString() {
        return name + " (" + dosageForm + ") - " + brand + ", " + strength + " - ৳" + String.format("%.2f", price);
    }
}
