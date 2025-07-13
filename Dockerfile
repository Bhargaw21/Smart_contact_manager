FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy wrapper and project files first (for build cache)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (helps Docker cache and speed builds)
RUN ./mvnw dependency:go-offline

# Now copy the rest of the application code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the application port
EXPOSE 8081

# Run the jar
CMD ["java", "-jar", "target/scm2.0-0.0.1-SNAPSHOT.jar"]
