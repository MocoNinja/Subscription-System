package es.javier.backendservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration of the requests that will call the API. This makes every request sent to be of type Basic Auth, with
 *   the credentials specified in the properties file
 */
@Configuration
public class RestTemplateBeanConfig {

    @Value("${api.username}")
    private String username;

    @Value("${api.token}")
    private String token;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder
                .basicAuthentication(username, token)
                .build();
    }
}

