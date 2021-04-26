package cat.santfeliu.api.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cat.santfeliu.api.exceptions.ApiErrorException;

@Component("securityService")
public class SecurityService {
    
    public boolean allowedOrigin() {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        boolean allowed = false;
        String hOrigin = request.getHeader("Origin");
        if (!hOrigin.equals("localhost:8091")) {
            throw new ApiErrorException("error_403", HttpStatus.FORBIDDEN, "FORBIDDEN - You are not allowed", "You are not allowed.");
        }
        return allowed;
    }
}
