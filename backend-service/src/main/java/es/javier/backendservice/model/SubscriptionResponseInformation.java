package es.javier.backendservice.model;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstraction of a response with information about the subscription
 */
public class SubscriptionResponseInformation implements Serializable {
    public static final String HEADER_ID = "Subscription-Id";
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
