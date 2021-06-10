package es.javier.backendservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.javier.backendservice.model.SubscriptionResponseInformation;
import es.javier.backendservice.rest.BackendRestController;
import es.javier.backendservice.service.SubscriptionRestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BackendRestControllerTest {

    private final String rootEndpoint = "/rest/subscriptions";
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private BackendRestController controller;

    @Mock
    private SubscriptionRestService subscriptionService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetAllSubscriptions() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation responseWithData = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 3);

        when(subscriptionService.getAll()) //
                .thenReturn(new ResponseEntity(responseWithData.getSubscriptionData(), null, HttpStatus.OK));

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andExpect(status().isOk())
                .andReturn();

        String jsonExpected = mapper.writeValueAsString(responseWithData.getSubscriptionData());
        String jsonObtained = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(jsonExpected, jsonObtained);
    }

    @Test
    public void testGetAllSubscriptionsNoData() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation emptyResponse = TestUtils.generateResponseWithData( //
                HttpStatus.NOT_FOUND, 0);

        when(subscriptionService.getAll()) //
                .thenReturn(new ResponseEntity(emptyResponse.getSubscriptionData(), null, HttpStatus.NOT_FOUND));

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonExpected = "";
        String jsonObtained = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(jsonExpected, jsonObtained);
    }

    @Test
    public void testDelete() throws Exception {
        String endpoint = rootEndpoint.concat("/{id}");

        Long failingId = 1L;

        when(subscriptionService.deleteOne(failingId)) //
                .thenReturn(ResponseEntity.ok(null));

        mockMvc.perform(MockMvcRequestBuilders.delete(endpoint, failingId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        String endpoint = rootEndpoint.concat("/{id}");

        Long failingId = 1L;

        when(subscriptionService.deleteOne(failingId)) //
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(MockMvcRequestBuilders.delete(endpoint, failingId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    public void testCreateSubscription() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation newResponse = TestUtils.generateResponseWithData( //
                HttpStatus.CREATED, 1);

        String jsonExpected = String.format("{\"%s\":\"%d\"}", subscriptionService.CREATED_HEADER, //
                TestUtils.getFromResponseTheIdOfTheFirstRecord(newResponse));

        ResponseEntity<String> expectedEntity = new ResponseEntity<>(jsonExpected, null, HttpStatus.CREATED);

        when(subscriptionService.addOne(newResponse.getSubscriptionData().get(0))) //
                .thenReturn(expectedEntity);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.post(endpoint) //
                .content(TestUtils.asJsonString(newResponse.getSubscriptionData().get(0)))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(jsonExpected))
                .andReturn();


        String jsonObtained = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(jsonExpected, jsonObtained);
    }

    @Test
    public void testCreateSubscriptionAlreadyExists() throws Exception {
        String endpoint = rootEndpoint;

        SubscriptionResponseInformation wrongResponse = TestUtils.generateResponseWithData( //
                HttpStatus.OK, 1);

        String jsonExpected = String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}",
                subscriptionService.FOUND_MESSAGE_HEADER,
                subscriptionService.FOUND_MESSAGE_VALUE,
                subscriptionService.FOUND_MESSAGE_URI_HEADER,
                endpoint.concat(TestUtils.getFromResponseTheIdOfTheFirstRecord(wrongResponse).toString()));

        ResponseEntity<String> expectedEntity = new ResponseEntity<>(jsonExpected, null, //
                HttpStatus.EXPECTATION_FAILED);

        when(subscriptionService.addOne(wrongResponse.getSubscriptionData().get(0))) //
                .thenReturn(expectedEntity);

        MvcResult rt = mockMvc.perform(MockMvcRequestBuilders.post(endpoint) //
                .content(TestUtils.asJsonString(wrongResponse.getSubscriptionData().get(0)))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string(jsonExpected))
                .andReturn();


        String jsonObtained = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(jsonExpected, jsonObtained);
    }

}
