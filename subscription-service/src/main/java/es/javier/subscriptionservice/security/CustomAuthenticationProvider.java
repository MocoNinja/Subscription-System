package es.javier.subscriptionservice.security;

import es.javier.subscriptionservice.repository.AccessTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * This simple authentication provider checks if the credentials provided in a simple auth request are valid.
 * The validity of these credentials is checked with an {@link AccessTokenRepository}.
 * This method encodes the plain token with SHA256, because it is the way the token is stored.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    AccessTokenRepository repo;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String applicationId = authentication.getName();
        String token = authentication.getCredentials().toString();
        String encodedToken = DigestUtils.sha256Hex(token);

        if (repo.isValid(applicationId, encodedToken)) {
            return new UsernamePasswordAuthenticationToken(applicationId, token, new ArrayList<>());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}