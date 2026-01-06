# MediConnect

A JavaFX-based medical platform that connects doctors and patients for appointment management and consultation booking.

## Features

### For Patients
- **User Registration & Login**: Secure patient account creation and authentication
- **Doctor Search**: Search for doctors by specialization
- **View Doctor Details**: See doctor profiles with specialization and available time slots
- **Book Reservations**: Schedule appointments with doctors for the next 7 days
- **View Upcoming Appointments**: Track all booked reservations

### For Doctors
- **Doctor Registration & Login**: Secure doctor account creation with specialization and consultation duration
- **Update Availability**: Set working days and time slots for the week
- **View Dashboard**: See all upcoming patient reservations
- **Manage Profile**: Update consultation duration and availability

## Technology Stack

- **Frontend**: JavaFX with FXML
- **Backend**: Java (JDK 11+)
- **Database**: MySQL
- **Build Tool**: Maven

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- MySQL Server 5.7 or higher
- Maven 3.6 or higher

## Database Setup

1. Create a MySQL database named `mediconnect`:
```sql
CREATE DATABASE mediconnect;
```

2. Run the SQL migration script (if needed):
```sql
-- The add_consultation_duration_column.sql is included for reference
```

3. Update database credentials in `DatabaseManager.java`:
```java
DB_URL = "jdbc:mysql://localhost:3306/mediconnect"
DB_USER = "root"
DB_PASSWORD = "your_password"
```

## Installation & Running

1. Clone the repository:
```bash
git clone https://github.com/YOUR-USERNAME/mediconnect.git
cd mediconnect
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn javafx:run
```

Or run the `Launcher.java` class directly from your IDE.

## Project Structure

```
MediConnect/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/mediconnect/
│   │   │       ├── HelloApplication.java
│   │   │       ├── Launcher.java
│   │   │       ├── LoginController.java
│   │   │       ├── RegisterController.java
│   │   │       ├── PatientHomeController.java
│   │   │       ├── PatientLandingController.java
│   │   │       ├── DoctorDashboard.java
│   │   │       ├── DoctorDetailController.java
│   │   │       ├── BookReservationController.java
│   │   │       ├── UpdateInfoController.java
│   │   │       ├── Doctor.java
│   │   │       ├── DoctorDAO.java
│   │   │       ├── Reservation.java
│   │   │       ├── ReservationDAO.java
│   │   │       └── DatabaseManager.java
│   │   └── resources/
│   │       ├── background.png
│   │       ├── icon.png
│   │       └── com/example/mediconnect/
│   │           ├── *.fxml (UI layouts)
│   │           └── style.css
├── pom.xml
└── README.md
```

## Usage

### Patient Workflow
1. Register as a new patient or login with existing credentials
2. Search for doctors by specialization
3. View doctor profiles and available time slots
4. Book appointments for preferred dates and times
5. View upcoming reservations

### Doctor Workflow
1. Register as a new doctor (provide specialization and consultation duration)
2. Login with doctor credentials
3. Set availability for each day of the week
4. View dashboard to see patient reservations
5. Update profile and availability as needed

## Features in Detail

- **7-Day Booking System**: Patients can book appointments for the next 7 days
- **Consultation Duration**: Doctors can set consultation duration (10-60 minutes)
- **Time Slot Management**: Dynamic time slot generation based on doctor availability
- **Search Functionality**: Exact match search for doctor specializations
- **Responsive UI**: Clean and intuitive JavaFX interface

## Contributing

Feel free to fork this project and submit pull requests for any improvements.

## License

This project is open source and available under the MIT License.

## Contact

For any questions or issues, please open an issue on GitHub.

