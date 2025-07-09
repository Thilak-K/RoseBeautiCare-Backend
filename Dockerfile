# Use a lightweight Java base image
FROM eclipse-temurin:17-jdk-alpine
# Set working directory
WORKDIR /app
# Copy the compiled JAR file (adjust path if needed)
COPY target/*.jar app.jar
# Expose port 8080 (matches your local backend)
EXPOSE 8080
# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]