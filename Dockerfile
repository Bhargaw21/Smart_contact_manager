# Use official Eclipse Temurin image for JDK 21
FROM eclipse-temurin:21-jdk as builder

# Set working directory
WORKDIR /app

# Copy only what's needed for the build first
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Pre-download dependencies (for caching)
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application
RUN ./mvnw clean package -DskipTests

# --------------------------------------------
# Runtime stage (slim image for better performance)
# --------------------------------------------
FROM eclipse-temurin:21-jdk-jammy

# Set working directory in runtime container
WORKDIR /app

# Copy only the built jar from the builder stage
COPY --from=builder /app/target/scm2.0-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
