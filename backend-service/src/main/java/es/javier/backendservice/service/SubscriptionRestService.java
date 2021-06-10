package es.javier.backendservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.javier.backendservice.exception.ConnectionException;
import es.javier.backendservice.exception.UnexpectedErrorException;
import es.javier.backendservice.model.Subscription;
import es.javier.backendservice.model.SubscriptionResponseInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that consumes the Subscription api that interacts with the database and creates a response with the
 *  information received
 */
@Service
public class SubscriptionRestService {

    private static Logger logger = LoggerFactory.getLogger(SubscriptionRestService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    public static final String CREATED_HEADER = "Created-Id";
    public static final String FOUND_MESSAGE_HEADER = "Message";
    public static final String FOUND_MESSAGE_VALUE = "Element already exists";
    public static final String FOUND_MESSAGE_URI_HEADER = "Resource-Uri";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${endpoint.host}")
    private String host;

    @Value("${endpoint.port}")
    private String port;

    @Value("${endpoint.root}")
    private String root;

    public ResponseEntity<List<Subscription>> getAll() throws ConnectionException, UnexpectedErrorException {
        try {
            ResponseEntity<SubscriptionResponseInformation> response = restTemplate.getForEntity(getUrl(), SubscriptionResponseInformation.class);
            return parseResponseWithSubscriptionData(response.getBody());
        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                case UNAUTHORIZED:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                case INTERNAL_SERVER_ERROR:
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception ex) {
            if (ex.getCause().getClass().equals(ConnectException.class)) {
                logger.error("API cannot be reached!");
                throw new ConnectionException("Error when connecting to the API");
            }
            logger.error("Unexpected API error!");
            throw new UnexpectedErrorException("Unexpected error happened when connecting to the API");
        }
    }

    public ResponseEntity<List<Subscription>> getOne(Long id) throws ConnectionException, UnexpectedErrorException {
        try {
            ResponseEntity<SubscriptionResponseInformation> response = restTemplate.getForEntity(getUrl(id), SubscriptionResponseInformation.class);
            return parseResponseWithSubscriptionData(response.getBody());
        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                case UNAUTHORIZED:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                case INTERNAL_SERVER_ERROR:
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception ex) {
            if (ex.getCause().getClass().equals(ConnectException.class)) {
                logger.error("API cannot be reached!");
                throw new ConnectionException("Error when connecting to the API");
            }
            logger.error("Unexpected API error!");
            throw new UnexpectedErrorException("Unexpected error happened when connecting to the API");
        }
    }

    public ResponseEntity<String> addOne(Subscription subscription) throws ConnectionException, UnexpectedErrorException {
        Map<String, String> result = new HashMap<>();
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            ResponseEntity<SubscriptionResponseInformation> response = restTemplate.postForEntity(getUrl(),
                    subscription,
                    SubscriptionResponseInformation.class
            );

            Long elementId = response.getBody().getSubscriptionData().get(0).getSubscriptionId();

            switch (response.getStatusCode()) {
                case CREATED:
                    result.put(CREATED_HEADER, elementId.toString());
                    code = HttpStatus.CREATED;
                    break;
                case FOUND:
                    result.put(FOUND_MESSAGE_HEADER, FOUND_MESSAGE_VALUE);
                    result.put(FOUND_MESSAGE_URI_HEADER, getUrl(elementId));
                    code = HttpStatus.EXPECTATION_FAILED;
                    break;
            }
            return generateResponseWithCustomMessages(code, result);
        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                case UNAUTHORIZED:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                case INTERNAL_SERVER_ERROR:
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception ex) {
            if (ex.getCause().getClass().equals(ConnectException.class)) {
                logger.error("API cannot be reached!");
                throw new ConnectionException("Error when connecting to the API");
            }
            logger.error("Unexpected API error!");
            throw new UnexpectedErrorException("Unexpected error happened when connecting to the API");
        }
    }

    public ResponseEntity deleteOne(Long id) throws ConnectionException, UnexpectedErrorException {
        try {
            ResponseEntity<SubscriptionResponseInformation> response = restTemplate.exchange(getUrl(id),
                    HttpMethod.DELETE,
                    null,
                    SubscriptionResponseInformation.class
            );
            return generateResponseWithCustomMessages(response.getStatusCode(), null);
        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case NOT_FOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                case UNAUTHORIZED:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                case INTERNAL_SERVER_ERROR:
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception ex) {
            if (ex.getCause().getClass().equals(ConnectException.class)) {
                logger.error("API cannot be reached!");
                throw new ConnectionException("Error when connecting to the API");
            }
            logger.error("Unexpected API error!");
            throw new UnexpectedErrorException("Unexpected error happened when connecting to the API");
        }
    }

    // <editor-fold desc="Utilities">
    private String getUrl() {
        return String.format("%s:%s/%s", host, port, root);
    }

    private String getUrl(Long id) {
        return String.format("%s/%d", getUrl(), id);
    }

    private ResponseEntity<List<Subscription>> parseResponseWithSubscriptionData(SubscriptionResponseInformation apiResponse) {
        HttpStatus code = apiResponse.getCode();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity(apiResponse.getSubscriptionData(), headers, code);
    }

    protected ResponseEntity<String> generateResponseWithCustomMessages(HttpStatus code, Map<String, String> codes) throws JsonProcessingException {
        String json = null;

        if (codes != null && !codes.isEmpty()) {
            json = mapper.writeValueAsString(codes);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(json, headers, code);
    }
    //</editor-fold>

}
