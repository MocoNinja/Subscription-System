package es.javier.emailservice.exception;

/**
 * Exception that is thrown when a received email is not valid.
 */
public class InvalidEmailException extends Exception {

    public InvalidEmailException(String msg) {
        super(msg);
    }

}
