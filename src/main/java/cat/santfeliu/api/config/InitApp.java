package cat.santfeliu.api.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import cat.santfeliu.api.repo.InventoryRepo;

/**
 * Component spring a executar quan app s'ha iniciat.
 * @author kfiertek
 *
 */
@Component
@EnableAsync
@EnableWebSecurity
public class InitApp implements ApplicationListener<ApplicationReadyEvent> {


    Logger log = LoggerFactory.getLogger(InitApp.class);
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.url}")
    private String externalUrl;

    @Autowired
    private InventoryRepo invRepo;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent arg0) {
        init();
    }

    /**
     * Mètode cridat quan l'aplicació s'ha iniciat correctament.
     */
    public void init( ) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss '(Europe/Madrid)'");
        
        log.info("init@InitApp - Application with name {} started at {}", appName, sdf.format(Calendar.getInstance().getTime()));
        log.info("init@InitApp - Application url is '{}'", externalUrl);
 
    }

}
