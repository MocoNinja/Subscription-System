package es.javier.emailservice.model;

import es.javier.emailservice.exception.InvalidEmailException;

/**
 * Model that mocks what an email should look like if actual sending were involved.
 */
public class EmailMessage {

    public static final String EMAIL_BODY = "You are receiving this email because you subscribed to one of our " +
            "newsletters and consented to receive emails.\n" +
            "Remember that you can always cancel your subscription.\n" +
            "Best regards!\n";

    private String email;
    private String name;
    private String emailBody;

    public EmailMessage(Subscription subscription) throws InvalidEmailException {
        if (subscription == null) {
            throw new InvalidEmailException("Subscription was not valid.");
        }
        this.email = subscription.getEmail();
        this.name = subscription.getFirstName();
        this.emailBody = assembleEmailBody();
    }

    private String assembleEmailBody() {
        StringBuilder sb = new StringBuilder();

        String introduction = (name == null) //
                ? "Dear user," //
                : "Dear " + name + ",";

        sb.append(introduction).append("\n");
        sb.append(EMAIL_BODY);

        return sb.toString();

    }

    public String getEmailAddress() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getEmailBody() {
        return emailBody;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EmailMessage{");

        sb.append("email='").append(email).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", emailBody='").append(emailBody.replace("\n", "[LINE BREAK]")).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
