package cat.santfeliu.api.service;

import cat.santfeliu.api.components.ConnectorInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Service;

@Service
public class RegisterBeansDynamically implements BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(RegisterBeansDynamically.class);
    private ConfigurableBeanFactory beanFactory;

    /**
     * Registra un bean en temps real funciona com @Autowired pero
     * es dinamic
     * @param <T>
     * @param beanName
     * @param bean
     */
    public <T> void registerBean(String beanName, T bean) {
        beanFactory.registerSingleton(beanName, bean);
        log.debug("registerBeean@RegisterBeansDynamically - register bean of name {}",beanName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        log.debug("setBeanFactory@RegisterBeansDynamically - set a bean factory {}", beanFactory.toString());
    }
}