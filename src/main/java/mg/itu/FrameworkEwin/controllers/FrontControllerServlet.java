package mg.itu.FrameworkEwin.controllers;

import java.lang.reflect.Method;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.lang.annotation.Annotation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.FrameworkEwin.annotations.utils.Utils;
import mg.itu.FrameworkEwin.models.*;
import mg.itu.FrameworkEwin.annotations.methods.*;

public class FrontControllerServlet extends HttpServlet {

    // @Override
    // public void init() throws ServletException {
    //     String nomPackage = getServletContext().getInitParameter("controller-package");

    //     Utils utils = new Utils(nomPackage, "controller", "mg.itu.FrameworkEwin.annotations.controllers.MonControleur");

    //     List<String> controleurs = utils.scanControllers();
    //     Map<URLMethod, Mapping> MapURL = utils.mapMethod_Url();

    //     getServletContext().setAttribute("listControlleur", controleurs);
    //     getServletContext().setAttribute("MapURL", MapURL);
    // }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String urlComplete = request.getRequestURI();
        String contexte = request.getContextPath();
        String urlRelative = urlComplete.substring(contexte.length());

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Tonga eto tsika aaaaa le Framework !</h1>");
        out.println("<p>URL complete : <b>" + urlComplete + "</b></p>");
        out.println("<p>URL relative <b>" + urlRelative + "</b></p>");
        Map<URLMethod, Mapping> routes = (Map<URLMethod, Mapping>) getServletContext().getAttribute("MapURL");
        String methodHttp = request.getMethod();

        URLMethod key = new URLMethod(urlRelative, methodHttp);

        Mapping mapping = routes.get(key);

        if (mapping != null) {
            try {
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object controller = clazz.getDeclaredConstructor().newInstance();

                Method method = clazz.getDeclaredMethod(mapping.getMethodeName());
                Object result = method.invoke(controller);

                if (result instanceof ModelAndView) {
                    ModelAndView modelAndView = (ModelAndView) result;
                    String viewName = modelAndView.getViewName();
                    Map<String, Object> modelData = modelAndView.getDonnéeÀafficher();

                    for (Map.Entry<String, Object> entry : modelData.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }

                    request.getRequestDispatcher("/WEB-INF/views/" + viewName + ".jsp").forward(request, response);
                    return;
                }

            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {

            out.println("<html><body>");
            out.println("<h2>Liste des routes disponibles</h2>");
            for (Map.Entry<URLMethod, Mapping> entry : routes.entrySet()) {
                Mapping m = entry.getValue();
                out.println("<p>" + entry.getKey() + " => Classe = " + m.getClassName()
                        + " Methode = " + m.getMethodeName() + "</p>");
            }
            out.println("</body></html>");
        }

        out.println("</body></html>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}