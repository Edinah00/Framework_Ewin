#!/bin/bash

# --- CONFIGURATION (À adapter selon ton PC) ---
TOMCAT_PATH="/home/edinah/Documents/logiciel/tomcat" # Chemin vers ton dossier Tomcat
WAR_NAME="EwinFramework"
SOURCE_WAR="target/EwinFramework.war"

echo "🚀 Début du déploiement de $WAR_NAME..."

# 1. Compilation avec Maven
echo "📦 Compilation du projet..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation Maven. Arrêt."
    exit 1
fi
