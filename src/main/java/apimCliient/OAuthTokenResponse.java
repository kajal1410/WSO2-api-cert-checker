package apimCliient;
import org.json.JSONObject;

public class OAuthTokenResponse {
		    private String accessToken;
		    private String refreshToken;
		    private String scope;
		    private String tokenType;
		    private int expiresIn;

		    // Getters and setters
		    public String getAccessToken() {
		        return accessToken;
		    }

		    public void setAccessToken(String accessToken) {
		        this.accessToken = accessToken;
		    }

		    public String getRefreshToken() {
		        return refreshToken;
		    }

		    public void setRefreshToken(String refreshToken) {
		        this.refreshToken = refreshToken;
		    }

		    public String getScope() {
		        return scope;
		    }

		    public void setScope(String scope) {
		        this.scope = scope;
		    }

		    public String getTokenType() {
		        return tokenType;
		    }

		    public void setTokenType(String tokenType) {
		        this.tokenType = tokenType;
		    }

		    public int getExpiresIn() {
		        return expiresIn;
		    }

		    public void setExpiresIn(int expiresIn) {
		        this.expiresIn = expiresIn;
		    }

		    // Method to parse JSON and create an object from the response
		    public static OAuthTokenResponse fromJson(String json) {
		        JSONObject jsonObject = new JSONObject(json);
		        OAuthTokenResponse response = new OAuthTokenResponse();
		        response.setAccessToken(jsonObject.getString("access_token"));
		        response.setRefreshToken(jsonObject.getString("refresh_token"));
		        response.setScope(jsonObject.getString("scope"));
		        response.setTokenType(jsonObject.getString("token_type"));
		        response.setExpiresIn(jsonObject.getInt("expires_in"));
		        return response;
		    }
		


	}
