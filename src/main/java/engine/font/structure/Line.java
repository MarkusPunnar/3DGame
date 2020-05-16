package engine.font.structure;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private float maxLength;
    private float currentLength;
    private float spaceSize;

    private List<Word> lineWords;

    public Line(float maxLength, float fontSize, float spaceSize) {
        this.maxLength = maxLength;
        this.currentLength = 0;
        this.spaceSize = spaceSize * fontSize;
        lineWords = new ArrayList<>();
    }

    public boolean addWord(Word word) {
        float wordLength = word.getWidth();
        wordLength += !lineWords.isEmpty() ? spaceSize : 0;
        float newLength = currentLength + wordLength;
        if (newLength <= maxLength) {
            lineWords.add(word);
            currentLength = newLength;
            return true;
        }
        return false;
    }

    public float getMaxLength() {
        return maxLength;
    }

    public float getCurrentLength() {
        return currentLength;
    }

    public List<Word> getLineWords() {
        return lineWords;
    }
}
