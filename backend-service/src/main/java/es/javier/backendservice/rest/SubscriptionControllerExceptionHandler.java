package es.javier.backendservice.rest;

import es.javier.backendservice.exception.ConnectionException;
import es.javier.backendservice.exception.UnexpectedErrorException;
import es.javier.backendservice.model.ErrorResponseInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * {@link RestControllerAdvice} that handles the exceptions that can be thrown when using the API.
 */
@RestControllerAdvice
public class SubscriptionControllerExceptionHandler {

    @ExceptionHandler(value = {ConnectionException.class})
    public ResponseEntity<ErrorResponseInformation> errorConnectingToTheApi(ConnectionException ex) {
        ErrorResponseInformation response = ErrorResponseInformation.builder() //
                .withCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .havingInThePayload("Error-Cause", "Api cannot be reached") //
                .withMessage("Error contacting the API. Please, try again later.");
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(value = {UnexpectedErrorException.class})
    public ResponseEntity<ErrorResponseInformation> errorConnectingToTheApiUnexpected(UnexpectedErrorException ex) {
        ErrorResponseInformation response = ErrorResponseInformation.builder() //
                .withCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .havingInThePayload("Error-Cause", "Api did not respond as expected") //
                .withMessage("Error contacting the API. Please, try again later.");
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
