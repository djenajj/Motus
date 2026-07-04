# Motus — Application Web orientée Services (Microservices)

Projet **M2 MIAGE SITN — Applications Web orientées Services** (M. Menceur, 2025‑2026).
Application de gestion du jeu **Motus** réalisée sous forme de **microservices Spring Boot**.

**Binôme :** Djena Haddar · Jules Maurice

---

## 1. Architecture

```
                            ┌───────────────────────────┐
   Navigateur               │      API Gateway          │
   (client web nginx) ─────▶│  service-gateway  :8080   │  (point d'entrée unique)
   http://localhost:8090    └────────────┬──────────────┘
                                         │  routage par chemin
              ┌──────────────────────────┼───────────────────────────┐
              ▼                          ▼                           ▼
   ┌────────────────────┐   ┌────────────────────┐    ┌────────────────────┐
   │  service-joueur    │   │  service-partie    │    │   service-dico     │
   │  :8081             │   │  :8082 (cœur jeu)  │    │   :8083            │
   │  /api/joueurs/**   │   │  /api/parties/**   │    │   /api/mots/**     │
   └─────────┬──────────┘   └─────────┬──────────┘    └─────────┬──────────┘
             │                        │  REST interne            │
             │                        ├──────────────────────────┘
             ▼                        ▼
   ┌──────────────────────────────────────────────────────────────────────┐
   │            PostgreSQL  :5432   (1 base par service)                    │
   │            joueurdb   ·   partiedb   ·   dicodb                        │
   └──────────────────────────────────────────────────────────────────────┘
```

- **service-dico** — dictionnaire français : tire un mot mystère aléatoire, vérifie l'existence d'un mot, ajoute des mots (validés).
- **service-joueur** — inscription / connexion (pseudo + mot de passe), rôle (`JOUEUR`/`ADMIN`), historique des résultats, classement.
- **service-partie** — cœur du jeu : création de partie, propositions, calcul des lettres bien/mal placées, fin de partie. Appelle `service-dico` (mot + validité) et `service-joueur` (historisation + contrôle d'accès admin).
- **service-gateway** — **point d'entrée unique** (Spring Cloud Gateway) : le client n'appelle que `:8080`, le gateway route vers le bon service.
- **client-web** — client HTML/JS (grille Motus, clavier virtuel, tableau de bord admin).

Communication **REST** entre services ; persistance **JPA / Spring Data** sur **PostgreSQL** (une base par microservice).

## 2. Stack technique

| Élément | Version |
|---|---|
| Java (cible du projet) | 21 |
| Spring Boot | 3.3.5 |
| Spring Cloud (gateway) | 2023.0.3 |
| PostgreSQL | 16 |
| Build | Maven |
| Conteneurisation | Docker / Docker Compose |

> **Note JDK.** Le projet cible Java 21 (`<java.version>21</java.version>`). Il compile et s'exécute aussi avec un JDK plus récent (testé sur **JDK 26**).

## 3. Lancement

Prérequis : **Docker Desktop** démarré.

```bash
docker compose up -d --build
```

à exécuter dans le dossier du projet

Puis ouvrir le client web : **http://localhost:8090**

Arrêt : `docker compose down` (ajouter `-v` pour effacer aussi les données PostgreSQL).

### Ports exposés

| Service | URL |
|---|---|
| Client web | http://localhost:8090 |
| **API Gateway** (entrée unique) | http://localhost:8080 |
| service-joueur | http://localhost:8081 |
| service-partie | http://localhost:8082 |
| service-dico | http://localhost:8083 |
| PostgreSQL | localhost:5432 (user/mdp : `motus`/`motus`) |

## 4. Utilisation

### Jouer
**Un compte est obligatoire pour jouer.** À l'ouverture, l'écran d'accueil propose de **se connecter** ou de **créer un compte** (pseudo + mot de passe). Une fois connecté, le joueur accède à son profil (statistiques + historique), choisit la longueur du mot (5 à 8 lettres) et lance une partie. Chaque partie terminée est automatiquement historisée et prise en compte dans le classement.

> Les **comptes administrateur ne jouent pas** de partie : à la place de leur profil, ils accèdent au tableau de bord d'administration (voir ci-dessous).

### Espace d'administration
Se connecter avec le **compte administrateur créé automatiquement** :

- **pseudo :** `admin`
- **mot de passe :** `admin123`

Le tableau de bord admin permet de **lister / rechercher les parties** par joueur, statut et plage de dates, de trier par date ou performance, et d'**ajouter un mot** au dictionnaire.
