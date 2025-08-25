# 🚀 M-Santé Local Development Guide

## 🎯 **Quick Start**

Get the complete M-Santé system running locally in minutes!

### **1. Prerequisites**
- Docker Desktop running
- Docker Compose installed
- At least 8GB RAM available
- 10GB free disk space

### **2. One-Command Startup**
```bash
# Make script executable (first time only)
chmod +x start-local-system.sh

# Start the complete system
./start-local-system.sh start full
```

### **3. Access Your Services**
Once started, access these services:

| Service | URL | Credentials |
|---------|-----|-------------|
| **M-Santé Backend** | http://localhost:8080 | - |
| **Nginx Proxy** | http://localhost:80 | - |
| **Grafana Dashboards** | http://localhost:3000 | admin/GrafanaSecurePassword2024! |
| **Prometheus Metrics** | http://localhost:9090 | - |
| **Jaeger Tracing** | http://localhost:16686 | - |
| **Kibana Logs** | http://localhost:5601 | - |
| **Adminer (DB)** | http://localhost:8082 | - |
| **Redis Commander** | http://localhost:8083 | - |
| **Portainer** | http://localhost:9000 | - |

---

## 🏗️ **System Architecture**

The local system includes all components from the architecture diagrams:

### **Core Application**
- **M-Santé Backend** - Spring Boot application with all APIs
- **MySQL Database** - Primary data storage
- **Redis Cache** - Session storage and caching

### **API Gateway & Load Balancing**
- **Nginx** - Reverse proxy and load balancer
- **HAProxy** - Alternative load balancer (optional)

### **Monitoring & Observability**
- **Prometheus** - Metrics collection
- **Grafana** - Dashboards and visualization
- **Jaeger** - Distributed tracing
- **ELK Stack** - Logging (Elasticsearch, Logstash, Kibana)

### **Development Tools**
- **Adminer** - Database management
- **Redis Commander** - Redis management
- **Portainer** - Container management

---

## 🚀 **Startup Profiles**

### **Minimal Profile** (Core Services Only)
```bash
./start-local-system.sh start minimal
```
- MySQL Database
- Redis Cache
- M-Santé Backend

### **Monitoring Profile** (Default)
```bash
./start-local-system.sh start monitoring
```
- All core services
- Prometheus + Grafana
- Jaeger tracing
- ELK logging stack

### **High Availability Profile**
```bash
./start-local-system.sh start ha
```
- All monitoring services
- Redis Sentinel
- MySQL Replica

### **Development Tools Profile**
```bash
./start-local-system.sh start dev-tools
```
- All monitoring services
- Adminer database tool
- Redis Commander
- Portainer

### **Full Profile** (Everything)
```bash
./start-local-system.sh start full
```
- All services and profiles combined

---

## 🔧 **Configuration**

### **Environment Variables**
The system uses `.env.local` for configuration:

```bash
# Copy example configuration
cp env.local.example .env.local

# Edit configuration
nano .env.local
```

### **Key Configuration Options**
```bash
# Database
DB_USERNAME=msante_user
DB_PASSWORD=SecurePassword123!

# Redis
REDIS_PASSWORD=RedisSecurePassword789!

# JWT
JWT_SECRET=YourSuperSecureJWTSecretKeyForLocalDevelopment2024!

# Grafana
GRAFANA_PASSWORD=GrafanaSecurePassword2024!
```

---

## 📊 **Monitoring & Debugging**

### **Application Health**
```bash
# Check backend health
curl http://localhost:8080/actuator/health

# Check all actuator endpoints
curl http://localhost:8080/actuator
```

### **Database Health**
```bash
# Check MySQL
docker exec msante-mysql-local mysqladmin ping -h localhost -u root -pRootSecurePassword456!

# Check Redis
docker exec msante-redis-local redis-cli -a RedisSecurePassword789! ping
```

### **Service Logs**
```bash
# View all logs
./start-local-system.sh logs

# View specific service logs
./start-local-system.sh logs msante-backend
./start-local-system.sh logs mysql
./start-local-system.sh logs redis
```

---

## 🛠️ **Development Workflow**

### **1. Start Development Environment**
```bash
# Start with development tools
./start-local-system.sh start dev-tools
```

### **2. Make Code Changes**
```bash
# The backend source is mounted, changes are reflected immediately
# Edit files in the src/ directory
```

### **3. Test Your Changes**
```bash
# Run tests
mvn test

# Run Phase 4 validation
mvn test -Dtest=Phase4ValidationTest
```

### **4. Monitor Performance**
```bash
# View metrics in Grafana
open http://localhost:3000

# View traces in Jaeger
open http://localhost:16686

# View logs in Kibana
open http://localhost:5601
```

---

## 🔍 **Troubleshooting**

### **Common Issues**

#### **Port Already in Use**
```bash
# Check what's using a port
lsof -i :8080

# Stop conflicting services
sudo lsof -ti:8080 | xargs kill -9
```

#### **Docker Resources**
```bash
# Check Docker resource usage
docker system df

# Clean up unused resources
docker system prune -a
```

#### **Service Won't Start**
```bash
# Check service status
./start-local-system.sh status

# View service logs
./start-local-system.sh logs [service-name]

# Restart the system
./start-local-system.sh restart
```

### **Reset Everything**
```bash
# Complete cleanup
./start-local-system.sh cleanup

# Start fresh
./start-local-system.sh start full
```

---

## 📚 **API Testing**

### **Test Authentication**
```bash
# Register a user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "userType": "PATIENT"
  }'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### **Test Health Endpoints**
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Redis health
curl http://localhost:8080/actuator/health/redis
```

---

## 🎯 **Next Steps**

### **1. Explore the System**
- Visit http://localhost:3000 (Grafana) to see metrics
- Visit http://localhost:16686 (Jaeger) to see traces
- Visit http://localhost:5601 (Kibana) to see logs

### **2. Run the Frontend**
```bash
# In the Teranga directory
cd ../bank-app-ui
ng serve
```

### **3. Test End-to-End**
- Frontend: http://localhost:4200
- Backend: http://localhost:8080
- API Gateway: http://localhost:80

### **4. Customize Configuration**
- Edit `nginx/nginx.conf` for proxy settings
- Edit `monitoring/prometheus/prometheus.yml` for metrics
- Edit `redis/redis.conf` for cache settings

---

## 🆘 **Need Help?**

### **System Commands**
```bash
./start-local-system.sh help          # Show all commands
./start-local-system.sh status        # Check system status
./start-local-system.sh logs          # View all logs
```

### **Docker Commands**
```bash
# View all containers
docker ps -a

# View container logs
docker logs msante-backend-local

# Execute commands in container
docker exec -it msante-backend-local /bin/bash
```

### **Useful URLs**
- **Health Check**: http://localhost:8080/actuator/health
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Metrics**: http://localhost:8080/actuator/metrics
- **Environment**: http://localhost:8080/actuator/env

---

**🎉 You now have the complete M-Santé system running locally with all the resources from the architecture diagrams!**
