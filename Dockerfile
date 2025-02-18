# Image for Java21
FROM eclipse-temurin:21-jdk

# Working directory setting
WORKDIR /app

# Copy JAR
COPY build/libs/RemitlySwiftHub.jar RemitlySwiftHub.jar

# Copy .xlsx
COPY src/main/resources/ /app/

# Application port
EXPOSE 8080

# Run the application
<<<<<<< HEAD
CMD ["java","-jar", "RemitlySwiftHub.jar"]
=======
CMD ["java", "-Dlog4j.configurationFile=/app/log4j2.properties","-jar", "RemitlySwiftHub.jar"]
>>>>>>> 1374262cd0d8286ad15fc21c0288e33bb8b8d956

