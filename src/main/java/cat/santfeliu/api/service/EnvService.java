package cat.santfeliu.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EnvService {

	@Autowired
    private Environment environment;

    public boolean isLocal() {
        String activeProfile = getActiveProfile();
    	if (activeProfile == null || activeProfile.isEmpty()) {
    		return true;
    	}
        return false;
    }
    
    public boolean isDev() {
    	String activeProfile = getActiveProfile();
    	if (activeProfile != null && activeProfile.equals("dev")) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isProd() {
    	String activeProfile = getActiveProfile();
    	if (activeProfile != null && activeProfile.equals("prod")) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public String getActiveProfile() {
    	for (String profileName : environment.getActiveProfiles()) {
           return profileName;
        }
    	return null;
    }
}
