[README.md](https://github.com/user-attachments/files/26433570/README.md)
# Hi Dent - Dental Clinic Management System

Full-stack dental clinic management system built with Java 21, Spring Boot 4, PostgreSQL, and Thymeleaf. Designed for small to medium dental practices, providing patient management, clinical records, treatment budgets, appointment scheduling, and automated backups.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4.0.2, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap 5.3, custom CSS
- **Database:** PostgreSQL
- **Authentication:** JWT (stateless, cookie-based)
- **Build:** Maven
- **Integrations:** Google Drive API (backups), Gmail SMTP (notifications)

## Features

### Patient Management
- Patient registration with personal and contact data
- Search and filtering by name or document number
- Auto-generated clinical record numbers

### Clinical History
- **Anamnesis:** Complete medical history form
- **Odontogram:** Interactive dental chart with per-tooth status tracking
- **Evolution Notes:** Chronological treatment records
- **Oral Revision:** Soft/hard tissue examination records
- **Documents:** File upload and management per patient
- **Informed Consent:** Digital consent document management

### Budgets & Proformas
- Treatment budget creation with service catalog (46 dental services across 6 categories)
- Dual currency support (PEN/USD)
- Budget item editing with quantity and discount management
- Proforma generation for prospective patients
- PDF-ready budget views

### Appointments
- Calendar view with month/week/day modes
- Appointment creation linked to patients and services
- Status tracking (scheduled, completed, cancelled)

### Automated Backups
- Scheduled PostgreSQL backups via pg_dump
- Automatic upload to Google Drive
- Email notifications on backup completion/failure
- Configurable retention policy

## Project Structure

```
src/main/java/com/odontologia/odontologia/
├── config/          Configuration and data initializers
├── controller/      MVC and REST API controllers
├── dto/             Request/response data transfer objects
├── model/           JPA entity classes
├── repository/      Spring Data JPA repositories
├── security/        JWT filter, service, and security config
└── service/         Business logic layer

src/main/resources/
├── static/
│   ├── css/         Custom stylesheets
│   ├── img/         Application logos
│   └── js/          Client-side JavaScript
├── templates/       Thymeleaf HTML templates
└── application.properties
```

## Prerequisites

- Java 21+
- PostgreSQL 15+
- Maven 3.9+

## Setup

1. Create the database:
```sql
CREATE DATABASE odontologia_db;
```

2. Configure environment variables (or use defaults for development):
```bash
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret_base64
export SMTP_USERNAME=your_email@gmail.com
export SMTP_PASSWORD=your_app_password
export DRIVE_FOLDER_ID=your_google_drive_folder_id
export DRIVE_CREDENTIALS=/path/to/credentials.json
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Access at `http://localhost:8080`

## Default Data

On first run, the application automatically seeds:
- A default admin user
- The dental service catalog with 46 services across 6 categories

## License

This project is proprietary software developed for a specific dental clinic client.
