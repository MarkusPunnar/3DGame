package engine.font.structure;

public class Character {

    private int characterID;
    private float xTextureCoord;
    private float yTextureCoord;
    private float xMaxTextureCoord;
    private float yMaxTextureCoord;
    private float xOffset;
    private float yOffset;
    private float quadWidth;
    private float quadHeight;
    private float xAdvance;

    public Character(int characterID, float xTextureCoord, float yTextureCoord,
                     float charWidth, float charHeight, float xOffset, float yOffset, float quadWidth, float quadHeight, float xAdvance) {
        this.characterID = characterID;
        this.xTextureCoord = xTextureCoord;
        this.yTextureCoord = yTextureCoord;
        this.xMaxTextureCoord = xTextureCoord + charWidth;
        this.yMaxTextureCoord = yTextureCoord + charHeight;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.quadWidth = quadWidth;
        this.quadHeight = quadHeight;
        this.xAdvance = xAdvance;
    }

    public int getCharacterID() {
        return characterID;
    }

    public float getxTextureCoord() {
        return xTextureCoord;
    }

    public float getyTextureCoord() {
        return yTextureCoord;
    }

    public float getxMaxTextureCoord() {
        return xMaxTextureCoord;
    }

    public float getyMaxTextureCoord() {
        return yMaxTextureCoord;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public float getxAdvance() {
        return xAdvance;
    }

    public float getQuadWidth() {
        return quadWidth;
    }

    public float getQuadHeight() {
        return quadHeight;
    }
}
