#version 460 core

//VertexShader input attributes from model data
in vec3 aPosition;
in vec2 aTextureCoords;
in vec3 aNormalCoords;

out vec3 fragmentCoords;
out vec2 textureCoords;
out vec3 normalCoords;
out vec3 lightCoords;

//Uniform variables
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;

void main(void) {
    //Calculate vertex and light positions in world space
    vec4 vertexViewSpacePosition = viewMatrix * transformationMatrix * vec4(aPosition, 1.0);
    vec3 lightViewSpacePosition = (viewMatrix * transformationMatrix * vec4(lightPosition, 1.0)).xyz;

    //Pass information to fragment shader
    textureCoords = aTextureCoords;
    normalCoords = aNormalCoords;
    lightCoords = lightViewSpacePosition;
    fragmentCoords = vertexViewSpacePosition.xyz;
    gl_Position = projectionMatrix * vertexViewSpacePosition;
}