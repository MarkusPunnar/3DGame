#version 460 core

in vec3 aPosition;

uniform mat4 lightSpaceMatrix;
uniform mat4 transformationMatrix;

void main(void) {
    gl_Position = lightSpaceMatrix * transformationMatrix * vec4(aPosition, 1.0);
}