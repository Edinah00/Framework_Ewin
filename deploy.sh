#!/bin/bash

# --- CONFIGURATION (À adapter selon ton PC) ---
TOMCAT_PATH="/home/edinah/Documents/logiciel/tomcat" # Chemin vers ton dossier Tomcat
WAR_NAME="EwinFramework"
SOURCE_WAR="target/EwinFramework-0.0.1-SNAPSHOT.war"

echo "🚀 Début du déploiement de $WAR_NAME..."

# 1. Compilation avec Maven
echo "📦 Compilation du projet..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation Maven. Arrêt."
    exit 1
fi

# 2. Arrêt de Tomcat (optionnel mais conseillé)
echo "🛑 Arrêt de Tomcat..."
$TOMCAT_PATH/bin/shutdown.sh 2>/dev/null

# 3. Nettoyage de l'ancien déploiement
echo "🧹 Nettoyage de l'ancien WAR et du dossier dans webapps..."
rm -rf $TOMCAT_PATH/webapps/$WAR_NAME
rm -f $TOMCAT_PATH/webapps/$WAR_NAME.war

# 4. Copie du nouveau WAR
echo "🚚 Copie du nouveau fichier WAR..."
cp $SOURCE_WAR $TOMCAT_PATH/webapps/$WAR_NAME.war

# 5. Relance de Tomcat
echo "🏁 Démarrage de Tomcat..."
$TOMCAT_PATH/bin/startup.sh

echo "✅ Déploiement terminé !"
echo "🌍 Accès ici : http://localhost:8080/$WAR_NAME/"
echo "📂 Pour voir les logs : tail -f $TOMCAT_PATH/logs/catalina.out"
