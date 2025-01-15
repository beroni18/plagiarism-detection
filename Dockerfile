# Utiliser l'image OpenJDK 17
FROM openjdk:17-oracle

# Définir un volume pour les fichiers temporaires
VOLUME /tmp

# Copier le fichier JAR construit dans le conteneur
COPY target/plagiarism-detection-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port 8083, qui est utilisé par votre application
EXPOSE 8083

# Définir le point d'entrée pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]