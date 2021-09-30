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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.service.EnvService;
import cat.santfeliu.api.utils.InventoryErrorSentinelUtils;
import cat.santfeliu.api.utils.InventoryUtils;

/**
 * Spring component to run when app has started.
 * 
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

	@Autowired
	private InventoryUtils invUtils;
	
	@Autowired
	private EnvService env;
	
	@Autowired
	private InventoryErrorSentinelUtils sentinel;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {
		try {
			init();
		} catch (Exception e) {
			log.error("init@InitApp - error when starting app", e);
		}
	}

	/**
	 * Method called when the application has started successfully.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss '(Europe/Madrid)'");

		log.info("init@InitApp - Application with name {} started at {}", appName,
				sdf.format(Calendar.getInstance().getTime()));
		log.info("init@InitApp - Application url is '{}'", externalUrl);
		log.info("init@InitApp - Spring active profile is '{}'", env.getActiveProfile());
		prepareConnectors();
		testConnectors();
	}
	
	public void prepareConnectors() {
		log.info("init@InitApp - Preparing connectors.");
		if (!env.isLocal()) {
			log.info("init@InitApp - App in dev/prod mode makes all connectors status is reset to offline.");
			Iterable<ConnectorStatusDb> ite = statusRepo.findAll();

			for (ConnectorStatusDb status : ite) {
				if (status.getConnectorStatus().equals("online")) {
					log.info("init@InitApp - Connector {} status is reset to offline", status.getConnectorName());
					status.setConnectorEndDate(Calendar.getInstance().getTime());
					status.setConnectorStatus("offline");
					statusRepo.save(status);			
				}

			}
		}
	}
	
	public void testConnectors() {
		log.info("init@InitApp - Testing connectors.");
//		log.info("test - {}", sentinel.senError("TestConnector", 934, "ERROR_TEST").describe("ERROR de prova").foundErr().foundErr("globalId1").foundErr("globalId2").exception(new NumberFormatException("test")));
//		log.info("test - {}", sentinel.senError("TestConnector", 934, "ERROR_TEST2").describe("ERROR de prova").foundErr().foundErr("globalId1").foundErr("globalId2").exception(new NumberFormatException("test")));
//		log.info("test - {}", sentinel.senError("TestConnector", 934, "ERROR_TEST3").describe("ERROR de prova").foundErr().foundErr("globalId1").foundErr("globalId2").exception(new NumberFormatException("test")));
//		
//		log.info("test - {}", sentinel.senError("TestConnector2", 934, "ERROR_TEST").describe("ERROR de prova").foundErr().foundErr("globalId1").foundErr("globalId2").exception(new NumberFormatException("test")));
//		log.info("test - {}", sentinel.senError("TestConnector2", 934, "ERROR_TEST2").describe("ERROR de prova").foundErr().foundErr("globalId1").foundErr("globalId2").exception(new NumberFormatException("test")));
//		log.info("test - {}", sentinel.senError("TestConnector2", 934, "ERROR_TEST3").describe("ERROR de prova").foundErr().foundErr("globalId1").foundErr("globalId2").exception(new NumberFormatException("test")));
//		sentinel.saveAllAndFlush("TestConnector");
//		
//		try {
//			log.info("test - {}" , new ObjectMapper().writeValueAsString(sentinel.actualErrorStack));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//      GeoserverTestLoader testLoader = new GeoserverTestLoader();
//      ConfigContainer params = new ConfigContainer();
//      autowireCapableBeanFactory.autowireBean(params);
//      params.init("testConnector", "loader");
//      testLoader.init("testConnector", params);
//      autowireCapableBeanFactory.autowireBean(testLoader);
//		ConnectorDb connector = new ConnectorDb();
//		connector.setInventoryName("testConnector");
//		connector.setConnectorName("testConnector");
//      ConnectorInstance instance = new ConnectorInstance(connector,null, null, null);
//      testLoader.initLoader(instance);
//      
//      while (!testLoader.hasEnd()) {
//      	log.info(testLoader.load(testLoader.getLoadTimeout()).toPrettyString());
//      }
//      
//      RhinoTransformer transformer = new RhinoTransformer();
//      ConfigContainer params = new ConfigContainer();
//  	autowireCapableBeanFactory.autowireBean(params);
//		params.init("ContainerConsumer", ComponentTypeEnum.TRANSFORMER.getName());
//		transformer.init("WasteContainerOut", params);
//		log.info("{}", new ObjectMapper().writeValueAsString(transformer.transform(new ObjectMapper().readTree("{\"globalId\":\"0GNpiMm41BNOQWOJv5ayQC\",\"element\":{\"type\":\"Feature\",\"id\":\"equipaments.149\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[419834.94350044,4581816.95425736],[419849.64534847,4581820.41660114],[419854.55439083,4581809.0465984],[419854.78626612,4581808.50954382],[419852.970256,4581807.54655611],[419842.41420174,4581802.54862851],[419841.54615645,4581802.1597275],[419837.95017966,4581800.5486593],[419831.47560517,4581815.64886866],[419831.28528725,4581816.09273379],[419834.94350044,4581816.95425736]]]]},\"geometry_name\":\"geom\",\"properties\":{\"fid\":149,\"id\":0,\"caseid\":8,\"nom\":\"3337\",\"nomcerca\":\"Departament d'Ensenyament de la Generalitat\",\"categoria\":\"DEPARTAMENT D'ENSENYAMENT DE LA GENERALITAT\",\"tipus\":\"EquipamentAdmin\",\"tipusdesc\":\"/Case/Equipament/EquipamentAdmin/\",\"dataalta\":\"Equipament administratiu i proveïment\",\"descripcio\":\"20000101\",\"gestio\":\"Serveis Territorials al Baix Llobregat\\r\\n\",\"propietat\":\"Generalitat\",\"manteniment\":\"Generalitat\",\"codi_rosmiman\":\"Generalitat\"}}}"))));
//		
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
