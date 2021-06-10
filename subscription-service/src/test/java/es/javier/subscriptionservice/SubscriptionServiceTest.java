package es.javier.subscriptionservice;

import es.javier.subscriptionservice.model.Subscription;
import es.javier.subscriptionservice.model.SubscriptionResponseInformation;
import es.javier.subscriptionservice.repository.SubscriptionRepository;
import es.javier.subscriptionservice.service.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static es.javier.subscriptionservice.model.SubscriptionResponseInformation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository mockRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllWhenEmpty() {
        List<Subscription> emptySubscriptions = Collections.emptyList();

        when(mockRepository.findAll()).thenReturn(emptySubscriptions);

        SubscriptionResponseInformation response = subscriptionService.findAllSubscriptions();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getCode());
        Assertions.assertEquals(0, response.getSubscriptionData().size(), 0);
        Assertions.assertEquals("0", response.getPayload().get(HEADER_AMOUNT));
    }

    @Test
    public void testGetAllWithElements() {
        List<Subscription> subscriptions = new ArrayList<Subscription>();

        subscriptions.add(TestUtils.generateRandomSubscriptionWithMandatoryData());
        subscriptions.add(TestUtils.generateRandomSubscriptionWithMandatoryData());
        subscriptions.add(TestUtils.generateRandomSubscriptionWithMandatoryData());

        when(mockRepository.findAll()).thenReturn(subscriptions);

        SubscriptionResponseInformation response = subscriptionService.findAllSubscriptions();

        Assertions.assertEquals(HttpStatus.OK, response.getCode());
        Assertions.assertEquals(3, response.getSubscriptionData().size(), 3);
        Assertions.assertEquals("3", response.getPayload().get(HEADER_AMOUNT));
    }

    @Test
    public void testFindSubscriptionByEmail() {
        Subscription present = TestUtils.generateRandomSubscriptionWithMandatoryData();
        String missingEmail = "failingTest@test.test";

        when(mockRepository.findByEmail(present.getEmail())).thenReturn(Optional.of(present));
        when(mockRepository.findById(present.getSubscriptionId())).thenReturn(Optional.of(present));

        when(mockRepository.findByEmail(missingEmail)).thenReturn(Optional.empty());

        SubscriptionResponseInformation expectedOkResponse = subscriptionService.findSubscriptionByEmail(present.getEmail());
        SubscriptionResponseInformation expectedKoResponse = subscriptionService.findSubscriptionByEmail(missingEmail);

        // Status codes
        Assertions.assertEquals(HttpStatus.OK, expectedOkResponse.getCode());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, expectedKoResponse.getCode());

        // Data
        Assertions.assertNotNull(expectedOkResponse.getSubscriptionData());
        Assertions.assertEquals(1, expectedOkResponse.getSubscriptionData().size());
        Assertions.assertEquals(present, expectedOkResponse.getSubscriptionData().get(0));

        Assertions.assertNull(expectedKoResponse.getSubscriptionData());

        // Reported Id
        Assertions.assertEquals(present.getSubscriptionId().toString(), expectedOkResponse.getPayload() //
                .get(HEADER_ID_FOUND));

        Assertions.assertEquals(null, expectedKoResponse.getPayload().get(HEADER_ID_FOUND));
    }

    @Test
    public void testFindSubscriptionById() {
        Subscription present = TestUtils.generateRandomSubscriptionWithMandatoryData();

        when(mockRepository.findById(present.getSubscriptionId())).thenReturn(Optional.of(present));
        when(mockRepository.findById(100L)).thenReturn(Optional.empty());

        SubscriptionResponseInformation expectedOkResponse = subscriptionService.
                findSubscriptionById(present.getSubscriptionId());
        SubscriptionResponseInformation expectedKoResponse = subscriptionService.findSubscriptionById(100L);

        // Status codes
        Assertions.assertEquals(HttpStatus.OK, expectedOkResponse.getCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, expectedKoResponse.getCode());

        // Data
        Assertions.assertNotNull(expectedOkResponse.getSubscriptionData());
        Assertions.assertEquals(1, expectedOkResponse.getSubscriptionData().size());
        Assertions.assertEquals(present, expectedOkResponse.getSubscriptionData().get(0));

        Assertions.assertNull(expectedKoResponse.getSubscriptionData());

        // Reported Id
        Assertions.assertEquals(present.getSubscriptionId().toString(), expectedOkResponse.getPayload() //
                .get(HEADER_ID_FOUND));
        Assertions.assertEquals(null, expectedKoResponse.getPayload().get(HEADER_ID_FOUND));
    }

    @Test
    public void testDeleteSubscriptionByEmail() {
        Subscription present = TestUtils.generateRandomSubscriptionWithMandatoryData();
        String missingEmail = "failingTest@test.test";

        when(mockRepository.findByEmail(present.getEmail())).thenReturn(Optional.of(present));
        when(mockRepository.findByEmail(missingEmail)).thenReturn(Optional.empty());

        SubscriptionResponseInformation expectedOkResponse = subscriptionService //
                .deleteSubscriptionByEmail(present.getEmail());
        SubscriptionResponseInformation expectedKoResponse = subscriptionService.deleteSubscriptionByEmail(missingEmail);

        // Status codes
        Assertions.assertEquals(HttpStatus.OK, expectedOkResponse.getCode());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, expectedKoResponse.getCode());

        // Data
        Assertions.assertNotNull(expectedOkResponse.getSubscriptionData());
        Assertions.assertEquals(1, expectedOkResponse.getSubscriptionData().size());
        Assertions.assertEquals(present, expectedOkResponse.getSubscriptionData().get(0));
    }

    @Test
    public void testDeleteSubscription() {
        Subscription present = TestUtils.generateRandomSubscriptionWithMandatoryData();
        Subscription absent = TestUtils.generateRandomSubscriptionWithMandatoryData();

        when(mockRepository.findById(present.getSubscriptionId())).thenReturn(Optional.of(present));
        when(mockRepository.findById(absent.getSubscriptionId())).thenReturn(Optional.empty());

        SubscriptionResponseInformation expectedOkResponse = subscriptionService.deleteSubscriptionById(present.getSubscriptionId());
        SubscriptionResponseInformation expectedKoResponse = subscriptionService.deleteSubscriptionById(absent.getSubscriptionId());

        // Status codes
        Assertions.assertEquals(HttpStatus.OK, expectedOkResponse.getCode());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, expectedKoResponse.getCode());

        // Data
        Assertions.assertNotNull(expectedOkResponse.getSubscriptionData());
        Assertions.assertEquals(1, expectedOkResponse.getSubscriptionData().size());
        Assertions.assertEquals(present, expectedOkResponse.getSubscriptionData().get(0));
    }

    @Test
    public void testCreateSubscription() {
        Subscription present = TestUtils.generateRandomSubscriptionWithMandatoryData();
        String missingEmail = "failingTest@test.test";
        Subscription absent = TestUtils.generateRandomSubscription(missingEmail);

        when(mockRepository.findByEmail(present.getEmail())).thenReturn(Optional.of(present));
        when(mockRepository.findByEmail(missingEmail)).thenReturn(Optional.empty());
        when(mockRepository.save(any(Subscription.class))) //
                .thenAnswer(returnItem -> returnItem.getArgument(0));

        SubscriptionResponseInformation expectedFoundResponse = subscriptionService.createSubscription(present);
        SubscriptionResponseInformation expectedCreatedResponse = subscriptionService.createSubscription(absent);

        // Status codes
        Assertions.assertEquals(HttpStatus.FOUND, expectedFoundResponse.getCode());

        Assertions.assertEquals(HttpStatus.CREATED, expectedCreatedResponse.getCode());

        // Data
        Assertions.assertNotNull(expectedFoundResponse.getSubscriptionData());
        Assertions.assertEquals(1, expectedFoundResponse.getSubscriptionData().size());
        Assertions.assertEquals(present, expectedFoundResponse.getSubscriptionData().get(0));

        Assertions.assertNotNull(expectedCreatedResponse.getSubscriptionData());
        Assertions.assertEquals(1, expectedCreatedResponse.getSubscriptionData().size());
        Assertions.assertEquals(absent, expectedCreatedResponse.getSubscriptionData().get(0));

        // Payload (headers)
        Assertions.assertNull(expectedFoundResponse.getPayload().get(HEADER_ID_CREATED));

        Assertions.assertNull(expectedCreatedResponse.getPayload().get(HEADER_ID_FOUND));

        Assertions.assertEquals(expectedCreatedResponse.getSubscriptionData().get(0).getSubscriptionId(), //
                Long.valueOf(expectedCreatedResponse.getPayload().get(HEADER_ID_CREATED).toString()));

        Assertions.assertEquals(expectedFoundResponse.getSubscriptionData().get(0).getSubscriptionId(), //
                Long.valueOf(expectedFoundResponse.getPayload().get(HEADER_ID_FOUND).toString()));
    }
}


