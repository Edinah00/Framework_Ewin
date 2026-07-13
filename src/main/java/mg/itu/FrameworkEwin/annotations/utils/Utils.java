package mg.itu.FrameworkEwin.annotations.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import jakarta.servlet.ServletException;
import mg.itu.FrameworkEwin.annotations.controllers.MonControleur;
import mg.itu.FrameworkEwin.annotations.methods.URLMapping;
import mg.itu.FrameworkEwin.models.*;

public class Utils {

    private String nomPackage;
    private String niveau;
    private String annotationCherche;

    public Utils() {
    }

    public Utils(String nomPackage, String niveau, String annotationCherche) {
        this.nomPackage = nomPackage;
        this.niveau = niveau;
        this.annotationCherche = annotationCherche;
    }

    public String getNomPackage() {
        return nomPackage;
    }

    public String getNiveau() {
        return niveau;
    }

    public String getAnnotationCherche() {
        return annotationCherche;
    }

    public void setNomPackage(String nomPackage) {
        this.nomPackage = nomPackage;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public void setAnnotationCherche(String annotationCherche) {
        this.annotationCherche = annotationCherche;
    }

    private List<Class<?>> findClassesInDirectory(File dir, String packageName)
            throws ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null)
            return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName + "." + file.getName();
                classes.addAll(findClassesInDirectory(file, subPackage)); // récursion
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    public List<Class<?>> getClasses() throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        String path = this.nomPackage.replace('.', '/');
        System.out.println("=== Recherche dans le path : " + path);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        if (!resources.hasMoreElements()) {
            System.out.println("=== AUCUNE ressource trouvée pour : " + path);
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            System.out.println("=== Ressource trouvée : " + resource);

            if (resource.getProtocol().equals("file")) {
                File dir = new File(resource.toURI());
                System.out.println("=== Dossier : " + dir.getAbsolutePath());
                System.out.println("=== Existe : " + dir.exists());
                classes.addAll(findClassesInDirectory(dir, this.nomPackage));
            }
        }
        return classes;
    }
    // // ── Scan des contrôleurs annotés @MonControleur (instance) ────────────────
    // public List<String> scanControllers() throws ServletException {
    // List<String> listControlleur = new ArrayList<>();
    // try {
    // List<Class<?>> classes = getClasses(); // ✔ utilise this.nomPackage en
    // interne

    // for (Class<?> clazz : classes) {
    // if (clazz.isAnnotationPresent(MonControleur.class)) {
    // listControlleur.add(clazz.getName());
    // System.out.println("Contrôleur détecté : " + clazz.getName());
    // }
    // }
    // return listControlleur;

    // } catch (Exception e) {
    // throw new ServletException("Erreur lors du scan des contrôleurs", e);
    // }
    // }
    public List<String> scanControllers() throws ServletException {
        List<String> listControlleur = new ArrayList<>();
        try {
            List<Class<?>> classes = getClasses();

            for (Class<?> clazz : classes) {
                // Chargement dynamique de l'annotation via son nom complet
                Class<?> annotationClass = Class.forName(this.annotationCherche);

                if (annotationClass.isAnnotation()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotation = (Class<? extends Annotation>) annotationClass;

                    if (clazz.isAnnotationPresent(annotation)) {
                        listControlleur.add(clazz.getName());
                        System.out.println("Classe détectée [" + this.niveau + "] : " + clazz.getName());
                    }
                }
            }
            return listControlleur;

        } catch (Exception e) {
            throw new ServletException("Erreur lors du scan des " + this.niveau, e);
        }
    }

    public Map<URLMethod, Mapping> mapMethod_Url() throws ServletException {

        Map<URLMethod, Mapping> mappings = new HashMap<>();
    
        try {

            List<Class<?>> classes = getClasses();

            for (Class<?> clazz : classes) {

                if (clazz.isAnnotationPresent(MonControleur.class)) {

                    Method[] methods = clazz.getDeclaredMethods();

                    for (Method method : methods) {

                        if (method.isAnnotationPresent(URLMapping.class)) {

                            URLMapping annotation = method.getAnnotation(URLMapping.class);

                            String url = annotation.url();
                            String method_http = annotation.method();

                            Mapping mapping = new Mapping(
                                    clazz.getName(),
                                    method.getName());
                            URLMethod url_method = new URLMethod(url, method_http);
                            if (mappings.containsKey(url_method)) {
                                throw new ServletException(
                                        "URL déjà mappée : " + url_method);
                            }

                            mappings.put(url_method, mapping);

                            System.out.println(
                                    url + "" + " -> "
                                            + clazz.getName()
                                            + "." + method.getName());
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new ServletException("Erreur scan URLMapping", e);
        }

        return mappings;
    }
}