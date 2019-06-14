#version 330

in vec3 position;
in vec2 textureCoords;

uniform mat4 projModelMatrix;

out vec2 outTexCoord;

void main()
{
    gl_Position = projModelMatrix * vec4(position, 1.0);
    outTexCoord = textureCoords;
}
