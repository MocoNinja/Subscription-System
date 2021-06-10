package es.javier.backendservice.model;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstraction of a response with error messages
 */
public class ErrorResponseInformation implements Serializable {

    private HttpStatus code;
    private Map<String, String> payload;
    private String message;

    // <editor-fold desc="builder interface">
    public static ErrorResponseInformation builder() {
        return new ErrorResponseInformation();
    }

    public ErrorResponseInformation withCode(HttpStatus code) {
        this.setCode(code);
        return this;
    }

    public ErrorResponseInformation withMessage(String message) {
        this.setMessage(message);
        return this;
    }

    public ErrorResponseInformation havingInThePayload(String key, String value) {
        this.setPayload(key, value);
        return this;
    }
    // </editor-fold>

    // <editor-fold desc="getters && setters">
    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public Map getPayload() {
        return payload;
    }

    public void setPayload(String entry, String information) {
        if (payload == null) {
            payload = new HashMap<>();
        }
        payload.put(entry, information);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    // </editor-fold>
}
