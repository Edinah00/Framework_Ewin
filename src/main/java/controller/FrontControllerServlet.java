package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
@WebServlet("/front")
public class FrontControllerServlet extends HttpServlet {
   protected void processRequest(HttpServletRequest request,
                              HttpServletResponse response)
        throws ServletException, IOException {
        
        // URL complète : /ProjetTest/foo/bar
        String urlComplete = request.getRequestURI();

        // URL sans le contexte : /foo/bar
        String contexte = request.getContextPath();
        String urlRelative = urlComplete.substring(contexte.length());

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Tonga eto tsika depuis le Framework !</h1>");
        out.println("<p>URL complete : <b>" + urlComplete + "</b></p>");
        out.println("<p>URL relative : <b>" + urlRelative + "</b></p>");
        out.println("</body></html>");
        }
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

}
