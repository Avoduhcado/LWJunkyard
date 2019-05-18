#version 330

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 outTexCoord;

uniform mat4 projModelMatrix;

void main()
{
    gl_Position = projModelMatrix * vec4(position, 1.0);
    outTexCoord = textureCoords;
}
