# ===================================================================
# Dockerfile du Service Joueur
# Conteneurise le microservice en deux etapes (multi-stage build).
# ===================================================================

# ---- Etape 1 : compilation ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Etape 2 : execution ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/service-joueur-1.0.0.jar app.jar
# Le microservice ecoute sur le port 8081
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
