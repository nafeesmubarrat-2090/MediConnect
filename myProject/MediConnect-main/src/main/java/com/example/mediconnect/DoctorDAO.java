package com.example.mediconnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /**
     * Register a new doctor in the database
     */
    public boolean registerDoctor(Doctor doctor) {
        String sql = """
            INSERT INTO doctors (username, password, name, sector, qualifications, 
                               experience, age, phone, email, chamber, location, 
                               consultation_hours, available_days, gender, fees, 
                               hospital_affiliations, online_consultation, consultation_duration)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getUsername());
            pstmt.setString(2, doctor.getPassword());
            pstmt.setString(3, doctor.getName());
            pstmt.setString(4, doctor.getSector());
            pstmt.setString(5, doctor.getQualifications());
            pstmt.setString(6, doctor.getExperience());
            pstmt.setString(7, doctor.getAge());
            pstmt.setString(8, doctor.getPhone());
            pstmt.setString(9, doctor.getEmail());
            pstmt.setString(10, doctor.getChamber());
            pstmt.setString(11, doctor.getLocation());
            pstmt.setString(12, doctor.getConsultationHours());
            pstmt.setString(13, doctor.getAvailableDays());
            pstmt.setString(14, doctor.getGender());
            pstmt.setString(15, doctor.getFees());
            pstmt.setString(16, doctor.getHospitalAffiliations());
            pstmt.setString(17, doctor.getOnlineConsultation());
            pstmt.setInt(18, doctor.getConsultationDuration());

            pstmt.executeUpdate();
            System.out.println("✅ Doctor registered successfully: " + doctor.getUsername());
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error registering doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticate doctor login
     */
    public Doctor authenticateDoctor(String username, String password) {
        String sql = "SELECT * FROM doctors WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Try to get consultation_duration, default to 30 if column doesn't exist
                int consultationDuration = 30;
                try {
                    consultationDuration = rs.getInt("consultation_duration");
                } catch (SQLException e) {
                    System.out.println("⚠️ consultation_duration column not found, using default value 30");
                }

                Doctor doctor = new Doctor(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("sector"),
                    rs.getString("qualifications"),
                    rs.getString("experience"),
                    rs.getString("age"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("chamber"),
                    rs.getString("location"),
                    rs.getString("consultation_hours"),
                    rs.getString("available_days"),
                    rs.getString("gender"),
                    rs.getString("fees"),
                    rs.getString("hospital_affiliations"),
                    rs.getString("online_consultation"),
                    consultationDuration
                );
                doctor.setId(rs.getInt("id"));
                return doctor;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error authenticating doctor: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if username already exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking username: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update doctor information
     */
    public boolean updateDoctor(Doctor doctor) {
        String sql = """
            UPDATE doctors SET password = ?, name = ?, sector = ?, qualifications = ?,
                             experience = ?, age = ?, phone = ?, email = ?, chamber = ?,
                             location = ?, consultation_hours = ?, available_days = ?,
                             gender = ?, fees = ?, hospital_affiliations = ?,
                             online_consultation = ?, consultation_duration = ?
            WHERE username = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getPassword());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getSector());
            pstmt.setString(4, doctor.getQualifications());
            pstmt.setString(5, doctor.getExperience());
            pstmt.setString(6, doctor.getAge());
            pstmt.setString(7, doctor.getPhone());
            pstmt.setString(8, doctor.getEmail());
            pstmt.setString(9, doctor.getChamber());
            pstmt.setString(10, doctor.getLocation());
            pstmt.setString(11, doctor.getConsultationHours());
            pstmt.setString(12, doctor.getAvailableDays());
            pstmt.setString(13, doctor.getGender());
            pstmt.setString(14, doctor.getFees());
            pstmt.setString(15, doctor.getHospitalAffiliations());
            pstmt.setString(16, doctor.getOnlineConsultation());
            pstmt.setInt(17, doctor.getConsultationDuration());
            pstmt.setString(18, doctor.getUsername());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Doctor updated successfully: " + doctor.getUsername());
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all doctors (useful for patient search feature later)
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Try to get consultation_duration, default to 30 if column doesn't exist
                int consultationDuration = 30;
                try {
                    consultationDuration = rs.getInt("consultation_duration");
                } catch (SQLException ex) {
                    // Column doesn't exist, use default
                }

                Doctor doctor = new Doctor(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("sector"),
                    rs.getString("qualifications"),
                    rs.getString("experience"),
                    rs.getString("age"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("chamber"),
                    rs.getString("location"),
                    rs.getString("consultation_hours"),
                    rs.getString("available_days"),
                    rs.getString("gender"),
                    rs.getString("fees"),
                    rs.getString("hospital_affiliations"),
                    rs.getString("online_consultation"),
                    consultationDuration
                );
                doctor.setId(rs.getInt("id"));
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching doctors: " + e.getMessage());
            e.printStackTrace();
        }

        return doctors;
    }



    /**
     * Advanced flexible search for patient portal.
     * Any parameter may be null or blank to indicate no filtering on that field.
     */
    public List<Doctor> searchDoctors(String sectorFilter, String locationFilter, String onlineFilter) {
        List<Doctor> doctors = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM doctors WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (sectorFilter != null && !sectorFilter.trim().isEmpty()) {
            sql.append(" AND sector = ?");
            params.add(sectorFilter.trim());
        }
        if (locationFilter != null && !locationFilter.trim().isEmpty()) {
            sql.append(" AND location = ?");
            params.add(locationFilter.trim());
        }
        if (onlineFilter != null && (onlineFilter.equalsIgnoreCase("Yes") || onlineFilter.equalsIgnoreCase("No"))) {
            sql.append(" AND online_consultation = ?");
            params.add(onlineFilter);
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Try to get consultation_duration, default to 30 if column doesn't exist
                    int consultationDuration = 30;
                    try {
                        consultationDuration = rs.getInt("consultation_duration");
                    } catch (SQLException ex) {
                        // Column doesn't exist, use default
                    }

                    Doctor doctor = new Doctor(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("sector"),
                        rs.getString("qualifications"),
                        rs.getString("experience"),
                        rs.getString("age"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("chamber"),
                        rs.getString("location"),
                        rs.getString("consultation_hours"),
                        rs.getString("available_days"),
                        rs.getString("gender"),
                        rs.getString("fees"),
                        rs.getString("hospital_affiliations"),
                        rs.getString("online_consultation"),
                        consultationDuration
                    );
                    doctor.setId(rs.getInt("id"));
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error performing patient search: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    /**
     * Get all unique sectors from the database
     */
    public List<String> getUniqueSectors() {
        List<String> sectors = new ArrayList<>();
        String sql = "SELECT DISTINCT sector FROM doctors ORDER BY sector";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String sector = rs.getString("sector");
                if (sector != null && !sector.trim().isEmpty()) {
                    sectors.add(sector);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching unique sectors: " + e.getMessage());
            e.printStackTrace();
        }

        return sectors;
    }

    /**
     * Get all unique locations from the database
     */
    public List<String> getUniqueLocations() {
        List<String> locations = new ArrayList<>();
        String sql = "SELECT DISTINCT location FROM doctors ORDER BY location";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String location = rs.getString("location");
                if (location != null && !location.trim().isEmpty()) {
                    locations.add(location);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching unique locations: " + e.getMessage());
            e.printStackTrace();
        }

        return locations;
    }
}
