package es.javier.backendservice.exception;

/**
 * Custom exception thrown when calling the api produces an error that is not expected
 */
public class UnexpectedErrorException extends Exception {

    public UnexpectedErrorException(String msg) {
        super(msg);
    }

}
