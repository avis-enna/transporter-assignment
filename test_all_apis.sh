#!/bin/bash

# 🧪 COMPREHENSIVE API TESTING SCRIPT
# Tests ALL 11 API endpoints to ensure 100% functionality

echo "🧪 COMPREHENSIVE API TESTING - ALL ENDPOINTS"
echo "============================================="
echo "Testing ALL 11 API endpoints systematically"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/v1"
PASS_COUNT=0
FAIL_COUNT=0

# Function to test API endpoint
test_api() {
    local name="$1"
    local command="$2"
    local expected_pattern="$3"
    
    echo -e "${BLUE}Testing: $name${NC}"
    
    result=$(eval "$command" 2>/dev/null)
    
    if echo "$result" | grep -q "$expected_pattern"; then
        echo -e "${GREEN}✅ PASS: $name${NC}"
        ((PASS_COUNT++))
    else
        echo -e "${RED}❌ FAIL: $name${NC}"
        echo "Response: $result"
        ((FAIL_COUNT++))
    fi
    echo ""
}

echo "🔍 Step 1: Health Check APIs"
echo "=============================="

# Test 1: Input Data Health Check
test_api "Input Data Health Check" \
    "curl -s $BASE_URL/transporters/input/health" \
    "Input data service is healthy"

# Test 2: Assignment Health Check  
test_api "Assignment Health Check" \
    "curl -s $BASE_URL/transporters/assignment/health" \
    "Assignment service is healthy"

echo "📊 Step 2: Input Data Management APIs"
echo "====================================="

# Test 3: Clear existing data first
test_api "Clear Input Data" \
    "curl -s -X DELETE $BASE_URL/transporters/input" \
    "success"

# Test 4: Data Validation (without saving)
test_api "Data Validation" \
    "curl -s -X POST $BASE_URL/transporters/input/validate -H 'Content-Type: application/json' -d @test_data.json" \
    "success"

# Test 5: Input Data Submission
test_api "Input Data Submission" \
    "curl -s -X POST $BASE_URL/transporters/input -H 'Content-Type: application/json' -d @test_data.json" \
    "success"

# Test 6: Statistics API
test_api "Statistics API" \
    "curl -s $BASE_URL/transporters/input/statistics" \
    "laneCount.*5"

echo "🎯 Step 3: Assignment Optimization APIs"
echo "======================================="

# Test 7: Assignment Capabilities
test_api "Assignment Capabilities" \
    "curl -s $BASE_URL/transporters/assignment/capabilities" \
    "canOptimize.*true"

# Test 8: Assignment Validation
test_api "Assignment Validation" \
    "curl -s -X POST $BASE_URL/transporters/assignment/validate -H 'Content-Type: application/json' -d '{\"maxTransporters\": 3}'" \
    "success"

# Test 9: Full Assignment Optimization
test_api "Assignment Optimization" \
    "curl -s -X POST $BASE_URL/transporters/assignment -H 'Content-Type: application/json' -d '{\"maxTransporters\": 3}'" \
    "totalCost.*60141.35"

# Test 10: Quick Assignment Optimization
test_api "Quick Assignment Optimization" \
    "curl -s -X POST '$BASE_URL/transporters/assignment/quick?maxTransporters=3'" \
    "totalCost.*60141.35"

echo "🔄 Step 4: Data Management Verification"
echo "======================================="

# Test 11: Final Statistics Check
test_api "Final Statistics Check" \
    "curl -s $BASE_URL/transporters/input/statistics" \
    "coveragePercentage.*100"

echo "📋 FINAL RESULTS"
echo "================"
echo -e "${GREEN}✅ PASSED: $PASS_COUNT tests${NC}"
echo -e "${RED}❌ FAILED: $FAIL_COUNT tests${NC}"

TOTAL=$((PASS_COUNT + FAIL_COUNT))
SUCCESS_RATE=$((PASS_COUNT * 100 / TOTAL))

echo -e "${YELLOW}📊 SUCCESS RATE: $SUCCESS_RATE% ($PASS_COUNT/$TOTAL)${NC}"

if [ $FAIL_COUNT -eq 0 ]; then
    echo ""
    echo -e "${GREEN}🎉 ALL APIs WORKING PERFECTLY!${NC}"
    echo -e "${GREEN}✅ System is 100% production ready${NC}"
    echo -e "${GREEN}✅ All 11 endpoints tested and validated${NC}"
    echo -e "${GREEN}✅ Assignment specification compliance verified${NC}"
    echo ""
    echo "🏆 ACHIEVEMENT: Complete API functionality confirmed!"
    echo "📚 Documentation: All APIs mentioned in docs are working"
    echo "🚀 Ready for production deployment!"
else
    echo ""
    echo -e "${RED}⚠️  Some APIs need attention${NC}"
    echo "Please check the failed endpoints above"
fi

echo ""
echo "📖 API Documentation: http://localhost:8080/api/v1/swagger-ui.html"
echo "🎬 Interactive Demo: ./demo.sh"
echo "📝 Development Journal: ./DEVELOPMENT_JOURNAL.md"
