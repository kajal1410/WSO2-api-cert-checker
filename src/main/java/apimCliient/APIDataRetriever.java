package apimCliient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIDataRetriever {

    // Propagate exception to the calling method if it occurs
    public static void getApiIdbyCN(String accessToken, String CN, String PUBLISHER_REST_API_URL, String TENANT_DOMAIN) throws Exception {
        // Retrieve all APIs
        List<RetriveApiResponse> apiList = retriveAllAPI(accessToken, PUBLISHER_REST_API_URL, TENANT_DOMAIN);

        // Check if the API list is null or empty, and return early if true
        if (apiList == null || apiList.isEmpty()) {
            System.err.println("No APIs found or an error occurred while fetching APIs.");
            return; // Stop further execution and don't proceed to the loop
        }

        // Proceed if API list is not empty
        int count = 0;
        for (RetriveApiResponse api : apiList) {
            count = retrieveCertByApiID(api.getId(), accessToken, CN, PUBLISHER_REST_API_URL);
        }

        if (count == 0) {
            System.out.println("No APIs have a certificate matching with CN= " + CN);
        }
    }

    // Method that could throw exceptions, so it should declare throws
    public static List<RetriveApiResponse> retriveAllAPI(String accessToken, String PUBLISHER_REST_API_URL, String TENANT_DOMAIN) throws Exception {
        List<RetriveApiResponse> apiList = new ArrayList<>();

        String response = sendRequest(PUBLISHER_REST_API_URL, accessToken, TENANT_DOMAIN);
        JSONObject responseJson = new JSONObject(response);
        JSONArray apiArray = responseJson.getJSONArray("list");

        // Loop through each API and store it in the list
        for (int i = 0; i < apiArray.length(); i++) {
            JSONObject apiJson = apiArray.getJSONObject(i);
            RetriveApiResponse api = new RetriveApiResponse(
                    apiJson.getString("id"),
                    apiJson.getString("name"),
                    apiJson.getString("context"),
                    apiJson.getString("version")
            );
            apiList.add(api);
        }

        return apiList;
    }

    public static int retrieveCertByApiID(String apiId, String accessToken, String CN, String PUBLISHER_REST_API_URL) throws IOException {
        String apiUrl = PUBLISHER_REST_API_URL + apiId + "/client-certificates";
        int count = 0;

        String response = sendRequest(apiUrl, accessToken, "");
        JSONObject responseJson = new JSONObject(response);
        JSONArray certificateArray = responseJson.getJSONArray("certificates");

        // Loop through each certificate
        if(response != null) {
        for (int i = 0; i < certificateArray.length(); i++) {
            JSONObject certJson = certificateArray.getJSONObject(i);
            APICertsResponse certificate = new APICertsResponse(
                    certJson.getString("alias"),
                    certJson.getString("apiId"),
                    certJson.getString("tier")
            );
            count += retriveCertificateDetails(apiId, certificate.getAlias(), accessToken, CN, certificate.getApiId(), PUBLISHER_REST_API_URL);
        }}

        return count;
    }

    private static int retriveCertificateDetails(String apiId, String alias, String accessToken, String CN, String Name, String PUBLISHER_REST_API_URL) throws IOException {
        String apiUrl = PUBLISHER_REST_API_URL + apiId + "/client-certificates/" + alias;
        int count = 0;

        String response = sendRequest(apiUrl, accessToken, "");
        JSONObject responseJson = new JSONObject(response);
        String subject = responseJson.getString("subject");

        if (subject.contains("CN=" + CN)) {
            System.out.println("API ID: " + apiId + " and Name: " + Name + " has a certificate with CN=" + CN);
            count++;
        }

        return count;
    }

    private static String sendRequest(String apiUrl, String accessToken, String tenant_domain) throws IOException {
        URL url = new URL(apiUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);

        // Add X-WSO2-Tenant header if it's provided
        if (tenant_domain != null && !tenant_domain.isEmpty()) {
            connection.setRequestProperty("X-WSO2-Tenant", tenant_domain);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }
}
