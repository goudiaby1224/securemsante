# 🏗️ M-Santé Architecture Summary

## 🎯 **Quick Overview**

M-Santé is a **Spring Boot 3.3.0** application built with **Java 21**, featuring a **layered architecture** with **RESTful APIs**, **JWT authentication**, **Redis caching**, and **MySQL database**.

## 🏛️ **Architecture Pattern**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Layered Architecture                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Controllers   │  │    Services     │  │  Repositories   │ │
│  │   (REST APIs)   │  │ (Business Logic)│  │  (Data Access)  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│           │                   │                   │             │
│           │───HTTP Request───▶│                   │             │
│           │                   │───Business Logic▶│             │
│           │                   │                   │             │
│           │                   │◀───Data Access───│             │
│           │◀───HTTP Response─│                   │             │
│           │                   │                   │             │
└─────────────────────────────────────────────────────────────────┘
```

## 🔧 **Technology Stack**

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Runtime** | Java 21 | High-performance JVM |
| **Framework** | Spring Boot 3.3.0 | Rapid development |
| **Security** | Spring Security 6 + JWT | Authentication & authorization |
| **Database** | MySQL 8.0 | Primary data storage |
| **Caching** | Redis 7.0 | Session & data caching |
| **Testing** | JUnit 5 + Mockito | Unit & integration testing |
| **Monitoring** | Spring Actuator + Prometheus + Grafana | Observability |
| **Build** | Maven | Dependency management |

## 🗄️ **Database Design**

### **Core Entities**
- **Users** - Authentication & profiles
- **Doctors** - Medical professionals
- **Patients** - Healthcare recipients
- **Appointments** - Scheduled consultations
- **Availability** - Doctor time slots
- **Analytics** - Healthcare metrics

### **Key Relationships**
```
users (1) ←→ (1) doctors
users (1) ←→ (1) patients
doctors (1) ←→ (N) availability
doctors (1) ←→ (N) appointments
patients (1) ←→ (N) appointments
```

## 🔌 **API Structure**

### **Authentication**
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/refresh` - Token refresh

### **Core Operations**
- `GET/POST/PUT/DELETE /api/appointments` - Appointment management
- `GET/POST/PUT/DELETE /api/availability` - Doctor availability
- `GET/PUT /api/doctors/profile` - Doctor profiles
- `GET/PUT /api/patients/profile` - Patient profiles
- `GET /api/analytics/*` - Healthcare metrics

### **Monitoring**
- `GET /actuator/health` - Application health
- `GET /actuator/metrics` - Performance metrics

## 🔒 **Security Model**

### **Authentication Flow**
1. **Login** → Validate credentials → Generate JWT
2. **API Call** → Validate JWT → Set security context
3. **Authorization** → Check user roles → Grant/deny access

### **Security Features**
- **JWT Tokens** - Stateless authentication
- **BCrypt** - Password hashing
- **RBAC** - Role-based access control
- **CORS** - Cross-origin resource sharing
- **Rate Limiting** - API abuse prevention

## 📊 **Performance & Caching**

### **Caching Strategy**
- **Redis** - Session storage & data caching
- **Hibernate** - Query result caching
- **Spring Cache** - Method-level caching
- **TTL Management** - Cache expiration

### **Performance Features**
- **Connection Pooling** - HikariCP for database
- **Async Processing** - CompletableFuture
- **Circuit Breaker** - Fault tolerance
- **Retry Mechanisms** - Exponential backoff

## 🚀 **Deployment Options**

### **Development**
```bash
mvn spring-boot:run -Dspring.profiles.active=development
```

### **Production (Docker)**
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### **Production (JAR)**
```bash
java -jar target/msante-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

## 📈 **Monitoring & Observability**

### **Health Checks**
- **Application Health** - `/actuator/health`
- **Database Health** - Connection pool status
- **Cache Health** - Redis connectivity
- **Custom Health** - Business logic validation

### **Metrics Collection**
- **Application Metrics** - Response times, throughput
- **Business Metrics** - Appointments, user growth
- **System Metrics** - CPU, memory, disk usage
- **Security Metrics** - Failed logins, auth attempts

### **Dashboards**
- **Grafana** - Real-time monitoring
- **Prometheus** - Metrics collection
- **Custom Views** - Healthcare-specific insights

## 🔄 **Key Use Cases**

### **1. User Authentication**
```
Frontend → AuthController → AuthService → UserRepository → Database
```

### **2. Appointment Booking**
```
Frontend → AppointmentController → AppointmentService → AvailabilityRepository → Database
```

### **3. Doctor Search**
```
Frontend → DoctorController → DoctorService → DoctorRepository → Database
```

### **4. Analytics & Reporting**
```
Frontend → AnalyticsController → AnalyticsService → AnalyticsRepository → Database + Cache
```

## 🎯 **Development Workflow**

### **1. Setup Environment**
```bash
# Install dependencies
mvn install

# Configure database
cp env.production.example .env.production
# Edit configuration

# Start services
docker-compose up -d
```

### **2. Development**
```bash
# Run tests
mvn test

# Start application
mvn spring-boot:run

# Run Phase 4 validation
mvn test -Dtest=Phase4ValidationTest
```

### **3. Deployment**
```bash
# Build production JAR
mvn clean package -Pproduction

# Deploy with Docker
docker-compose -f docker-compose.prod.yml up -d
```

## 🏆 **Key Benefits**

- **Scalable** - Horizontal scaling with load balancers
- **Secure** - JWT authentication with RBAC
- **Performant** - Multi-layer caching strategy
- **Observable** - Comprehensive monitoring stack
- **Resilient** - Circuit breakers and retry mechanisms
- **Testable** - Comprehensive test coverage
- **Deployable** - Docker and cloud-native ready

---

**🏥 M-Santé - Enterprise-Grade Healthcare Management System**  
**Built with modern Spring Boot architecture and cloud-native principles**
