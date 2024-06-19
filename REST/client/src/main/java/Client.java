import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import service.core.ClientInfo;
import service.core.Quotation;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class Client {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:8083";

    public void createApplication(ClientInfo info) throws IOException {

        String json = mapper.writeValueAsString(info);

        List<String> urls = fetchQuoteUrls();

        for (String url : urls) {
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                if (response.body() == null) {
                    throw new IOException("Response body is null");
                } else {
                    String responseBody = response.body().string();
                    Quotation quotation = mapper.readValue(responseBody, Quotation.class);
                    displayQuotation(quotation);
                }

            }
        }

    }

    public List<String> fetchQuoteUrls() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/services")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return mapper.readValue(response.body().string(), new TypeReference<List<String>>(){});
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        for (ClientInfo info : clients) {
            try {
                displayProfile(info);
                client.createApplication(info);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println();
            }
        }

        System.exit(0);
    }

    public static void displayProfile(ClientInfo info) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
                        " | Age: " + String.format("%1$-30s", info.age)+" |");
        System.out.println(
                "| Weight/Height: " + String.format("%1$-20s", info.weight+"kg/"+info.height+"m") +
                        " | Smoker: " + String.format("%1$-27s", info.smoker?"YES":"NO") +
                        " | Medical Problems: " + String.format("%1$-17s", info.medicalIssues?"YES":"NO")+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Display a quotation nicely - note that the assumption is that the quotation will follow
     * immediately after the profile (so the top of the quotation box is missing).
     *
     * @param quotation
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Peter Piper", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
            new ClientInfo("Mickey Mouse", ClientInfo.MALE, 49, 1.8, 120, false, true),
            new ClientInfo("Lionel Messi", ClientInfo.MALE, 55, 1.9, 75, true, false),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };
}
