package com.example.mediconnect;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private int id;
    private int doctorId;
    private String patientName;
    private String patientAge;
    private String patientGender;
    private String patientPhone;
    private String patientEmail;
    private String pastComplexities;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String dayOfWeek;

    public Reservation() {
    }

    public Reservation(int doctorId, String patientName, String patientAge, String patientGender,
                      String patientPhone, String patientEmail, String pastComplexities,
                      LocalDate reservationDate, LocalTime startTime, LocalTime endTime, String dayOfWeek) {
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        this.patientPhone = patientPhone;
        this.patientEmail = patientEmail;
        this.pastComplexities = pastComplexities;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPastComplexities() {
        return pastComplexities;
    }

    public void setPastComplexities(String pastComplexities) {
        this.pastComplexities = pastComplexities;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}

