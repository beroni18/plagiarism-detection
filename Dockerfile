# Utiliser l'image OpenJDK 17
FROM openjdk:17-jdk

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR dans l'image
COPY target/plagiarism-detection-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application écoute
EXPOSE 8083

# Définir la variable d'environnement PORT
ENV PORT=8083

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
