package es.javier.backendservice.rest;

import es.javier.backendservice.exception.ConnectionException;
import es.javier.backendservice.exception.UnexpectedErrorException;
import es.javier.backendservice.model.Subscription;
import es.javier.backendservice.service.SubscriptionRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller that exposes the functionality to a frontend or whatever service we want to use.
 */
@RestController
@RequestMapping("/rest/subscriptions")
public class BackendRestController {

    private static Logger logger = LoggerFactory.getLogger(BackendRestController.class);

    @Autowired
    private SubscriptionRestService subscriptionRestService;

    @Operation(summary = "Gets all subscriptions in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found 1 or more items",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Subscription[].class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "No items found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @GetMapping
    public ResponseEntity<List<Subscription>> getAll() throws UnexpectedErrorException, ConnectionException {
        return subscriptionRestService.getAll();
    }

    @Operation(summary = "Gets the subscription with the id specified")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found an item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Subscription[].class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "No items found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @GetMapping("{id}")
    public ResponseEntity<List<Subscription>> getEntityById(@PathVariable Long id) throws UnexpectedErrorException, ConnectionException {
        return subscriptionRestService.getOne(id);
    }

    @Operation(summary = "Persists the Subscription in the request body if it was not in the database; if it was already in the system, that one is returned, without any change happenning in the data layer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Created a new item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "417", description = "Found an item that already existed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @PostMapping
    public ResponseEntity<String> addOne(@Valid @RequestBody Subscription subscription) throws UnexpectedErrorException, ConnectionException {
        ResponseEntity<String> responseEntity =  subscriptionRestService.addOne(subscription);
        return responseEntity;
    }


    @Operation(summary = "Deletes the subscription corresponding to the specified id if present in the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Deleted the specified item",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "Item not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class))}
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity removeEntity(@PathVariable Long id) throws UnexpectedErrorException, ConnectionException {
        return subscriptionRestService.deleteOne(id);
    }
}
