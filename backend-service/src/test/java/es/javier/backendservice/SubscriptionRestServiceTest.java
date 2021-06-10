package es.javier.backendservice;

import es.javier.backendservice.exception.ConnectionException;
import es.javier.backendservice.exception.UnexpectedErrorException;
import es.javier.backendservice.model.Subscription;
import es.javier.backendservice.model.SubscriptionResponseInformation;
import es.javier.backendservice.service.SubscriptionRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.Mockito.when;

public class SubscriptionRestServiceTest {

    @InjectMocks
    private SubscriptionRestService subscriptionService;

    @Mock
    private RestTemplate mockTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllWhenData() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 3);

        when(mockTemplate.getForEntity(getUrl(), SubscriptionResponseInformation.class)) //
                .thenReturn(new ResponseEntity(responseData, HttpStatus.OK));

        try {
            ResponseEntity<List<Subscription>> response = subscriptionService.getAll();
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(3, response.getBody().size());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testGetAllWhenNoData() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        when(mockTemplate.getForEntity(getUrl(), SubscriptionResponseInformation.class)) //
                .thenReturn(new ResponseEntity(responseData, HttpStatus.NOT_FOUND));

        try {
            ResponseEntity<List<Subscription>> response = subscriptionService.getAll();
            Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            Assertions.assertNull(response.getBody());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testGetOne() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 1);

        when(mockTemplate.getForEntity(getUrl(TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData)), //
                SubscriptionResponseInformation.class)) //
                .thenReturn(new ResponseEntity(responseData, HttpStatus.OK));

        try {
            ResponseEntity<List<Subscription>> response = subscriptionService //
                    .getOne(TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData));
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(1, response.getBody().size());
            Assertions.assertEquals(TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData), //
                    response.getBody().get(0).getSubscriptionId());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testGetOneNotFound() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        when(mockTemplate.getForEntity(getUrl(1L), //
                SubscriptionResponseInformation.class)) //
                .thenReturn(new ResponseEntity(responseData, HttpStatus.NOT_FOUND));

        try {
            ResponseEntity<List<Subscription>> response = subscriptionService //
                    .getOne(1L);
            Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            Assertions.assertNull(response.getBody());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testPostCreated() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.CREATED, 1);

        when(mockTemplate.postForEntity(getUrl(),
                responseData.getSubscriptionData().get(0),
                SubscriptionResponseInformation.class))
                .thenReturn(new ResponseEntity(responseData, HttpStatus.CREATED));

        String expectedJson = String.format("{\"%s\":\"%d\"}", subscriptionService.CREATED_HEADER, //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData));

        try {
            ResponseEntity<String> response = subscriptionService //
                    .addOne(responseData.getSubscriptionData().get(0));
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertEquals(expectedJson, response.getBody());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testPostFound() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.FOUND, 1);

        when(mockTemplate.postForEntity(getUrl(),
                responseData.getSubscriptionData().get(0),
                SubscriptionResponseInformation.class))
                .thenReturn(new ResponseEntity(responseData, HttpStatus.FOUND));

        String expectedJson = String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}",
                subscriptionService.FOUND_MESSAGE_HEADER,
                subscriptionService.FOUND_MESSAGE_VALUE,
                subscriptionService.FOUND_MESSAGE_URI_HEADER,
                getUrl(TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData)));

        try {
            ResponseEntity<String> response = subscriptionService //
                    .addOne(responseData.getSubscriptionData().get(0));
            Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertEquals(expectedJson, response.getBody());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");

        }
    }

    @Test
    public void testDeleteOne() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 1);

        when(mockTemplate.exchange( //
                getUrl(TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData)), //
                HttpMethod.DELETE, //
                null, //
                SubscriptionResponseInformation.class))
                .thenReturn(new ResponseEntity(responseData, HttpStatus.OK));

        try {
            ResponseEntity response = subscriptionService //
                    .deleteOne(TestUtils.getFromResponseTheIdOfTheFirstRecord(responseData));
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNull(response.getBody());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testDeleteOneNotFound() {
        SubscriptionResponseInformation responseData = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        when(mockTemplate.exchange( //
                getUrl(1L), //
                HttpMethod.DELETE, //
                null, //
                SubscriptionResponseInformation.class))
                .thenReturn(new ResponseEntity(responseData, HttpStatus.NOT_FOUND));

        try {
            ResponseEntity response = subscriptionService //
                    .deleteOne(1L);
            Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            Assertions.assertNull(response.getBody());
        } catch (ConnectionException | UnexpectedErrorException e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    private String getUrl() {
        // Properties won't be read in a test
        return String.format("%s:%s/%s", null, null, null);
    }

    private String getUrl(Long id) {
        return String.format("%s/%d", getUrl(), id);
    }
}
