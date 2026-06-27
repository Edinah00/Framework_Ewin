package mg.itu.FrameworkEwin.models;

public class Mapping {
    private String className;
    private String methodeName;

    public Mapping(String classname , String methodname){
        this.className =classname;
        this.methodeName =methodname;

    }
    public String getClassName() {
        return className;
    }
    public String getMethodeName() {
        return methodeName;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public void setMethodeName(String methodeName) {
        this.methodeName = methodeName;
    }

}
