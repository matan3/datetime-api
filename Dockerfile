# === Build stage ===
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy only Gradle build files & wrapper first (for caching)
COPY build.gradle settings.gradle gradlew gradlew.bat gradle/ /app/

# Ensure gradlew is executable
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

# Copy only the fat JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
