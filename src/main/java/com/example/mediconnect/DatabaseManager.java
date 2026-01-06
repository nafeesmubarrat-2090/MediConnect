package com.example.mediconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // MySQL connection settings - UPDATE PASSWORD IF NEEDED
    private static final String URL = "jdbc:mysql://localhost:3306/mediconnect";
    private static final String USER = "root";
    private static final String PASSWORD = "402090"; // Change this to your MySQL password

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS doctors (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                name VARCHAR(100) NOT NULL,
                sector VARCHAR(100) NOT NULL,
                qualifications VARCHAR(255) NOT NULL,
                experience VARCHAR(50) NOT NULL,
                age VARCHAR(10) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                email VARCHAR(100) NOT NULL,
                chamber VARCHAR(255) NOT NULL,
                location VARCHAR(100) NOT NULL,
                consultation_hours VARCHAR(100) NOT NULL,
                available_days VARCHAR(255) NOT NULL,
                gender VARCHAR(10) NOT NULL,
                fees VARCHAR(20) NOT NULL,
                hospital_affiliations VARCHAR(255) NOT NULL,
                online_consultation VARCHAR(10) NOT NULL,
                consultation_duration INT DEFAULT 30,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createReservationsTableSQL = """
            CREATE TABLE IF NOT EXISTS reservations (
                id INT AUTO_INCREMENT PRIMARY KEY,
                doctor_id INT NOT NULL,
                patient_name VARCHAR(100) NOT NULL,
                patient_age VARCHAR(10) NOT NULL,
                patient_gender VARCHAR(10) NOT NULL,
                patient_phone VARCHAR(20) NOT NULL,
                patient_email VARCHAR(100),
                past_complexities TEXT,
                reservation_date DATE NOT NULL,
                start_time TIME NOT NULL,
                end_time TIME NOT NULL,
                day_of_week VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
                UNIQUE KEY unique_slot (doctor_id, reservation_date, start_time)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.execute(createReservationsTableSQL);
            System.out.println("✅ Database tables initialized successfully!");

            // Check if consultation_duration column exists, if not add it
            try {
                String checkColumnSQL = """
                    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                    WHERE TABLE_SCHEMA = 'mediconnect' 
                    AND TABLE_NAME = 'doctors' 
                    AND COLUMN_NAME = 'consultation_duration'
                """;

                var rs = stmt.executeQuery(checkColumnSQL);
                if (rs.next() && rs.getInt(1) == 0) {
                    // Column doesn't exist, add it
                    String addColumnSQL = "ALTER TABLE doctors ADD COLUMN consultation_duration INT DEFAULT 30";
                    stmt.execute(addColumnSQL);
                    System.out.println("✅ Consultation duration column added successfully!");
                } else {
                    System.out.println("ℹ️ Consultation duration column already exists.");
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Error checking/adding consultation_duration column: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

