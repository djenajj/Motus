# Service Partie — Microservice Partie / cœur du jeu (Motus)

Microservice Spring Boot qui gère le déroulement des parties de Motus.
C'est le cœur du jeu : il crée les parties, calcule la réponse à
chaque proposition (lettres bien/mal placées/absentes) et communique
avec les autres microservices.

Projet Master 2 MIAGE SITN — Applications Web orientées Services.

## Rôle du service

- Créer une partie (en demandant un mot mystère au Service Dico).
- Traiter chaque proposition du joueur via l'algorithme Motus.
- Valider chaque mot proposé auprès du Service Dico.
- En fin de partie, envoyer le résultat au Service Joueur.

## Communication entre microservices

Ce service utilise **RestTemplate** pour appeler les autres services
en HTTP/REST (communication directe, sans bus de messages) :

    Service Partie  --GET-->  Service Dico    (mot mystère, validation)
    Service Partie  --POST->  Service Joueur  (résultat de fin de partie)

Les URLs sont configurées dans `application.properties` et peuvent
être surchargées par des variables d'environnement (utile en Docker
et Kubernetes).

## Stack technique

- Java 21
- Spring Boot 3.3.5 (Spring Web, Spring Data JPA, Validation)
- RestTemplate pour les appels inter-services
- Base de données H2 (en mémoire)
- Maven Wrapper fourni (`mvnw`)
- Docker et Kubernetes / MiniKube

## IMPORTANT : ordre de démarrage

Pour jouer une partie complète, les **trois services** doivent tourner :

1. Service Dico   (port 8083)
2. Service Joueur (port 8081)
3. Service Partie (port 8082)

Le Service Partie a besoin des deux autres pour fonctionner. S'ils
ne tournent pas, la création de partie échouera (le mot mystère vient
du Service Dico).

## Compiler et exécuter

### Depuis IntelliJ IDEA

1. `File > Open` et sélectionner le dossier `service-partie`.
2. Attendre le téléchargement des dépendances Maven.
3. Lancer la classe `ServicePartieApplication`.

### En ligne de commande

    ./mvnw spring-boot:run      (Linux/macOS)
    mvnw.cmd spring-boot:run    (Windows)

Le service démarre sur le port **8082**.

### Lancer les tests

    ./mvnw test

Les tests utilisent des mocks pour les Services Dico et Joueur :
ils n'ont PAS besoin que les autres services tournent.

## Scénario de test complet (les 3 services démarrés)

1. Créer une partie pour le joueur 1, mot de 6 lettres :

       curl -X POST http://localhost:8082/api/parties \
            -H "Content-Type: application/json" \
            -d '{"joueurId":1,"longueur":6}'

   La réponse contient l'id de la partie (par ex. 1) et son statut
   EN_COURS.

2. Proposer un mot :

       curl -X POST http://localhost:8082/api/parties/1/propositions \
            -H "Content-Type: application/json" \
            -d '{"motPropose":"JARDIN"}'

   La réponse contient la liste des propositions avec, pour chacune,
   le résultat encodé (ex. "BIEN_PLACEE,ABSENTE,MAL_PLACEE,...").

3. Consulter l'état de la partie :

       curl http://localhost:8082/api/parties/1

## API REST exposées

| Méthode | URI                                | Description                  |
|---------|-------------------------------------|------------------------------|
| POST    | /api/parties                        | Crée une partie              |
| GET     | /api/parties                        | Liste les parties (admin)    |
| GET     | /api/parties/{id}                   | État d'une partie            |
| POST    | /api/parties/{id}/propositions      | Soumet une proposition       |
| GET     | /api/parties/{id}/propositions      | Historique des essais        |

## L'algorithme Motus

Le calcul se fait en deux passes (classe `AlgorithmeMotus`) pour
gérer correctement les lettres en double :

1. Première passe : on repère les lettres **bien placées** et on
   décompte leurs occurrences dans le mot mystère.
2. Deuxième passe : pour les lettres restantes, on les marque
   **mal placées** seulement s'il reste des occurrences disponibles,
   sinon **absentes**.

Exemple : mot mystère TABLE, proposition ALLEE ->
MAL, MAL, ABSENTE, ABSENTE, BIEN_PLACEE. Le 2e L et le 1er E sont
ABSENTS car TABLE ne contient qu'un seul L et un seul E.

## Conteneurisation avec Docker

    docker build -t service-partie:1.0.0 .
    docker run -p 8082:8082 \
      -e SERVICES_DICO_URL=http://host.docker.internal:8083 \
      -e SERVICES_JOUEUR_URL=http://host.docker.internal:8081 \
      service-partie:1.0.0

## Déploiement sur MiniKube

    minikube start
    eval $(minikube docker-env)
    docker build -t service-partie:1.0.0 .
    kubectl apply -f k8s-deployment.yaml
    minikube service service-partie --url

Dans Kubernetes, les services se joignent par leur nom
(service-dico, service-joueur) : voir le fichier k8s-deployment.yaml.

## Structure du projet

    service-partie/
    +-- pom.xml
    +-- Dockerfile
    +-- k8s-deployment.yaml
    +-- mvnw / mvnw.cmd
    +-- src/main/java/.../partie/
    |   +-- ServicePartieApplication   Point d'entrée + bean RestTemplate
    |   +-- Partie                     Entité JPA (OneToMany)
    |   +-- Proposition                Entité JPA (ManyToOne)
    |   +-- StatutPartie               Enum (EN_COURS, GAGNEE, PERDUE)
    |   +-- StatutLettre               Enum (BIEN/MAL_PLACEE, ABSENTE)
    |   +-- AlgorithmeMotus            *** Algorithme central du jeu ***
    |   +-- DicoClient                 Appels REST au Service Dico
    |   +-- JoueurClient               Appels REST au Service Joueur
    |   +-- PartieRepository           Repository Spring Data
    |   +-- PartieService              Orchestration de la logique
    |   +-- PartieController           Contrôleur REST
    |   +-- DTOs                       Objets de requête
    |   +-- PartieIntrouvableException
    |   +-- CoupInvalideException
    |   +-- CommunicationServiceException
    |   +-- GestionnaireExceptions     Gestion globale des erreurs
    +-- src/main/resources/
    |   +-- application.properties     Config (port 8082, URLs services)
    +-- src/test/java/.../partie/
        +-- AlgorithmeMotusTest        Tests unitaires de l'algorithme
        +-- PartieControllerTest       Tests d'intégration (mocks REST)
