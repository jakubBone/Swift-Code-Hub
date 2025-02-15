# Używamy obrazu Javy z OpenJDK 17
FROM eclipse-temurin:21-jdk

# Ustawienie katalogu roboczego
WORKDIR /app

# Kopiowanie pliku JAR z katalogu build/libs/
COPY build/libs/RemitlySwiftHub.jar RemitlySwiftHub.jar

# Eksponowanie portu aplikacji
EXPOSE 8080

# Uruchomienie aplikacji
CMD ["java", "-jar", "RemitlySwiftHub.jar"]

