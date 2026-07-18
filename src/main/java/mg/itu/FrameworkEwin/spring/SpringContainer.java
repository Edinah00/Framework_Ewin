package mg.itu.FrameworkEwin.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContainer {
    private static AnnotationConfigApplicationContext context;

    public static void init(String basePackages) {
        context = new AnnotationConfigApplicationContext();
        context.scan(basePackages.split(","));
        context.refresh();
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}