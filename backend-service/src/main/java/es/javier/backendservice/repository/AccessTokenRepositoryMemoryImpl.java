package es.javier.backendservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.javier.backendservice.model.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Memory implementation of {@link AccessTokenRepository}.
 * Loads a list of allowed tokens from a Json file and loads them in memory, so when a request is received, we can use
 * that information to check if the token is allowed or not.
 */
@Repository
public class AccessTokenRepositoryMemoryImpl implements AccessTokenRepository {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenRepositoryMemoryImpl.class);

    private static final String JSON_FILE_PATH = "classpath:access_tokens.json";
    private static final String ENV_PATH = "ACCESS_TOKENS_PATH";

    private static Map<String, AccessToken> allowedTokens;

    public AccessTokenRepositoryMemoryImpl() throws Exception {
        File file;

        try {
            logger.debug("Trying to read file from classpath...");
            file = ResourceUtils.getFile(JSON_FILE_PATH);
        } catch (FileNotFoundException e) {
            logger.debug("File is not located in the classpath. Reading environment variable...");
            String envPath = System.getenv(ENV_PATH);
            file = ResourceUtils.getFile(envPath);
        }

        List<AccessToken> langList = new ObjectMapper() //
                .readValue(new FileInputStream(file), new TypeReference<List<AccessToken>>() {
                });

        allowedTokens = langList.stream().distinct() //
                .collect(Collectors.toMap(token -> token.getApplicationId(), token -> token));
    }

    @Override
    public Map<String, AccessToken> getAllTokens() {
        return allowedTokens;
    }

    @Override
    public AccessToken getToken(String applicationId) {
        if (applicationId != null) {
            return allowedTokens.get(applicationId);
        } else {
            return null;
        }
    }

    @Override
    public boolean isValid(String applicationId, String tokenIn) {
        AccessToken token = getToken(applicationId);
        if (token != null) {
            return allowedTokens.get(applicationId).getToken().equals(tokenIn);
        } else {
            return false;
        }
    }

}
