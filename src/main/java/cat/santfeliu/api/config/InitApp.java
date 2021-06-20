package cat.santfeliu.api.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.enumerator.ComponentTypeEnum;
import cat.santfeliu.api.loaders.GemwebLoader;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.utils.ConfigContainer;

/**
 * Component spring a executar quan app s'ha iniciat.
 * @author kfiertek
 *
 */
@Component
@EnableWebSecurity
public class InitApp implements ApplicationListener<ApplicationReadyEvent> {


    Logger log = LoggerFactory.getLogger(InitApp.class);
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.url}")
    private String externalUrl;
    
    @Autowired
    private ConnectorStatusRepo statusRepo;
    
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
    @Override
    public void onApplicationEvent(ApplicationReadyEvent arg0) {
        try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Mètode cridat quan l'aplicació s'ha iniciat correctament.
     * @throws Exception 
     */
    public void init( ) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss '(Europe/Madrid)'");
        
        log.info("init@InitApp - Application with name {} started at {}", appName, sdf.format(Calendar.getInstance().getTime()));
        log.info("init@InitApp - Application url is '{}'", externalUrl);
        
        Iterable<ConnectorStatusDb> ite = statusRepo.findAll();
        
        for (ConnectorStatusDb status : ite) {
        	status.setConnectorEndDate(null);
        	status.setConnectorStartDate(null);
        	status.setConnectorStatus("offline");
        	statusRepo.save(status);
        }
//        
//		GemwebLoader loader = new GemwebLoader();
//		ConfigContainer params = new ConfigContainer();
//		autowireCapableBeanFactory.autowireBean(params);
//		params.init("CentresProducer", ComponentTypeEnum.LOADER.getName());
//		autowireCapableBeanFactory.autowireBean(loader);
//		loader.init("GemwebCentres", params);
//		ConnectorDb connector = new ConnectorDb();
//		connector.setInventoryName("GemwebCentres");
//		
//		ConnectorInstance instance = new ConnectorInstance(connector, null, null, null);
//		
//		loader.initLoader(instance);
//		log.info(loader.getAccessToken());
//		JsonObject obj = loader.load(60000);
//		while (obj != null) {
//			log.info(obj.toString());
//			obj = loader.load(60000); 
//		}
    }
}
