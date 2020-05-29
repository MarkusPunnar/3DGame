#version 440

in vec2 positionCoords;

out vec2 textureCoords;

uniform mat4 transformationMatrix;

void main(void) {
    gl_Position = transformationMatrix * vec4(positionCoords, 0.0, 1.0);
    textureCoords = vec2((positionCoords.x + 1.0) / 2.0, 1 - (positionCoords.y + 1.0) / 2.0);
}