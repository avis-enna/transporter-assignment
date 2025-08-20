# Transporter Assignment System

A Spring Boot application that optimizes transporter assignments to shipping lanes while minimizing costs.

## What it does

This system helps logistics companies assign their transporters to different shipping routes in the most cost-effective way. Given a set of lanes (routes) and transporters with their quotes, it finds the optimal assignment that covers all lanes at minimum cost.

## Problem it solves

In logistics, companies need to assign transporters to various shipping lanes. Each transporter provides quotes for different lanes, and the goal is to:
- Cover all lanes with transporters
- Minimize the total cost
- Respect constraints (like maximum number of transporters to use)

## Quick Start

```bash
# Clone and run
git clone https://github.com/avis-enna/transporter-assignment.git
cd transporter-assignment
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

API Documentation: http://localhost:8080/api/v1/swagger-ui.html

## Features

- **Cost Optimization**: Finds the minimum cost assignment of transporters to lanes
- **Constraint Handling**: Respects business rules like maximum transporters limit
- **Data Validation**: Validates input data for consistency and completeness
- **REST APIs**: Clean API endpoints for data input and optimization
- **Health Checks**: Monitor system health and data status
- **Swagger Documentation**: Interactive API documentation

## How it works

1. **Input Data**: Submit lanes and transporters with their quotes
2. **Validation**: System validates the data for completeness
3. **Optimization**: Algorithm finds the minimum cost assignment
4. **Results**: Returns the optimal assignment with total cost

## API Endpoints

### Input Data
- `POST /api/v1/transporters/input` - Submit lanes and transporters data
- `GET /api/v1/transporters/input/statistics` - Get data statistics
- `DELETE /api/v1/transporters/input` - Clear all data

### Assignment Optimization
- `POST /api/v1/transporters/assignment` - Get optimal assignment
- `GET /api/v1/transporters/assignment/capabilities` - Check if optimization is possible

## Example Usage

### 1. Submit Input Data
```bash
curl -X POST http://localhost:8080/api/v1/transporters/input \
  -H "Content-Type: application/json" \
  -d @test_data.json
```

### 2. Get Optimal Assignment
```bash
curl -X POST http://localhost:8080/api/v1/transporters/assignment \
  -H "Content-Type: application/json" \
  -d '{"maxTransporters": 3}'
```

The system will return the optimal assignment with minimum total cost.

## Testing

Run the test suite:
```bash
mvn test
```

The project includes unit tests, integration tests, and validation tests.

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Database access
- **H2 Database** - In-memory database for development
- **Maven** - Build tool
- **JUnit 5** - Testing framework

## Running the Application

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package
java -jar target/transporter-assignment-0.0.1-SNAPSHOT.jar
```

## Project Structure

- `src/main/java` - Application source code
- `src/test/java` - Test cases
- `test_data.json` - Sample input data for testing
- `demo.sh` - Demo script to test the APIs
