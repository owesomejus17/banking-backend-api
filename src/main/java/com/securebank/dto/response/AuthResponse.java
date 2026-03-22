package com.securebank.dto.response;



/**
 * Returned after a successful register or login.
 * The accessToken must be sent in subsequent requests as:
 *   Authorization: Bearer <accessToken>
 */
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String fullName;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String tokenType, String email, String fullName, String role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Manual Builder
    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String accessToken;
        private String tokenType = "Bearer";
        private String email;
        private String fullName;
        private String role;

        public AuthResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AuthResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(accessToken, tokenType, email, fullName, role);
        }
    }
}
