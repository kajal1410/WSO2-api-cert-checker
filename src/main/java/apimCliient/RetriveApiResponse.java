package apimCliient;

public class RetriveApiResponse {
    private String id;
    private String name;
    private String context;
    private String version;

    // Constructor
    public RetriveApiResponse(String id, String name, String context, String version) {
        this.id = id;
        this.name = name;
        this.context = context;
        this.version = version;
    }

    // Getters for API properties
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContext() {
        return context;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "API Name: " + name + "\n" +
               "API ID: " + id + "\n" +
               "API Context: " + context + "\n" +
               "API Version: " + version + "\n";
    }
}
