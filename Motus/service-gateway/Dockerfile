# ===================================================================
# Dockerfile de l'API Gateway
# Meme principe que les autres services : build multi-etapes
#  1. etape "build" : compile le projet avec Maven (JDK 21) ;
#  2. etape "run" : image legere JRE 21 + le JAR.
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
COPY --from=build /app/target/service-gateway-1.0.0.jar app.jar
# Le gateway ecoute sur le port 8080
EXPOSE 8080
# Commande de demarrage du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]
