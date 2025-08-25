#!/bin/bash

echo "🧪 Testing Phase 4 Fixes - M-Santé Backend"
echo "=============================================="
echo ""

# Step 1: Compile the project
echo "📦 Step 1: Compiling the project..."
mvn compile -q
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed!"
    exit 1
fi

echo ""

# Step 2: Run Phase 4 validation test
echo "🔍 Step 2: Running Phase 4 validation test..."
mvn test -Dtest=Phase4ValidationTest
if [ $? -eq 0 ]; then
    echo "✅ Phase 4 validation test passed!"
else
    echo "❌ Phase 4 validation test failed!"
    echo "Check the test output above for details."
fi

echo ""

# Step 3: Test individual endpoints (if application is running)
echo "🌐 Step 3: Testing individual endpoints..."
echo "Note: Make sure the application is running first with: mvn spring-boot:run -Dspring-boot.run.profiles=test"
echo ""

echo "Testing health endpoint..."
curl -s http://localhost:8080/actuator/health | head -1

echo "Testing doctors health endpoint..."
curl -s http://localhost:8080/api/doctors/health | head -1

echo "Testing doctors test-specialties endpoint..."
curl -s http://localhost:8080/api/doctors/test-specialties | head -1

echo ""
echo "🎯 Phase 4 testing complete!"
echo "Expected result: 100% success rate (7/7 tests passed)"
