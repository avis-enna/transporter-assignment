#!/bin/bash

# Transporter Assignment System - Quick Start Script
# This script sets up and runs the application with sample data

set -e  # Exit on any error

echo "Transporter Assignment System - Quick Start"
echo "==========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed. Please install Java 17 or higher."
    echo "       Download from: https://adoptium.net/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java 17 or higher is required. Current version: $JAVA_VERSION"
    echo "       Download from: https://adoptium.net/"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed. Please install Maven."
    echo "       Download from: https://maven.apache.org/download.cgi"
    echo "       Or install via package manager:"
    echo "       - macOS: brew install maven"
    echo "       - Ubuntu: sudo apt install maven"
    exit 1
fi

echo "Java $JAVA_VERSION detected"
echo "Maven detected"
echo ""

# Clean and compile
echo "Building the application..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed. Please check the error messages above."
    exit 1
fi

echo "Build successful"
echo ""

# Start the application in background
echo "Starting the application..."
mvn spring-boot:run > application.log 2>&1 &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/v1/transporters/input/health > /dev/null 2>&1; then
        echo "Application started successfully!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "ERROR: Application failed to start within 30 seconds"
        echo "Check application.log for details:"
        tail -20 application.log
        kill $APP_PID 2>/dev/null
        exit 1
    fi
    sleep 1
done

echo ""
echo "Application is running at: http://localhost:8080"
echo "API Documentation: http://localhost:8080/api/v1/swagger-ui.html"
echo ""

# Load sample data
echo "Loading sample data..."
if curl -s -X POST http://localhost:8080/api/v1/transporters/input \
   -H "Content-Type: application/json" \
   -d @test_data.json > /dev/null; then
    echo "Sample data loaded successfully"
else
    echo "WARNING: Failed to load sample data, but application is running"
fi

echo ""
echo "Testing the optimization API..."
RESULT=$(curl -s -X POST http://localhost:8080/api/v1/transporters/assignment \
   -H "Content-Type: application/json" \
   -d '{"maxTransporters": 3}')

if echo "$RESULT" | grep -q "success"; then
    TOTAL_COST=$(echo "$RESULT" | grep -o '"totalCost":[0-9.]*' | cut -d':' -f2)
    echo "Optimization successful! Total cost: $TOTAL_COST"
else
    echo "WARNING: Optimization test failed, but application is running"
fi

echo ""
echo "Setup complete! Here's what you can do:"
echo ""
echo "Quick API Tests:"
echo "   # Check health"
echo "   curl http://localhost:8080/api/v1/transporters/input/health"
echo ""
echo "   # Get data statistics"
echo "   curl http://localhost:8080/api/v1/transporters/input/statistics"
echo ""
echo "   # Get optimal assignment"
echo "   curl -X POST http://localhost:8080/api/v1/transporters/assignment \\"
echo "        -H 'Content-Type: application/json' \\"
echo "        -d '{\"maxTransporters\": 3}'"
echo ""
echo "Web Interface:"
echo "   Open http://localhost:8080/api/v1/swagger-ui.html in your browser"
echo ""
echo "View logs:"
echo "   tail -f application.log"
echo ""
echo "To stop the application:"
echo "   kill $APP_PID"
echo "   # Or press Ctrl+C if running in foreground"
echo ""

# Keep script running and show logs
echo "Application logs (press Ctrl+C to exit):"
echo "========================================="
tail -f application.log
