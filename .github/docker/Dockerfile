# Použijte OpenJDK 11 jako základní obraz
FROM openjdk:17-jdk-slim

# Nastavte pracovní adresář
WORKDIR /app

# Nakopírujte zdrojové soubory do kontejneru
COPY . .

# Nahraďte application.yml za application-prod.yml
RUN mv /app/src/main/resources/application-prod.yml /app/src/main/resources/application.yml

# Sestavte projekt Spring Boot
RUN ./gradlew build

# Spusťte aplikaci Spring Boot
CMD ["java", "-jar", "/app/build/libs/your-application.jar"]