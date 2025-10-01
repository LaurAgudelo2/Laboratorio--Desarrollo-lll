package yu_gi_oh.api;

import org.json.JSONArray;
import org.json.JSONObject;
import yu_gi_oh.model.Card;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class ApiClient {
    private static final String[] MONSTER_NAMES = {
            "Blue-Eyes White Dragon", "Dark Magician", "Summoned Skull",
            "Gaia The Fierce Knight", "Celtic Guardian", "Mystical Elf",
            "Battle Ox", "Time Wizard", "Kuriboh", "Jinzo",
            "Red-Eyes B. Dragon", "Exodia the Forbidden One", "Magnet Warrior",
            "Harpie Lady", "Toon Mermaid", "Man-Eater Bug"
    };

    private final Random random;
    private final HttpClient httpClient;

    public ApiClient() {
        this.random = new Random();
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public Card getRandomMonsterCard() throws Exception {
        // Primero intentar con la API real
        try {
            return getRandomMonsterCardFromAPI();
        } catch (Exception e) {
            System.err.println("Error con API, usando datos de muestra: " + e.getMessage());
            // Fallback a datos de muestra
            return getSampleMonsterCard();
        }
    }

    private Card getRandomMonsterCardFromAPI() throws Exception {
        // Usar el endpoint de búsqueda con tipo Monster
        String url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?type=Normal%20Monster";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API responded with: " + response.statusCode());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray cards = jsonResponse.getJSONArray("data");

        if (cards.length() == 0) {
            throw new RuntimeException("No monsters found");
        }

        JSONObject cardData = cards.getJSONObject(random.nextInt(cards.length()));

        String name = cardData.getString("name");
        int atk = cardData.optInt("atk", 1000 + random.nextInt(2000));
        int def = cardData.optInt("def", 1000 + random.nextInt(2000));

        String imageUrl = "";
        if (cardData.has("card_images") && cardData.getJSONArray("card_images").length() > 0) {
            imageUrl = cardData.getJSONArray("card_images")
                    .getJSONObject(0)
                    .getString("image_url");
        }

        return new Card(name, atk, def, imageUrl, "Monster");
    }

    private Card getSampleMonsterCard() {
        // Datos de muestra para cuando la API falle
        String name = MONSTER_NAMES[random.nextInt(MONSTER_NAMES.length)];
        int atk = 500 + random.nextInt(3000); // ATK entre 500 y 3500
        int def = 500 + random.nextInt(3000); // DEF entre 500 y 3500

        // Imagen placeholder
        String imageUrl = "https://via.placeholder.com/200x280/4A6572/FFFFFF?text=" +
                name.replace(" ", "+");

        return new Card(name, atk, def, imageUrl, "Monster");
    }
}