package es.javier.backendservice.repository;

import es.javier.backendservice.model.AccessToken;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Interface that defines the basic operations that a token provider must provide.
 * The token will follow this model: {@link AccessToken}
 */
@Repository
public interface AccessTokenRepository {

    public Map<String, AccessToken> getAllTokens();

    public AccessToken getToken(String token);

    public boolean isValid(String applicationId, String tokenIn);

}
