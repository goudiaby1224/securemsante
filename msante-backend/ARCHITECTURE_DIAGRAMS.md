# 🏗️ M-Santé Architecture Diagrams & Sequence Flows

## 📊 **System Architecture Overview**

### **High-Level System Architecture**
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

## 🔄 **Detailed Sequence Diagrams**

### **1. Complete User Authentication Flow**
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
     │                │                   │───storeInRedis──▶│                   │
     │                │                   │                   │───SET Redis────▶│
     │                │                   │                   │◀───Stored──────│
     │                │◀───JWT Token─────│                   │                   │
     │◀───Success────│                   │                   │                   │
     │                │                   │                   │                   │
     │───API Call────▶│                   │                   │                   │
     │                │───validateJWT────▶│                   │                   │
     │                │                   │───checkRedis────▶│                   │
     │                │                   │                   │───GET Redis────▶│
     │                │                   │                   │◀───Token Valid─│
     │                │◀───Authorized────│                   │                   │
     │◀───Data───────│                   │                   │                   │
```

### **2. Complete Appointment Booking Flow**
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
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───Available────│
     │                │                   │───createAppt─────▶│                   │
     │                │                   │                   │───INSERT───────▶│
     │                │                   │                   │◀───Success─────│
     │                │                   │◀───Appt Created──│                   │
     │                │                   │───sendNotification│                   │
     │                │                   │───updateCache────▶│                   │
     │                │                   │                   │───SET Redis────▶│
     │                │                   │                   │◀───Updated─────│
     │                │◀───Success───────│                   │                   │
     │◀───Booked─────│                   │                   │                   │
     │                │                   │                   │                   │
```

### **3. Complete Doctor Availability Management Flow**
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
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───No Conflict──│
     │                │                   │───createSlot─────▶│                   │
     │                │                   │                   │───INSERT───────▶│
     │                │                   │                   │◀───Success─────│
     │                │                   │◀───Slot Created──│                   │
     │                │                   │───updateCache────▶│                   │
     │                │                   │                   │───SET Redis────▶│
     │                │                   │                   │◀───Updated─────│
     │                │◀───Success───────│                   │                   │
     │◀───Created────│                   │                   │                   │
     │                │                   │                   │                   │
```

### **4. Complete Patient Search & Booking Flow**
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
     │───Book Slot───▶│                   │                   │                   │
     │                │───bookSlot───────▶│                   │                   │
     │                │                   │───createBooking──▶│                   │
     │                │                   │                   │───INSERT───────▶│
     │                │                   │                   │◀───Success─────│
     │                │◀───Booked───────│                   │                   │
     │◀───Confirmed─│                   │                   │                   │
```

### **5. Complete Analytics & Reporting Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │AnalyticsCtrl│    │AnalyticsSvc│    │AnalyticsRepo│   │   Database  │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───Get Metrics▶│                   │                   │                   │
     │                │───getMetrics────▶│                   │                   │
     │                │                   │───checkCache────▶│                   │
     │                │                   │                   │───GET Redis────▶│
     │                │                   │                   │◀───Cache Miss──│
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

### **6. Complete Error Handling & Resilience Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Frontend│    │   Service   │    │ErrorHandler │    │   Fallback  │   │   Database  │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───API Call────▶│                   │                   │                   │
     │                │───processRequest─▶│                   │                   │
     │                │                   │───tryOperation───▶│                   │
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───Error───────│
     │                │                   │◀───Operation Failed│                   │
     │                │                   │───retryOperation─▶│                   │
     │                │                   │                   │───SELECT───────▶│
     │                │                   │                   │◀───Error───────│
     │                │                   │◀───Retry Failed──│                   │
     │                │                   │───circuitBreaker──▶│                   │
     │                │                   │───fallbackValue──▶│                   │
     │                │                   │                   │───Return Fallback│
     │                │                   │◀───Fallback─────│                   │
     │                │◀───Resilient────│                   │                   │
     │◀───Response───│                   │                   │                   │
```

## 🗄️ **Data Flow Architecture**

### **Data Flow Between Components**
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Data Flow Architecture                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │   Frontend  │    │   API       │    │   Service   │    │ Repository  │   │
│  │   (Angular) │    │   Gateway   │    │   Layer     │    │   Layer     │   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           │                   │                   │                   │     │
│           │───HTTP Request───▶│                   │                   │     │
│           │                   │───Route Request──▶│                   │     │
│           │                   │                   │───Business Logic▶│     │
│           │                   │                   │                   │     │
│           │                   │                   │◀───Data Access──│     │
│           │                   │◀───Processed─────│                   │     │
│           │◀───HTTP Response─│                   │                   │     │
│           │                   │                   │                   │     │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │   Cache     │    │   Database  │    │   Logs      │    │   Metrics   │   │
│  │   (Redis)   │    │   (MySQL)   │    │ (Logback)   │    │(Prometheus) │   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           ▲                   ▲                   ▲                   ▲     │
│           │                   │                   │                   │     │
│           └───────────────────┼───────────────────┼───────────────────┘     │
│                               │                   │                         │
│                    ┌──────────┴───────────────────┴─────────────────────┐   │
│                    │              Service Layer                          │   │
│                    │  • Data Caching & Invalidation                     │   │
│                    │  • Transaction Management                          │   │
│                    │  • Audit Logging                                   │   │
│                    │  • Performance Monitoring                          │   │
│                    └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🔒 **Security Architecture Flow**

### **Complete Security Flow**
```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Request │    │   CORS      │    │JWT Validator│    │SecurityCtx  │    │AuthProvider │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     │                │                   │                   │                   │
     │───HTTP Request▶│                   │                   │                   │
     │                │───Validate Origin▶│                   │                   │
     │                │◀───Origin Valid──│                   │                   │
     │                │───Check JWT──────▶│                   │                   │
     │                │                   │───validateJWT────▶│                   │
     │                │                   │                   │───verifyToken────▶│
     │                │                   │                   │                   │
     │                │                   │                   │◀───Valid Token──│
     │                │                   │◀───Set Context───│                   │
     │                │                   │───Check Roles────▶│                   │
     │                │                   │                   │───Validate RBAC─▶│
     │                │                   │                   │                   │
     │                │                   │                   │◀───Authorized───│
     │                │                   │◀───Access Granted│                   │
     │                │◀───Authenticated─│                   │                   │
     │◀───Authorized─│                   │                   │                   │
     │                │                   │                   │                   │
```

## 📊 **Performance & Caching Architecture**

### **Multi-Layer Caching Strategy**
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Multi-Layer Caching Strategy                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │   Browser   │    │   CDN       │    │   Load      │    │ Application │   │
│  │   Cache     │    │   Cache     │    │   Balancer  │    │   Cache     │   │
│  │             │    │             │    │   Cache     │    │   (Redis)   │   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           │                   │                   │                   │     │
│           │───Cache Miss─────▶│                   │                   │     │
│           │                   │───Cache Miss─────▶│                   │     │
│           │                   │                   │───Cache Miss─────▶│     │
│           │                   │                   │                   │     │
│           │                   │                   │◀───Data from DB──│     │
│           │                   │◀───Cached Data───│                   │     │
│           │◀───Cached Data───│                   │                   │     │
│           │                   │                   │                   │     │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │   Database  │    │   Query     │    │   Result    │    │   Cache     │   │
│  │   (MySQL)   │    │   Cache     │    │   Cache     │    │   Invalidation│   │
│  │             │    │   (Hibernate)│   │   (Redis)   │    │   (Events)  │   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           ▲                   ▲                   ▲                   ▲     │
│           │                   │                   │                   │     │
│           └───────────────────┼───────────────────┼───────────────────┘     │
│                               │                   │                         │
│                    ┌──────────┴───────────────────┴─────────────────────┐   │
│                    │              Cache Management                       │   │
│                    │  • TTL Management                                 │   │
│                    │  • Cache Invalidation                             │   │
│                    │  • Cache Warming                                  │   │
│                    │  • Cache Statistics                               │   │
│                    └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🚀 **Deployment & Scaling Architecture**

### **Horizontal Scaling Strategy**
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Horizontal Scaling Strategy                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │   Load      │    │   App       │    │   App       │    │   App       │   │
│  │   Balancer  │    │   Instance 1│    │   Instance 2│    │   Instance N│   │
│  │ (HAProxy)   │    │             │    │             │    │             │   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           │                   │                   │                   │     │
│           │───Route Request──▶│                   │                   │     │
│           │                   │───Process───────▶│                   │     │
│           │                   │                   │───Process───────▶│     │
│           │                   │                   │                   │     │
│           │                   │◀───Response─────│                   │     │
│           │                   │                   │◀───Response─────│     │
│           │◀───Response───────│                   │                   │     │
│           │                   │                   │                   │     │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │   Shared    │    │   Shared    │    │   Shared    │    │   Shared    │   │
│  │   Database  │    │   Cache     │    │   Storage   │    │   Services  │   │
│  │  (MySQL)    │    │  (Redis)    │    │   (S3)      │    │ (Monitoring)│   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           ▲                   ▲                   ▲                   ▲     │
│           │                   │                   │                   │     │
│           └───────────────────┼───────────────────┼───────────────────┘     │
│                               │                   │                         │
│                    ┌──────────┴───────────────────┴─────────────────────┐   │
│                    │              Scaling Benefits                       │   │
│                    │  • Load Distribution                               │   │
│                    │  • High Availability                               │   │
│                    │  • Fault Tolerance                                 │   │
│                    │  • Performance Scaling                             │   │
│                    └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 📈 **Monitoring & Observability Architecture**

### **Complete Monitoring Stack**
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Monitoring & Observability Stack                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐   │
│  │ Application│    │   System    │    │   Business  │    │   Security  │   │
│  │   Metrics  │    │   Metrics   │    │   Metrics   │    │   Metrics   │   │
│  │             │    │             │    │             │    │             │   │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘   │
│           │                   │                   │                   │     │
│           └───────────────────┼───────────────────┼───────────────────┘     │
│                               │                   │                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Prometheus Metrics Collector                     │   │
│  │                                                                     │   │
│  │  • HTTP Endpoints (/actuator/metrics)                              │   │
│  │  • Custom Business Metrics                                          │   │
│  │  • System Performance Metrics                                       │   │
│  │  • Security Event Metrics                                           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Grafana Dashboards                           │   │
│  │                                                                     │   │
│  │  • Real-time System Monitoring                                     │   │
│  │  • Business Intelligence Dashboards                                 │   │
│  │  • Performance Analytics                                            │   │
│  │  • Alert Management                                                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Alerting & Notification                      │   │
│  │                                                                     │   │
│  │  • PagerDuty Integration                                           │   │
│  │  • Email Notifications                                             │   │
│  │  • Slack Integration                                               │   │
│  │  • SMS Alerts                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

**🏗️ These architecture diagrams provide a comprehensive view of the M-Santé system design, data flows, and deployment strategies. Use them for system understanding, development planning, and stakeholder communication.**
