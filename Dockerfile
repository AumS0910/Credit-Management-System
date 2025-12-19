# Use OpenJDK 11 as the base image
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to download dependencies
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

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
CMD ["java", "-jar", "target/credit-management-1.0.0.jar"]