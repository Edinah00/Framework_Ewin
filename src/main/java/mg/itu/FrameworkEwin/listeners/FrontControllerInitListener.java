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
import mg.itu.FrameworkEwin.spring.SpringContainer;

@WebListener
public class FrontControllerInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String nomPackage = sce.getServletContext().getInitParameter("controller-package");
        String beansPackage = sce.getServletContext().getInitParameter("beans-package");

        if (beansPackage != null && !beansPackage.isBlank()) {
            SpringContainer.init(beansPackage);
        }

        Utils utils = new Utils(nomPackage, "controller", "mg.itu.FrameworkEwin.annotations.controllers.MonControleur");
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

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}