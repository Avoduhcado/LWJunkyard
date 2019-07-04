#version 430

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

const int MAX_CASCADES = 3;

const float transitionDistance = 10.0;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec4 weights;
in ivec4 jointIndices;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 jointsMatrix[MAX_JOINTS];
uniform mat4 orthoProjectionMatrix[MAX_CASCADES];
uniform mat4 lightViewMatrix[MAX_CASCADES];

out vec2 fragTextureCoords;
out vec3 fragVertexNormal;
out vec3 fragVertexPosition;
out vec4 fragLightViewVertexPos[MAX_CASCADES];

void main() {

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	int count = 0;
	for(int i = 0; i < MAX_WEIGHTS; i++) {
		float weight = weights[i];
		if(weight > 0) {
			count++;
			mat4 jointTransform = jointsMatrix[jointIndices[i]];
			vec4 posePosition = jointTransform * vec4(position, 1.0);
			totalLocalPos += posePosition * weight;

			vec4 worldNormal = jointTransform * vec4(normal, 0.0);
			totalNormal += worldNormal * weight;
		}
	}
	if(count == 0) {
		totalLocalPos = vec4(position, 1.0);
		totalNormal = vec4(normal, 0.0);
	}

	vec4 worldPosition = model * totalLocalPos;
	vec4 positionRelativeToCamera = view * worldPosition;
	gl_Position = projection * positionRelativeToCamera;

	fragTextureCoords = textureCoords;
	fragVertexNormal = normalize((model * view) * totalNormal).xyz;
    fragVertexPosition = positionRelativeToCamera.xyz;
    
    for (int i = 0 ; i < MAX_CASCADES ; i++) {
        fragLightViewVertexPos[i] = orthoProjectionMatrix[i] * lightViewMatrix[i] * worldPosition;
    }

}
