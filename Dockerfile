# Build stage
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
# Copy pom.xml and source code
COPY ./pom.xml .
COPY ./src ./src
# Build the application
RUN mvn clean package -DskipTests


# Runtime stage
FROM openjdk:17-jdk-alpine
WORKDIR /app
# Copy the JAR from build stage
COPY --from=build /app/target/*.jar ./app.jar

# Environment variable configuration
ENV mysql_url=db

# Expose the port your application runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]