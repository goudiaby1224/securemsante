#!/bin/bash

# ============================================================================
# M-Santé Local Development System Startup Script
# ============================================================================
# This script starts the complete M-Santé system locally using Docker Compose

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install it and try again."
        exit 1
    fi
    print_success "Docker Compose is available"
}

# Function to create necessary directories
create_directories() {
    print_status "Creating necessary directories..."
    
    mkdir -p logs
    mkdir -p config
    mkdir -p nginx/ssl
    mkdir -p nginx/conf.d
    mkdir -p haproxy
    mkdir -p monitoring/prometheus
    mkdir -p monitoring/grafana/dashboards
    mkdir -p monitoring/grafana/datasources
    mkdir -p monitoring/grafana/plugins
    mkdir -p monitoring/logstash/pipeline
    mkdir -p monitoring/logstash/config
    mkdir -p redis
    mkdir -p db/init
    
    print_success "Directories created"
}

# Function to create basic configuration files
create_config_files() {
    print_status "Creating basic configuration files..."
    
    # Create basic nginx configuration
    if [ ! -f nginx/nginx.conf ]; then
        cat > nginx/nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    upstream msante_backend {
        server msante-backend:8080;
    }
    
    server {
        listen 80;
        server_name localhost;
        
        location / {
            proxy_pass http://msante_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
EOF
        print_success "Nginx configuration created"
    fi
    
    # Create basic HAProxy configuration
    if [ ! -f haproxy/haproxy.cfg ]; then
        cat > haproxy/haproxy.cfg << 'EOF'
global
    log stdout format raw local0 info
    maxconn 4096

defaults
    log global
    mode http
    option httplog
    option dontlognull
    timeout connect 5000
    timeout client 50000
    timeout server 50000

frontend stats
    bind *:8404
    stats enable
    stats uri /stats
    stats refresh 10s

frontend msante_frontend
    bind *:80
    default_backend msante_backend

backend msante_backend
    balance roundrobin
    server msante1 msante-backend:8080 check
EOF
        print_success "HAProxy configuration created"
    fi
    
    # Create basic Prometheus configuration
    if [ ! -f monitoring/prometheus/prometheus.yml ]; then
        cat > monitoring/prometheus/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "rules/*.yml"

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'msante-backend'
    static_configs:
      - targets: ['msante-backend:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
EOF
        print_success "Prometheus configuration created"
    fi
    
    # Create basic Redis configuration
    if [ ! -f redis/redis.conf ]; then
        cat > redis/redis.conf << 'EOF'
bind 0.0.0.0
port 6379
requirepass RedisSecurePassword789!
appendonly yes
appendfsync everysec
save 900 1
save 300 10
save 60 10000
maxmemory 256mb
maxmemory-policy allkeys-lru
EOF
        print_success "Redis configuration created"
    fi
    
    # Create basic Redis Sentinel configuration
    if [ ! -f redis/sentinel.conf ]; then
        cat > redis/sentinel.conf << 'EOF'
port 26379
sentinel monitor mymaster redis 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 10000
sentinel parallel-syncs mymaster 1
EOF
        print_success "Redis Sentinel configuration created"
    fi
}

# Function to check if .env.local exists
check_env_file() {
    if [ ! -f .env.local ]; then
        print_warning ".env.local file not found. Creating from example..."
        if [ -f env.local.example ]; then
            cp env.local.example .env.local
            print_success ".env.local created from example"
        else
            print_error "env.local.example not found. Please create .env.local manually."
            exit 1
        fi
    else
        print_success ".env.local file found"
    fi
}

# Function to start the system
start_system() {
    local profile=${1:-default}
    
    print_status "Starting M-Santé system with profile: $profile"
    
    case $profile in
        "minimal")
            print_status "Starting minimal system (core services only)..."
            docker-compose -f docker-compose.local.yml up -d mysql redis msante-backend
            ;;
        "monitoring")
            print_status "Starting system with monitoring..."
            docker-compose -f docker-compose.local.yml up -d
            ;;
        "ha")
            print_status "Starting high-availability system..."
            docker-compose -f docker-compose.local.yml --profile ha up -d
            ;;
        "dev-tools")
            print_status "Starting system with development tools..."
            docker-compose -f docker-compose.local.yml --profile dev-tools up -d
            ;;
        "full")
            print_status "Starting full system with all profiles..."
            docker-compose -f docker-compose.local.yml --profile ha --profile dev-tools up -d
            ;;
        *)
            print_status "Starting default system..."
            docker-compose -f docker-compose.local.yml up -d
            ;;
    esac
    
    print_success "System started successfully!"
}

# Function to show system status
show_status() {
    print_status "Checking system status..."
    docker-compose -f docker-compose.local.yml ps
    
    echo ""
    print_status "Service URLs:"
    echo "  M-Santé Backend: http://localhost:8080"
    echo "  Nginx: http://localhost:80"
    echo "  HAProxy: http://localhost:8081"
    echo "  Prometheus: http://localhost:9090"
    echo "  Grafana: http://localhost:3000 (admin/${GRAFANA_PASSWORD:-GrafanaSecurePassword2024!})"
    echo "  Jaeger: http://localhost:16686"
    echo "  Elasticsearch: http://localhost:9200"
    echo "  Kibana: http://localhost:5601"
    echo "  Adminer: http://localhost:8082"
    echo "  Redis Commander: http://localhost:8083"
    echo "  Portainer: http://localhost:9000"
}

# Function to show logs
show_logs() {
    local service=${1:-msante-backend}
    print_status "Showing logs for service: $service"
    docker-compose -f docker-compose.local.yml logs -f $service
}

# Function to stop the system
stop_system() {
    print_status "Stopping M-Santé system..."
    docker-compose -f docker-compose.local.yml down
    print_success "System stopped successfully!"
}

# Function to clean up the system
cleanup_system() {
    print_warning "This will remove all containers, volumes, and networks. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_status "Cleaning up M-Santé system..."
        docker-compose -f docker-compose.local.yml down -v --remove-orphans
        docker system prune -f
        print_success "System cleaned up successfully!"
    else
        print_status "Cleanup cancelled"
    fi
}

# Function to show help
show_help() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  start [PROFILE]    Start the system with specified profile"
    echo "  stop               Stop the system"
    echo "  restart [PROFILE]  Restart the system with specified profile"
    echo "  status             Show system status"
    echo "  logs [SERVICE]     Show logs for specified service"
    echo "  cleanup            Clean up all containers, volumes, and networks"
    echo "  help               Show this help message"
    echo ""
    echo "Profiles:"
    echo "  minimal            Core services only (MySQL, Redis, Backend)"
    echo "  monitoring         System with monitoring stack"
    echo "  ha                 High-availability system"
    echo "  dev-tools          System with development tools"
    echo "  full               Full system with all profiles"
    echo ""
    echo "Examples:"
    echo "  $0 start                    # Start default system"
    echo "  $0 start monitoring         # Start with monitoring"
    echo "  $0 start full               # Start full system"
    echo "  $0 logs msante-backend      # Show backend logs"
    echo "  $0 status                   # Show system status"
}

# Main script logic
main() {
    local command=${1:-start}
    local profile=${2:-default}
    
    case $command in
        "start")
            check_docker
            check_docker_compose
            create_directories
            create_config_files
            check_env_file
            start_system $profile
            show_status
            ;;
        "stop")
            stop_system
            ;;
        "restart")
            stop_system
            sleep 2
            start_system $profile
            show_status
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs $profile
            ;;
        "cleanup")
            cleanup_system
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        *)
            print_error "Unknown command: $command"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
