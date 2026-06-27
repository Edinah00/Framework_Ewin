package mg.itu.FrameworkEwin.annotations.methods;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface URLMapping {
    String value() default "/";
}
