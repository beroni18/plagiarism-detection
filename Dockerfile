# Utiliser l'image Maven avec OpenJDK 17
FROM maven:3.8.1-openjdk-17

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier pom.xml dans le conteneur
COPY pom.xml ./

# Télécharger les dépendances (cela permet de profiter du cache Docker)
RUN mvn dependency:go-offline

# Copier le reste des fichiers source dans le conteneur
COPY src ./src

# Compiler le projet et générer le fichier JAR en ignorant les tests
RUN mvn clean install -DskipTests

# Copier le fichier JAR généré dans le conteneur
COPY target/plagiarism-detection-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port 8083, utilisé par votre application
EXPOSE 8083

# Définir le point d'entrée pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
