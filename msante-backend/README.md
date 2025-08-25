# 🏥 M-Santé Backend - Healthcare Management System

## 📋 **Table of Contents**
- [System Overview](#system-overview)
- [Architecture Overview](#architecture-overview)
- [Component Architecture](#component-architecture)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Use Case Sequence Diagrams](#use-case-sequence-diagrams)
- [Deployment Architecture](#deployment-architecture)
- [Security Architecture](#security-architecture)
- [Performance & Monitoring](#performance--monitoring)
- [Getting Started](#getting-started)

## 🎯 **System Overview**

M-Santé is a comprehensive healthcare management system designed for Senegalese healthcare facilities. The system manages patient appointments, doctor availability, medical records, and provides real-time healthcare analytics.

### **Key Features**
- 🔐 **Authentication & Authorization** - JWT-based security with role-based access control
- 📅 **Appointment Management** - Booking, rescheduling, and cancellation
- 👨‍⚕️ **Doctor Management** - Profiles, availability, and specialties
- 👥 **Patient Management** - Medical history and profile management
- 📊 **Analytics & Reporting** - Healthcare metrics and insights
- 🔄 **Real-time Updates** - Live availability and notification system
- 📱 **API-First Design** - RESTful APIs for frontend and mobile applications

## 🏗️ **Architecture Overview**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           M-Santé Healthcare System                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐ │
│  │   Frontend      │    │   Mobile App    │    │     External APIs      │ │
│  │   (Teranga)     │    │   (Future)      │    │   (Insurance, Labs)    │ │
│  └─────────────────┘    └─────────────────┘    └─────────────────────────┘ │
│           │                       │                       │                 │
│           └───────────────────────┼───────────────────────┘                 │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    API Gateway & Load Balancer                      │   │
│  │                        (Nginx)                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Spring Boot Application                          │   │
│  │                                                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │   │
│  │  │ Controllers │ │  Services   │ │ Repositories│ │   Models    │     │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Data Layer                                   │   │
│  │                                                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │   │
│  │  │    MySQL    │ │    Redis    │ │   Logs      │ │   Metrics   │     │   │
│  │  │  Database   │ │    Cache    │ │  (Logback)  │ │(Prometheus) │     │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Monitoring & Observability                       │   │
│  │                                                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │   │
│  │  │  Prometheus │ │   Grafana   │ │   Jaeger    │ │   ELK Stack │     │   │
│  │  │  Metrics    │ │  Dashboards │ │   Tracing   │ │   Logging   │     │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🔧 **Component Architecture**

### **1. Presentation Layer**
```
┌─────────────────────────────────────────────────────────────────┐
│                    Presentation Layer                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  AuthController │  │AppointmentCtrlr │  │  DoctorController│ │
│  │                 │  │                 │  │                 │ │
│  │ • Login         │  │ • Book          │  │ • Profile       │ │
│  │ • Register      │  │ • Reschedule    │  │ • Search        │ │
│  │ • Logout        │  │ • Cancel        │  │ • Availability  │ │
│  │ • Refresh       │  │ • List          │  │ • Specialties   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ PatientController│  │AnalyticsCtrlr  │  │AvailabilityCtrlr│ │
│  │                 │  │                 │  │                 │ │
│  │ • Profile       │  │ • Metrics       │  │ • Create        │ │
│  │ • History       │  │ • Reports       │  │ • Update        │ │
│  │ • Medical Data  │  │ • Dashboard     │  │ • Delete        │ │
│  │ • Allergies     │  │ • Trends        │  │ • Search        │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### **2. Business Logic Layer**
```
┌─────────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   AuthService   │  │AppointmentService│  │  DoctorService  │ │
│  │                 │  │                 │  │                 │ │
│  │ • Authentication│  │ • Validation    │  │ • Profile Mgmt  │ │
│  │ • JWT Handling │  │ • Scheduling    │  │ • Availability  │ │
│  │ • Role Mgmt     │  │ • Conflicts     │  │ • Search Logic  │ │
│  │ • Security      │  │ • Notifications │  │ • Rating System │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  PatientService │  │AnalyticsService │  │AvailabilitySvc  │ │
│  │                 │  │                 │  │                 │ │
│  │ • Profile Mgmt  │  │ • Data Aggreg.  │  │ • Slot Mgmt     │ │
│  │ • Medical Hist. │  │ • Calculations  │  │ • Time Logic    │ │
│  │ • Data Validation│  │ • Report Gen.   │  │ • Conflicts    │ │
│  │ • Allergies     │  │ • Performance   │  │ • Optimization  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │NotificationSvc  │  │PerformanceTestSvc│  │ErrorHandlingSvc │ │
│  │                 │  │                 │  │                 │ │
│  │ • Email         │  │ • Load Testing  │  │ • Retry Logic   │ │
│  │ • SMS           │  │ • Benchmarking  │  │ • Circuit Breaker│ │
│  │ • Push          │  │ • Cache Tests   │  │ • Fallbacks     │ │
│  │ • Templates     │  │ • Performance   │  │ • Timeouts      │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### **3. Data Access Layer**
```
┌─────────────────────────────────────────────────────────────────┐
│                    Data Access Layer                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  UserRepository │  │AppointmentRepo  │  │  DoctorRepo     │ │
│  │                 │  │                 │  │                 │ │
│  │ • CRUD          │  │ • CRUD          │  │ • CRUD          │ │
│  │ • Search        │  │ • Search        │  │ • Search        │ │
│  │ • Auth          │  │ • Conflicts     │  │ • Specialties   │ │
│  │ • Validation    │  │ • Analytics     │  │ • Availability  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ PatientRepo     │  │AvailabilityRepo │  │AnalyticsRepo    │ │
│  │                 │  │                 │  │                 │ │
│  │ • CRUD          │  │ • CRUD          │  │ • Aggregations  │ │
│  │ • Medical Data  │  │ • Time Slots    │  │ • Metrics       │ │
│  │ • History       │  │ • Conflicts     │  │ • Reports       │ │
│  │ • Search        │  │ • Optimization  │  │ • Performance   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 🛠️ **Technology Stack**

### **Backend Framework**
- **Java 21** - Latest LTS version for performance and features
- **Spring Boot 3.3.0** - Rapid application development framework
- **Spring Security 6** - Comprehensive security framework
- **Spring Data JPA** - Data access abstraction
- **Spring Cache** - Caching abstraction with Redis support

### **Database & Caching**
- **MySQL 8.0** - Primary relational database
- **Redis 7.0** - In-memory caching and session storage
- **H2 Database** - In-memory database for testing

### **Security & Authentication**
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing
- **Role-Based Access Control (RBAC)** - User authorization
- **CORS Configuration** - Cross-origin resource sharing

### **Testing & Quality**
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing with real databases
- **JaCoCo** - Code coverage analysis
- **PITest** - Mutation testing

### **Monitoring & Observability**
- **Spring Boot Actuator** - Application monitoring endpoints
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization and dashboards
- **Logback** - Advanced logging with structured output

### **Performance & Resilience**
- **Spring Retry** - Retry mechanisms with exponential backoff
- **Circuit Breaker Pattern** - Fault tolerance implementation
- **Connection Pooling** - HikariCP for database connections
- **Async Processing** - CompletableFuture for non-blocking operations

## 🗄️ **Database Schema**

```
┌─────────────────────────────────────────────────────────────────┐
│                        Database Schema                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │    users    │    │   doctors   │    │   patients  │         │
│  │             │    │             │    │             │         │
│  │ • id (PK)   │◄───┤ • id (PK)   │◄───┤ • id (PK)   │         │
│  │ • email     │    │ • user_id   │    │ • user_id   │         │
│  │ • password  │    │ • specialty │    │ • blood_type│         │
│  │ • role      │    │ • department│    │ • allergies │         │
│  │ • status    │    │ • rating    │    │ • conditions│         │
│  │ • created   │    │ • bio       │    │ • history   │         │
│  │ • updated   │    │ • languages │    │ • insurance │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                   │                   │               │
│         │                   │                   │               │
│         │                   │                   │               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │appointments │    │ availability│    │  analytics  │         │
│  │             │    │             │    │             │         │
│  │ • id (PK)   │    │ • id (PK)   │    │ • id (PK)   │         │
│  │ • patient_id│    │ • doctor_id │    │ • metric    │         │
│  │ • doctor_id │    │ • start_time│    │ • value     │         │
│  │ • date      │    │ • end_time  │    │ • timestamp │         │
│  │ • time      │    │ • status    │    │ • category  │         │
│  │ • status    │    │ • recurring │    │ • source    │         │
│  │ • notes     │    │ • conflicts │    │ • metadata  │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │ notifications│    │ audit_logs  │    │ cache_data  │         │
│  │             │    │             │    │             │         │
│  │ • id (PK)   │    │ • id (PK)   │    │ • key       │         │
│  │ • user_id   │    │ • user_id   │    │ • value     │         │
│  │ • type      │    │ • action    │    │ • ttl       │         │
│  │ • message   │    │ • resource  │    │ • created   │         │
│  │ • status    │    │ • timestamp │    │ • updated   │         │
│  │ • sent_at   │    │ • ip_address│    │ • metadata  │         │
│  │ • metadata  │    │ • user_agent│    │ • version   │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

## 🔌 **API Endpoints**

### **Authentication Endpoints**
```
POST   /auth/login              - User login
POST   /auth/register           - User registration
POST   /auth/logout             - User logout
POST   /auth/refresh            - Token refresh
POST   /auth/change-password    - Password change
```

### **User Management Endpoints**
```
GET    /api/users/profile       - Get user profile
PUT    /api/users/profile       - Update user profile
GET    /api/users/{id}          - Get user by ID
DELETE /api/users/{id}          - Delete user
```

### **Doctor Management Endpoints**
```
GET    /api/doctors/profile     - Get doctor profile
PUT    /api/doctors/profile     - Update doctor profile
GET    /api/doctors/search      - Search doctors
GET    /api/doctors/specialties - Get all specialties
GET    /api/doctors/{id}        - Get doctor by ID
GET    /api/doctors/health      - Health check
GET    /api/doctors/test-specialties - Test endpoint
```

### **Patient Management Endpoints**
```
GET    /api/patients/profile    - Get patient profile
PUT    /api/patients/profile    - Update patient profile
GET    /api/patients/{id}       - Get patient by ID
GET    /api/patients/history    - Get medical history
```

### **Appointment Management Endpoints**
```
POST   /api/appointments        - Book appointment
GET    /api/appointments        - List appointments
PUT    /api/appointments/{id}   - Update appointment
DELETE /api/appointments/{id}   - Cancel appointment
GET    /api/appointments/{id}   - Get appointment details
```

### **Availability Management Endpoints**
```
POST   /api/availability        - Create availability
GET    /api/availability        - List availability
PUT    /api/availability/{id}   - Update availability
DELETE /api/availability/{id}   - Delete availability
GET    /api/availability/search - Search availability
```

### **Analytics Endpoints**
```
GET    /api/analytics/metrics   - Get metrics
GET    /api/analytics/reports   - Get reports
GET    /api/analytics/dashboard - Get dashboard data
GET    /api/analytics/trends    - Get trends
```

### **Monitoring Endpoints**
```
GET    /actuator/health         - Application health
GET    /actuator/info           - Application info
GET    /actuator/metrics        - Application metrics
GET    /actuator/env            - Environment variables
```

## 🔄 **Use Case Sequence Diagrams**

### **1. User Authentication Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │AuthController│    │ AuthService │    │UserRepository│   │   Database  │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───Login───────▶│                   │                   │                   │
     │                │───validateUser───▶│                   │                   │
     │                │                   │───findByEmail────▶│                   │
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───User Data────│
     │                │                   │◀───User Found────│                   │
     │                │                   │───validatePassword│                   │
     │                │                   │───generateJWT────│                   │
     │                │◀───JWT Token─────│                   │                   │
     │◀───Success────│                   │                   │                   │
     │                │                   │                   │                   │
```

### **2. Appointment Booking Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │Appointment  │    │Appointment  │    │Availability │   │   Database  │
│         │    │Controller   │    │Service      │    │Repository   │   │             │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───Book Appt───▶│                   │                   │                   │
     │                │───bookAppointment▶│                   │                   │
     │                │                   │───validateSlot───▶│                   │
     │                │                   │                   │───checkConflict─▶│
     │                │                   │                   │◀───Available────│
     │                │                   │───createAppt─────▶│                   │
     │                │                   │                   │───INSERT───────▶│
     │                │                   │                   │◀───Success─────│
     │                │                   │◀───Appt Created──│                   │
     │                │                   │───sendNotification│                   │
     │                │◀───Success───────│                   │                   │
     │◀───Booked─────│                   │                   │                   │
     │                │                   │                   │                   │
```

### **3. Doctor Availability Management Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │Availability │    │Availability │    │Availability │   │   Database  │
│         │    │Controller   │    │Service      │    │Repository   │   │             │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───Create Slot─▶│                   │                   │                   │
     │                │───createSlot─────▶│                   │                   │
     │                │                   │───validateTime───▶│                   │
     │                │                   │                   │───checkOverlap─▶│
     │                │                   │                   │◀───No Conflict──│
     │                │                   │───createSlot─────▶│                   │
     │                │                   │                   │───INSERT───────▶│
     │                │                   │                   │◀───Success─────│
     │                │                   │◀───Slot Created──│                   │
     │                │◀───Success───────│                   │                   │
     │◀───Created────│                   │                   │                   │
     │                │                   │                   │                   │
```

### **4. Patient Search & Booking Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │ DoctorCtrlr │    │DoctorService│    │DoctorRepo   │   │   Database  │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───Search──────▶│                   │                   │                   │
     │                │───searchDoctors──▶│                   │                   │
     │                │                   │───findByCriteria─▶│                   │
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───Doctors─────│
     │                │                   │◀───Results───────│                   │
     │                │◀───Doctors───────│                   │                   │
     │◀───Results────│                   │                   │                   │
     │                │                   │                   │                   │
     │───Check Avail▶│                   │                   │                   │
     │                │───getAvailability▶│                   │                   │
     │                │                   │───findAvailable──▶│                   │
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───Slots───────│
     │                │                   │◀───Available────│                   │
     │                │◀───Slots────────│                   │                   │
     │◀───Available──│                   │                   │                   │
     │                │                   │                   │                   │
```

### **5. Analytics & Reporting Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │AnalyticsCtrl│    │AnalyticsSvc│    │AnalyticsRepo│   │   Database  │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───Get Metrics▶│                   │                   │                   │
     │                │───getMetrics────▶│                   │                   │
     │                │                   │───aggregateData──▶│                   │
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───Raw Data────│
     │                │                   │───calculateMetrics│                   │
     │                │                   │───cacheResults───▶│                   │
     │                │                   │                   │───SET Redis────▶│
     │                │                   │                   │◀───Cached──────│
     │                │                   │◀───Metrics──────│                   │
     │                │◀───Metrics──────│                   │                   │
     │◀───Metrics────│                   │                   │                   │
     │                │                   │                   │                   │
```

## 🚀 **Deployment Architecture**

### **Production Deployment**
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Production Environment                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐ │
│  │   Load Balancer │    │   CDN/Edge      │    │     SSL Termination     │ │
│  │   (HAProxy)     │    │   (CloudFlare)  │    │      (Nginx)            │ │
│  └─────────────────┘    └─────────────────┘    └─────────────────────────┘ │
│           │                       │                       │                 │
│           └───────────────────────┼───────────────────────┘                 │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Application Servers                              │   │
│  │                                                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │   │
│  │  │   App 1     │ │   App 2     │ │   App 3     │ │   App N     │     │   │
│  │  │ (Port 8080) │ │ (Port 8080) │ │ (Port 8080) │ │ (Port 8080) │     │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Data Layer                                   │   │
│  │                                                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │   │
│  │  │   MySQL     │ │    Redis    │ │   Backup    │ │   Archive   │     │   │
│  │  │  Primary    │ │   Cluster   │ │   Storage   │ │   Storage   │     │   │
│  │  │  (Master)   │ │   (Sentinel)│ │   (S3)      │ │   (S3)      │     │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Monitoring & Observability                       │   │
│  │                                                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │   │
│  │  │  Prometheus │ │   Grafana   │ │   Alerting  │ │   Logging   │     │   │
│  │  │  Server     │ │  Dashboard  │ │   (PagerDuty│ │   (ELK)     │     │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### **Docker Deployment**
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  msante-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    networks:
      - msante-network

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=msante
      - MYSQL_USER=${DB_USERNAME}
      - MYSQL_PASSWORD=${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - msante-network

  redis:
    image: redis:7.0-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data
    networks:
      - msante-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - msante-backend
    networks:
      - msante-network

  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    networks:
      - msante-network

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - msante-network

volumes:
  mysql_data:
  redis_data:
  grafana_data:

networks:
  msante-network:
    driver: bridge
```

## 🔒 **Security Architecture**

### **Authentication & Authorization Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Request │    │JWT Validator│    │SecurityCtx  │    │AuthProvider │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │
     │───JWT Token──▶│                   │                   │
     │                │───validateJWT────▶│                   │
     │                │                   │───verifyToken────▶│
     │                │                   │                   │
     │                │                   │◀───Valid Token──│
     │                │◀───Set Context───│                   │
     │                │                   │                   │
     │◀───Authorized─│                   │                   │
     │                │                   │                   │
```

### **Security Layers**
```
┌─────────────────────────────────────────────────────────────────┐
│                        Security Layers                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Network       │  │   Application   │  │     Data        │ │
│  │   Security      │  │   Security      │  │   Security      │ │
│  │                 │  │                 │  │                 │ │
│  │ • Firewall      │  │ • JWT Auth      │  │ • Encryption    │ │
│  │ • DDoS         │  │ • RBAC          │  │ • Hashing       │ │
│  │ • VPN          │  │ • CORS          │  │ • Audit Logs    │ │
│  │ • Load Balancer│  │ • Rate Limiting │  │ • Access Control│ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Transport     │  │   Session       │  │   Monitoring    │ │
│  │   Security      │  │   Security      │  │   Security      │ │
│  │                 │  │                 │  │                 │ │
│  │ • HTTPS/TLS     │  │ • Session Mgmt  │  │ • Intrusion     │ │
│  │ • SSL Pinning   │  │ • Timeout       │  │   Detection     │ │
│  │ • Certificate   │  │ • Invalidation  │  │ • Security      │ │
│  │   Validation    │  │ • Secure        │  │   Events        │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 📊 **Performance & Monitoring**

### **Performance Metrics**
```
┌─────────────────────────────────────────────────────────────────┐
│                        Performance Metrics                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Application   │  │   Database      │  │   Infrastructure│ │
│  │   Metrics       │  │   Metrics       │  │   Metrics       │ │
│  │                 │  │                 │  │                 │ │
│  │ • Response Time │  │ • Query Time    │  │ • CPU Usage     │ │
│  │ • Throughput    │  │ • Connection    │  │ • Memory Usage  │ │
│  │ • Error Rate    │  │   Pool          │  │ • Disk I/O      │ │
│  │ • Active Users  │  │ • Cache Hit     │  │ • Network I/O   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Business      │  │   Security      │  │   User          │ │
│  │   Metrics       │  │   Metrics       │  │   Experience    │ │
│  │                 │  │                 │  │   Metrics       │ │
│  │ • Appointments  │  │ • Failed Logins │  │ • Page Load     │ │
│  │ • User Growth   │  │ • Auth Attempts │  │ • API Response  │ │
│  │ • Revenue       │  │ • Security      │  │ • Error Rate    │ │
│  │ • Utilization   │  │   Events        │  │ • User Journey  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### **Monitoring Dashboard**
```
┌─────────────────────────────────────────────────────────────────┐
│                        Grafana Dashboard                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   System        │  │   Application   │  │   Business      │ │
│  │   Health        │  │   Performance   │  │   Metrics       │ │
│  │                 │  │                 │  │                 │ │
│  │ • CPU Usage     │  │ • Response Time │  │ • Appointments  │ │
│  │ • Memory Usage  │  │ • Throughput    │  │ • User Growth   │ │
│  │ • Disk Usage    │  │ • Error Rate    │  │ • Revenue       │ │
│  │ • Network I/O   │  │ • Active Users  │  │ • Utilization   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Database      │  │   Security      │  │   Alerts        │ │
│  │   Performance   │  │   Monitoring    │  │   & Notifications│ │
│  │                 │  │                 │  │                 │ │
│  │ • Query Time    │  │ • Failed Logins │  │ • System Down   │ │
│  │ • Connection    │  │ • Auth Attempts │  │ • High Error    │ │
│  │   Pool          │  │ • Security      │  │   Rate          │ │
│  │ • Cache Hit     │  │   Events        │  │ • Performance   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 🚀 **Getting Started**

### **Prerequisites**
- Java 21 or higher
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+
- Docker & Docker Compose (optional)

### **Quick Start**
```bash
# Clone the repository
git clone <repository-url>
cd msante-backend

# Configure environment
cp env.production.example .env.production
# Edit .env.production with your configuration

# Start with Docker
docker-compose -f docker-compose.prod.yml up -d

# Or start manually
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

### **Development Setup**
```bash
# Install dependencies
mvn install

# Run tests
mvn test

# Start development server
mvn spring-boot:run -Dspring-boot.run.run.profiles=development

# Run Phase 4 validation
mvn test -Dtest=Phase4ValidationTest
```

### **Production Deployment**
```bash
# Build production JAR
mvn clean package -Pproduction

# Run with production profile
java -jar target/msante-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

## 📚 **Additional Resources**

- [API Documentation](http://localhost:8080/swagger-ui.html)
- [Health Check](http://localhost:8080/actuator/health)
- [Metrics](http://localhost:8080/actuator/metrics)
- [Grafana Dashboard](http://localhost:3000)
- [Prometheus](http://localhost:9090)

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**🏥 M-Santé - Empowering Healthcare in Senegal**  
**Built with ❤️ using Spring Boot, Java 21, and modern cloud-native technologies**
