#version 330

in vec2 position;
in vec2 textureCoords;

uniform mat4 projModelMatrix;

out vec2 fragTextureCoords;

void main() {

    gl_Position = projModelMatrix * vec4(position, 0.0, 1.0);

	fragTextureCoords = textureCoords;
	
}