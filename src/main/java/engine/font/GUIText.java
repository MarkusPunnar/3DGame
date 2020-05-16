package engine.font;

import engine.font.structure.FontType;
import engine.model.Model;
import engine.model.TextModel;
import engine.render.RenderObject;
import engine.shader.Shader;
import engine.texture.ObjectType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GUIText implements RenderObject {

    private String text;
    private float fontSize;
    private TextModel model;
    private Vector2f position;
    private float maxLineLength;
    private int lineCount;
    private FontType font;
    private boolean centerText;

    public GUIText(String text, float fontSize, Vector2f position, float maxLineLength, FontType font, boolean centerText) {
        this.text = text;
        this.fontSize = fontSize;
        this.model = new TextModel();
        this.position = position;
        this.maxLineLength = maxLineLength;
        this.font = font;
        this.centerText = centerText;
    }

    public String getText() {
        return text;
    }

    public float getFontSize() {
        return fontSize;
    }

    public int getID() {
        return font.getTextureAtlas();
    }

    public void setMeshInfo(int vaoID, int vertexCount) {
        model.setModelID(vaoID);
        model.setVertexCount(vertexCount);
    }

    public void setColour(float r, float g, float b) {
        model.setColour(new Vector3f(r, g, b));
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Vector3f getPosition() {
        return null;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    @Override
    public Vector3f getRotation() {
        return new Vector3f();
    }

    @Override
    public Vector3f getScaleVector() {
        return new Vector3f(1);
    }

    @Override
    public void prepareObject(Shader shader) {
        GL30.glBindVertexArray(model.getModelID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        shader.doLoad2DVector(position, "translation");
    }

    public float getMaxLineLength() {
        return maxLineLength;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public FontType getFont() {
        return font;
    }

    public boolean isCentered() {
        return centerText;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ObjectType getType() {
        return ObjectType.TEXT;
    }

    public GUIText copyWithValueChange(String newValue) {
        GUIText copy = this;
        copy.setText(newValue);
        return copy;
    }
}
