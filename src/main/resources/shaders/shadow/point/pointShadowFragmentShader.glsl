#version 440

in vec4 fragmentCoords;

uniform vec3 lightCoords;
uniform float farPlane;

void main() {
    float lightDistance = length(fragmentCoords.xyz - lightCoords);
    lightDistance = lightDistance / farPlane;
    gl_FragDepth = lightDistance;
}
