#version 440

in vec3 aPosition;

uniform mat4 transformationMatrix;

void main() {
    gl_Position = transformationMatrix * vec4(aPosition, 1.0);
}
