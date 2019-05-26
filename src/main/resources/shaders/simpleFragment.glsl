#version 330

in vec2 fragTextureCoords;

uniform sampler2D colorTexture;

out vec4 color;

void main() {

	color = texture(colorTexture, fragTextureCoords);

}
