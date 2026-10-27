package mg.itu.FrameworkEwin.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

public class EwinSession {
    private final HttpSession session;

    public EwinSession(HttpSession session) {
        this.session = session;
    }

    public void setAttribute(String key, Object value) {
        session.setAttribute(key, value);
    }

    public void removeAttribute(String key) {
        session.removeAttribute(key);
    }

    public Object getAttribute(String key) {
        return session.getAttribute(key);
    }

    public <T> T getAttribute(String key, Class<T> type) {
        Object value = session.getAttribute(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalStateException("Attribut '" + key + "' de type "
                    + value.getClass().getName() + ", attendu " + type.getName());
        }
        return type.cast(value);
    }

    // copie : modifier la map ne modifie pas la session
    public Map<String, Object> getAttributes() {
        Map<String, Object> copie = new LinkedHashMap<>();
        Enumeration<String> noms = session.getAttributeNames();
        while (noms.hasMoreElements()) {
            String nom = noms.nextElement();
            copie.put(nom, session.getAttribute(nom));
        }
        return copie;
    }

    public void clear() {
        Collections.list(session.getAttributeNames()).forEach(session::removeAttribute);
    }

    public String getId() {
        return session.getId();
    }
}