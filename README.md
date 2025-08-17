# M-Santé - Secure Healthcare Platform

A secure Spring Boot backend for a healthcare application enabling appointment booking between patients and doctors.

## Features

- **Secure Authentication**: JWT-based authentication with role-based access control
- **User Management**: Patient and Doctor registration with profile management
- **Appointment System**: Complete booking system with availability management
- **RESTful API**: Clean REST endpoints following OpenAPI standards
- **Database Security**: Encrypted passwords and secure data handling

## Architecture

The application follows a layered architecture:
- **Model Layer**: JPA entities representing core domain objects
- **Repository Layer**: Data access with Spring Data JPA
- **Service Layer**: Business logic implementation
- **Controller Layer**: REST API endpoints
- **Security Layer**: JWT authentication and authorization

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Docker (optional)

### Quick Start with Docker

1. Clone the repository
2. Start the database:
   ```bash
   docker-compose up -d
   ```
3. Build and run the application:
   ```bash
   cd msante-backend
   ./mvnw spring-boot:run
   ```

### Manual Setup

1. Start MySQL and create database:
   ```sql
   CREATE DATABASE psychapp;
   ```

2. Run the schema and seed scripts:
   ```bash
   mysql -u root -p psychapp < db/schema.sql
   mysql -u root -p psychapp < db/seed-data.sql
   ```

3. Configure database connection in `application.properties`

4. Run the application:
   ```bash
   cd msante-backend
   ./mvnw spring-boot:run
   ```

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user (patient or doctor)
- `POST /auth/login` - Login and get JWT token

### Appointments
- `GET /api/appointments/patient` - Get patient's appointments
- `GET /api/appointments/doctor` - Get doctor's appointments  
- `POST /api/appointments/book` - Book an appointment
- `DELETE /api/appointments/{id}` - Cancel appointment

### Availabilities
- `GET /api/availabilities` - Get all available slots
- `GET /api/availabilities/doctor/{doctorId}` - Get doctor's available slots

### Doctors
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/specialty/{specialty}` - Find doctors by specialty

## Sample API Usage

### Register a Patient
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@msante.sn",
    "password": "password123",
    "role": "PATIENT",
    "address": "123 Rue Example, Dakar",
    "phone": "+221-77-123-4567",
    "birthDate": "1990-01-01"
  }'
```

### Register a Doctor
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@msante.sn",
    "password": "password123",
    "role": "DOCTOR",
    "specialty": "Cardiologie",
    "licenseNumber": "LIC-123456",
    "phone": "+221-77-987-6543"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@msante.sn",
    "password": "password123"
  }'
```

### Book Appointment (requires JWT token)
```bash
curl -X POST http://localhost:8080/api/appointments/book \
  -H "Content-Type: application/json" \
  -H "Authorization: [JWT_TOKEN]" \
  -d '{
    "availabilityId": 1,
    "notes": "Consultation de routine"
  }'
```

## Database Schema

The application uses the following main entities:
- **Users**: Authentication and basic user information
- **Patients**: Patient-specific profile data
- **Doctors**: Doctor-specific profile and credentials
- **Availabilities**: Doctor availability time slots
- **Appointments**: Booking records linking patients to doctor availability

## Security Features

- JWT-based stateless authentication
- BCrypt password encryption
- Role-based access control (RBAC)
- CORS configuration for frontend integration
- Input validation and sanitization

## Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package
```

### Database Access
When using Docker Compose, access phpMyAdmin at http://localhost:8081 with:
- Server: mysql
- Username: root
- Password: root

## Project Structure

```
msante-backend/
├── src/main/java/sn/goudiaby/msante/
│   ├── config/          # Security and application configuration
│   ├── constants/       # Application constants
│   ├── controller/      # REST API controllers
│   ├── dto/            # Data Transfer Objects
│   ├── filter/         # Security filters (JWT)
│   ├── model/          # JPA entities
│   ├── repository/     # Data access layer
│   ├── security/       # Security services
│   └── service/        # Business logic layer
├── src/main/resources/
│   └── application.properties
├── db/
│   ├── schema.sql      # Database schema
│   └── seed-data.sql   # Sample data
├── docker-compose.yml  # Development database setup
└── pom.xml            # Maven configuration
```

## Contributing

1. Follow the existing code structure and patterns
2. Add appropriate tests for new features
3. Ensure security best practices are maintained
4. Update documentation as needed

## License

This project is licensed under the MIT License.
