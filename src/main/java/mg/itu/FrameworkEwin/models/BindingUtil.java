package mg.itu.FrameworkEwin.models;

import java.lang.reflect.Field;

import jakarta.servlet.http.HttpServletRequest;

public class BindingUtil {
    public static boolean isSimple(Class<?> t) {
    return t.isPrimitive() || t == String.class
        || Number.class.isAssignableFrom(t)
        || t == Boolean.class || t == Character.class
        || t == java.util.Date.class || t.isEnum();
}

// conversion String -> type du champ
public static Object convert(String v, Class<?> t) {
    if (v == null) return null;
    if (t == String.class) return v;
    if (t == int.class || t == Integer.class) return Integer.parseInt(v);
    if (t == long.class || t == Long.class) return Long.parseLong(v);
    if (t == double.class || t == Double.class) return Double.parseDouble(v);
    if (t == boolean.class || t == Boolean.class) return Boolean.parseBoolean(v);
    return v;
    
}

// remplit l'objet depuis la requête
public static Object bind(Class<?> type, String prefix, HttpServletRequest req) throws Exception {
    Object obj = type.getDeclaredConstructor().newInstance();
    for (Field f : type.getDeclaredFields()) {
        // clé : "nom" ou "p.nom"
        String key = prefix.isEmpty() ? f.getName() : prefix + "." + f.getName();
        // objet imbriqué
        if (!isSimple(f.getType())) {
            f.setAccessible(true);
            f.set(obj, bind(f.getType(), key, req));
            continue;
        }
        String val = req.getParameter(key);
        if (val == null) continue;
        f.setAccessible(true);
        f.set(obj, convert(val, f.getType()));
    }
    return obj;
}
}
