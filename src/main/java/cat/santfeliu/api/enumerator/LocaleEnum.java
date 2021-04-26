package cat.santfeliu.api.enumerator;


public enum LocaleEnum {

    CAT("ca"),
    ES("es"),
    EN("en");
    private String key;
    
    private LocaleEnum(String enuma) {
        this.key = enuma;
    
    }
    
    public String getKey() {
        return this.key;
    }
    
    public boolean is(String locale) {
        return this.key.equals(locale) || this.key.toUpperCase().equals(locale.toUpperCase());
    }
}
