package mg.itu.FrameworkEwin.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.lang.annotation.Annotation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.FrameworkEwin.annotations.utils.Utils;

@WebServlet("/front")
public class FrontControllerServlet extends HttpServlet {

    @Override
public void init() throws ServletException {
    String nomPackage = getServletContext().getInitParameter("controller-package");

    Utils utils = new Utils(nomPackage, "controller", "mg.itu.FrameworkEwin.annotations.controllers.MonControleur");
    List<String> controleurs = utils.scanControllers();

    getServletContext().setAttribute("listControlleur", controleurs);
}
        
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String urlComplete = request.getRequestURI();
        String contexte = request.getContextPath();
        String urlRelative = urlComplete.substring(contexte.length());

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Tonga eto tsika aaaaa le Framework !</h1>");
        out.println("<p>URL complète : <b>" + urlComplete + "</b></p>");
        out.println("<p>URL relative : <b>" + urlRelative + "</b></p>");
        for (String ctrl : (List<String>) getServletContext().getAttribute("listControlleur")) {
            out.println("<p>Contrôleur détecté : <b>" + ctrl + "</b></p>");
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