#version 330

in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D fontTexture;
uniform vec4 color;

void main()
{
    fragColor = color * texture(fontTexture, outTexCoord);
}
