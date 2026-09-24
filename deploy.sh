#!/bin/bash

# --- CONFIGURATION ---
TOMCAT_PATH="/home/edinah/Documents/logiciel/tomcat"
WAR_NAME="EwinFramework"
SOURCE_WAR="target/EwinFramework.war"

# Dossiers de destination
DEST1="/home/edinah/Documents/L3/S5/Web Dynamique/Projet Framework/Test_Ewin/src/main/webapp/WEB-INF/lib"
DEST2="/home/edinah/Documents/L3/S5/Web Dynamique/Projet Framework/Test_Ewin/lib"

echo "🚀 Début du déploiement de $WAR_NAME..."

# 1. Compilation Maven
echo "📦 Compilation du projet..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation Maven. Arrêt."
    exit 1
fi

# 2. Copie du JAR dans les projets de test
echo "📂 Copie des bibliothèques..."

JAR_FILE=$(find target -maxdepth 1 -name "*.jar" | head -n 1)

if [ -f "$JAR_FILE" ]; then
    cp "$JAR_FILE" "$DEST1/"
    cp "$JAR_FILE" "$DEST2/"
    echo "✅ Copie effectuée : $(basename "$JAR_FILE")"
else
    echo "❌ Aucun fichier JAR trouvé dans target/"
    exit 1
fi