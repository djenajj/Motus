# Service Dico — Microservice Dictionnaire (jeu Motus)

Microservice Spring Boot qui gère le dictionnaire de mots du jeu Motus.
Il fait partie de l'application Motus en architecture microservices
(projet Master 2 MIAGE SITN — Applications Web orientées Services).

## Rôle du service

Le Service Dico est interrogé par le Service Partie. Il permet de :

- fournir un **mot mystère aléatoire** d'une longueur donnée ;
- vérifier qu'un **mot proposé existe** dans le dictionnaire.

## Stack technique

- Java 21
- Spring Boot 3.3.5 (Spring Web, Spring Data JPA, Validation)
- Base de données H2 (en mémoire)
- Maven (un Maven Wrapper `mvnw` est fourni : aucune installation requise)
- Docker (conteneurisation) et Kubernetes / MiniKube (déploiement)

## Compiler et exécuter le projet

### Option A — depuis IntelliJ IDEA

1. `File > Open` et sélectionner le dossier `service-dico` (ou le fichier `pom.xml`).
2. Attendre qu'IntelliJ télécharge les dépendances Maven.
3. Lancer la classe `ServiceDicoApplication`.

### Option B — en ligne de commande (avec le Maven Wrapper)

Sous Linux / macOS :

    ./mvnw spring-boot:run

Sous Windows :

    mvnw.cmd spring-boot:run

Le service démarre sur le port **8083**.

### Lancer les tests

    ./mvnw test

## Tester les API

Une fois le service démarré :

- Liste des mots :
  http://localhost:8083/api/mots

- Mot mystère aléatoire de 7 lettres :
  http://localhost:8083/api/mots/aleatoire?longueur=7

- Vérifier l'existence d'un mot :
  http://localhost:8083/api/mots/existe?valeur=MAISON

- Console de la base H2 (pour visualiser les données) :
  http://localhost:8083/h2-console
  (JDBC URL : `jdbc:h2:mem:dicodb`, utilisateur : `sa`, pas de mot de passe)

Pour ajouter un mot (méthode POST), avec curl :

    curl -X POST http://localhost:8083/api/mots \
         -H "Content-Type: application/json" \
         -d '{"valeur":"ORANGE","langue":"FR"}'

## API REST exposées

| Méthode | URI                              | Description                       |
|---------|----------------------------------|-----------------------------------|
| GET     | /api/mots                        | Liste tous les mots               |
| GET     | /api/mots/aleatoire?longueur=N   | Tire un mot mystère de N lettres  |
| GET     | /api/mots/existe?valeur=MOT      | Vérifie si un mot existe          |
| POST    | /api/mots                        | Ajoute un mot au dictionnaire     |

## Conteneurisation avec Docker

Construire l'image :

    docker build -t service-dico:1.0.0 .

Lancer le conteneur :

    docker run -p 8083:8083 service-dico:1.0.0

## Déploiement sur MiniKube

1. Démarrer MiniKube :

       minikube start

2. Utiliser le Docker de MiniKube pour construire l'image dans le cluster :

       eval $(minikube docker-env)
       docker build -t service-dico:1.0.0 .

3. Déployer :

       kubectl apply -f k8s-deployment.yaml

4. Obtenir l'URL d'accès :

       minikube service service-dico --url

## Structure du projet

    service-dico/
    +-- pom.xml                      Configuration Maven et dépendances
    +-- Dockerfile                   Conteneurisation Docker
    +-- k8s-deployment.yaml          Manifeste de déploiement Kubernetes
    +-- mvnw / mvnw.cmd              Maven Wrapper
    +-- src/main/java/.../dico/
    |   +-- ServiceDicoApplication   Point d'entrée Spring Boot
    |   +-- Mot                      Entité JPA (un mot)
    |   +-- MotRepository            Repository Spring Data JPA
    |   +-- DicoService              Logique métier
    |   +-- DicoController           Contrôleur REST (les API)
    |   +-- InitialisationDonnees    Chargement du dictionnaire au démarrage
    |   +-- MotIntrouvableException  Exception métier
    |   +-- GestionnaireExceptions   Gestion globale des erreurs HTTP
    +-- src/main/resources/
    |   +-- application.properties   Configuration (port, base H2...)
    +-- src/test/java/.../dico/
        +-- DicoControllerTest       Tests JUnit des API
