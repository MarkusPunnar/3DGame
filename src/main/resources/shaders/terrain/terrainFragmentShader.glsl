#version 460 core

in vec2 textureCoords;
in vec3 normalCoords;
in vec3 lightCoords[1];
in vec3 fragmentCoords;
in vec4 shadowCoords;

out vec4 outColour;

uniform sampler2D backgroundSampler;
uniform sampler2D rSampler;
uniform sampler2D gSampler;
uniform sampler2D bSampler;
uniform sampler2D blendMapSampler;
uniform sampler2D shadowMap;

uniform vec3 lightColour[1];
uniform vec3 cameraCoords;
uniform vec3 attenuation[1];

uniform float reflectivity;
uniform float shineDamper;
uniform float fakeLighting;

const float ambientStrength = 0.2;
const float specularStrength = 0.5;

float shadowCalculation(vec4 shadowCoords) {
    vec3 projectedCoords = shadowCoords.xyz / shadowCoords.w;
    projectedCoords = projectedCoords * 0.5 + 0.5;
    float closestDepth = texture(shadowMap, projectedCoords.xy).r;
    float currentDepth = projectedCoords.z;
    return closestDepth < currentDepth ? 1.0 : 0.0;
}


void main (void) {

    //BlendMap calculations
    vec4 blendColour = texture(blendMapSampler, textureCoords);
    float backgroundTextureAmount = 1 - (blendColour.r + blendColour.g + blendColour.b);

    vec2 tiledTextureCoords = textureCoords * 20.0;
    vec4 backgroundColour = texture(backgroundSampler, tiledTextureCoords) * backgroundTextureAmount;
    vec4 rColour = texture(rSampler, tiledTextureCoords) * blendColour.r;
    vec4 gColour = texture(gSampler, tiledTextureCoords) * blendColour.g;
    vec4 bColour = texture(bSampler, tiledTextureCoords) * blendColour.b;

    vec4 totalColour = backgroundColour + rColour + gColour + bColour;

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    vec3 totalAmbient = vec3(0.0);
    for(int i = 0; i < 1; i++) {
        if (lightColour[i].x == 0 && lightColour[i].y == 0 && lightColour[i].z == 0) {
            continue;
        }
        vec3 toLightVector = lightCoords[i] - fragmentCoords;
        float distance = length(toLightVector);
        float attFactor = attenuation[i].x + attenuation[i].y * distance + attenuation[i].z * distance * distance;
        //Diffuse lighting calculations
        vec3 normalizedNormal = normalize(normalCoords);
        vec3 normalizedLight = normalize(toLightVector);
        float lightDot = dot(normalizedNormal, normalizedLight);
        vec3 diffuse = max(lightDot, 0.0) * lightColour[i];
        //Specular lighting calculations
        vec3 normalizedView = normalize(-fragmentCoords);
        vec3 reflectedLight = reflect(-normalizedLight, normalizedNormal);
        float specularValue = pow(max(dot(normalizedView, reflectedLight), 0.0), 32) * reflectivity;
        vec3 specular = specularStrength * specularValue * lightColour[i];
        //Set final colour
        totalDiffuse = totalDiffuse + diffuse / attFactor;
        totalSpecular = totalSpecular + specular / attFactor;
    }
    vec3 lighting = (1 - shadowCalculation(shadowCoords)) * (totalDiffuse + totalSpecular);
    lighting = max(lighting, ambientStrength);
    outColour = vec4(lighting, 1.0) * totalColour;
}