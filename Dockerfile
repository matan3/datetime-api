# === Build stage ===
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy only Gradle wrapper and build files first (for caching)
COPY gradlew gradlew.bat /app/
COPY gradle/wrapper /app/gradle/wrapper
COPY build.gradle settings.gradle /app/

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Download dependencies (cache this layer)
RUN ./gradlew --no-daemon dependencies || true

# Copy full source code
COPY . /app

# Build the project (skip tests for speed)
RUN ./gradlew --no-daemon clean build -x test

# === Runtime stage ===
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy only the fat JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
