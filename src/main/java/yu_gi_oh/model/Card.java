package yu_gi_oh.model;

public class Card {
    private String name;
    private int atk;
    private int def;
    private String imageUrl;
    private String type;

    public Card(String name, int atk, int def, String imageUrl, String type) {
        this.name = name;
        this.atk = atk;
        this.def = def;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    // Getters
    public String getName() { return name; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return name + " (ATK: " + atk + ", DEF: " + def + ")";
    }
}