package es.javier.subscriptionservice.rest;

import es.javier.subscriptionservice.model.SubscriptionResponseInformation;
import org.springframework.amqp.AmqpAuthenticationException;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * {@link RestControllerAdvice} that handles the exceptions that can be thrown when using the API.
 */
@RestControllerAdvice
public class SubscriptionControllerExceptionHandler {

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<SubscriptionResponseInformation> methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        SubscriptionResponseInformation response = SubscriptionResponseInformation.builder() //
                .withCode(HttpStatus.METHOD_NOT_ALLOWED) //
                .withSubscription(null) //
                .withMessage("Unsupported method: " + ex.getMessage());

        return new ResponseEntity(response, response.getCode());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<SubscriptionResponseInformation> validationErrorException(MethodArgumentNotValidException ex) {
        SubscriptionResponseInformation response = SubscriptionResponseInformation.builder() //
                .withCode(HttpStatus.BAD_REQUEST) //
                .withSubscription(null) //
                .havingInThePayload("Error-Message", ex.getMessage())
                .havingInThePayload("Error-Values", ex.getAllErrors().toString())
                .havingInThePayload("Message", "Validation error")
                .withMessage("Validation error");

        return new ResponseEntity(response, response.getCode());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<SubscriptionResponseInformation> validationErrorBecauseOfParsingException(HttpMessageNotReadableException ex) {

        SubscriptionResponseInformation response = SubscriptionResponseInformation.builder() //
                .withCode(HttpStatus.BAD_REQUEST) //
                .withSubscription(null) //
                .havingInThePayload("Error-Message", ex.getMessage())
                .havingInThePayload("Message", "Validation error")
                .withMessage("Validation error");

        return new ResponseEntity(response, response.getCode());
    }


    @ExceptionHandler(value = {CannotCreateTransactionException.class})
    public ResponseEntity<SubscriptionResponseInformation> databaseConnectionException(CannotCreateTransactionException ex) {

        SubscriptionResponseInformation response = SubscriptionResponseInformation.builder() //
                .withCode(HttpStatus.INTERNAL_SERVER_ERROR) //
                .withSubscription(null) //
                .havingInThePayload("Error-Message", ex.getMessage())
                .havingInThePayload("Message", "Database connection error. Try again later.")
                .withMessage("Database connection error. Try again later.");

        return new ResponseEntity(response, response.getCode());
    }

    @ExceptionHandler(value = {AmqpAuthenticationException.class, AmqpConnectException.class})
    public ResponseEntity<SubscriptionResponseInformation> databaseConnectionException(Exception ex) {

        SubscriptionResponseInformation response = SubscriptionResponseInformation.builder() //
                .withCode(HttpStatus.INTERNAL_SERVER_ERROR) //
                .withSubscription(null) //
                .havingInThePayload("Error-Message", ex.getMessage())
                .havingInThePayload("Info", "Subscription was successfully persisted, but email could not be sent")
                .havingInThePayload("Message", "Message broker connection error. Message could not be sent, it should be retried")
                .withMessage("Message Broker connection error. Try again later.");

        return new ResponseEntity(response, response.getCode());
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<SubscriptionResponseInformation> badRequestException(MethodArgumentTypeMismatchException ex) {

        SubscriptionResponseInformation response = SubscriptionResponseInformation.builder() //
                .withCode(HttpStatus.BAD_REQUEST) //
                .withSubscription(null) //
                .havingInThePayload("Error-Message", ex.getMessage())
                .havingInThePayload("Message", "Request malformed")
                .withMessage("Check the wrong request");

        return new ResponseEntity(response, response.getCode());
    }

}

