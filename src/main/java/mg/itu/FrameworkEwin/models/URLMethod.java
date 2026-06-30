package mg.itu.FrameworkEwin.models;
import java.util.Objects;

public class URLMethod {

    private String url;
    private String method;

    public URLMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof URLMethod)) return false;

        URLMethod other = (URLMethod) obj;

        return url.equals(other.url)
                && method.equals(other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return "[" + method + "] " + url;
    }
}