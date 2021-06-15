package cat.santfeliu.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
/**
 * Punt d'entrada de l'aplicació Spring
 * @author kfiertek
 *
 */
public class InventariMunicipalApp {

    /**
     * Mètode main
     * @param args
     */
	public static void main(String[] args) {
SpringApplication.run(InventariMunicipalApp.class, args);
	}

}
