#version 460 core

//VertexShader input attributes from model data
in vec3 aPosition;
in vec2 aTextureCoords;
in vec3 aNormalCoords;

out vec3 fragmentCoords;
out vec2 textureCoords;
out vec3 normalCoords;
out vec3 lightCoords[10];

//Uniform variables
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[10];

void main(void) {
    vec4 vertexWorldSpacePosition = transformationMatrix * vec4(aPosition, 1.0);

    //Pass information to fragment shader
    textureCoords = aTextureCoords;
    normalCoords = aNormalCoords;
    for(int i = 0; i < 10; i++) {
        lightCoords[i] = lightPosition[i];
    }
    fragmentCoords = vertexWorldSpacePosition.xyz;
    gl_Position = projectionMatrix * viewMatrix * vertexWorldSpacePosition;
}