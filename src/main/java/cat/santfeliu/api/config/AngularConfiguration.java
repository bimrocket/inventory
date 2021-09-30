package cat.santfeliu.api.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import cat.santfeliu.api.service.EnvService;

@Configuration
public class AngularConfiguration implements WebMvcConfigurer {


    private final static Logger log = LoggerFactory.getLogger(AngularConfiguration.class);
    
	@Autowired
	private EnvService env;
	
	@Autowired
	private PropertiesConfiguration props;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String contentPath = env.isLocal() ? "local" : (env.isDev() ? "dev" : (env.isProd() ? "prod" : "local"));
		log.info(props.getBaseHref());
		registry.addResourceHandler(props.getBaseHref(), props.getBaseHref().concat("**"), props.getBaseHref().concat("/**")).addResourceLocations("classpath:/content/" + contentPath + "/gestor-inventari/")
				.resourceChain(true).addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String resourcePath, Resource location) throws IOException {
						var requestedResource = location.createRelative(resourcePath);
						return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
								: new ClassPathResource("/content/" + contentPath + "/gestor-inventari/index.html");
					}
				});

	}
}
