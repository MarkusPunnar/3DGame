#version 440

const int maxLights = 10;

//VertexShader input attributes from model data
in vec3 aPosition;
in vec2 aTextureCoords;
in vec3 aNormalCoords;

out vec3 fragmentCoords;
out vec2 textureCoords;
out vec3 normalCoords;
out vec4 shadowCoords;
out vec3 lightCoords[maxLights];

//Uniform variables
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightSpaceMatrix;
uniform vec3 lightPosition[maxLights];

void main(void) {
    vec4 vertexWorldSpacePosition = transformationMatrix * vec4(aPosition, 1.0);

    //Pass information to fragment shader
    textureCoords = aTextureCoords;
    normalCoords = aNormalCoords;
    for(int i = 0; i < maxLights; i++) {
        lightCoords[i] = lightPosition[i];
    }
    shadowCoords = lightSpaceMatrix * vertexWorldSpacePosition;
    fragmentCoords = vertexWorldSpacePosition.xyz;
    gl_Position = projectionMatrix * viewMatrix * vertexWorldSpacePosition;
}