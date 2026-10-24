package mg.itu.FrameworkEwin.controllers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.FrameworkEwin.annotations.utils.FileManager;
import mg.itu.FrameworkEwin.annotations.utils.FileUploads;
import mg.itu.FrameworkEwin.annotations.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import mg.itu.FrameworkEwin.models.*;
import mg.itu.FrameworkEwin.annotations.methods.*;

@MultipartConfig(maxFileSize = 50L * 1024 * 1024, maxRequestSize = 100L * 1024 * 1024)
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

        // Ne pas écrire d'HTML global ici : on renvoie soit JSON (REST), soit on forward vers JSP
        Map<URLMethod, Mapping> routes = (Map<URLMethod, Mapping>) getServletContext().getAttribute("MapURL");
        String methodHttp = request.getMethod();

        URLMethod key = new URLMethod(urlRelative, methodHttp);

        Mapping mapping = routes.get(key);
        PrintWriter out = response.getWriter();
        if (mapping != null) {
            try {
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object controller = clazz.getDeclaredConstructor().newInstance();
                String methodName = mapping.getMethodeName();

                Method method = null;
                for(Method x : clazz.getDeclaredMethods()) {
                    if(x.getName().equals(methodName)) {
                        method = x;
                        break;
                    }
                }
                if (method == null) throw new ServletException("Méthode introuvable : " + methodName);
                request.setCharacterEncoding("UTF-8");
                Parameter[] params= method.getParameters();
                Object[] args = new Object[params.length];
                Map<String, String[]> parameterMap = request.getParameterMap();
                Map<String, List<FileManager>> files = extractFiles(request);
                for (int i = 0; i < params.length; i++) {
                    String paramName = params[i].getName();
                    Class<?> paramType = params[i].getType();
                    Object value = bindParameterValue(paramName, paramType, parameterMap, files);
                    args[i] = value;
                    System.out.println(params[i].getName() + " -> " + (value == null ? "null" : value));
                }


                Object result = new Object();
                try {
    result = method.invoke(controller, args);
} catch (InvocationTargetException e) {
    e.getCause().printStackTrace(); // vraie cause
    throw new ServletException(e.getCause());
}
                if (result instanceof ModelAndView) {
                    ModelAndView modelAndView = (ModelAndView) result;
                    String viewName = modelAndView.getViewName();
                    Map<String, Object> modelData = modelAndView.getDonnéeÀafficher();

                    
                    for (Map.Entry<String, Object> entry : modelData.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    if (method.isAnnotationPresent(RestAPI.class)) {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        ObjectMapper mapper = new ObjectMapper();
                        String json = mapper.writeValueAsString(modelData);

                        // response.getWriter().write(json);
                        out.print(json);
                        return;
                    } else {
                        System.out.println("Attributs envoyés à la vue : " + modelData);
                        request.getRequestDispatcher("/WEB-INF/views/" + viewName + ".jsp").forward(request, response);
                        return;
                    }
                    
                }
                // Si le résultat n'est pas un ModelAndView
                if (method.isAnnotationPresent(RestAPI.class)) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(result);

                    response.getWriter().write(json);
                    return;
                } else {
                    // Pas de JSP ni REST : on affiche simplement la représentation texte
                    out.println("<html><body>");
                    out.println("<h1>Tonga eto tsika aaaaa le Framework !</h1>");
                    out.println("<p>URL complete : <b>" + urlComplete + "</b></p>");
                    out.println("<p>URL relative <b>" + urlRelative + "</b></p>");
                    out.println("<pre>" + String.valueOf(result) + "</pre>");
                    out.println("</body></html>");
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

    private static Object bindParameterValue(String paramName, Class<?> paramType, Map<String, String[]> parameterMap) {
        return bindParameterValue(paramName, paramType, parameterMap, new LinkedHashMap<>());
    }

    // fichiers envoyes par le formulaire, groupes par nom de champ
    private static Map<String, List<FileManager>> extractFiles(HttpServletRequest request)
            throws IOException, ServletException {
        Map<String, List<FileManager>> files = new LinkedHashMap<>();
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            return files;
        }
        for (Part part : request.getParts()) {
            String submitted = part.getSubmittedFileName();
            if (submitted == null || submitted.isEmpty()) {
                continue;
            }
            files.computeIfAbsent(part.getName(), k -> new ArrayList<>())
                    .add(FileManager.from(submitted, part.getInputStream().readAllBytes()));
        }
        return files;
    }

    private static Object bindParameterValue(String paramName, Class<?> paramType,
            Map<String, String[]> parameterMap, Map<String, List<FileManager>> files) {
        if (paramType == null) {
            return null;
        }
        if (paramType == FileManager.class) {
            List<FileManager> l = files.get(paramName);
            return (l == null || l.isEmpty()) ? null : l.get(0);
        }
        if (paramType == FileUploads.class) {
            return new FileUploads(files);
        }

        String directValue = parameterMap.containsKey(paramName) ? parameterMap.get(paramName)[0] : null;
        if (paramType == String.class) {
            return directValue;
        }
        if (paramType == Integer.TYPE || paramType == Integer.class) {
            return directValue == null ? null : Integer.valueOf(directValue);
        }
        if (paramType == Long.TYPE || paramType == Long.class) {
            return directValue == null ? null : Long.valueOf(directValue);
        }
        if (paramType == Double.TYPE || paramType == Double.class) {
            return directValue == null ? null : Double.valueOf(directValue);
        }
        if (paramType == Float.TYPE || paramType == Float.class) {
            return directValue == null ? null : Float.valueOf(directValue);
        }
        if (paramType == Boolean.TYPE || paramType == Boolean.class) {
            return directValue == null ? null : Boolean.valueOf(directValue);
        }
        if (paramType == Character.TYPE || paramType == Character.class) {
            return directValue == null || directValue.isEmpty() ? null : directValue.charAt(0);
        }

        if (paramType.isPrimitive() || paramType.isEnum() || Number.class.isAssignableFrom(paramType)) {
            return directValue == null ? null : directValue;
        }

        String prefix = paramName + ".";
        boolean avecPrefixe = parameterMap.keySet().stream().anyMatch(key -> key.startsWith(prefix));
        return bindBean(paramType, avecPrefixe ? prefix : "", parameterMap, files);
    }

    // lie les attributs de n'importe quelle classe, par nom d'attribut
    private static Object bindBean(Class<?> type, String prefix, Map<String, String[]> parameterMap,
            Map<String, List<FileManager>> files) {
        try {
            Object bean = type.getDeclaredConstructor().newInstance();
            for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    String key = prefix + field.getName();
                    Object value;
                    if (field.getType() == FileManager.class) {
                        List<FileManager> l = files.get(key);
                        if (l == null || l.isEmpty()) {
                            continue;
                        }
                        value = l.get(0);
                    } else if (field.getType() == FileUploads.class) {
                        value = new FileUploads(files);
                    } else if (isFileManagerList(field)) {
                        value = files.get(key);
                        if (value == null) {
                            continue;
                        }
                    } else if (isSimple(field.getType())) {
                        String[] raw = parameterMap.get(key);
                        if (raw == null) {
                            continue;
                        }
                        value = convertToType(field.getType(), raw);
                    } else {
                        String nested = key + ".";
                        if (parameterMap.keySet().stream().noneMatch(k -> k.startsWith(nested))) {
                            continue;
                        }
                        value = bindBean(field.getType(), nested, parameterMap, files);
                    }
                    if (value != null) {
                        assign(bean, c, field, value);
                    }
                }
            }
            return bean;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static void assign(Object bean, Class<?> owner, Field field, Object value)
            throws ReflectiveOperationException {
        String name = field.getName();
        String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        try {
            owner.getDeclaredMethod(setterName, field.getType()).invoke(bean, value);
        } catch (NoSuchMethodException e) {
            field.setAccessible(true);
            field.set(bean, value);
        }
    }

    private static boolean isFileManagerList(Field field) {
        if (field.getType() != List.class || !(field.getGenericType() instanceof ParameterizedType)) {
            return false;
        }
        return ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0] == FileManager.class;
    }

    private static boolean isSimple(Class<?> t) {
        return t.isPrimitive() || t == String.class || t.isEnum()
                || Number.class.isAssignableFrom(t) || t == Boolean.class
                || t == Character.class || t == LocalDate.class;
    }

    private static Object convertToType(Class<?> targetType, String[] values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return null;
        }

        String value = values[0];
        if (targetType == String.class) {
            return value;
        }
        if (value.isEmpty()) {
            return null;
        }
        if (targetType.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetType, value);
        }
        if (targetType == LocalDate.class) {
            return LocalDate.parse(value);
        }
        if (targetType == Integer.TYPE || targetType == Integer.class) {
            return Integer.valueOf(value);
        }
        if (targetType == Long.TYPE || targetType == Long.class) {
            return Long.valueOf(value);
        }
        if (targetType == Double.TYPE || targetType == Double.class) {
            return Double.valueOf(value);
        }
        if (targetType == Float.TYPE || targetType == Float.class) {
            return Float.valueOf(value);
        }
        if (targetType == Boolean.TYPE || targetType == Boolean.class) {
            return Boolean.valueOf(value);
        }
        if (targetType == Character.TYPE || targetType == Character.class) {
            return value.isEmpty() ? null : value.charAt(0);
        }
        return value;
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