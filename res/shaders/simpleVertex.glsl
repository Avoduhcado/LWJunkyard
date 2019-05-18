#version 330

in vec3 position;
in vec2 textureCoords;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 fragTextureCoords;

void main() {

	vec4 worldPosition = model * vec4(position, 1.0);
	vec4 positionRelativeToCamera = view * worldPosition;
	gl_Position = projection * positionRelativeToCamera;

	fragTextureCoords = textureCoords;
	
}
