#version 330

in vec3 position;
in vec3 colors;
//in vec3 normals;

//uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 fragColor;

void main() {

	//vec4 worldPosition = model * vec4(position, 1.0);
	vec4 worldPosition = vec4(position, 1.0);
	vec4 positionRelativeToCamera = view * worldPosition;
	gl_Position = projection * positionRelativeToCamera;
	
	fragColor = colors;

}
