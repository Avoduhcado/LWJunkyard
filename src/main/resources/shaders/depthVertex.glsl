#version 330

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec4 weights;
in ivec4 jointIndices;

uniform mat4 modelViewProjection;
uniform mat4 jointsMatrix[MAX_JOINTS];

out vec2 fragTextureCoords;

void main() {

	vec4 totalLocalPos = vec4(0.0);
	int count = 0;
	for(int i = 0; i < MAX_WEIGHTS; i++) {
		float weight = weights[i];
		if(weight > 0) {
			count++;
			mat4 jointTransform = jointsMatrix[jointIndices[i]];
			vec4 posePosition = jointTransform * vec4(position, 1.0);
			totalLocalPos += posePosition * weight;
		}
	}
	if(count == 0) {
		totalLocalPos = vec4(position, 1.0);
	}

    gl_Position = modelViewProjection * totalLocalPos;
    
    fragTextureCoords = textureCoords;
    
}