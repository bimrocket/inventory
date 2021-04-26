package cat.santfeliu.api.dto;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ApiError {

    private String errorCode;

    private HttpStatus status;

    private String message;

    private List<String> errors;

    private List<String> params;

    public ApiError (HttpStatus status, String message, List<String> errors) {
        super();
        this.errorCode = "error_501";
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError (HttpStatus status, String message, List<String> errors, List<String> params) {
        super();
        this.errorCode = "error_501";
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.params = params;
    }

    public ApiError (HttpStatus status, String message, String error) {
        super();
        this.errorCode = "error_501";
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(error);
    }

    public ApiError (String errorCode, HttpStatus status, String message, List<String> errors) {
        super();
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError (String errorCode, HttpStatus status, String message, String errors) {
        super();
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(errors);
    }

    public ApiError (String errorCode, HttpStatus status, String message, String errors, List<String> params) {
        super();
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(errors);
        this.params = params;
    }

    public HttpStatus getStatus( ) {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage( ) {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors( ) {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getErrorCode( ) {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getParams( ) {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

}