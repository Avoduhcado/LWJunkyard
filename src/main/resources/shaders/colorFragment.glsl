#version 330

in vec3 fragColor;

out vec4 outColor;

void main() {

	// TODO Lighting from normals
	outColor = vec4(fragColor, 1.0);

}
