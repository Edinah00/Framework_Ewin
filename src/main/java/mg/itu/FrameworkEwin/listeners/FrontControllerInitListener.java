package mg.itu.FrameworkEwin.listeners;

import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;
import mg.itu.FrameworkEwin.annotations.utils.Utils;
import mg.itu.FrameworkEwin.models.Mapping;
import mg.itu.FrameworkEwin.models.URLMethod;

/**
 * Listener de contexte responsable de l'initialisation du Framework Ewin.
 *
 * <p>
 * Avant, tout ce travail était réalisé dans la méthode {@code init()} de
 * {@code FrontControllerServlet}. Le problème avec cette approche, c'est que
 * l'initialisation du Framework (scan des contrôleurs, construction de la
 * table des routes, etc.) se retrouvait couplée au cycle de vie d'une servlet
 * précise. Or {@code init()} d'une servlet n'est exécutée qu'au premier appel
 * de celle-ci (ou au démarrage si {@code load-on-startup} est configuré), ce
 * qui n'est pas garanti et dépend de la configuration du déploiement.
 * </p>
 *
 * <p>
 * En utilisant un {@link ServletContextListener}, on garantit que
 * l'initialisation est exécutée une seule fois, dès le démarrage de
 * l'application (déploiement du contexte), et ce indépendamment du nombre de
 * servlets ou de la manière dont elles sont appelées. C'est l'endroit idéal
 * pour préparer des données globales à toute l'application (ici, stockées en
 * tant qu'attributs du {@code ServletContext}).
 * </p>
 *
 * <p>
 * Grâce à l'annotation {@code @WebListener}, ce listener est automatiquement
 * détecté et enregistré par le conteneur de servlets, sans qu'il soit
 * nécessaire de le déclarer dans le fichier {@code web.xml}.
 * </p>
 */
@WebListener
public class FrontControllerInitListener implements ServletContextListener {

    /**
     * Appelée automatiquement par le conteneur au démarrage de l'application
     * (c'est-à-dire lorsque le contexte est initialisé).
     *
     * <p>
     * Reprend exactement la logique qui se trouvait auparavant dans
     * {@code FrontControllerServlet.init()} :
     * </p>
     * <ol>
     *   <li>Lecture du paramètre de contexte {@code controller-package}, qui
     *       indique le package racine où le Framework doit chercher les
     *       contrôleurs de l'application cliente ;</li>
     *   <li>Scan de ce package à la recherche des classes annotées
     *       {@code @MonControleur} ;</li>
     *   <li>Construction de la table de correspondance entre les couples
     *       (URL, méthode HTTP) et les méthodes de contrôleur associées ;</li>
     *   <li>Stockage de ces informations dans le {@code ServletContext}, afin
     *       qu'elles soient accessibles depuis {@code FrontControllerServlet}
     *       (et depuis n'importe quelle autre servlet de l'application) lors
     *       du traitement des requêtes.</li>
     * </ol>
     *
     * @param sce l'événement contenant le {@code ServletContext} de
     *            l'application en cours de démarrage
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String nomPackage = sce.getServletContext().getInitParameter("controller-package");

        Utils utils = new Utils(nomPackage, "controller", "mg.itu.FrameworkEwin.annotations.controllers.MonControleur");

        // scanControllers() et mapMethod_Url() déclarent "throws ServletException".
        // On regroupe les deux appels dans un seul bloc try/catch : si l'un des
        // deux échoue, on ne dispose d'aucune donnée exploitable (pas de
        // contrôleurs, pas de table de routes), donc on ne peut pas continuer
        // sereinement. Plutôt que d'avaler l'exception (ex. printStackTrace())
        // et de laisser l'application démarrer dans un état incohérent —
        // ce qui provoquerait une NullPointerException confuse dans
        // FrontControllerServlet dès la première requête — on relance une
        // exception non vérifiée. Cela fait échouer le déploiement
        // immédiatement, avec un message d'erreur explicite pointant vers la
        // vraie cause (ex. package de contrôleurs introuvable, annotation
        // mal configurée, etc.).
        try {
            List<String> controleurs = utils.scanControllers();
            Map<URLMethod, Mapping> mapURL = utils.mapMethod_Url();

            sce.getServletContext().setAttribute("listControlleur", controleurs);
            sce.getServletContext().setAttribute("MapURL", mapURL);
        } catch (ServletException e) {
            throw new RuntimeException(
                    "Echec de l'initialisation du Framework Ewin : impossible de scanner "
                            + "les contrôleurs du package \"" + nomPackage + "\".",
                    e);
        }
    }

    /**
     * Appelée automatiquement par le conteneur à l'arrêt de l'application.
     * Aucun nettoyage spécifique n'est nécessaire ici : les attributs du
     * {@code ServletContext} sont libérés automatiquement par le conteneur
     * lors de la destruction du contexte.
     *
     * @param sce l'événement contenant le {@code ServletContext} de
     *            l'application en cours d'arrêt
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Rien à nettoyer explicitement pour le moment.
    }
}