package es.javier.backendservice.exception;

/**
 * Custom exception thrown when the api cannot be reached
 */
public class ConnectionException extends Exception {

    public ConnectionException(String msg) {
        super(msg);
    }

}
