#version 440

const float ambientStrength = 0.2;
const float specularStrength = 0.5;
const int maxLights = 10;

in vec2 textureCoords;
in vec3 normalCoords;
in vec3 lightCoords[maxLights];
in vec3 fragmentCoords;
in vec4 shadowCoords;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform sampler2D shadowMap;
uniform samplerCube shadowCube[maxLights - 1];

uniform vec3 lightColour[maxLights];
uniform vec3 cameraCoords;

uniform float reflectivity;
uniform float shineDamper;
uniform float fakeLighting;
uniform float farPlane;
uniform vec3 skyColour;
uniform vec3 attenuation[maxLights];

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

float directionalShadowCalculation(vec4 shadowCoords) {
    vec3 projectedCoords = shadowCoords.xyz / shadowCoords.w;
    projectedCoords = projectedCoords * 0.5 + 0.5;
    float mapSize = 8192.0;
    float texelSize = 1 / mapSize;
    float total = 0.0;
    float currentDepth = projectedCoords.z;
    float bias = 0.0015;
    for (int x = -pcfCount; x <= pcfCount; x++) {
        for (int y = -pcfCount; y <= pcfCount; y++) {
            float closestDepth = texture(shadowMap, projectedCoords.xy + vec2(x, y) * texelSize).r;
            if (closestDepth < currentDepth - bias) {
                total += 1.0;
            }
        }
    }
    return total / totalTexels;
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
    for(int i = 0; i < maxLights; i++) {
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