package cat.santfeliu.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cat.santfeliu.api.dto.ApiError;
import cat.santfeliu.api.exceptions.ApiErrorException;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {


    Logger log = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

    private static final String ERR_MSG_API_ERROR = "Api error";

   

    @ExceptionHandler(ApiErrorException.class)
    protected ResponseEntity<Object> customHandleApiError(ApiErrorException ex, WebRequest request) {

        // Les API error no s'han de logar, ja ho fan a les api!
        ApiError error = ex.getApiError();
        log.error("exceptionHandler - returning ApiErrorException", ex);
        return new ResponseEntity<>(error, error.getStatus()); 

    }
    
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> exceptionHandleApiError(Exception ex, WebRequest request) {

        String message = ex.getMessage() == null || ex.getMessage().isEmpty() ? "No message available" : ex.getMessage();
        log.error("API Excepcion (Exception) -  ({}): {}", request.getDescription(true), message);
        log.error("API Excepcion (Exception) ", ex);
        return new ResponseEntity<>(new ApiError("error_501", HttpStatus.INTERNAL_SERVER_ERROR, message, ""), HttpStatus.INTERNAL_SERVER_ERROR);

    }

}