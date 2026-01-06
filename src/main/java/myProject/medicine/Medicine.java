package myProject.medicine;

public class Medicine {
    private final String name;
    private final String brand;
    private final String shop;
    private final String location;
    private final double price;

    public Medicine(String name, String brand, String shop, String location, double price) {
        this.name = name;
        this.brand = brand;
        this.shop = shop;
        this.location = location;
        this.price = price;
    }

    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getShop() { return shop; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (" + brand + ") - " + shop + ", " + location + " - $" + String.format("%.2f", price);
    }
}
