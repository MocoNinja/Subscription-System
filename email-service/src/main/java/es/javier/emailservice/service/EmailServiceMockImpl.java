package es.javier.emailservice.service;

import es.javier.emailservice.exception.InvalidEmailException;
import es.javier.emailservice.model.EmailMessage;
import es.javier.emailservice.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mock implementation that assembles a {@link EmailMessage} from a {@link Subscription} model.
 * Instead of sending the email if valid, it is displayed in a log.
 */
@Component
public class EmailServiceMockImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceMockImpl.class);

    @Override
    public EmailMessage sendEmail(Subscription subscription) throws InvalidEmailException {
        logger.info("Sending mock email, relative to subscription: {}", subscription);

        EmailMessage emailMessage = new EmailMessage(subscription);

        logger.info("The email would be addressed to: {}", emailMessage.getEmailAddress());
        logger.info("The email would be: {}", emailMessage);

        return emailMessage;
    }

    @Override
    public void checkValidity(Subscription subscription) throws InvalidEmailException {
        if ( subscription == null || !subscription.isConsent()) {
            throw new InvalidEmailException("Error: subscription has no consent.");
        }
    }
}
