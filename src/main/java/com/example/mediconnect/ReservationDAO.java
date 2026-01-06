

package com.example.mediconnect;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // Create a new reservation
    public boolean createReservation(Reservation reservation) {
        String sql = """
            INSERT INTO reservations (doctor_id, patient_name, patient_age, patient_gender, 
                                     patient_phone, patient_email, past_complexities, 
                                     reservation_date, start_time, end_time, day_of_week)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservation.getDoctorId());
            pstmt.setString(2, reservation.getPatientName());
            pstmt.setString(3, reservation.getPatientAge());
            pstmt.setString(4, reservation.getPatientGender());
            pstmt.setString(5, reservation.getPatientPhone());
            pstmt.setString(6, reservation.getPatientEmail());
            pstmt.setString(7, reservation.getPastComplexities());
            pstmt.setDate(8, Date.valueOf(reservation.getReservationDate()));
            pstmt.setTime(9, Time.valueOf(reservation.getStartTime()));
            pstmt.setTime(10, Time.valueOf(reservation.getEndTime()));
            pstmt.setString(11, reservation.getDayOfWeek());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get all reservations for a specific doctor
    public List<Reservation> getReservationsByDoctorId(int doctorId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
            SELECT * FROM reservations 
            WHERE doctor_id = ? 
            ORDER BY reservation_date, start_time
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctorId(rs.getInt("doctor_id"));
                reservation.setPatientName(rs.getString("patient_name"));
                reservation.setPatientAge(rs.getString("patient_age"));
                reservation.setPatientGender(rs.getString("patient_gender"));
                reservation.setPatientPhone(rs.getString("patient_phone"));
                reservation.setPatientEmail(rs.getString("patient_email"));
                reservation.setPastComplexities(rs.getString("past_complexities"));
                reservation.setReservationDate(rs.getDate("reservation_date").toLocalDate());
                reservation.setStartTime(rs.getTime("start_time").toLocalTime());
                reservation.setEndTime(rs.getTime("end_time").toLocalTime());
                reservation.setDayOfWeek(rs.getString("day_of_week"));

                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.err.println("Error getting reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    // Get reservation for a specific time slot
    public Reservation getReservationBySlot(int doctorId, LocalDate date, LocalTime startTime) {
        String sql = """
            SELECT * FROM reservations 
            WHERE doctor_id = ? AND reservation_date = ? AND start_time = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setTime(3, Time.valueOf(startTime));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctorId(rs.getInt("doctor_id"));
                reservation.setPatientName(rs.getString("patient_name"));
                reservation.setPatientAge(rs.getString("patient_age"));
                reservation.setPatientGender(rs.getString("patient_gender"));
                reservation.setPatientPhone(rs.getString("patient_phone"));
                reservation.setPatientEmail(rs.getString("patient_email"));
                reservation.setPastComplexities(rs.getString("past_complexities"));
                reservation.setReservationDate(rs.getDate("reservation_date").toLocalDate());
                reservation.setStartTime(rs.getTime("start_time").toLocalTime());
                reservation.setEndTime(rs.getTime("end_time").toLocalTime());
                reservation.setDayOfWeek(rs.getString("day_of_week"));

                return reservation;
            }

        } catch (SQLException e) {
            System.err.println("Error getting reservation by slot: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Check if a time slot is available
    public boolean isSlotAvailable(int doctorId, LocalDate date, LocalTime startTime) {
        String sql = """
            SELECT COUNT(*) FROM reservations 
            WHERE doctor_id = ? AND reservation_date = ? AND start_time = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setTime(3, Time.valueOf(startTime));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking slot availability: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Delete past reservations (cleanup old data)
    public void deletePastReservations() {
        String sql = """
            DELETE FROM reservations 
            WHERE CONCAT(reservation_date, ' ', end_time) < NOW()
        """;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsDeleted = stmt.executeUpdate(sql);
            if (rowsDeleted > 0) {
                System.out.println("✅ Deleted " + rowsDeleted + " past reservations.");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting past reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Delete a specific reservation
    public boolean deleteReservation(int reservationId) {
        String sql = "DELETE FROM reservations WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get upcoming reservations for a doctor (next 7 days)
    public List<Reservation> getUpcomingReservations(int doctorId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
            SELECT * FROM reservations 
            WHERE doctor_id = ? 
            AND reservation_date >= CURDATE()
            AND reservation_date <= DATE_ADD(CURDATE(), INTERVAL 7 DAY)
            ORDER BY reservation_date, start_time
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctorId(rs.getInt("doctor_id"));
                reservation.setPatientName(rs.getString("patient_name"));
                reservation.setPatientAge(rs.getString("patient_age"));
                reservation.setPatientGender(rs.getString("patient_gender"));
                reservation.setPatientPhone(rs.getString("patient_phone"));
                reservation.setPatientEmail(rs.getString("patient_email"));
                reservation.setPastComplexities(rs.getString("past_complexities"));
                reservation.setReservationDate(rs.getDate("reservation_date").toLocalDate());
                reservation.setStartTime(rs.getTime("start_time").toLocalTime());
                reservation.setEndTime(rs.getTime("end_time").toLocalTime());
                reservation.setDayOfWeek(rs.getString("day_of_week"));

                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.err.println("Error getting upcoming reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }
}

