package es.javier.subscriptionservice.repository;

import es.javier.subscriptionservice.model.Subscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link CrudRepository} that allows easy CRUD operations for {@link Subscription} entities.
 * A method to find by email is defined so it can be used to identify subscriptions, because it is an unique field that
 * allows finding easily the entities if the id is not known.
 */
@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    Optional<Subscription> findByEmail(String email);

}
