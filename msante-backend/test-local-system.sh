#!/bin/bash

# ============================================================================
# M-Santé Local System Test Script
# ============================================================================
# This script tests if all services are running correctly

set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
}

print_error() {
    echo -e "${RED}[FAIL]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Function to test HTTP endpoint
test_endpoint() {
    local url=$1
    local description=$2
    local expected_status=${3:-200}
    
    print_status "Testing $description: $url"
    
    if curl -s -f -o /dev/null -w "%{http_code}" "$url" | grep -q "$expected_status"; then
        print_success "$description is working"
        return 0
    else
        print_error "$description is not responding correctly"
        return 1
    fi
}

# Function to test database connection
test_database() {
    print_status "Testing MySQL database connection..."
    
    if docker exec msante-mysql-local mysqladmin ping -h localhost -u root -pRootSecurePassword456! > /dev/null 2>&1; then
        print_success "MySQL database is accessible"
        return 0
    else
        print_error "MySQL database is not accessible"
        return 1
    fi
}

# Function to test Redis connection
test_redis() {
    print_status "Testing Redis connection..."
    
    if docker exec msante-redis-local redis-cli -a RedisSecurePassword789! ping > /dev/null 2>&1; then
        print_success "Redis is accessible"
        return 0
    else
        print_error "Redis is not accessible"
        return 1
    fi
}

# Function to test Docker containers
test_containers() {
    print_status "Checking Docker containers..."
    
    local containers=(
        "msante-backend-local"
        "msante-mysql-local"
        "msante-redis-local"
        "msante-nginx-local"
        "msante-prometheus-local"
        "msante-grafana-local"
    )
    
    local all_running=true
    
    for container in "${containers[@]}"; do
        if docker ps --format "{{.Names}}" | grep -q "^$container$"; then
            print_success "Container $container is running"
        else
            print_error "Container $container is not running"
            all_running=false
        fi
    done
    
    if [ "$all_running" = true ]; then
        return 0
    else
        return 1
    fi
}

# Function to test API endpoints
test_api_endpoints() {
    print_status "Testing API endpoints..."
    
    local base_url="http://localhost:8080"
    local tests_passed=0
    local total_tests=0
    
    # Test health endpoint
    total_tests=$((total_tests + 1))
    if test_endpoint "$base_url/actuator/health" "Health endpoint" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test info endpoint
    total_tests=$((total_tests + 1))
    if test_endpoint "$base_url/actuator/info" "Info endpoint" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test metrics endpoint
    total_tests=$((total_tests + 1))
    if test_endpoint "$base_url/actuator/metrics" "Metrics endpoint" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test doctors health endpoint
    total_tests=$((total_tests + 1))
    if test_endpoint "$base_url/api/doctors/health" "Doctors health endpoint" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test doctors test-specialties endpoint
    total_tests=$((total_tests + 1))
    if test_endpoint "$base_url/api/doctors/test-specialties" "Doctors test-specialties endpoint" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    print_status "API Tests: $tests_passed/$total_tests passed"
    
    if [ $tests_passed -eq $total_tests ]; then
        return 0
    else
        return 1
    fi
}

# Function to test monitoring services
test_monitoring_services() {
    print_status "Testing monitoring services..."
    
    local tests_passed=0
    local total_tests=0
    
    # Test Prometheus
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:9090" "Prometheus" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Grafana
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:3000" "Grafana" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Jaeger
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:16686" "Jaeger" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Elasticsearch
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:9200" "Elasticsearch" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Kibana
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:5601" "Kibana" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    print_status "Monitoring Tests: $tests_passed/$total_tests passed"
    
    if [ $tests_passed -eq $total_tests ]; then
        return 0
    else
        return 1
    fi
}

# Function to test development tools
test_dev_tools() {
    print_status "Testing development tools..."
    
    local tests_passed=0
    local total_tests=0
    
    # Test Adminer
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:8082" "Adminer" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Redis Commander
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:8083" "Redis Commander" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    # Test Portainer
    total_tests=$((total_tests + 1))
    if test_endpoint "http://localhost:9000" "Portainer" 200; then
        tests_passed=$((tests_passed + 1))
    fi
    
    print_status "Development Tools Tests: $tests_passed/$total_tests passed"
    
    if [ $tests_passed -eq $total_tests ]; then
        return 0
    else
        return 1
    fi
}

# Function to run comprehensive test
run_comprehensive_test() {
    echo "============================================================================"
    echo "🏥 M-Santé Local System Comprehensive Test"
    echo "============================================================================"
    echo ""
    
    local overall_success=true
    
    # Test 1: Docker containers
    if test_containers; then
        print_success "All required containers are running"
    else
        print_error "Some containers are not running"
        overall_success=false
    fi
    
    echo ""
    
    # Test 2: Database connections
    if test_database && test_redis; then
        print_success "Database connections are working"
    else
        print_error "Database connections are not working"
        overall_success=false
    fi
    
    echo ""
    
    # Test 3: API endpoints
    if test_api_endpoints; then
        print_success "API endpoints are responding correctly"
    else
        print_error "Some API endpoints are not responding correctly"
        overall_success=false
    fi
    
    echo ""
    
    # Test 4: Monitoring services
    if test_monitoring_services; then
        print_success "Monitoring services are accessible"
    else
        print_error "Some monitoring services are not accessible"
        overall_success=false
    fi
    
    echo ""
    
    # Test 5: Development tools
    if test_dev_tools; then
        print_success "Development tools are accessible"
    else
        print_error "Some development tools are not accessible"
        overall_success=false
    fi
    
    echo ""
    echo "============================================================================"
    
    if [ "$overall_success" = true ]; then
        print_success "🎉 ALL TESTS PASSED! Your M-Santé system is running perfectly!"
        echo ""
        echo "🌐 Access your services:"
        echo "   • Backend API: http://localhost:8080"
        echo "   • Grafana: http://localhost:3000 (admin/GrafanaSecurePassword2024!)"
        echo "   • Prometheus: http://localhost:9090"
        echo "   • Jaeger: http://localhost:16686"
        echo "   • Kibana: http://localhost:5601"
        echo "   • Adminer: http://localhost:8082"
        echo "   • Redis Commander: http://localhost:8083"
        echo "   • Portainer: http://localhost:9000"
        return 0
    else
        print_error "❌ Some tests failed. Check the output above for details."
        echo ""
        echo "🔧 Troubleshooting tips:"
        echo "   • Check if all containers are running: docker ps"
        echo "   • View container logs: ./start-local-system.sh logs"
        echo "   • Restart the system: ./start-local-system.sh restart"
        echo "   • Check system status: ./start-local-system.sh status"
        return 1
    fi
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  --comprehensive    Run comprehensive system test (default)"
    echo "  --containers       Test only Docker containers"
    echo "  --databases        Test only database connections"
    echo "  --apis             Test only API endpoints"
    echo "  --monitoring       Test only monitoring services"
    echo "  --dev-tools        Test only development tools"
    echo "  --help             Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                    # Run comprehensive test"
    echo "  $0 --containers       # Test containers only"
    echo "  $0 --apis             # Test APIs only"
}

# Main script logic
main() {
    local option=${1:---comprehensive}
    
    case $option in
        "--comprehensive")
            run_comprehensive_test
            ;;
        "--containers")
            test_containers
            ;;
        "--databases")
            test_database && test_redis
            ;;
        "--apis")
            test_api_endpoints
            ;;
        "--monitoring")
            test_monitoring_services
            ;;
        "--dev-tools")
            test_dev_tools
            ;;
        "--help"|"-h"|"help")
            show_help
            ;;
        *)
            echo "Unknown option: $option"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
