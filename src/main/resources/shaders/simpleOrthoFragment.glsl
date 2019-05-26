#version 330

in vec2 fragTextureCoords;

uniform sampler2D colorTexture;

//uniform float nearPlane;
//uniform float farPlane;

out vec4 color;

//float LinearizeDepth(vec2 uv) {
//	float z = texture(colorTexture, uv).x;
//	return (2.0 * nearPlane) / (farPlane + nearPlane - z * (farPlane - nearPlane));
//}

void main() {

	//float depth = LinearizeDepth(fragTextureCoords);
	//color = vec4(depth, depth, depth, 1);
	color = texture(colorTexture, fragTextureCoords);

}
