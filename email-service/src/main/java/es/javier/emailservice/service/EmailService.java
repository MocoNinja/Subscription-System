package es.javier.emailservice.service;

import es.javier.emailservice.exception.InvalidEmailException;
import es.javier.emailservice.model.EmailMessage;
import es.javier.emailservice.model.Subscription;
import org.springframework.stereotype.Component;

/**
 * Interface that describes what an email service should do.
 */
@Component
public interface EmailService {

    public EmailMessage sendEmail(Subscription subscription) throws InvalidEmailException;

    public void checkValidity(Subscription subscription) throws InvalidEmailException;

}
