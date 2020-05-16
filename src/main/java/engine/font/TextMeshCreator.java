package engine.font;

import engine.font.structure.Character;
import engine.font.structure.FontFile;
import engine.font.structure.Line;
import engine.font.structure.TextMeshData;
import engine.font.structure.Word;

import java.util.ArrayList;
import java.util.List;

public class TextMeshCreator {

    public static final float LINE_HEIGHT = 0.06f;
    public static final int SPACE_ASCII = 32;

    private FontFile fontFile;

    public TextMeshCreator(FontFile fontFile) {
        this.fontFile = fontFile;
    }

    public TextMeshData createTextMesh(GUIText text) {
        List<Line> lines = createStructure(text);
        return createQuadData(text, lines);
    }

    private TextMeshData createQuadData(GUIText text, List<Line> lines) {
        text.setLineCount(lines.size());
        float cursorX = 0f;
        float cursorY = 0f;
        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        for (Line line : lines) {
            if (text.isCentered()) {
                cursorX = (line.getMaxLength() - line.getCurrentLength()) / 2;
            }
            for (Word word : line.getLineWords()) {
                for (Character character : word.getCharacters()) {
                    addCharacterVertices(cursorX, cursorY, character, text.getFontSize(), vertices);
                    addCharacterTextureCoords(textureCoords, character);
                    cursorX += character.getxAdvance() * text.getFontSize();
                }
                cursorX += fontFile.getSpaceWidth() * text.getFontSize();
            }
            cursorX = 0;
            cursorY += LINE_HEIGHT * text.getFontSize();
        }
        return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
    }

    private void addCharacterTextureCoords(List<Float> textureCoords, Character character) {
        float textureX = character.getxTextureCoord();
        float textureY = character.getyTextureCoord();
        float maxTextureX = character.getxMaxTextureCoord();
        float maxTextureY = character.getyMaxTextureCoord();
        textureCoords.add(textureX);
        textureCoords.add(textureY);
        textureCoords.add(textureX);
        textureCoords.add(maxTextureY);
        textureCoords.add(maxTextureX);
        textureCoords.add(maxTextureY);
        textureCoords.add(maxTextureX);
        textureCoords.add(maxTextureY);
        textureCoords.add(maxTextureX);
        textureCoords.add(textureY);
        textureCoords.add(textureX);
        textureCoords.add(textureY);
    }

    private void addCharacterVertices(float cursorX, float cursorY, Character character, float fontSize, List<Float> vertices) {
        float x = cursorX + (character.getxOffset() * fontSize);
        float y = cursorY + (character.getyOffset() * fontSize);
        float maxX = x + (character.getQuadWidth() * fontSize);
        float maxY = y + (character.getQuadHeight() * fontSize);
        x = (2 * x) - 1;
        y = (-2 * y) + 1;
        maxX = (2 * maxX) - 1;
        maxY = (-2 * maxY) + 1;
        addVertices(vertices, x, y, maxX, maxY);
    }

    private void addVertices(List<Float> vertices, float x, float y, float maxX, float maxY) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(x);
        vertices.add(maxY);
        vertices.add(maxX);
        vertices.add(maxY);
        vertices.add(maxX);
        vertices.add(maxY);
        vertices.add(maxX);
        vertices.add(y);
        vertices.add(x);
        vertices.add(y);
    }

    private List<Line> createStructure(GUIText text) {
        char[] chars = text.getText().toCharArray();
        List<Line> lines = new ArrayList<>();
        Line currentLine = new Line(text.getMaxLineLength(), text.getFontSize(), fontFile.getSpaceWidth());
        Word currentWord = new Word(text.getFontSize());
        for (char c : chars) {
            if ((int) c == SPACE_ASCII) {
                boolean added = currentLine.addWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new Line(text.getMaxLineLength(), text.getFontSize(), fontFile.getSpaceWidth());
                    currentLine.addWord(currentWord);
                }
                currentWord = new Word(text.getFontSize());
                continue;
            }
            Character character = fontFile.getCharacter(c);
            currentWord.addCharacter(character);
        }
        completeStructure(lines, currentLine, currentWord, text);
        return lines;
    }

    private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, GUIText text) {
        boolean added = currentLine.addWord(currentWord);
        if (!added) {
            lines.add(currentLine);
            currentLine = new Line(text.getMaxLineLength(), text.getFontSize(), fontFile.getSpaceWidth());
            currentLine.addWord(currentWord);
        }
        lines.add(currentLine);
    }

    private float[] listToArray(List<Float> floatList) {
        float[] array = new float[floatList.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = floatList.get(i);
        }
        return array;
    }
}
