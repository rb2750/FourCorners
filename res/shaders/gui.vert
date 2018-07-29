#version 330 core

layout (location = 0) in vec2 position;

out DATA
{
    vec2 tc;
} vs_out;

uniform mat4 ml_matrix;

void main() {
	gl_Position = ml_matrix * vec4(position, 0.0, 1.0);
	vs_out.tc = vec2((position.x + 1.0) / 2.0, 1 - (position.y + 1.0) / 2.0);
}
