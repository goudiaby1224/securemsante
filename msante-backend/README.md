# M-Santé Backend

A secure healthcare platform backend for Senegal and West Africa, providing appointment booking, payment processing, and secure messaging for patients and healthcare professionals.

## Features

### 🔐 Security & Authentication
- **OTP-based Authentication**: SMS/WhatsApp verification for patients
- **JWT Tokens**: Short-lived access tokens (15 min) with refresh token support
- **Role-based Access Control**: Patient, Practitioner, Admin, SuperAdmin roles
- **Multi-tenant Architecture**: Secure tenant isolation using PostgreSQL RLS

### 📅 Appointment Management
- **Real-time Availability**: Practitioner schedule management with conflict prevention
- **Guest Booking**: No account required for basic appointment booking
- **Cancellation Policy**: Configurable cancellation rules (default 72 hours)
- **Multiple Consultation Modes**: In-person, video, and phone consultations

### 💰 Payment Integration
- **Mobile Money Support**: Orange Money, Wave, Free Money
- **Card Payments**: Traditional card processing
- **Automatic Payouts**: Scheduled practitioner payments
- **Commission Management**: Configurable platform fees

### 👩‍⚕️ Healthcare Profiles
- **Patient Management**: Medical history, allergies, emergency contacts
- **Practitioner Verification**: KYC workflow with document validation
- **Specialization Support**: Multiple medical specialties and languages
- **Rating System**: Patient feedback and review management

### 🏥 Multi-tenant SaaS
- **Clinic Management**: Multiple healthcare organizations
- **Data Isolation**: Secure tenant-based data segregation
- **Configurable Policies**: Per-tenant business rules and settings

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 15+
- Redis 6+

### Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/goudiaby1224/securemsante.git
   cd securemsante/msante-backend
   ```

2. **Configure the database**
   ```bash
   # Create PostgreSQL database
   createdb msante_dev
   
   # Run migrations
   psql -d msante_dev -f src/main/resources/db/migration/V001__create_msante_schema.sql
   
   # Load sample data
   psql -d msante_dev -f src/main/resources/db/seed/sample_data.sql
   ```

3. **Start Redis (for OTP storage)**
   ```bash
   redis-server
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

The application will start on http://localhost:8080

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## API Usage Examples

### 1. Request OTP for Authentication
```bash
curl -X POST http://localhost:8080/api/auth/otp \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+221701234567",
    "channel": "sms"
  }'
```

### 2. Verify OTP and Get Tokens
```bash
curl -X POST http://localhost:8080/api/auth/verify \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+221701234567",
    "code": "123456"
  }'
```

### 3. Create Guest Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "practitioner_id": "550e8400-e29b-41d4-a716-446655440002",
    "slot_id": "850e8400-e29b-41d4-a716-446655440002",
    "contact": {
      "name": "Aminata Diallo",
      "phone": "+221701234568",
      "email": "aminata@example.com"
    },
    "consultation_reason": "Consultation générale"
  }'
```

### 4. Get User's Bookings (Authenticated)
```bash
curl -X GET http://localhost:8080/api/bookings/my \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Database Schema

The platform uses PostgreSQL with the following key entities:

- **tenant**: Multi-tenant organization management
- **user_account**: User authentication and roles
- **patient_profile**: Patient medical information
- **practitioner_profile**: Healthcare provider details
- **availability_slot**: Practitioner scheduling
- **booking**: Appointment management
- **payment**: Transaction tracking
- **message**: Internal communications

## Sample Data

The development environment includes sample data:
- 5 Healthcare practitioners across different specialties
- Multiple availability slots for testing
- Sample bookings in various states
- Payment transactions with different methods

### Sample Practitioners
1. **Dr. Amadou Diop** - General Medicine (Orange Money)
2. **Dr. Fatou Ndiaye** - Pediatrics (Wave)
3. **Dr. Moussa Fall** - Cardiology (Free Money)
4. **Psy. Aïcha Sarr** - Psychology (Orange Money)
5. **Dr. Ibrahima Ba** - Dermatology (Wave)

## Configuration

### Environment Variables
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/msante_db
SPRING_DATASOURCE_USERNAME=msante_user
SPRING_DATASOURCE_PASSWORD=msante_password

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# Security
MSANTE_SECURITY_JWT_SECRET=your-secret-key
MSANTE_SECURITY_JWT_EXPIRATION=900000
MSANTE_SECURITY_OTP_EXPIRATION=300

# CORS
MSANTE_SECURITY_CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

## Security

### Authentication Flow
1. Patient enters phone number
2. OTP sent via SMS/WhatsApp
3. OTP verification returns JWT tokens
4. Access token used for API calls
5. Refresh token for seamless re-authentication

### Data Protection
- **Encryption**: All sensitive data encrypted at rest and in transit
- **GDPR/CDP Compliance**: Data protection and user rights management
- **Audit Logging**: Complete audit trail for all data access
- **Rate Limiting**: Protection against brute force attacks

## Testing

### Run Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test with coverage
mvn test jacoco:report
```

### Test Coverage
- Authentication service tests
- Booking workflow tests
- Security configuration tests
- Repository integration tests

## Deployment

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/msante-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Configuration
- PostgreSQL with connection pooling
- Redis cluster for high availability
- SSL/TLS termination at load balancer
- Environment-specific secrets management

## Contributing

### Development Guidelines
1. Follow Spring Boot best practices
2. Maintain test coverage >80%
3. Use conventional commit messages
4. Update API documentation for changes
5. Ensure security compliance

### Code Style
- Java 17 features where appropriate
- Lombok for boilerplate reduction
- MapStruct for DTO mapping
- OpenAPI 3.1 documentation

## License

This project is proprietary software. All rights reserved.

## Support

For support and questions:
- Email: support@msante.sn
- Documentation: https://docs.msante.sn
- Issues: GitHub Issues

---

**M-Santé** - Democratizing healthcare access in Senegal and West Africa through technology.