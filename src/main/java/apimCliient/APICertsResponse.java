package apimCliient;

public class APICertsResponse {
	
	private String alias;
    private String apiId;
    private String tier;

    // Constructor
    public APICertsResponse(String alias, String apiId, String tier) {
        this.alias = alias;
        this.apiId = apiId;
        this.tier = tier;
    }

    // Getters for certificate properties
    public String getAlias() {
        return alias;
    }

    public String getApiId() {
        return apiId;
    }

    public String getTier() {
        return tier;
    }

    @Override
    public String toString() {
        return "Certificate Alias: " + alias + "\n" +
               "API ID: " + apiId + "\n" +
               "Tier: " + tier + "\n";
    }



}

