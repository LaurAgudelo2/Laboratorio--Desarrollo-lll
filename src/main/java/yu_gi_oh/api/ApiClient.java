package yu_gi_oh.api;

import org.json.JSONArray;
import org.json.JSONObject;
import yu_gi_oh.model.Card;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private static final String API_URL = "https://db.ygoprodeck.com/api/v7/randomcard.php";
    private final HttpClient httpClient;
    private static final int MAX_ATTEMPTS = 5; // Límite de intentos por carta

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public Card fetchRandomMonsterCard() throws IOException, InterruptedException {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("API error: Status " + response.statusCode() + " - " + response.body());
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray data = json.optJSONArray("data");
            if (data == null || data.isEmpty()) {
                throw new IOException("No data returned from API");
            }

            JSONObject cardData = data.getJSONObject(0);
            String type = cardData.optString("type", "").toLowerCase();
            if (type.contains("monster")) {
                String name = cardData.optString("name", "Unknown");
                int atk = cardData.optInt("atk", 0);
                int def = cardData.optInt("def", 0);
                String imageUrl = cardData.optJSONArray("card_images")
                        .optJSONObject(0, new JSONObject())
                        .optString("image_url", "");
                return new Card(name, atk, def, imageUrl);
            }
        }
        throw new IOException("No se pudo obtener una carta Monster después de " + MAX_ATTEMPTS + " intentos");
    }
}