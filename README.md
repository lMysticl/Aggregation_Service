# User Aggregation Service

A Spring Boot service that aggregates user data from multiple databases (PostgreSQL and MongoDB).

## Features

- REST API endpoint `/users` for retrieving aggregated user data
- Support for multiple data sources (PostgreSQL and MongoDB)
- Caching with Caffeine for improved performance
- OpenAPI documentation with Swagger UI
- Automatic database schema updates
- Debug-friendly SQL logging
- Docker support for easy deployment

## Prerequisites

For Docker deployment:
- Docker
- Docker Compose

For local development:
- Java 21 or higher
- Maven
- PostgreSQL 12 or higher
- MongoDB 6 or higher

## Quick Start with Docker

1. Clone the repository:
```bash
git clone <repository-url>
cd aggregation-service
```

2. Build and start the services using Docker Compose:
```bash
docker-compose up -d
```

The application will be available at http://localhost:8080

## Local Development Setup

1. Start PostgreSQL and create a database named `users_db`
2. Start MongoDB service
3. Configure the application in `application.yml` if needed (default configuration below)
4. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

## Docker Configuration

The application uses Docker Compose to set up the following services:

1. Application Service:
   - Built from the Dockerfile
   - Exposes port 8080
   - Connects to PostgreSQL and MongoDB containers

2. PostgreSQL Service:
   - Uses PostgreSQL 15 Alpine image
   - Exposes port 5432
   - Creates database `users_db`
   - Persists data using Docker volume

3. MongoDB Service:
   - Uses MongoDB 6 image
   - Exposes port 27017
   - Persists data using Docker volume

## Configuration

The application uses the following default configuration in `application.yml`:

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: users_db
      auto-index-creation: true
  datasource:
    url: jdbc:postgresql://localhost:5432/users_db
    username: postgres
    password: root
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=300s
```

When running with Docker, the environment variables in docker-compose.yml will override these settings.

## API Documentation

OpenAPI documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Available Endpoints

- `GET /users`: Get all users from both PostgreSQL and MongoDB
- `GET /users/search?query={query}`: Search users by username across both databases

## Troubleshooting

### Docker Issues:
1. Check container status:
```bash
docker-compose ps
```

2. View container logs:
```bash
docker-compose logs -f app
```

3. Restart services:
```bash
docker-compose restart
```

### Local Development Issues:

1. MongoDB Connection Issues:
   - Ensure MongoDB service is running: `sc query MongoDB`
   - Start MongoDB if stopped: Run Command Prompt as Administrator and execute `net start MongoDB`

2. PostgreSQL Connection Issues:
   - Verify PostgreSQL service is running
   - Check database exists: `users_db`
   - Verify credentials in `application.yml`

## Logging

The application uses the following log levels:
- `DEBUG` for application-specific logs
- `INFO` for Spring Data operations
- `DEBUG` for SQL queries

These can be adjusted in the `application.yml` file.
