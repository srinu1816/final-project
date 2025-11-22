# Dockerfile
FROM eclipse-temurin:17-jdk-alpine

# Install Maven and other dependencies
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create directory for logs
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "target/flightservices-0.0.1-SNAPSHOT.jar"]
