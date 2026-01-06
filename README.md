# AidAccess - Healthcare Management System

A JavaFX application for managing healthcare services including medicines, ambulance services, and doctor appointments.

## Project Structure

```
MY_JAVA_PROJECT-main/
├── pom.xml                         # Maven configuration
├── src/
│   └── main/
│       ├── java/
│       │   └── myProject/          # Main package
│       │       ├── AidAccessApp.java           # Main application
│       │       ├── ClientFX.java               # Help chat client
│       │       ├── ServerFX.java               # Chat server
│       │       ├── SupportAgentApp.java        # Support agent interface
│       │       ├── ambulance/                  # Ambulance service module
│       │       │   └── AmbulanceServicePage.java
│       │       └── medicine/                   # Medicine ordering module
│       │           ├── BuyPage.java
│       │           ├── Medicine.java
│       │           ├── MedicineApp.java
│       │           ├── MedicineHomePage.java
│       │           ├── MedicineNavigator.java
│       │           ├── MedicineRepository.java
│       │           ├── MedicineResultsPage.java
│       │           ├── OrderException.java
│       │           ├── OrderLogger.java
│       │           ├── OrderSuccessPage.java
│       │           ├── PaymentPage.java
│       │           └── SelectionRow.java
│       └── resources/
│           └── medicines.csv       # Medicine database
└── myProject/                      # Old structure (can be deleted after verification)
```

## Requirements

- Java 24 or higher
- Maven (bundled with IntelliJ IDEA)
- JavaFX 21.0.1 (automatically downloaded by Maven)

## Setup Instructions

### Option 1: Using IntelliJ IDEA

1. **Open the project** in IntelliJ IDEA
2. **Wait for Maven to import** - IntelliJ should automatically detect `pom.xml` and import dependencies
   - If prompted, click "Load Maven Project" or "Import Changes"
3. **Mark directories as source roots** (if not automatic):
   - Right-click `src/main/java` → Mark Directory as → Sources Root
   - Right-click `src/main/resources` → Mark Directory as → Resources Root
4. **Run the application**:
   - Open the Maven tool window (View → Tool Windows → Maven)
   - Navigate to: Plugins → javafx → javafx:run
   - Or create a run configuration with main class: `myProject.AidAccessApp`

### Option 2: Using Command Line (if Maven is installed)

```bash
# Navigate to project directory
cd F:\MY_JAVA_PROJECT-main

# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run

# Or package as JAR
mvn clean package
```

## Running the Application

The application will launch with a landing page offering three services:

1. **Doctor** - Appointment booking (coming soon)
2. **Medicines** - Search, compare, and order medicines
3. **Ambulance** - Request emergency ambulance services
4. **Help** - Live chat support with agents

## Features

### Medicine Ordering
- Search medicines by name
- Filter by location, shop, and brand
- Compare prices across different shops
- Place orders with delivery details
- Payment processing simulation

### Ambulance Service
- Request ambulance by entering address
- Real-time dispatch status
- Emergency contact information

### Live Chat Support
- Client-server chat system
- Support agent interface
- Real-time messaging

## Troubleshooting

### Package errors
If you see "Package name does not correspond to file path" errors:
- Make sure `src/main/java` is marked as Sources Root
- Rebuild the project (Build → Rebuild Project)

### JavaFX not found
- Make sure Maven has downloaded dependencies (check Maven tool window)
- Reload Maven project (right-click `pom.xml` → Maven → Reload Project)

### CSV file not loading
- Verify `medicines.csv` exists in `src/main/resources/`
- The application will use in-memory data if CSV fails to load

## Submitting as Assignment

To submit this project:

1. **Delete old folders** (optional):
   - Delete the `myProject/` folder at the root (old structure)
   - Delete `out/` and `target/` folders (build outputs)
   
2. **ZIP the project**:
   - Zip the entire `MY_JAVA_PROJECT-main` folder
   
3. **Include this README** with your submission

Your teacher can:
- Extract the ZIP file
- Open in IntelliJ IDEA
- Run with Maven: `mvn javafx:run`

## Notes

- The project uses Maven's standard directory layout
- JavaFX dependencies are managed automatically by Maven
- All source code is in `src/main/java/myProject/`
- Resources (CSV files) are in `src/main/resources/`

