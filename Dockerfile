FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Ensure correct JAR file location
RUN mkdir -p /app/target
RUN cp target/scm2.0-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "/app/app.jar"]
