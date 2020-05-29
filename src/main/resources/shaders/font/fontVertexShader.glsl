#version 440

in vec2 aPosition;
in vec2 aTextureCoords;

out vec2 textureCoords;

uniform vec2 translation;

void main(void) {
    gl_Position = vec4(aPosition + translation * vec2(2.0, -2.0), 0.0, 1.0);
    textureCoords = aTextureCoords;
}