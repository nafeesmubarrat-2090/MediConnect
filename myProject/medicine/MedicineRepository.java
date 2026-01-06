package myProject.medicine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MedicineRepository {
    private final ObservableList<Medicine> all = FXCollections.observableArrayList();
    private static final String CSV_FILE = "/medex_medicines.csv";

    public MedicineRepository() {
        loadData();
    }

    public ObservableList<Medicine> all() { return all; }

    public List<String> distinctNames() {
        return all.stream().map(Medicine::getName)
                .distinct().sorted().collect(Collectors.toList());
    }

    public Set<String> distinctLocations() {
        return new TreeSet<>(all.stream().map(Medicine::getStrength).collect(Collectors.toSet()));
    }

    public Set<String> distinctShops() {
        return new TreeSet<>(all.stream().map(Medicine::getDosageForm).collect(Collectors.toSet()));
    }

    public Set<String> distinctBrands() {
        return new TreeSet<>(all.stream().map(Medicine::getBrand).collect(Collectors.toSet()));
    }

    private void loadData() {
        int count = 0;
        count += loadFromFile(CSV_FILE);

        if (count == 0) {
            System.err.println("Warning: No medicines loaded from CSV files. Using in-memory data.");
            seedInMemory();
        } else {
            System.out.println("Loaded " + count + " medicines from CSV files.");
        }
    }

    private int loadFromFile(String fileName) {
        int count = 0;
        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            if (is == null) {
                System.err.println("Warning: Could not find " + fileName + " in resources");
                return 0;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            // NO HEADER LINE - start reading immediately
            StringBuilder currentLine = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                currentLine.append(line);

                // Check if we have a complete record (ends with URL containing medex.com.bd)
                String fullLine = currentLine.toString();
                if (fullLine.contains("medex.com.bd")) {
                    Medicine med = parseMedicineLine(fullLine);
                    if (med != null) {
                        all.add(med);
                        count++;
                    }
                    currentLine = new StringBuilder();
                } else {
                    // Multi-line field - add space and continue
                    currentLine.append(" ");
                }
            }
            reader.close();
            System.out.println("Loaded " + count + " medicines from " + fileName);
        } catch (Exception e) {
            System.err.println("Warning: Could not load " + fileName + " (" + e.getMessage() + ")");
            e.printStackTrace();
        }
        return count;
    }

    private Medicine parseMedicineLine(String line) {
        try {
            // CSV format: title,dosage_form,manufacturer,strength,price,url
            // Split by comma, but handle quoted fields
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            if (parts.length >= 6) {
                String name = parts[0].trim();
                String dosageForm = parts[1].trim();
                String manufacturer = parts[2].trim();
                String strength = parts[3].trim();
                String priceStr = parts[4].trim().replace("\"", "");
                String url = parts[parts.length - 1].trim();

                // Parse price - extract number from "Unit Price : 12.00" or similar format
                double price = extractPrice(priceStr);

                return new Medicine(name, dosageForm, manufacturer, strength, price, url);
            }
        } catch (Exception e) {
            // Skip malformed lines
        }
        return null;
    }

    private double extractPrice(String priceStr) {
        // Extract numeric value from price string like "Unit Price :  12.00" or "100 ml bottle :       195.00"
        // For multi-line prices, find the last number (which is the actual price)
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+|\\d+)");
        Matcher matcher = pattern.matcher(priceStr);
        String lastNumber = null;
        while (matcher.find()) {
            lastNumber = matcher.group(1);
        }
        if (lastNumber != null) {
            try {
                return Double.parseDouble(lastNumber);
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse price from: " + priceStr);
            }
        }
        return 0.0;
    }

    private void seedInMemory() {
        String[] dosageForms = {"Tablet", "Capsule", "Syrup"};
        String[] manufacturers = {"Square Pharmaceuticals", "Beximco", "Incepta"};
        String[] strengths = {"500 mg", "250 mg", "100 mg"};
        String[] meds = {"Paracetamol", "Ibuprofen", "Cetirizine", "Omeprazole", "Azithromycin"};

        double base = 5.0;
        for (String med : meds) {
            for (int i = 0; i < dosageForms.length; i++) {
                String dosageForm = dosageForms[i];
                String manufacturer = manufacturers[i % manufacturers.length];
                String strength = strengths[i % strengths.length];
                double price = base + (i * 1.25) + (med.length() % 3);
                all.add(new Medicine(med, dosageForm, manufacturer, strength, price, ""));
            }
        }
    }
}
