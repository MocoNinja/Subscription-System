package es.javier.backendservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.javier.backendservice.model.Subscription;
import es.javier.backendservice.model.SubscriptionResponseInformation;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Random RANDOM = new Random();

    private static final String[] NAMES = {"Jack", "Jane", "John", "Jill"};

    public static Long id = 1L;

    public static Subscription generateRandomSubscriptionWithMandatoryData() {
        String email = getRandomName().toLowerCase().concat("@test.com");
        return generateRandomSubscription(email, null, null);
    }

    public static Subscription generateRandomSubscription(String email, String name, String gender) {
        Subscription subscription = new Subscription();

        subscription.setSubscriptionId(id++);
        subscription.setEmail(email);
        subscription.setConsent(true);
        subscription.setNewsletterId(1L);
        subscription.setBirthdate(LocalDate.of(1993, 04, 01));
        subscription.setFirstName(name);
        subscription.setGender(gender);

        return subscription;
    }

    public static SubscriptionResponseInformation generateResponseWithData(HttpStatus code, int amountOfValidSubscriptions) {
        SubscriptionResponseInformation subscriptionResponseInformation = new SubscriptionResponseInformation();
        subscriptionResponseInformation.setCode(code);
        if (amountOfValidSubscriptions > 0) {
            List<Subscription> subscriptionList = new ArrayList<>();
            for (int i = 0; i < amountOfValidSubscriptions; i++) {
                subscriptionList.add(generateRandomSubscriptionWithMandatoryData());
            }
            subscriptionResponseInformation.setSubscriptionData(subscriptionList);
        } else {
            subscriptionResponseInformation.setSubscriptionData(null);
        }

        return subscriptionResponseInformation;
    }

    public static Long getFromResponseTheIdOfTheFirstRecord(SubscriptionResponseInformation response) {
        return (response.getSubscriptionData() == null || response.getSubscriptionData().size() == 0)
                ? null
                : response.getSubscriptionData().get(0).getSubscriptionId();
    }

    public static String asJsonString(Subscription subscription) throws JsonProcessingException {
        return MAPPER.writeValueAsString(subscription);
    }

    private static String getRandomName() {
        return NAMES[RANDOM.nextInt(NAMES.length)];
    }

}
