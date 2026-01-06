package myProject.medicine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MedicineRepository {
    private final ObservableList<Medicine> all = FXCollections.observableArrayList();
    private static final String MED_EX_CSV = "/medex_medicines.csv";
    private static final String FALLBACK_CSV = "/medicines.csv";

    public MedicineRepository() {
        loadData();
    }

    public ObservableList<Medicine> all() { return all; }

    public List<String> distinctNames() {
        return all.stream()
            .map(Medicine::getName)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toList());
    }

    public Set<String> distinctLocations() {
        return new TreeSet<>(all.stream().map(Medicine::getLocation).collect(Collectors.toSet()));
    }

    public Set<String> distinctShops() {
        return new TreeSet<>(all.stream().map(Medicine::getShop).collect(Collectors.toSet()));
    }

    public Set<String> distinctBrands() {
        return new TreeSet<>(all.stream().map(Medicine::getBrand).collect(Collectors.toSet()));
    }

    private void loadData() {
        boolean loaded = loadFromFile(MED_EX_CSV, true);
        if (!loaded) {
            loaded = loadFromFile(FALLBACK_CSV, false);
        }

        if (!loaded) {
            System.err.println("Warning: No CSV data loaded. Using in-memory data.");
            seedInMemory();
        }
    }

    private boolean loadFromFile(String path, boolean isMedExFormat) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Warning: Could not find " + path + " in resources");
            return false;
        }

        int loadedRows = 0;
        int skippedRows = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            reader.readLine(); // skip header
            String pending = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (pending != null) {
                    line = pending + "\n" + line;
                }

                if (!quotesBalanced(line)) {
                    pending = line;
                    continue;
                }

                List<String> parts = parseCsvLine(line);
                pending = null;

                try {
                    if (isMedExFormat && parts.size() >= 6) {
                        // CSV format: title,dosage_form,manufacturer,strength,price,url
                        String name = normalizeName(parts.get(0));      // title
                        String dosageForm = parts.get(1).trim();         // dosage_form
                        String manufacturer = parts.get(2).trim();       // manufacturer
                        String strength = parts.get(3).trim();           // strength
                        double price = parsePrice(parts.get(4));         // price
                        // Map to Medicine(name, brand, shop, location, price)
                        all.add(new Medicine(name, manufacturer, dosageForm, strength, price));
                        loadedRows++;
                    } else if (!isMedExFormat && parts.size() == 5) {
                        String name = normalizeName(parts.get(0));
                        String brand = parts.get(1).trim();
                        String shop = parts.get(2).trim();
                        String location = parts.get(3).trim();
                        double price = Double.parseDouble(parts.get(4).trim());
                        all.add(new Medicine(name, brand, shop, location, price));
                        loadedRows++;
                    } else {
                        skippedRows++;
                    }
                } catch (NumberFormatException parseEx) {
                    skippedRows++;
                }
            }
            if (skippedRows > 0) {
                System.err.println("Notice: Skipped " + skippedRows + " rows while loading " + path + ". Loaded " + loadedRows + " rows.");
            }
            System.out.println("Loaded " + loadedRows + " medicines from " + path + ".");
            return loadedRows > 0;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: Could not load " + path + " (" + e.getMessage() + ").");
            return false;
        }
    }

    private boolean quotesBalanced(String s) {
        long cnt = s.chars().filter(c -> c == '"').count();
        return cnt % 2 == 0;
    }

    private List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                cols.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        cols.add(cur.toString());
        return cols;
    }

    private double parsePrice(String raw) {
        String cleaned = raw == null ? "" : raw.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) throw new NumberFormatException("Empty price value");
        return Double.parseDouble(cleaned);
    }

    private String normalizeName(String s) {
        if (s == null) return "";
        String normalized = s
                .replace('\u00A0', ' ')
                .replaceAll("[\\u2010-\\u2015\\u2212]", "-"); // normalize various dash characters to hyphen
        normalized = normalized.trim().replaceAll("\\s+", " ");
        return normalized;
    }

    private void seedInMemory() {
        String[] locations = {"Dhaka", "Dinajpur", "Chankharpul"};
        String[] shops = {"Shop X", "Shop Y", "Shop Z"};
        String[] brands = {"BrandA", "BrandB", "BrandC"};
        String[] meds = {"Paracetamol", "Ibuprofen", "Cetirizine", "Omeprazole", "Azithromycin"};

        double base = 5.0;
        for (String med : meds) {
            for (String loc : locations) {
                for (int i = 0; i < shops.length; i++) {
                    String shop = shops[i];
                    String brand = brands[(i + med.length()) % brands.length];
                    double price = base + (i * 1.25) + (med.length() % 3);
                    all.add(new Medicine(med, brand, shop, loc, price));
                }
            }
        }
    }
}
