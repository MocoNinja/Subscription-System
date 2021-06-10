package es.javier.subscriptionservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.javier.subscriptionservice.model.Subscription;
import es.javier.subscriptionservice.model.SubscriptionResponseInformation;
import es.javier.subscriptionservice.rest.SubscriptionRestController;
import es.javier.subscriptionservice.service.MessageQueueService;
import es.javier.subscriptionservice.service.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static es.javier.subscriptionservice.model.SubscriptionResponseInformation.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SubscriptionRestControllerTest {

    private final String rootEndpoint = "/rest/subscriptions";

    @InjectMocks
    private SubscriptionRestController controller;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private MessageQueueService messageQueueService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetAllSubscriptions() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation emptyResponse = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 3);

        emptyResponse.setPayload(HEADER_AMOUNT, "3");
        when(subscriptionService.findAllSubscriptions()).thenReturn(emptyResponse);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER_AMOUNT, "3"))
                .andExpect(content().contentType("application/json"))
                .andReturn();

        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNotNull(response.getSubscriptionData());
            Assertions.assertEquals(response.getSubscriptionData().size(), 3);
            Assertions.assertEquals(response.getSubscriptionData().get(0), //
                    emptyResponse.getSubscriptionData().get(0));
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testGetAllNoSubscriptions() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation emptyResponse = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        emptyResponse.setPayload(HEADER_AMOUNT, "0");
        when(subscriptionService.findAllSubscriptions()).thenReturn(emptyResponse);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HEADER_AMOUNT, "0"))
                .andExpect(content().contentType("application/json"))
                .andReturn();

        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNull(response.getSubscriptionData());
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testGet() throws Exception {
        String endpoint = rootEndpoint.concat("/{id}");

        SubscriptionResponseInformation foundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 1);

        foundResponse.setPayload(HEADER_ID_FOUND,  //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse).toString());

        when(subscriptionService.findSubscriptionById(TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse))) //
                .thenReturn(foundResponse);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.get(endpoint, //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse).toString()))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER_ID_FOUND,
                        TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse).toString()))
                .andExpect(content().contentType("application/json"))
                .andReturn();

        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNotNull(response.getSubscriptionData());
            Assertions.assertEquals(response.getSubscriptionData().size(), 1);
            Assertions.assertEquals(response.getSubscriptionData().get(0), //
                    foundResponse.getSubscriptionData().get(0));
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testGetNotFound() throws Exception {
        String endpoint = rootEndpoint.concat("/{id}");
        Long failingId = 10L;

        SubscriptionResponseInformation notFoundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        notFoundResponse.setPayload(HEADER_ID_FOUND, "null");

        when(subscriptionService.findSubscriptionById(failingId)) //
                .thenReturn(notFoundResponse);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.get(endpoint, failingId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HEADER_ID_FOUND, "null"))
                .andExpect(content().contentType("application/json"))
                .andReturn();

        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNull(response.getSubscriptionData());
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testPostOk() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation foundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.CREATED, 1);

        foundResponse.setPayload(HEADER_ID_FOUND,  //
                "null");

        foundResponse.setPayload(HEADER_ID_CREATED,  //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse).toString());

        when(subscriptionService.createSubscription(foundResponse.getSubscriptionData().get(0))) //
                .thenReturn(foundResponse);

        String jsonString = "";
        try {
            jsonString = TestUtils.asJsonString(foundResponse.getSubscriptionData().get(0));
        } catch (JsonProcessingException e) {
            Assertions.fail("No exception should be thrown when generation the JSON");
        }

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().doesNotExist(HEADER_ID_FOUND))
                .andReturn();
        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNotNull(response.getSubscriptionData());
            Assertions.assertEquals(response.getSubscriptionData().size(), 1);
            Assertions.assertEquals(response.getSubscriptionData().get(0), //
                    foundResponse.getSubscriptionData().get(0));
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testPostOkExisting() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation foundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 1);

        foundResponse.setPayload(HEADER_ID_CREATED,  //
                null);

        foundResponse.setPayload(HEADER_ID_FOUND,  //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse).toString());

        when(subscriptionService.createSubscription(foundResponse.getSubscriptionData().get(0))) //
                .thenReturn(foundResponse);

        String jsonString = "";
        try {
            jsonString = TestUtils.asJsonString(foundResponse.getSubscriptionData().get(0));
        } catch (JsonProcessingException e) {
            Assertions.fail("No exception should be thrown when generation the JSON");
        }

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().doesNotExist(HEADER_ID_CREATED))
                .andReturn();
        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNotNull(response.getSubscriptionData());
            Assertions.assertEquals(response.getSubscriptionData().size(), 1);
            Assertions.assertEquals(response.getSubscriptionData().get(0), //
                    foundResponse.getSubscriptionData().get(0));
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testPostInvalid() throws Exception {
        String endpoint = rootEndpoint;

        Subscription failSubscription = TestUtils.generateRandomSubscriptionWithMandatoryData();
        failSubscription.setEmail(null);

        SubscriptionResponseInformation foundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.BAD_REQUEST, 0);

        foundResponse.setPayload(HEADER_ID_CREATED,  //
                null);

        foundResponse.setPayload(HEADER_ID_FOUND,  //
                null);

        foundResponse.setMessage("Validation Error");

        when(subscriptionService.createSubscription(failSubscription)) //
                .thenReturn(foundResponse);

        String jsonString = "";
        try {
            jsonString = TestUtils.asJsonString(failSubscription);
        } catch (JsonProcessingException e) {
            Assertions.fail("No exception should be thrown when generating the JSON");
        }

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HEADER_ID_CREATED))
                .andExpect(header().doesNotExist(HEADER_ID_FOUND))
                .andReturn();
        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);
            Assertions.fail("This should be handled by the Exception Handler, not by the controller itself");
        } catch (Exception e) {
        }
    }

    @Test
    public void testDeleteOk() throws Exception {
        String endpoint = rootEndpoint.concat("/{id}");

        SubscriptionResponseInformation foundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 1);

        when(subscriptionService.deleteSubscriptionById(TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse))) //
                .thenReturn(foundResponse);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.delete(endpoint, //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(foundResponse).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNotNull(response.getSubscriptionData());
            Assertions.assertEquals(response.getSubscriptionData().size(), 1);
            Assertions.assertEquals(response.getSubscriptionData().get(0), //
                    foundResponse.getSubscriptionData().get(0));
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        String endpoint = rootEndpoint.concat("/{id}");
        Long failingId = 10L;

        SubscriptionResponseInformation notFoundResponse = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        when(subscriptionService.deleteSubscriptionById(failingId)) //
                .thenReturn(notFoundResponse);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.delete(endpoint, failingId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        try {
            SubscriptionResponseInformation response = TestUtils.extractResponseFromMockMvcResult(rt);

            Assertions.assertNull(response.getSubscriptionData());
        } catch (Exception e) {
            Assertions.fail("No exception should be thrown");
        }
    }

}