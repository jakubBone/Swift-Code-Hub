# Image for Java21
FROM eclipse-temurin:21-jdk

# Working directory setting
WORKDIR /app

# Copy JAR
COPY build/libs/RemitlySwiftHub.jar RemitlySwiftHub.jar

# Application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "RemitlySwiftHub.jar"]

