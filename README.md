# Circuit Breaker Pattern in Spring Boot with Resilience4j

A Spring Boot application demonstrating the Circuit Breaker pattern using Resilience4j to build fault-tolerant microservices.

## Build Instructions

### Prerequisites
- Java 17 or later
- Apache Maven 3.8+

### Build the project
```bash
mvn clean install
```

This will compile the code, run tests, and package the application into a JAR file.

## Run Instructions

### Using Maven
```bash
mvn spring-boot:run
```

### Using the executable JAR
```bash
java -jar target/circuit-breaker-demo-1.0.0.jar
```

### Using Docker
```bash
# Build and run with Docker Compose
docker compose up --build

# Or build and run manually
docker build -t circuit-breaker-demo .
docker run -p 8080:8080 circuit-breaker-demo
```

The application will start on port `8080`.

## API Endpoints

| Endpoint | Description |
|---|---|
| `GET /api/users/{id}` | Returns user data (protected by circuit breaker) |
| `GET /api/circuit-breaker/state` | Returns current circuit breaker state (CLOSED, OPEN, HALF_OPEN) |
| `GET /api/circuit-breaker/metrics` | Returns circuit breaker metrics (failure rate, call counts, etc.) |
| `GET /actuator/circuitbreakers` | Actuator endpoint with detailed circuit breaker information |

## Circuit Breaker Configuration

The circuit breaker is configured in `application.yml` with the following properties:

- **failure-rate-threshold**: 50% — opens when 50% of calls fail
- **minimum-number-of-calls**: 5 — minimum calls before evaluating
- **sliding-window-type**: COUNT_BASED — evaluates last N calls
- **sliding-window-size**: 10 — window of 10 calls
- **wait-duration-in-open-state**: 10s — time before transitioning to half-open
- **permitted-number-of-calls-in-half-open-state**: 2 — test calls in half-open state

## How It Works

1. The application includes a mock external service that fails on every 2nd request
2. The `UserDataService.fetchUser()` method is annotated with `@CircuitBreaker`
3. When failures exceed 50% (5 out of 10), the circuit opens
4. Subsequent calls immediately return a fallback response (`UserDTO` with "Default User")
5. After 10 seconds, the circuit transitions to half-open and allows test requests
6. If test requests succeed, the circuit closes and normal operation resumes

## Testing Instructions

### Run the test script

Make sure the application is running, then:

```bash
# Make the script executable
chmod +x test-circuit-breaker.sh

# Run the test
./test-circuit-breaker.sh
```

### What to expect

The test script will:

1. **Check initial state** — Shows the circuit breaker state is `CLOSED`
2. **Trip the circuit** — Makes 12 calls to the user endpoint. The mock service fails every 2nd call, causing the circuit to open after ~5 failures
3. **Verify open state** — Checks that the state becomes `OPEN`
4. **Show fallback** — Makes a call while open and verifies the default fallback response is returned
5. **Wait** — Waits 10 seconds for `wait-duration-in-open-state` to pass
6. **Verify recovery** — Makes successful calls to transition the circuit to `HALF_OPEN` and then back to `CLOSED`
7. **Check metrics** — Displays the final circuit breaker metrics

### Using Postman

Import `api-collection.json` into Postman. It contains all API endpoints organized into three folders:

- **State & Health** — circuit breaker state, metrics, actuator endpoints
- **User Data** — fetch users by ID with success/fallback examples
- **Test Flow: Trip Circuit Breaker** — 10-step automated test sequence with assertions

### Video Demo

Refer to `DEMO_SCRIPT.md` for a full video walkthrough script covering build, test, and circuit breaker state transitions.

### Manual testing

```bash
# Check initial state
curl http://localhost:8080/api/circuit-breaker/state

# Fetch user data
curl http://localhost:8080/api/users/1

# Check metrics
curl http://localhost:8080/api/circuit-breaker/metrics

# Check actuator
curl http://localhost:8080/actuator/circuitbreakers
```

## Project Structure

```
├── pom.xml
├── README.md
├── Dockerfile
├── docker-compose.yml
├── test-circuit-breaker.sh
├── api-collection.json          (gitignored, for Postman)
├── DEMO_SCRIPT.md               (gitignored, video walkthrough)
├── src/
│   └── main/
│       ├── java/com/circuitbreaker/
│       │   ├── CircuitBreakerApplication.java
│       │   ├── config/AppConfig.java
│       │   ├── controller/
│       │   │   ├── CircuitBreakerInfoController.java
│       │   │   ├── MockExternalController.java
│       │   │   └── UserController.java
│       │   ├── dto/UserDTO.java
│       │   └── service/
│       │       ├── MockExternalService.java
│       │       └── UserDataService.java
│       └── resources/
│           └── application.yml
```
