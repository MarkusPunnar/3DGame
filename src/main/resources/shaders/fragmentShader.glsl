#version 460 core

in vec2 textureCoords;
in vec3 normalCoords;
in vec3 lightCoords[4];
in vec3 fragmentCoords;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform vec3 lightColour[4];
uniform vec3 cameraCoords;

uniform float reflectivity;
uniform float shineDamper;
uniform float fakeLighting;
uniform vec3 skyColour;
uniform vec3 attenuation[4];

const float ambientStrength = 0.2;
const float specularStrength = 0.5;

void main (void) {
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for(int i = 0; i < 4; i++) {
        if (lightColour[i].x == 0 && lightColour[i].y == 0 && lightColour[i].z == 0) {
            continue;
        }
        //Diffuse lighting calculations
        vec3 toLightVector = lightCoords[i] - fragmentCoords;
        float distance = length(toLightVector);
        float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 normalizedNormal = normalize(normalCoords);
        vec3 normalizedLight = normalize(toLightVector);
        float lightDot = dot(normalizedNormal, normalizedLight);
        vec3 diffuse = max(lightDot, 0.0) * lightColour[i] / attFactor;
        //Specular lighting calculations
        vec3 normalizedView = normalize(-fragmentCoords);
        vec3 reflectedLight = reflect(-normalizedLight, normalizedNormal);
        float specularValue = pow(max(dot(normalizedView, reflectedLight), 0.0), 32) * reflectivity;
        vec3 specular = specularStrength * specularValue * lightColour[i] / attFactor;
        //Set final colour
        totalDiffuse = totalDiffuse + diffuse;
        totalSpecular = totalSpecular + specular;
    }
    totalDiffuse = max(totalDiffuse, ambientStrength);
    vec3 lighting = totalDiffuse + totalSpecular;
    outColour = vec4(lighting, 1.0) * texture(textureSampler, textureCoords);
}