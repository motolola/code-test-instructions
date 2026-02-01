# URL Shortener Backend

Spring Boot REST API for the URL Shortener service.

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Running Locally

### Using Maven

```bash
cd app/backend
mvn spring-boot:run
```

### Building and Running JAR

```bash
cd app/backend
mvn clean package
java -jar target/url-shortener-backend-1.0.0-SNAPSHOT.jar
```

## Testing

Run tests with:

```bash
mvn test
```

## API Endpoints

- `GET /api/hello` - Returns "Hello World"

## Port

The application runs on port 8080 by default.

Visit: http://localhost:8080/api/hello

