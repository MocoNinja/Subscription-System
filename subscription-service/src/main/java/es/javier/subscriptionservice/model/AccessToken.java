package es.javier.subscriptionservice.model;

/**
 * Class that abstract an application's access token.
 * It has the very token and a name that represents the application that uses said token.
 * The token is stored with the SHA256 hash, not in plain
 */
public class AccessToken {
    private String applicationId;
    private String token;
    private String role;

    // <editor-fold desc="getters && setters">
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // </editor-fold>

}
