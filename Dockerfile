FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app

# Add non-root user for security
RUN useradd -m appuser
USER appuser

# Copy built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port & Run application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]