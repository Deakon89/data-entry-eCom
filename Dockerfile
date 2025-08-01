# Etapa 1: Build del progetto
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


# Etapa 2: Immagine runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
