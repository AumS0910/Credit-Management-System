# Multi-stage build: Maven build stage
FROM maven:3.9.4-eclipse-temurin-11 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:11-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/credit-management-1.0.0.jar .

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables (configure these in your deployment platform)
# These are default values - override in production deployment
ENV DATABASE_URL=jdbc:postgresql://localhost:5432/restaurant_db
ENV DATABASE_USERNAME=postgres
ENV DATABASE_PASSWORD=password
ENV SERVER_PORT=8080
# PEXELS_API_KEY should be set in deployment environment variables

# Run the application
CMD ["java", "-jar", "credit-management-1.0.0.jar"]