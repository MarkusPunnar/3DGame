package engine.font.structure;

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;
import engine.font.TextMeshCreator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FontFile {

    private static final int PADDING = 8;
    private static final String SPLITTER = " ";
    private static final String DELIMITER = ",";

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();


    private Map<Integer, Character> characterInfo = new HashMap<>();
    private Map<String, String> lineValues = new HashMap<>();

    private int paddingWidth;
    private int paddingHeight;
    private int[] padding;
    private float spaceWidth;
    private float verticalPerPixelSize;
    private float horizontalPerPixelSize;
    private Scanner sc;

    public FontFile(String fontFileName) throws URISyntaxException, IOException {
        initFontFile(fontFileName);
        processPaddingData();
        loadLineSizes();
        int imageWidth = getIntValueFromString(lineValues.get("scaleW"));
        loadCharacterData(imageWidth);
        sc.close();
    }

    private void initFontFile(String fontFileName) throws IOException, URISyntaxException {
        URL location = FontFile.class.getClassLoader().getResource("textures/fonts/" + fontFileName + ".fnt");
        if (location == null) {
            logger.atSevere().withStackTrace(StackSize.LARGE).log("Font file %s was not found", fontFileName);
            throw new IllegalArgumentException("Font file not found");
        }
        logger.atInfo().log("Successfully read font file %s", fontFileName);
        this.sc = new Scanner(Paths.get(location.toURI()));
    }


    private void loadCharacterData(int imageWidth) {
        processLine();
        processLine();
        while (processLine()) {
            Character character = loadCharacter(imageWidth);
            if (character != null) {
                characterInfo.put(character.getCharacterID(), character);
            }
        }
    }

    private Character loadCharacter(int imageWidth) {
        int id = getIntValueFromString(lineValues.get("id"));
        if (id == TextMeshCreator.SPACE_ASCII) {
            this.spaceWidth = (getIntValueFromString(lineValues.get("xadvance")) - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        float xTextureCoord = ((float) getIntValueFromString(lineValues.get("x")) + (padding[0] - PADDING)) / imageWidth;
        float yTextureCoord = ((float) getIntValueFromString(lineValues.get("y")) + (padding[1] - PADDING)) / imageWidth;
        int widthPixels = getIntValueFromString(lineValues.get("width")) - (paddingWidth - (2 * PADDING));
        int heightPixels = getIntValueFromString(lineValues.get("height")) - ((paddingHeight) - (2 * PADDING));
        float quadWidth = widthPixels * horizontalPerPixelSize;
        float quadHeight = heightPixels * verticalPerPixelSize;
        float xTexSize = widthPixels / (float) imageWidth;
        float yTexSize = heightPixels / (float) imageWidth;
        float xOffset = (getIntValueFromString(lineValues.get("xoffset")) + padding[0] - PADDING) * horizontalPerPixelSize;
        float yOffset = (getIntValueFromString(lineValues.get("yoffset")) + (padding[1] - PADDING)) * verticalPerPixelSize;
        float xAdvance = (getIntValueFromString(lineValues.get("xadvance")) - paddingWidth) * horizontalPerPixelSize;
        return new Character(id, xTextureCoord, yTextureCoord, xTexSize, yTexSize, xOffset, yOffset, quadWidth, quadHeight, xAdvance);
    }

    private void loadLineSizes() {
        processLine();
        int lineHeightPixels = getIntValueFromString(lineValues.get("lineHeight"));
        this.verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / lineHeightPixels;
        this.horizontalPerPixelSize = verticalPerPixelSize;
    }

    private void processPaddingData() {
        processLine();
        int[] paddingData = getIntValuesFromString(lineValues.get("padding"));
        this.padding = paddingData;
        this.paddingWidth = paddingData[0] + paddingData[2];
        this.paddingHeight = paddingData[1] + paddingData[3];
    }

    private int[] getIntValuesFromString(String padding) {
        String[] paddingValues = padding.split(DELIMITER);
        int[] paddingInts = new int[paddingValues.length];
        for (int i = 0; i < paddingValues.length; i++) {
            paddingInts[i] = Integer.parseInt(paddingValues[i]);
        }
        return paddingInts;
    }

    private int getIntValueFromString(String variable) {
        return Integer.parseInt(variable);
    }

    private boolean processLine() {
        lineValues.clear();
        if (sc.hasNext()) {
            String line = sc.nextLine();
            String[] lineParts = line.split(SPLITTER);
            for (String part : lineParts) {
                String[] partValues = part.split("=");
                if (partValues.length == 2) {
                    lineValues.put(partValues[0], partValues[1]);
                }
            }
            return true;
        }
        return false;
    }

    public float getSpaceWidth() {
        return spaceWidth;
    }

    public Character getCharacter(int ascii) {
        return characterInfo.get(ascii);
    }
}
