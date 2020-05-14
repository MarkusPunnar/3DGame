#version 460 core

in vec2 textureCoords;
in vec3 normalCoords;
in vec3 lightCoords;
in vec3 fragmentCoords;

out vec4 outColour;

uniform sampler2D backgroundSampler;
uniform sampler2D rSampler;
uniform sampler2D gSampler;
uniform sampler2D bSampler;
uniform sampler2D blendMapSampler;

uniform vec3 lightColour;
uniform vec3 cameraCoords;

uniform float reflectivity;
uniform float shineDamper;
uniform float fakeLighting;

const float ambientStrength = 0.2;
const float specularStrength = 0.5;

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
    outColour = vec4(lighting, 1.0) * totalColour;
}