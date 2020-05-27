package object.generation;

import engine.texture.GuiTexture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuGenerator implements Generator {

    @Override
    public List<GuiTexture> generate() throws IOException, URISyntaxException {
        List<GuiTexture> mainMenuComponents = new ArrayList<>();
        return mainMenuComponents;
    }
}
