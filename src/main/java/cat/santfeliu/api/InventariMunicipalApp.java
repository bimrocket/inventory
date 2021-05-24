package cat.santfeliu.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InventariMunicipalApp {

    
	public static void main(String[] args) {
SpringApplication.run(InventariMunicipalApp.class, args);
	}

}
