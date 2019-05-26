#version 330

in vec2 fragTextureCoords;

uniform sampler2D modelTexture;

void main() {

	float alpha = texture(modelTexture, fragTextureCoords).a;
	if(alpha < 0.5) {
		discard;
	}

	gl_FragDepth = gl_FragCoord.z;
	
}