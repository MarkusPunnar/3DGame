#version 460 core

in vec2 textureCoords;
in vec3 normalCoords;
in vec3 lightCoords[10];
in vec3 fragmentCoords;
in vec4 shadowCoords;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform sampler2D shadowMap;
uniform samplerCube shadowCube[9];

uniform vec3 lightColour[10];
uniform vec3 cameraCoords;

uniform float reflectivity;
uniform float shineDamper;
uniform float fakeLighting;
uniform float farPlane;
uniform vec3 skyColour;
uniform vec3 attenuation[10];

const float ambientStrength = 0.2;
const float specularStrength = 0.5;

float directionalShadowCalculation(vec4 shadowCoords) {
    vec3 projectedCoords = shadowCoords.xyz / shadowCoords.w;
    projectedCoords = projectedCoords * 0.5 + 0.5;
    float closestDepth = texture(shadowMap, projectedCoords.xy).r;
    float currentDepth = projectedCoords.z;
    float bias = 0.00005;
    return closestDepth < currentDepth - bias ? 1.0 : 0.0;
}

float pointShadowCalculation(vec3 fragmentCoords, vec3 lightCoords, int cubeIndex) {
    vec3 fragToLight = fragmentCoords - lightCoords;
    float closestDepth = texture(shadowCube[cubeIndex], fragToLight).r;
    closestDepth *= farPlane;
    float currentDepth = length(fragToLight);
    float bias = 0.1;
    return closestDepth < currentDepth - bias ? 1.0 : 0.0;
}


void main (void) {
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for(int i = 0; i < 10; i++) {
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
        float shadowConstant = i == 0 ? (1 - directionalShadowCalculation(shadowCoords)) : (1 - pointShadowCalculation(fragmentCoords, lightCoords[i], i-1));
        totalDiffuse = max(totalDiffuse, shadowConstant * diffuse);
        totalSpecular = max(totalSpecular, shadowConstant * specular);
    }
    vec3 lighting =  totalDiffuse + totalSpecular;
    lighting = max(lighting, ambientStrength);
    outColour = vec4(lighting, 1.0) * texture(textureSampler, textureCoords);
}