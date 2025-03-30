# Stage 1 : Build the app
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Stage 3 : Run
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]