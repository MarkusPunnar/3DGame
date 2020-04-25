#version 460 core

in vec2 textureCoords;
in vec3 normalCoords;
in vec3 lightCoords;
in vec3 fragmentCoords;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform vec3 lightColour;
uniform vec3 cameraCoords;

uniform float reflectivity;
uniform float shineDamper;
uniform float fakeLighting;
uniform vec3 skyColour;

const float ambientStrength = 0.2;
const float specularStrength = 0.5;

void main (void) {
    //Diffuse lighting calculations
    vec3 normalizedNormal = normalize(normalCoords);
    vec3 normalizedLight = normalize(lightCoords - fragmentCoords);
    float lightDot = dot(normalizedNormal, normalizedLight);
    vec3 diffuse = max(lightDot, 0.0) * lightColour;
    //Ambient lighting calculations
    vec3 ambient = ambientStrength * lightColour;
    //Specular lighting calculations
    vec3 normalizedView = normalize(-fragmentCoords);
    vec3 reflectedLight = reflect(-normalizedLight, normalizedNormal);
    float specularValue = pow(max(dot(normalizedView, reflectedLight), 0.0), 32);
    vec3 specular = specularStrength * specularValue * lightColour;
    //Set final colour
    vec3 lighting = ambient + diffuse + specular;
    outColour = vec4(lighting, 1.0) * texture(textureSampler, textureCoords);
}