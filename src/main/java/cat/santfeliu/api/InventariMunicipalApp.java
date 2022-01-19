package cat.santfeliu.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
/**
 * Punt d'entrada de l'aplicació Spring
 * 
 * @author kfiertek
 *
 */
public class InventariMunicipalApp {
	private static final Logger log = LoggerFactory.getLogger(InventariMunicipalApp.class);
	/**
	 * Mètode main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		log.debug("main@InventariMunicipalApp - Springboot aplication init");
		SpringApplication.run(InventariMunicipalApp.class, args);
	}

}
