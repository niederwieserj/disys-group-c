# Energy community

## Getting started

### Prerequisites

- Java 25 installed
- Maven installed
- Docker installed (for PostgreSQL)
- Git
- Intellij

### Start the database
```
cd docker
docker-compose up -d
```

### Build all components
```
mvn clean install
```

## Technology Stack
- Language: Java
- Build Tool: Maven (pom.xml files)
- Database: PostgreSQL (Docker container running on port 5432)
  - see file docker/README.md for details
- Message Queue: RabbitMQ (Docker container running on port 15672)
  - see file docker/README.md for details
- API Framework: Spring Boot
- GUI: JavaFX
- Weather API: Open-Meteo or OpenWeatherMap

## Requirements
Requirements can be found in [moodle](https://moodle.technikum-wien.at/mod/page/view.php?id=2246638)

## Project structure
The project consists of six individual modules that can be built and run separately. Additionally, there is a module "common" for classes that need to be shared across modules.

```
.
├── docker
│   ├── docker-compose.yml
│   └── README.md
├── energy-community
│   ├── common
│   ├── current-percentage-service
│   ├── energy-producer-service
│   ├── energy-user-service
│   ├── gui-app
│   ├── rest-api
│   └── usage-service
```
