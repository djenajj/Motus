# Service Joueur — Microservice Joueur (jeu Motus)

Microservice Spring Boot qui gère les joueurs du jeu Motus :
inscription, consultation, historique des parties et classement.
Il fait partie de l'application Motus en architecture microservices
(projet Master 2 MIAGE SITN — Applications Web orientées Services).

## Rôle du service

- Enregistrer et consulter les joueurs.
- Conserver l'historique des résultats de leurs parties.
- Calculer et exposer le classement global par score.

Quand une partie se termine, le Service Partie envoie le résultat
à ce microservice (POST /api/joueurs/{id}/resultats).

## Stack technique

- Java 21
- Spring Boot 3.3.5 (Spring Web, Spring Data JPA, Validation)
- Base de données H2 (en mémoire)
- Maven (un Maven Wrapper `mvnw` est fourni : aucune installation requise)
- Docker et Kubernetes / MiniKube

## Compiler et exécuter

### Depuis IntelliJ IDEA

1. `File > Open` et sélectionner le dossier `service-joueur`.
2. Attendre le téléchargement des dépendances Maven.
3. Lancer la classe `ServiceJoueurApplication`.

### En ligne de commande

Sous Linux / macOS :

    ./mvnw spring-boot:run

Sous Windows :

    mvnw.cmd spring-boot:run

Le service démarre sur le port **8081**.

### Lancer les tests

    ./mvnw test

## Tester les API

Une fois le service démarré :

- Liste des joueurs :
  http://localhost:8081/api/joueurs

- Classement par score :
  http://localhost:8081/api/joueurs/classement

- Consulter un joueur :
  http://localhost:8081/api/joueurs/1

- Historique d'un joueur :
  http://localhost:8081/api/joueurs/1/resultats

- Console H2 :
  http://localhost:8081/h2-console
  (JDBC URL : `jdbc:h2:mem:joueurdb`, utilisateur : `sa`, sans mot de passe)

Enregistrer un joueur (POST), avec curl :

    curl -X POST http://localhost:8081/api/joueurs \
         -H "Content-Type: application/json" \
         -d '{"pseudo":"david","email":"david@dauphine.fr","motDePasse":"secret"}'

## API REST exposées

| Méthode | URI                            | Description                    |
|---------|--------------------------------|--------------------------------|
| POST    | /api/joueurs                   | Enregistre un joueur           |
| GET     | /api/joueurs                   | Liste tous les joueurs         |
| GET     | /api/joueurs/classement        | Classement par score           |
| GET     | /api/joueurs/{id}              | Consulte un joueur             |
| POST    | /api/joueurs/{id}/resultats    | Enregistre un résultat         |
| GET     | /api/joueurs/{id}/resultats    | Historique d'un joueur         |

## Calcul du score

Pour une partie gagnée : `score = 100 - (nbEssais x 10)`, avec un
minimum de 10 points. Une partie perdue rapporte 0 point.
Le score total du joueur est la somme des scores de ses parties.

## Conteneurisation avec Docker

    docker build -t service-joueur:1.0.0 .
    docker run -p 8081:8081 service-joueur:1.0.0

## Déploiement sur MiniKube

    minikube start
    eval $(minikube docker-env)
    docker build -t service-joueur:1.0.0 .
    kubectl apply -f k8s-deployment.yaml
    minikube service service-joueur --url

## Structure du projet

    service-joueur/
    +-- pom.xml                       Configuration Maven
    +-- Dockerfile                    Conteneurisation Docker
    +-- k8s-deployment.yaml           Manifeste Kubernetes
    +-- mvnw / mvnw.cmd               Maven Wrapper
    +-- src/main/java/.../joueur/
    |   +-- ServiceJoueurApplication  Point d'entrée Spring Boot
    |   +-- Joueur                    Entité JPA (relation OneToMany)
    |   +-- Resultat                  Entité JPA (relation ManyToOne)
    |   +-- JoueurRepository          Repository des joueurs
    |   +-- ResultatRepository        Repository des résultats
    |   +-- JoueurService             Logique métier (calcul du score)
    |   +-- JoueurController          Contrôleur REST
    |   +-- InitialisationDonnees     Joueurs de test au démarrage
    |   +-- JoueurIntrouvableException
    |   +-- PseudoDejaPrisException
    |   +-- GestionnaireExceptions    Gestion globale des erreurs HTTP
    +-- src/main/resources/
    |   +-- application.properties    Configuration (port 8081, H2...)
    +-- src/test/java/.../joueur/
        +-- JoueurControllerTest      Tests JUnit des API
