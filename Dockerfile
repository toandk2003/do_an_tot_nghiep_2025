FROM openjdk:21-jdk-slim

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set working directory
WORKDIR /app

# Copy pom.xml first (for better caching)
COPY pom.xml .

# Download dependencies (cached layer nếu pom.xml không thay đổi)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application (biên dịch thành jar)
RUN mvn clean package -DskipTests

# Copy jar file to app directory
RUN cp target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
