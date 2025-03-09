package apimCliient;

import org.json.JSONObject;

public class ClientRegistrationResponse {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String callbackUrl;
    private boolean isSaasApplication;
    
    // Getters and setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public boolean isSaasApplication() {
        return isSaasApplication;
    }

    public void setSaasApplication(boolean saasApplication) {
        isSaasApplication = saasApplication;
    }

    // Method to parse JSON and create an object from the response
    public static ClientRegistrationResponse fromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        ClientRegistrationResponse response = new ClientRegistrationResponse();
        response.setClientId(jsonObject.getString("clientId"));
        response.setClientSecret(jsonObject.getString("clientSecret"));
        response.setClientName(jsonObject.getString("clientName"));
        response.setCallbackUrl(jsonObject.getString("callBackURL"));
        response.setSaasApplication(jsonObject.getBoolean("isSaasApplication"));
        return response;
    }}