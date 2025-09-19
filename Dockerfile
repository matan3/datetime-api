# === Build stage ===
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy Gradle wrapper and build files first (for caching)
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Download dependencies (cache layer)
RUN ./gradlew --no-daemon dependencies || true

# Copy source code
COPY src /app/src

# Build the project (skip tests for speed)
RUN ./gradlew --no-daemon clean build -x test

# === Runtime stage ===
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy only the fat JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
