package apimCliient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;

public class AuthenticationService {
	public static void main(String[] args) {
		disableSSLVerification();
//		try (Scanner sc = new Scanner(System.in)) {
//            System.out.print("Enter Username: ");
//            String username = sc.nextLine();
//            System.out.print("Enter Password: ");
//            String password = sc.nextLine();
//            System.out.print("Enter CN to get list of APIs: ");
//            String cn = sc.nextLine();
//
//            String accessToken = generateCredential(username, password);
//            if (accessToken != null) {
//                APIDataRetriever.getApiIdbyCN(accessToken, cn);
//            }
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }
		// Load properties from the file
		Properties properties = loadProperties();
        if (properties == null) {
            System.err.println("Failed to load properties file.");
            return;
        }

        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String cn = properties.getProperty("cn");
        String CLIENT_REGISTRATION_URL= properties.getProperty("CLIENT_REGISTRATION_URL");
        String payload = properties.getProperty("json.payload");
        String ACCESS_TOKEN_URL = properties.getProperty("ACCESS_TOKEN_URL");
        String PUBLISHER_REST_API_URL   = properties.getProperty("PUBLISHER_REST_API_URL");
        String TENANT_DOMAIN = properties.getProperty("TENANT_DOMAIN");
        if (username == null || password == null || cn == null) {
            System.err.println("Missing required properties: username, password, or cn.");
            return;
        }

        String accessToken = generateCredential(username, password,CLIENT_REGISTRATION_URL, ACCESS_TOKEN_URL,payload);
        if (accessToken != null) {
            try {
				APIDataRetriever.getApiIdbyCN(accessToken, cn,PUBLISHER_REST_API_URL,TENANT_DOMAIN);
			} catch(ConnectException e) {
				
			}
            catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(e instanceof ConnectException) {
	            	System.err.println("Please check if server is running");
	            }
			}
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            System.err.println("Error reading properties file: " + e.getMessage());
            return null;
        }
    }
		
   
		
	public static void disableSSLVerification() {
    	//DISABL ECERTIFICATE VALIDATION:
    			try {
    	            // Disable SSL verification
    	            TrustManager[] trustAllCertificates = new TrustManager[]{
    	                new X509TrustManager() {
    	                    public X509Certificate[] getAcceptedIssuers() {
    	                        return null;
    	                    }

    	                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

    	                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    	                }
    	           };

    	            SSLContext sc = SSLContext.getInstance("TLS");
    	            sc.init(null, trustAllCertificates, new java.security.SecureRandom());
    	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    	            // Also disable hostname verification
    	            HostnameVerifier allHostsValid = (hostname, session) -> true;
    	            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    			}catch (NoSuchAlgorithmException | NullPointerException | KeyManagementException e) {
    				System.out.println("Exception occured while executing disablecert()");
    				e.printStackTrace();
				}
    	        	
    }

	public static String generateCredential(String username, String password, String CLIENT_REGISTRATION_URL, String ACCESS_TOKEN_URL,String payload) {
//        String payload = "{\n" +
//                "  \"callbackUrl\": \"www.google.lk\",\n" +
//                "  \"clientName\": \"rest_api_publisher\",\n" +
//                "  \"owner\": \"admin\",\n" +
//                "  \"grantType\": \"client_credentials password refresh_token\",\n" +
//                "  \"saasApp\": true\n" +
//                "}";

        String base64Auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        try {
            HttpURLConnection connection = createConnection(CLIENT_REGISTRATION_URL, "POST", base64Auth);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String responseBody = readResponse(connection);
                ClientRegistrationResponse registrationResponse = ClientRegistrationResponse.fromJson(responseBody);
                return getAccessToken(registrationResponse.getClientId(), registrationResponse.getClientSecret(), username, password,ACCESS_TOKEN_URL);
            } else {
                System.err.println("Request failed with response code: " + connection.getResponseCode());  }
        } catch (IOException | SecurityException | IllegalStateException e) {
            System.err.println("Error generating credential: " + e.getMessage());
            e.printStackTrace();
            if(e instanceof ConnectException) {
            	System.out.println("Please check if server is running");
            }
            
        }
        return null;
    }

   
    public static String getAccessToken(String clientId, String clientSecret, String username, String password, String ACCESS_TOKEN_URL){
        String scope = "apim:api_view apim:api_create";
        String encodedCredentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        String requestBody = "grant_type=password&username=" + username + "&password=" + password + "&scope=" + scope;

        try {
            HttpURLConnection connection = createConnection(ACCESS_TOKEN_URL, "POST", encodedCredentials);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String responseBody = readResponse(connection);
                OAuthTokenResponse tokenResponse = OAuthTokenResponse.fromJson(responseBody);
                return tokenResponse.getAccessToken();
            } else {
                System.err.println("Error occurred while generating access token: " + connection.getResponseCode());
            }
        } catch (IOException | SecurityException | IllegalStateException e) {
            System.err.println("Error occurred while generating access token: " + e.getMessage());
        }
        return null;
    }
    
    
    public static HttpURLConnection createConnection(String urlString, String method, String authorizationHeader) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Basic " + authorizationHeader);
        return connection;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        return content.toString();
    }

}