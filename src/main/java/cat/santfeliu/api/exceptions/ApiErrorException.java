package cat.santfeliu.api.exceptions;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import cat.santfeliu.api.dto.ApiError;

@SuppressWarnings("serial")
public class ApiErrorException extends AuthenticationException {

    private ApiError apiError;

    public ApiErrorException (HttpStatus status, String message, List<String> errors) {
        super(message);
        this.apiError = new ApiError(status, message, errors);
    }

    public ApiErrorException (HttpStatus status, String message, String error) {
        super(message);
        this.apiError = new ApiError(status, message, Arrays.asList(error));
    } 

    public ApiErrorException (HttpStatus status, String message, String error, List<String> params) {
        super(message);
        this.apiError = new ApiError(status, message, Arrays.asList(error), params);
    }
    
    public ApiErrorException (String errorCode, HttpStatus status, String message, List<String> errors) {
        super(message);
        this.apiError = new ApiError(errorCode, status, message, errors);
    }

    public ApiErrorException (String errorCode, HttpStatus status, String message, String errors) {
        super(message);
        this.apiError = new ApiError(errorCode, status, message, errors);
    }
    
    public ApiErrorException (String errorCode, HttpStatus status, String message, String errors, List<String> params) {
        super(message);
        this.apiError = new ApiError(errorCode, status, message, errors, params);
    }

    public ApiErrorException (HttpStatus status, String message, List<String> errors, Exception e) {
        super(message, e);
        this.apiError = new ApiError(status, message, errors);
    }

    public ApiErrorException (HttpStatus status, String message, String error, Exception e) {
        super(message, e);
        this.apiError = new ApiError(status, message, Arrays.asList(error));
    }

    public ApiErrorException (String errorCode, HttpStatus status, String message, List<String> errors, Exception e) {
        super(message, e);
        this.apiError = new ApiError(errorCode, status, message, errors);
    }

    public ApiError getApiError( ) {
        return this.apiError;
    }

    public static ApiErrorException createAccessDeniedError( ) {

        return new ApiErrorException("error_200", HttpStatus.FORBIDDEN, "Not enough permission", "Your account doesn't have enough permission");

    }

}
