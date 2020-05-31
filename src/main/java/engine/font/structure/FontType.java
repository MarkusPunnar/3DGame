package engine.font.structure;

import engine.font.GUIText;
import engine.font.TextMeshCreator;

import java.io.IOException;
import java.net.URISyntaxException;

public class FontType {

    private int textureAtlas;
    private TextMeshCreator meshCreator;

    public FontType(int textureAtlas) throws IOException {
        this.textureAtlas = textureAtlas;
        this.meshCreator = new TextMeshCreator(new FontFile("gamefont"));
    }

    public int getTextureAtlas() {
        return textureAtlas;
    }

    public TextMeshData loadText(GUIText text) {
        return meshCreator.createTextMesh(text);
    }
}
