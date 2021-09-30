package cat.santfeliu.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class PropertiesConfiguration {

    private String baseHref;

    @Autowired
    public PropertiesConfiguration(@Value("${app.base.href}") String href) {
        this.baseHref = href;
    }

    public String getBaseHref() { return this.baseHref; }
}

