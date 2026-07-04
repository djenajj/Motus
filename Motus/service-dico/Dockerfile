# ===================================================================
# Dockerfile du Service Dico
# Conteneurise le microservice en deux etapes (multi-stage build) :
#  1. une etape "build" qui compile le projet avec Maven ;
#  2. une etape "run" legere qui ne contient que le JAR + une JVM.
# ===================================================================

# ---- Etape 1 : compilation ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# On copie d'abord le pom.xml pour profiter du cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Puis le code source
COPY src ./src
# Compilation et creation du JAR (sans relancer les tests ici)
RUN mvn clean package -DskipTests -B

# ---- Etape 2 : execution ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# On recupere uniquement le JAR produit a l'etape precedente
COPY --from=build /app/target/service-dico-1.0.0.jar app.jar
# Le microservice ecoute sur le port 8083
EXPOSE 8083
# Commande de demarrage du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]
