package es.javier.subscriptionservice.model;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity that abstracts all the information that the API responses.
 * - {@link HttpStatus} code: the response code to be returned by the API
 * - {@link List<Subscription>} subscriptionData: a placeholder for none or many elements
 * - {@link Map<String,String>} payload: a set of key-value pairs, if a header is to be sent
 * - {@link String} message: a simple message to have some info when debugging or if simple info is to be sent
 */
public class SubscriptionResponseInformation implements Serializable {
    public static final String HEADER_ID_CREATED = "Subscription-Id-Created";
    public static final String HEADER_ID_FOUND = "Subscription-Id-Found";
    public static final String HEADER_AMOUNT = "Subscriptions-Found-Amount";

    private HttpStatus code;
    private List<Subscription> subscriptionData;
    private Map<String, String> payload;
    private String message;

    // <editor-fold desc="builder interface">
    public static SubscriptionResponseInformation builder() {
        return new SubscriptionResponseInformation();
    }

    public SubscriptionResponseInformation withCode(HttpStatus code) {
        this.setCode(code);
        return this;
    }

    public SubscriptionResponseInformation withMessage(String message) {
        this.setMessage(message);
        return this;
    }

    public SubscriptionResponseInformation withSubscription(Subscription subscription) {
        if (subscription == null) {
            return this;
        }

        if (subscriptionData == null) {
            subscriptionData = new ArrayList<>();
        }

        this.subscriptionData.add(subscription);

        return this;
    }

    public SubscriptionResponseInformation withSubscriptions(List<Subscription> subscriptions) {
        this.setSubscriptionData(subscriptions);
        return this;
    }

    public SubscriptionResponseInformation havingInThePayload(String key, String value) {
        this.setPayload(key, value);
        return this;
    }
    // </editor-fold>

    // <editor-fold desc="getters && setters">
    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public Map getPayload() {
        return payload;
    }

    public void setPayload(String entry, String information) {
        if (payload == null) {
            payload = new HashMap<>();
        }
        payload.put(entry, information);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Subscription> getSubscriptionData() {
        return subscriptionData;
    }

    public void setSubscriptionData(List<Subscription> subscriptionData) {
        this.subscriptionData = subscriptionData;
    }
    // </editor-fold>

}
