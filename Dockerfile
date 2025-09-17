# === Build stage ===
FROM eclipse-temurin:17-jdk-jammy as build

WORKDIR /app

# Copy Gradle wrapper and build files first (for caching)
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Copy source code
COPY src /app/src

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the project (skip tests for speed)
RUN ./gradlew clean build -x test

# === Runtime stage ===
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy only the JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
