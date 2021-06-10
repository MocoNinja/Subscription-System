package es.javier.subscriptionservice.rest;

import es.javier.subscriptionservice.model.Subscription;
import es.javier.subscriptionservice.model.SubscriptionResponseInformation;
import es.javier.subscriptionservice.repository.AccessTokenRepository;
import es.javier.subscriptionservice.service.MessageQueueService;
import es.javier.subscriptionservice.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static es.javier.subscriptionservice.model.SubscriptionResponseInformation.*;

/**
 * {@link RestController} that exposes an API to interact with the subscriptions database.
 */
@RestController
@RequestMapping("/rest/subscriptions")
public class SubscriptionRestController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionRestController.class);
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AccessTokenRepository tokenRepository;

    @Autowired
    private MessageQueueService messageQueueService;

    @Operation(summary = "Gets all subscriptions in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found 1 or more items",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "No items found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @GetMapping
    public ResponseEntity<SubscriptionResponseInformation> getAll() {
        SubscriptionResponseInformation response = subscriptionService.findAllSubscriptions();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AMOUNT, String.valueOf(response.getPayload().get(HEADER_AMOUNT)));

        return new ResponseEntity(response, headers, response.getCode());
    }

    @Operation(summary = "Gets the subscription with the id specified")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found an item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "No items found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @GetMapping("{id}")
    public ResponseEntity<SubscriptionResponseInformation> getEntityById(@PathVariable Long id) {
        SubscriptionResponseInformation response = subscriptionService.findSubscriptionById(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_ID_FOUND, String.valueOf(response.getPayload().get(HEADER_ID_FOUND)));

        return new ResponseEntity(response, headers, response.getCode());
    }

    @Operation(summary = "Persists the Subscription in the request body if it was not in the database; if it was already in the system, that one is returned, without any change happenning in the data layer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Created a new item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "302", description = "Found an item that already existed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @PostMapping
    public ResponseEntity<SubscriptionResponseInformation> postEntity(@Valid @RequestBody Subscription subscription) {
        SubscriptionResponseInformation response = subscriptionService.createSubscription(subscription);

        HttpHeaders headers = new HttpHeaders();

        String reportedHeader = (response.getCode().equals(HttpStatus.CREATED))
                ? HEADER_ID_CREATED
                : HEADER_ID_FOUND;

        headers.add(reportedHeader, String.valueOf(response.getPayload().get(reportedHeader)));
        try {
            handleNotification(response);
        } catch (AmqpConnectException e) {
            logger.error("Error when enqueueing messages. These should be retried...");
        }

        return new ResponseEntity(response, headers, response.getCode());
    }


    @Operation(summary = "Deletes the subscription corresponding to the specified id if present in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Deleted the specified item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Item not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionResponseInformation.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SubscriptionResponseInformation> removeEntity(@PathVariable Long id) {
        SubscriptionResponseInformation response = subscriptionService.deleteSubscriptionById(id);

        return new ResponseEntity(response, null, response.getCode());
    }

    private void handleNotification(SubscriptionResponseInformation response) {
        logger.debug("A subscription creation request was handling. Checking if a notification must be sent...");

        if (response.getCode().equals(HttpStatus.CREATED)) {
            logger.debug("Sending email if consent is enabled...");
            response.getSubscriptionData().stream() //
                    .filter(subscription -> subscription.isConsent()) //
                    .forEach(messageQueueService::send);
        } else {
            logger.debug("No new subscription was created, so no email must be sent...");
        }
    }
}
