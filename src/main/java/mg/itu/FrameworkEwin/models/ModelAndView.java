package mg.itu.FrameworkEwin.models;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    String viewName;
    Map<String,Object> donnéeÀafficher;
    ModelAndView(String viewName, Map<String,Object> donnéeÀafficher){
        this.viewName = viewName;
        this.donnéeÀafficher = donnéeÀafficher;
    }
   public ModelAndView(){
    }
    public String getViewName() {
        return viewName;    
    }

    public Map<String, Object> getDonnéeÀafficher() {
        return donnéeÀafficher;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public void setDonnéeÀafficher(Map<String, Object> donnéeÀafficher) {
        this.donnéeÀafficher = donnéeÀafficher;
    }
    public void setAttribute(String key, Object value) {
    if (donnéeÀafficher == null) {
        donnéeÀafficher = new HashMap<>();
    }
    donnéeÀafficher.put(key, value);
}

    
}
