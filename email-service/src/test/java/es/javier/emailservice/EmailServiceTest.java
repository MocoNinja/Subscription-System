package es.javier.emailservice;

import es.javier.emailservice.exception.InvalidEmailException;
import es.javier.emailservice.model.EmailMessage;
import es.javier.emailservice.model.Subscription;
import es.javier.emailservice.service.EmailServiceMockImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmailServiceTest {

    private EmailServiceMockImpl service;

    @BeforeEach
    public void setup() {
        service = new EmailServiceMockImpl();
    }

    @Test
    public void testWrongSubscriptionDoesNotSendEmail() {
        Subscription subscription = null;

        try {
            service.sendEmail(subscription);
            Assertions.fail("Null subscription should have generated an exception");
        } catch (InvalidEmailException e) {
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("A generic Exception is too broad");
        }
    }

    @Test
    public void testValidityChecking() {
        Subscription subscription = new Subscription();

        subscription.setEmail("test@test.de");
        subscription.setConsent(false);

        try {
            service.checkValidity(subscription);
            Assertions.fail("Invalid subscription should have generated an exception");
        } catch (InvalidEmailException e) {
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("A generic Exception is too broad");
        }

        subscription.setConsent(true);

        try {
            service.checkValidity(subscription);
            Assertions.assertTrue(subscription.getConsent());
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testCorrectEmailGeneration() {
        Subscription subscription = new Subscription();

        subscription.setEmail("test@test.de");
        subscription.setConsent(true);
        try {
            EmailMessage email = service.sendEmail(subscription);
            Assertions.assertEquals("test@test.de", email.getEmailAddress());
            Assertions.assertNull(email.getName());
            Assertions.assertTrue(subscription.getConsent());
            Assertions.assertEquals("Dear user,\n" + EmailMessage.EMAIL_BODY, email.getEmailBody());
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }

        subscription.setFirstName("test");

        try {
            EmailMessage email = service.sendEmail(subscription);
            Assertions.assertEquals("test@test.de", email.getEmailAddress());
            Assertions.assertEquals("test", email.getName());
            Assertions.assertTrue(subscription.getConsent());
            Assertions.assertEquals("Dear test,\n" + EmailMessage.EMAIL_BODY, email.getEmailBody());
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

}
