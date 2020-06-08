package game.config;

public enum ShadowLevel {

    LOW(2048, "Low"),
    MEDIUM(4096, "Medium"),
    HIGH(8192, "High");

    private final int resolution;
    private final String levelAsString;

    ShadowLevel(int resolution, String levelAsString) {
        this.resolution = resolution;
        this.levelAsString = levelAsString;
    }

    public int getResolution() {
        return resolution;
    }

    public String getLevelAsString() {
        return levelAsString;
    }

    public static ShadowLevel getById(int id) {
        for (ShadowLevel level : values()) {
            if (level.ordinal() == id % 3) {
                return level;
            }
        }
        throw new IllegalArgumentException("No level with such Id found");
    }
}
