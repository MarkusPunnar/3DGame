package engine.font.structure;

import java.util.ArrayList;
import java.util.List;

public class Word {

    private List<Character> characters;
    private float fontSize;
    private float width;

    public Word(float fontSize) {
        this.fontSize = fontSize;
        this.characters = new ArrayList<>();
        this.width = 0;
    }

    public void addCharacter(Character character) {
        characters.add(character);
        width += character.getxAdvance() * fontSize;
    }

    public float getWidth() {
        return width;
    }

    public List<Character> getCharacters() {
        return characters;
    }
}
