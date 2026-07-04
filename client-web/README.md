# Client web Motus

Interface web du jeu Motus, au look TV authentique. Ce client
n'est qu'un simple fichier HTML/CSS/JavaScript : il appelle les
microservices via `fetch()`, sans aucune dépendance ni installation.

## Prérequis

Les trois microservices doivent tourner :

| Service        | Port | Rôle                                   |
|----------------|------|----------------------------------------|
| service-joueur | 8081 | liste des joueurs, classement          |
| service-partie | 8082 | créer une partie, proposer un mot      |
| service-dico   | 8083 | (appelé indirectement par partie)      |

## Lancer le client

Comme le client appelle des services sur d'autres ports, il faut le
servir via un petit serveur HTTP local (l'ouvrir directement en
double-cliquant peut poser des soucis de sécurité navigateur).

Le plus simple, depuis le dossier `client-web` :

    # Avec Python (déjà installé sur macOS)
    python3 -m http.server 5500

Puis ouvrir http://localhost:5500 dans le navigateur.

Alternative avec VS Code : l'extension **Live Server**
(clic droit sur index.html > "Open with Live Server").

## Comment jouer

1. Choisir un joueur et la longueur du mot.
2. Cliquer sur « Lancer la partie ».
3. La première lettre est révélée (règle Motus). Taper un mot de la
   bonne longueur et valider (ou touche Entrée).
4. Les cases se colorent :
   - **rouge** : lettre bien placée,
   - **rond jaune** : lettre présente mais mal placée,
   - **bleu** : lettre absente.
5. 6 essais pour trouver. Le score est mis à jour et le classement
   se rafraîchit en fin de partie.

## Configuration

Si les ports des services changent, modifier en haut du `<script>`
dans index.html :

    const URL_JOUEUR = "http://localhost:8081";
    const URL_PARTIE = "http://localhost:8082";

## Note technique

Le contrôleur du Service Partie autorise les appels depuis le
navigateur grâce à l'annotation `@CrossOrigin`. Sans elle, le
navigateur bloquerait les requêtes (politique CORS).
