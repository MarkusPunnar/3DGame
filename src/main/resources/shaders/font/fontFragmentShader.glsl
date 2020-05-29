#version 440

in vec2 textureCoords;

out vec4 out_colour;

uniform vec3 colour;
uniform sampler2D textureAtlas;

const float width = 0.5;
const float edge = 0.1;

void main(void) {
    float distance = 1.0 - texture(textureAtlas, textureCoords).a;
    float alpha = 1.0 - smoothstep(width, width + edge, distance);
    out_colour = vec4(colour, alpha);
}
