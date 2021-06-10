package es.javier.subscriptionservice.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstraction of a subscription and database entity.
 */
@Entity
@Table(name = "subscriptions")
public class Subscription implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBSCRIPTION_ID")
    private Long subscriptionId;

    @Column(name = "EMAIL")
    @NotBlank(message = "Email must be specified")
    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",  //
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email must be valid")
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "CONSENT")
    @NotNull(message = "Consent must be specified")
    private Boolean consent;

    @Column(name = "BIRTHDATE")
    @NotNull(message = "Birthdate must be specified")
    @Past(message = "Birthdate cannot be in the future")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthdate;

    @Column(name = "NEWSLETTER_ID")
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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
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
