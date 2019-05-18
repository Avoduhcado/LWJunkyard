#version 400

in vec3 position;
out vec3 textureCoords;

uniform mat4 projection;
uniform mat4 view;

void main(void) {
	
	gl_Position = projection * view * vec4(position, 1.0);
	textureCoords = position;
	
}