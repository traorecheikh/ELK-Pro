# Guide d'Installation du Stack Elastic

Ce guide vous aide à configurer le stack Elastic (Elasticsearch, Logstash, Kibana) avec Docker Compose. Ce stack inclut des outils pour la gestion et l'analyse des données.

## Prérequis

- **Docker** et **Docker Compose** doivent être installés sur votre machine.
- Connaissances de base sur Docker et Elasticsearch recommandées.

## Cloner le Repository

Clonez le repository sur votre machine locale.
`git clone https://github.com/traorecheikh/Elastic.git`

## Vue d'Ensemble

Le stack Elastic inclut les services suivants :

1. **Elasticsearch** : Le store de données central du stack.
2. **Logstash** : Outil pour collecter et envoyer des données vers Elasticsearch.
3. **Kibana** : Outil de visualisation des données stockées dans Elasticsearch.
4. **Setup** : Service qui initialise les utilisateurs et rôles dans Elasticsearch.

## Structure des Fichiers

- **.env** : Contient les variables d'environnement (mots de passe des utilisateurs).
- **docker-compose.yml** : Fichier de configuration de Docker Compose.
- **setup/** : Contient les scripts et rôles pour configurer Elasticsearch.

## Services

### Service Setup

Le service `setup` initialise les utilisateurs et rôles dans Elasticsearch. Ce service est nécessaire uniquement lors du premier démarrage.

### Service Elasticsearch

Démarre Elasticsearch, la base de données du stack. Il écoute sur les ports **9200** et **9300**.

### Service Logstash

Logstash collecte et envoie des données vers Elasticsearch. Il écoute sur les ports **5044** et **9600**.

### Service Kibana

Kibana permet de visualiser les données d'Elasticsearch. Il écoute sur le port **5601**.

## Utilisation

### Démarrer le Stack

Exécutez la commande suivante pour initializer les roles et utilisateurs:

`docker compose up setup`

Exécutez la commande suivante pour lancer la configuration docker :

`docker compose up`

Tout devrais tourner maintenant 

### Accéder à Kibana

Une fois le stack démarré, vous pouvez accéder à Kibana à l'adresse `http://localhost:5601`.

### Arrêter le Stack

Pour arrêter tous les services, utilisez la commande `docker-compose down`.

## Configuration

Les mots de passe des utilisateurs intégrés (comme `elastic`, `logstash_internal`, `kibana_system`, etc.) sont définis dans le fichier **.env** par defaut user= elastic et password= passer.

## Réseaux et Volumes

- **Réseau** : Le stack utilise un réseau de type bridge nommé `elk`.
- **Volume** : Les données d'Elasticsearch sont persistées dans un volume nommé `elasticsearch`.

## Dépannage

- **Elasticsearch ne démarre pas** : Consultez les logs avec `docker-compose logs elasticsearch` pour identifier les problèmes.
- **Problèmes de mots de passe** : Vérifiez les mots de passe dans le fichier **.env** si vous rencontrez des difficultés pour vous connecter à Kibana ou Elasticsearch.
