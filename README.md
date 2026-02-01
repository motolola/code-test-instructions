# URL Shortener Coding Task

## Task

Build a simple **URL shortener** in a ** preferably JVM-based language** (e.g. Java, Kotlin).

It should:

- Accept a full URL and return a shortened URL.
- A shortened URL should have a randomly generated alias.
- Allow a user to **customise the shortened URL** if they want to (e.g. user provides `my-custom-alias` instead of a random string).
- Persist the shortened URLs across restarts.
- Expose a **decoupled web frontend** built with a modern framework (e.g., React, Next.js, Vue.js, Angular, Flask with templates). This can be lightweight form/output just to demonstrate interaction with the API. Feel free to use UI frameworks like Bootstrap, Material-UI, Tailwind CSS, GOV.UK design system, etc. to speed up development.
- Expose a **RESTful API** to perform create/read/delete operations on URLs.  
  → Refer to the provided [`openapi.yaml`](./openapi.yaml) for API structure and expected behaviour.
- Include the ability to **delete a shortened URL** via the API.
- **Have tests**.
- Be containerised (e.g. Docker).
- Include instructions for running locally.

## Rules

- Fork the repository and work in your fork. Do not push directly to the main repository.
- There is no time limit, we want to see something you are proud of. We would like to understand roughly how long you spent on it though.
- **Commit often with meaningful messages.**
- Write tests.
- The API should validate inputs and handle errors gracefully.
- The Frontend should show errors from the API appropriately.
- Use the provided [`openapi.yaml`](./openapi.yaml) as the API contract.
- Focus on clean, maintainable code.
- AI tools (e.g., GitHub Copilot, ChatGPT) are allowed, but please **do not** copy-paste large chunks of code. Use them as assistants, not as a replacement for your own work. We will be asking.

## Deliverables

- Working software.
- Decoupled web frontend (using a modern framework like React, Next.js, Vue.js, Angular, or Flask with templates).
- RESTful API matching the OpenAPI spec.
- Tests.
- A git commit history that shows your thought process.
- Dockerfile.
- README with:
  - How to build and run locally.
  - Example usage (frontend and API).
  - Any notes or assumptions.

## Project Setup

### IntelliJ IDEA Setup

For detailed instructions on how to open and configure this project in IntelliJ IDEA, see [INTELLIJ_SETUP.md](./INTELLIJ_SETUP.md).

**Quick Start:**
1. Open IntelliJ IDEA
2. File → Open → Select the `app/backend` folder
3. IntelliJ will automatically detect it as a Maven project
4. Wait for Maven to download dependencies and index

### Current Project Structure

```
code-test-instructions/
├── app/
│   ├── backend/          # Spring Boot backend (Java 21, Maven)
│   │   ├── src/
│   │   │   ├── main/java/com/urlshortener/
│   │   │   │   ├── UrlShortenerApplication.java
│   │   │   │   └── controller/
│   │   │   │       └── HelloController.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │   └── test/java/
│   │   │       └── ...
│   │   ├── pom.xml
│   │   └── README.md
│   └── frontend/         # Frontend (to be implemented)
├── openapi.yaml          # API specification
├── README.md
└── INTELLIJ_SETUP.md     # Detailed IntelliJ setup guide
```

```bash
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the jar directly
java -jar target/url-shortener-1.0.0.jar
```

The backend will start on `http://localhost:8080`

### Running the Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will start on `http://localhost:3000`

### Running Tests

**Backend Tests:**
```bash
cd backend
mvn test
```

**Frontend Tests:**
```bash
cd frontend
npm test
```

## 📖 API Documentation

The API follows the provided OpenAPI specification. Here are the available endpoints:

### 1. Shorten URL
**POST** `/shorten`

**Request Body:**
```json
{
  "fullUrl": "https://example.com/very/long/url",
  "customAlias": "my-custom-alias"  // Optional
}
```

**Response (201 Created):**
```json
{
  "shortUrl": "http://localhost:8080/my-custom-alias"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Alias 'my-custom-alias' is already in use"
}
```

### 2. Redirect to Full URL
**GET** `/{alias}`

**Response:** 302 redirect to the original URL

**Error (404):** Alias not found

### 3. List All URLs
**GET** `/urls`

**Response (200 OK):**
```json
[
  {
    "alias": "my-custom-alias",
    "fullUrl": "https://example.com/very/long/url",
    "shortUrl": "http://localhost:8080/my-custom-alias"
  }
]
```

### 4. Delete Shortened URL
**DELETE** `/{alias}`

**Response (204 No Content):** Successfully deleted

**Error (404):** Alias not found

## 🎨 Frontend Usage

1. **Shorten a URL:**
    - Enter a full URL in the input field
    - Optionally provide a custom alias
    - Click "Shorten URL"
    - The shortened URL will appear in the success message

2. **View All URLs:**
    - All shortened URLs are displayed in a table below the form
    - Click the short URL to test the redirect
    - Click the copy button (📋) to copy the short URL to clipboard

3. **Delete a URL:**
    - Click the "Delete" button next to any URL
    - Confirm the deletion in the dialog

## 🎯 Example Usage with cURL

**Shorten a URL with custom alias:**
```bash
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "fullUrl": "https://www.example.com/very/long/url",
    "customAlias": "example"
  }'
```

**Shorten a URL with random alias:**
```bash
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{
    "fullUrl": "https://www.example.com/another/long/url"
  }'
```

**Get all URLs:**
```bash
curl http://localhost:8080/urls
```

**Test redirect:**
```bash
curl -L http://localhost:8080/example
```

**Delete a shortened URL:**
```bash
curl -X DELETE http://localhost:8080/example
```

## 🏗️ Building for Production

### Backend
```bash
cd backend
mvn clean package
# JAR file will be in target/url-shortener-1.0.0.jar
```

### Frontend
```bash
cd frontend
npm run build
# Production build will be in build/
```

### Docker Images
```bash
# Build backend image
docker build -t url-shortener-backend ./backend

# Build frontend image
docker build -t url-shortener-frontend ./frontend
```

## 📝 Notes and Assumptions

### Design Decisions

1. **Database Choice**: H2 file-based database for simplicity while maintaining persistence across restarts. For production, this could easily be switched to PostgreSQL or MySQL.

2. **Alias Generation**: 6-character random alphanumeric strings provide 56.8 billion possible combinations, sufficient for most use cases.

3. **Java Records**: Used for immutable DTOs (request/response objects) to leverage Java 21 features and reduce boilerplate.

4. **Lombok**: Used for entity classes to minimize boilerplate code for getters, setters, builders, etc.

5. **CORS Configuration**: Enabled for local development. In production, this should be configured with specific allowed origins.

6. **Error Handling**: Global exception handler provides consistent error responses across the API.

7. **Validation**: Input validation at the controller level with custom validators for URL format and alias characters.

### Security Considerations

- Input validation prevents injection attacks
- Custom aliases are restricted to alphanumeric characters, hyphens, and underscores
- URLs must start with http:// or https://

### Scalability Considerations

For production deployment:
- Replace H2 with a production database (PostgreSQL, MySQL)
- Add Redis caching for frequently accessed URLs
- Implement rate limiting
- Add authentication/authorization
- Use a CDN for the frontend
- Add monitoring and logging

## 🧪 Testing

The project includes comprehensive tests:

- **Service Layer Tests**: Unit tests with Mockito
- **Controller Layer Tests**: Integration tests with MockMvc
- **Test Coverage**: Core business logic and API endpoints

## 📦 Dependencies

### Backend
- Spring Boot 3.2.2
- Spring Data JPA
- Spring Boot Validation
- H2 Database
- Lombok
- JUnit 5
- Mockito

### Frontend
- React 18
- React-Bootstrap
- Bootstrap 5
- Axios

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes with meaningful messages
4. Write tests for new features
5. Submit a pull request

## 📄 License

This project is part of a coding assessment.

## ⏱️ Time Spent

Approximate development time: 3-4 hours
- Backend implementation: 1.5 hours
- Frontend implementation: 1 hour
- Docker setup: 30 minutes
- Testing: 45 minutes
- Documentation: 15 minutes


