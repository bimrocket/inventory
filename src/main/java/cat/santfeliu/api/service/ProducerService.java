//package cat.santfeliu.api.service;
//
//import java.lang.reflect.Constructor;
//import java.util.Optional;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import cat.santfeliu.api.beans.LoaderResponse;
//import cat.santfeliu.api.exceptions.ApiErrorException;
//import cat.santfeliu.api.loaders.Loader;
//import cat.santfeliu.api.model.ProducerDb;
//import cat.santfeliu.api.repo.ProducerRepo;
//import cat.santfeliu.api.transformers.ObjectTransformer;
//import cat.santfeliu.api.utils.ConfigContainer;
//
//@Service
//public class ProducerService {
//
//	private static final Logger log = LoggerFactory.getLogger(ProducerService.class);
//
//	@Autowired
//	private ProducerRepo proRepo;
//
//	@Autowired
//	private AutowireCapableBeanFactory autowireCapableBeanFactory;
//
//	public String runProducer(String producer) {
//		Optional<ProducerDb> proDbOpt = proRepo.findById(producer);
//		if (proDbOpt.isEmpty()) {
//			log.error("runProducer@ProducerService - no producer found with id {}", producer);
//			throw new ApiErrorException(HttpStatus.BAD_REQUEST, "no producer found with id " + producer,
//					"no producer found with id " + producer);
//		}
//
//		ConfigContainer paramsLoader = new ConfigContainer();
//		autowireCapableBeanFactory.autowireBean(paramsLoader);
//		paramsLoader.init(producer, "loader");
//		String loaderClassName = proDbOpt.get().getLoaderclass();
//		Loader loader = null;
//		try {
//			Object[] params = {};
//			Class[] paramsCon = {};
//			var loaderClass = Class.forName(loaderClassName);
//			Constructor con = loaderClass.getConstructor(paramsCon);
//
//			loader = (Loader) con.newInstance(params);
//		} catch (Exception e) {
//			String error = String.format("couldn't create loader class %s for producer %s", loaderClassName, producer);
//			log.error("runProducer@ProducerService - {}", error, e);
//			throw new ApiErrorException(HttpStatus.BAD_REQUEST, error, error);
//
//		}
//		loader.init(paramsLoader);
//		LoaderResponse response = loader.read();
//		if (!response.isValid()) {
//			log.error("runProducer@ProducerService - invalid response , content : {}", response.getResponse());
//			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "invalid response",
//					"invalid response from geoserver");
//		}
//		ObjectTransformer transformer = null;
//		ConfigContainer paramsTransformer = new ConfigContainer();
//		autowireCapableBeanFactory.autowireBean(paramsTransformer);
//		paramsTransformer.init(producer, "transformer");
//		String transformerClassName = proDbOpt.get().getTransformerclass();
//		try {
//			Object[] params = {};
//			Class[] paramsCon = {};
//			var transformerClass = Class.forName(transformerClassName);
//			Constructor con = transformerClass.getConstructor(paramsCon);
//
//			transformer = (ObjectTransformer) con.newInstance(params);
//		} catch (Exception e) {
//			String error = String.format("couldn't create loader class %s for producer %s", loaderClassName, producer);
//			log.error("runProducer@ProducerService - {}", error, e);
//			throw new ApiErrorException(HttpStatus.BAD_REQUEST, error, error);
//
//		}
//		autowireCapableBeanFactory.autowireBean(transformer);
//		transformer.initTransfomer(paramsTransformer);
//		
//		return transformer.transform("application/json", response.getResponse());
//	}
//}
