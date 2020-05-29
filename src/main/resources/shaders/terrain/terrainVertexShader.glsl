#version 440

in vec3 aPosition;
in vec2 aTextureCoords;
in vec3 aNormalCoords;

out vec3 fragmentCoords;
out vec2 textureCoords;
out vec3 normalCoords;
out vec3 lightCoords[1];
out vec4 shadowCoords;

//Uniform variables
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightSpaceMatrix;
uniform vec3 lightPosition[1];

void main(void) {
    //Calculate vertex and light positions in world space
    vec4 vertexWorldSpacePosition = transformationMatrix * vec4(aPosition, 1.0);

    //Pass information to fragment shader
    textureCoords = aTextureCoords;
    normalCoords = aNormalCoords;
    for(int i = 0; i < 1; i++) {
        lightCoords[i] = lightPosition[i];
    }
    fragmentCoords = vertexWorldSpacePosition.xyz;
    shadowCoords = lightSpaceMatrix * vertexWorldSpacePosition;
    gl_Position = projectionMatrix * viewMatrix * vertexWorldSpacePosition;
}