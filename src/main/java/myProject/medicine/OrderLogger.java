package myProject.medicine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderLogger {
    private static final String LOG_FILE = "orders.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logOrder(List<SelectionRow> selections, String paymentMethod) throws OrderException {
        if (selections == null || selections.isEmpty()) {
            throw new OrderException("Cannot log empty order");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write("=== ORDER START ===\n");
            writer.write("Timestamp: " + LocalDateTime.now().format(formatter) + "\n");
            writer.write("Payment Method: " + paymentMethod + "\n");
            writer.write("Items:\n");

            double subtotal = 0;
            for (SelectionRow row : selections) {
                Medicine m = row.getMedicine();
                double lineTotal = m.getPrice() * row.getQuantity();
                subtotal += lineTotal;
                writer.write(String.format("  %dx %s (%s) from %s, %s - Tk %.2f\n",
                    row.getQuantity(), m.getName(), m.getBrand(),
                    m.getShop(), m.getLocation(), lineTotal));
            }

            double delivery = 10.0;
            double total = subtotal + delivery;
            writer.write(String.format("Subtotal: Tk %.2f\n", subtotal));
            writer.write(String.format("Delivery: Tk %.2f\n", delivery));
            writer.write(String.format("Total: Tk %.2f\n", total));
            writer.write("=== ORDER END ===\n\n");
            writer.flush();

        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to log order - " + e.getMessage());
            throw new OrderException("Order logging failed: " + e.getMessage(), e);
        }
    }

    public static String getLogFilePath() {
        return new File(LOG_FILE).getAbsolutePath();
    }
}
