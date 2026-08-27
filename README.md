# Energy Community

Distributed Systems semester project that simulates an energy community.

The application consists of six independently startable components and uses RabbitMQ for asynchronous communication, PostgreSQL for persistence and a REST API for communication with the JavaFX GUI.

## Architecture

```text
Energy Producer ─┐
                 ├──> RabbitMQ ──> Usage Service ──> PostgreSQL
Energy User ─────┘                      │
                                       └──> RabbitMQ
                                              │
                                              v
                                 Current Percentage Service
                                              │
                                              v
                                         PostgreSQL
                                              │
                                              v
JavaFX GUI ── HTTP GET ──> REST API ──────────┘
```

## Components

### 1. Energy Producer Service

- Retrieves weather data from the Open-Meteo API.
- Uses sunshine duration to simulate photovoltaic energy production.
- Produces a new value after a random interval between 1 and 5 seconds.
- Sends messages to RabbitMQ.

### 2. Energy User Service

- Simulates electricity consumption.
- Consumption depends on the current time of day.
- Produces a new value after a random interval between 1 and 5 seconds.
- Sends messages to RabbitMQ.

### 3. Usage Service

- Consumes Producer and User messages from RabbitMQ.
- Aggregates values by hour.
- Stores:
  - community production
  - community consumption
  - grid consumption
- Uses available community energy before grid energy.
- Publishes an updated hourly usage message after every change.

### 4. Current Percentage Service

- Consumes usage updates from RabbitMQ.
- Only processes data for the current hour.
- Calculates:
  - community depletion percentage
  - grid portion percentage
- Stores the current result in PostgreSQL.

### 5. REST API

Provides read-only access to the energy data stored in PostgreSQL.

Available endpoints:

```text
GET /energy/current
GET /energy/historical?start=<datetime>&end=<datetime>
```

### 6. JavaFX GUI

- Communicates only with the REST API.
- Does not access PostgreSQL directly.
- Displays current community depletion.
- Displays current grid portion.
- Allows historical data to be queried for a selected period.
- Displays total production, community consumption and grid consumption.

## Technology

- Java 25
- Maven
- Spring Boot
- Spring AMQP
- RabbitMQ
- PostgreSQL
- Spring Data JPA
- Flyway
- JavaFX
- Open-Meteo API
- Docker Compose
- JUnit 5
- Mockito

## Requirements

Install:

- Java 25
- Maven
- Docker Desktop
- Git

The project can be opened with Visual Studio Code, IntelliJ IDEA or another Java IDE.

## Environment Variables

Database and RabbitMQ credentials are provided through environment variables and are not stored in the Java source code.

Required variables:

```text
DB_USERNAME
DB_PASSWORD
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
```

Optional connection variables have local defaults:

```text
DB_URL
RABBITMQ_HOST
RABBITMQ_PORT
```

Example for Windows PowerShell:

```powershell
[Environment]::SetEnvironmentVariable("DB_USERNAME", "<database-user>", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "<database-password>", "User")
[Environment]::SetEnvironmentVariable("RABBITMQ_USERNAME", "<rabbitmq-user>", "User")
[Environment]::SetEnvironmentVariable("RABBITMQ_PASSWORD", "<rabbitmq-password>", "User")
```

Restart the terminal or IDE after changing persistent environment variables.

## Start Infrastructure

From the repository root:

```powershell
docker compose -f docker/docker-compose.yml up -d
```

Check the containers:

```powershell
docker ps
```

The infrastructure uses:

```text
PostgreSQL:           localhost:5432
RabbitMQ:             localhost:5672
RabbitMQ Management:  localhost:15672
```

## Build and Test

Change into the Maven parent project:

```powershell
cd energy-community
```

Run all automated tests:

```powershell
mvn clean test
```

## Start the Application

Each component can be started independently in its own terminal.

Run the following commands from the `energy-community` directory.

### REST API

Starting the REST API first allows Flyway to initialize the database schema.

```powershell
mvn -pl rest-api spring-boot:run
```

### Usage Service

```powershell
mvn -pl usage-service spring-boot:run
```

### Current Percentage Service

```powershell
mvn -pl current-percentage-service spring-boot:run
```

### Energy Producer Service

```powershell
mvn -pl energy-producer-service spring-boot:run
```

### Energy User Service

```powershell
mvn -pl energy-user-service spring-boot:run
```

### JavaFX GUI

```powershell
mvn -pl gui-app javafx:run
```

## Message Format

Producer and User messages use the following structure:

```json
{
  "type": "PRODUCER",
  "association": "COMMUNITY",
  "kwh": 0.01,
  "datetime": "2026-08-27T14:00:00"
}
```

For consumption messages, `type` is:

```text
USER
```

## REST API Examples

### Current percentage

```text
GET http://localhost:8080/energy/current
```

### Historical data

```text
GET http://localhost:8080/energy/historical?start=2026-08-27T00:00:00&end=2026-08-27T23:59:59
```

The historical endpoint returns HTTP 400 when the start datetime is after the end datetime.

## Project Structure

```text
disys-group-c
├── docker
│   └── docker-compose.yml
│
├── energy-community
│   ├── energy-producer-service
│   ├── energy-user-service
│   ├── usage-service
│   ├── current-percentage-service
│   ├── rest-api
│   ├── gui-app
│   └── pom.xml
│
└── README.md
```

## Tests

Automated tests cover the main logic of:

- Energy Producer Service
- Energy User Service
- Usage Service
- Current Percentage Service
- REST API

Run the complete test suite with:

```powershell
mvn clean test
```