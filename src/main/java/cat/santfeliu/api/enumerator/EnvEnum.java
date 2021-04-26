package cat.santfeliu.api.enumerator;


public enum EnvEnum {

    LOCAL("local"),
    DEV("dev"),
    PRE("pre");
    
    private String env;
    
    private EnvEnum(String env) {
        this.env = env;
    }
    
    public String getKey() {
        return this.env;
    }
    
    public static boolean isWindows(String key) {
        return java.io.File.separatorChar != '/';
    }
}
