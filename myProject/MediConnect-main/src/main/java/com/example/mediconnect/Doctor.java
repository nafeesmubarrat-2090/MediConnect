package com.example.mediconnect;

public class Doctor {
    private int id;
    private String username;
    private String password;
    private String name;
    private String sector;
    private String qualifications;
    private String experience;
    private String age; // new
    private String phone;
    private String email;
    private String chamber;
    private String location; // reintroduced
    private String consultationHours;
    private String availableDays; // new: space-separated days
    private String gender;
    private String fees;
    private String hospitalAffiliations;
    private String onlineConsultation;
    private int consultationDuration; // in minutes, range 10-60

    public Doctor(String username, String password, String name, String sector, String qualifications, String experience, String age, String phone, String email, String chamber, String location, String consultationHours, String availableDays, String gender, String fees, String hospitalAffiliations, String onlineConsultation, int consultationDuration) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.sector = sector;
        this.qualifications = qualifications;
        this.experience = experience;
        this.age = age; // set
        this.phone = phone;
        this.email = email;
        this.chamber = chamber;
        this.location = location; // set
        this.consultationHours = consultationHours;
        this.availableDays = availableDays; // set
        this.gender = gender;
        this.fees = fees;
        this.hospitalAffiliations = hospitalAffiliations;
        this.onlineConsultation = onlineConsultation;
        this.consultationDuration = consultationDuration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getSector() { return sector; }
    public String getQualifications() { return qualifications; }
    public String getExperience() { return experience; }
    public String getAge() { return age; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getChamber() { return chamber; }
    public String getLocation() { return location; }
    public String getConsultationHours() { return consultationHours; }
    public String getAvailableDays() { return availableDays; }
    public String getGender() { return gender; }
    public String getFees() { return fees; }
    public String getHospitalAffiliations() { return hospitalAffiliations; }
    public String getOnlineConsultation() { return onlineConsultation; }
    public int getConsultationDuration() { return consultationDuration; }
}
