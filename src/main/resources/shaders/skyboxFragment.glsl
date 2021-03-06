#version 400

in vec3 textureCoords;
out vec4 outColor;

uniform samplerCube cubeMap;

void main(void) {
	outColor = texture(cubeMap, textureCoords);
}