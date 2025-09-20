# ⏰ TimeTools API

> A lightweight REST API for converting datetimes between timezones and logging conversions.
Built with Java 17, Spring Boot 3, PostgreSQL, Docker, and GitHub Actions CI/CD.
Deployed automatically to Render with versioned Docker images.

---

## 🚀 Features

- Convert a datetime from one timezone to another
- Store conversion logs in PostgreSQL
- Retrieve all past conversions
- Swagger UI with request/response examples
- Automatic JSON error responses for invalid input
- Fully Dockerized for easy local deployment
- CI/CD pipeline: GitHub → Docker Hub → Render

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA, Hibernate
- **API Docs:** Springdoc OpenAPI / Swagger UI
- **Monitoring:** Spring Boot Actuator
- **CI/CD:** GitHub Actions, Docker Hub, Render
- **Build Tool:** Gradle

---

## 📦 Project Structure
    timetools/      <-- Root project folder
    ├─ src/         <-- Source code folder
    │  └─ main/
    │     ├─ java/com/timetools/datetimeapi/   <-- Your Java code package
    │     │  ├─ config/OpenApiConfig.java    <-- OpenAPI / Swagger configuration
    │     │  ├─ controller/ConvertController.java  <-- REST API endpoints
    │     │  ├─ exception/GlobalExceptionHandler.java <-- Centralized error handling
    │     │  ├─ model/        <-- Data classes / entities
    │     │  │  ├─ ConversionLog.java
    │     │  │  ├─ ConversionRequest.java
    │     │  │  └─ ErrorResponse.java
    │     │  ├─ repository/ConversionLogRepository.java  <-- Database access layer
    │     │  └─ service/ConvertService.java   <-- Business logic
    │     └─ resources/application.yml  <-- Central Spring Boot config
    ├─ build.gradle            <-- Gradle build script
    ├─ Dockerfile              <-- Docker instructions for your app
    ├─ docker-compose.yml      <-- Docker Compose file for app + Postgres
    └─.github/workflows/ci.yml       <--  GitHub Actions CI/CD

---

## ⚡ API Endpoints

### 1. Convert datetime

- **POST** `/api/convert`

**Request Body Example:**

```json
{
  "datetime": "2025-09-17T12:00:00",
  "fromTimezone": "UTC",
  "toTimezone": "Asia/Jerusalem"
}
```
**Response Example:**

```json
"2025-09-17T15:00:00+03:00"
```
**Error Response Example:**

```json
{
  "message": "Invalid datetime format",
  "status": 400
}
```

### 2. List all conversions

- **GET** `/api/conversions`

**Response Example:**
```json
[
  {
    "id": 1,
    "inputDatetime": "2025-09-17T12:00:00",
    "fromTimezone": "UTC",
    "toTimezone": "Asia/Jerusalem",
    "outputDatetime": "2025-09-17T15:00:00+03:00",
    "createdAt": "2025-09-17T14:05:30.123"
  }
]
```
---
## 🐳 Run Locally with Docker

### 1. Build and start containers:

```bash 
  docker-compose up --build
```

### 2. Swagger UI:
**Visit http://localhost:8080/swagger-ui.html**

### 3. Stop containers:
```bash 
  docker-compose down
```
---

## 🔄 CI/CD Workflow

- **On every push to `master` → Build, test, push dev image (`dev-<run_number>`) to Docker Hub**
- **On tagged release (`vX.Y.Z`) →**
  - **Build Docker image with tags: `latest`, `vX.Y.Z`, `<commit_sha>`**
  - **Push to Docker Hub**
  - **Trigger automatic deploy on Render**
---

## 🌍 Live Deployment
- **Swagger UI:**
  - **👉 https://timetools-api-latest.onrender.com/swagger-ui/index.html#/**
- **Health check:**
    - **👉 https://timetools-api-latest.onrender.com/actuator/health**
---
## ⚙️ Configuration

**Environment variables used in `application.yml`:**
- **`SPRING_DATASOURCE_URL` – PostgreSQL URL**
- **`SPRING_DATASOURCE_USERNAME` – Database username**
- **`SPRING_DATASOURCE_PASSWORD` – Database password**
> For CI/CD, Docker, and Render deployment, these variables are set via GitHub Secrets and Render dashboard.


