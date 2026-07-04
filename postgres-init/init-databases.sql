-- Execute automatiquement par l'image officielle postgres au tout
-- premier demarrage du conteneur (dossier /docker-entrypoint-initdb.d/).
-- Cree les 3 bases, une par microservice, au sein du meme serveur
-- PostgreSQL (un seul conteneur, trois bases logiquement separees).
CREATE DATABASE dicodb;
CREATE DATABASE joueurdb;
CREATE DATABASE partiedb;
