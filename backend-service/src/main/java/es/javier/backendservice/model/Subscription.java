package es.javier.backendservice.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstraction of a subscription.
 */
public class Subscription implements Serializable {

    private Long subscriptionId;

    @NotBlank(message = "Email must be specified")
    private String email;

    private String firstName;

    private String gender;

    @NotNull(message = "Consent must be specified")
    private Boolean consent;

    @NotNull(message = "Birthdate must be specified")
    @Past(message = "Birthdate cannot be in the future")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthdate;

    @NotNull(message = "Newsletter Id must be specified")
    private Long newsletterId;

    // <editor-fold desc="getters && setters">
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean isConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getConsent() {
        return consent;
    }

    public Long getNewsletterId() {
        return newsletterId;
    }

    public void setNewsletterId(Long newsletterId) {
        this.newsletterId = newsletterId;
    }

    // </editor-fold>

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Subscription{");

        sb.append("subscriptionId=").append(subscriptionId);
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", gender='").append(gender).append('\'');
        sb.append(", consent=").append(consent);
        sb.append(", birthdate=").append(birthdate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(subscriptionId, that.subscriptionId) && Objects.equals(email, that.email) && Objects.equals(firstName, that.firstName) && Objects.equals(gender, that.gender) && Objects.equals(consent, that.consent) && Objects.equals(birthdate, that.birthdate) && Objects.equals(newsletterId, that.newsletterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriptionId, email, firstName, gender, consent, birthdate, newsletterId);
    }

}
