#version 440

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;
uniform float transparent;

void main(void){
    out_Color = texture(guiTexture, textureCoords);
    if (transparent < 0.5) {
        out_Color.a = 0.5;
    }
}