package es.javier.subscriptionservice.service;

import es.javier.subscriptionservice.model.Subscription;
import es.javier.subscriptionservice.model.SubscriptionResponseInformation;
import es.javier.subscriptionservice.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.javier.subscriptionservice.model.SubscriptionResponseInformation.*;

@Service
/**
 * Service class that wraps the {@link SubscriptionRepository} operations accessing the database.
 * Basic CRUD operations are provided, handling the expected possible business exceptions and returning a common model
 * {@link SubscriptionResponseInformation} to gather all the information (model, status code, possible informational
 *  messages, possible data...)
 */
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    private SubscriptionRepository repository;

    /**
     * Search a subscription by email. Wraps the response in an {@link Optional}.
     *
     * @param email the (unique) key we are looking the subscription for.
     * @return {@link Optional<Subscription>} optional that contains the {@link Subscription} if the entry exists in
     * the database already.
     */
    private Optional<Subscription> checkIfEmailIsInDatabase(String email) {
        Optional<Subscription> subscription = (email == null || email.isBlank())
                ? Optional.empty()
                : repository.findByEmail(email);

        if (subscription.isEmpty()) {
            logger.debug("Subscription email {} was not found...", email);
        } else {
            logger.debug("Subscription with id {} and email {} was found...",  //
                    subscription.get().getSubscriptionId(), email);
        }

        return subscription;
    }

    /**
     * Search a subscription by email. Wraps the response in an {@link Optional}.
     *
     * @param id the key from the entry
     * @return {@link Optional<Subscription>} optional that contains the {@link Subscription} if the entry exists in
     * the database already.
     */
    private Optional<Subscription> checkIfIdIsInDatabase(Long id) {
        Optional<Subscription> subscription = (id == null)
                ? Optional.empty()
                : repository.findById(id);

        if (subscription.isEmpty()) {
            logger.debug("Subscription id {} was not found...", id);
        } else {
            logger.debug("Subscription with id {} and email {} was found...",  //
                    id, subscription.get().getSubscriptionId());
        }

        return subscription;
    }

    /**
     * Given a subscription email, find if such element exists in the database.
     *
     * @param
     * @return {@link SubscriptionResponseInformation} having the information. Most importantly:
     * {@link Long} id: the id of the subscription, if it exists.
     * {@link HttpStatus} code: the result of the operation, either {@link HttpStatus#FOUND} if the entry was already
     * in the database or {@link HttpStatus#NOT_FOUND} if the entry does not exist.
     */
    public SubscriptionResponseInformation findSubscriptionByEmail(String email) {
        Optional<Subscription> found = checkIfEmailIsInDatabase(email);

        Subscription result;
        HttpStatus statusCode;
        String msg;
        String persistedId;

        if (found.isEmpty()) {
            statusCode = HttpStatus.NOT_FOUND;
            result = null;
            msg = "Entity of email: " + email + " was not found.";
            persistedId = null;
        } else {
            statusCode = HttpStatus.OK;
            result = found.get();
            msg = "Entity of email: " + result.getEmail() + " was found (id: " + result.getSubscriptionId() + ").";
            persistedId = result.getSubscriptionId().toString();
        }

        return SubscriptionResponseInformation.builder() //
                .withCode(statusCode) //
                .withSubscription(result) //
                .withMessage(msg) //
                .havingInThePayload(HEADER_ID_FOUND, persistedId);
    }

    /**
     * Given a subscription id, find if such element exists in the database.
     *
     * @param id
     * @return {@link SubscriptionResponseInformation} having the information. Most importantly:
     * {@link Long} id: the id of the subscription, if it exists.
     * {@link HttpStatus} code: the result of the operation, either {@link HttpStatus#OK} if the entry was already
     * in the database or {@link HttpStatus#NOT_FOUND} if the entry does not exist.
     */
    public SubscriptionResponseInformation findSubscriptionById(Long id) {
        Optional<Subscription> found = checkIfIdIsInDatabase(id);

        Subscription result;
        HttpStatus statusCode;
        String msg;
        String persistedId;

        if (found.isEmpty()) {
            statusCode = HttpStatus.NOT_FOUND;
            result = null;
            msg = "Entity of id: " + id + " was not found.";
            persistedId = null;
        } else {
            statusCode = HttpStatus.OK;
            result = found.get();
            msg = "Entity of email: " + result.getEmail() + " was found (id: " + result.getSubscriptionId() + ").";
            persistedId = result.getSubscriptionId().toString();
        }

        return SubscriptionResponseInformation.builder() //
                .withCode(statusCode) //
                .withSubscription(result) //
                .withMessage(msg) //
                .havingInThePayload(HEADER_ID_FOUND, persistedId);

    }

    /**
     * Finds all existing subscriptions in the database.
     *
     * @return {@link SubscriptionResponseInformation} having the information. Most importantly:
     * {@link HttpStatus} code: the result of the operation, either {@link HttpStatus#OK} if entries were found in the
     * the database or {@link HttpStatus#NOT_FOUND} if no entries were found.
     */
    public SubscriptionResponseInformation findAllSubscriptions() {
        List<Subscription> subscriptionList = new ArrayList<>();

        repository.findAll().forEach(subscriptionList::add);

        HttpStatus statusCode = (subscriptionList.size() == 0) //
                ? HttpStatus.NOT_FOUND //
                : HttpStatus.OK;

        String msg = "Amount of elements found: " + subscriptionList.size();

        return SubscriptionResponseInformation.builder() //
                .withCode(statusCode) //
                .withSubscriptions(subscriptionList) //
                .withMessage(msg) //
                .havingInThePayload(HEADER_AMOUNT, String.valueOf(subscriptionList.size()));
    }

    /**
     * Checks if the entry already exists in database before performing the insertion in order
     * to simplify exception handling.
     *
     * @param subscription {@link Subscription} having the information to be saved into database.
     * @return {@link SubscriptionResponseInformation} having the information. Most importantly:
     * {@link Long} id: the id of the subscription that has been generated or rescued from the database.
     * {@link HttpStatus} code: the result of the operation, either {@link HttpStatus#FOUND} if the entry was already
     * in the database and no insertion was performed or {@link HttpStatus#CREATED} if the entry has been generated.
     */
    public SubscriptionResponseInformation createSubscription(Subscription subscription) {
        Optional<Subscription> found = checkIfEmailIsInDatabase(subscription.getEmail());

        Subscription result;
        HttpStatus statusCode;
        String msg;
        String persistedId;
        String headerIdName;

        if (found.isPresent()) {
            logger.debug("Skipping insertion because entry with email {} is already present [ENTRY'S ID: {}]", //
                    found.get().getSubscriptionId(), subscription.getEmail());

            statusCode = HttpStatus.FOUND;
            result = found.get();
            msg = "Entry of email: " + subscription.getEmail() + " already exists.";
            persistedId = found.get().getSubscriptionId().toString();
            headerIdName = HEADER_ID_FOUND;
        } else {
            logger.info("Persisting new entity with email {}...", subscription.getEmail());

            result = repository.save(subscription);
            statusCode = HttpStatus.CREATED;
            msg = "Created entry with email: " + result.getEmail() + " and id: " + result.getSubscriptionId();
            persistedId = result.getSubscriptionId().toString();
            headerIdName = HEADER_ID_CREATED;
        }

        return SubscriptionResponseInformation.builder() //
                .withCode(statusCode) //
                .withSubscription(result) //
                .withMessage(msg) //
                .havingInThePayload(headerIdName, persistedId);
    }

    public SubscriptionResponseInformation deleteSubscriptionByEmail(String email) {
        Optional<Subscription> found = checkIfEmailIsInDatabase(email);

        Subscription result;
        HttpStatus statusCode;
        String msg;

        if (found.isEmpty()) {
            statusCode = HttpStatus.NOT_FOUND;
            result = null;
            msg = "Entity of email: " + email + " was not found.";
        } else {
            statusCode = HttpStatus.OK;
            result = found.get();
            msg = "Entity of email: " + email + " was deleted (id: " + result.getSubscriptionId() + ").";
            repository.delete(found.get());
        }

        return SubscriptionResponseInformation.builder() //
                .withCode(statusCode) //
                .withSubscription(result) //
                .withMessage(msg);
    }

    /**
     * Deletes a subscription from the database.
     *
     * @param id
     * @return {@link SubscriptionResponseInformation} having the information. Most importantly:
     * {@link Long} id: the id of the subscription that has been removed from the database if it was present.
     * {@link HttpStatus} code: the result of the operation, either {@link HttpStatus#OK} if the entry was in the
     * database or {@link HttpStatus#NOT_FOUND} if the entry was not in the database.
     */
    public SubscriptionResponseInformation deleteSubscriptionById(Long id) {
        Optional<Subscription> found = checkIfIdIsInDatabase(id);

        Subscription result;
        HttpStatus statusCode;
        String msg;

        if (found.isEmpty()) {
            statusCode = HttpStatus.NOT_FOUND;
            result = null;
            msg = "Entity of id: " + id + " was not found.";
        } else {
            statusCode = HttpStatus.OK;
            result = found.get();
            msg = "Entity of id: " + id + " was deleted (id: " + result.getSubscriptionId() + ").";
            repository.delete(found.get());
        }

        return SubscriptionResponseInformation.builder() //
                .withCode(statusCode) //
                .withSubscription(result) //
                .withMessage(msg);
    }

}
