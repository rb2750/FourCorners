#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

uniform mat4 pr_matrix = mat4(1.0);
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec4 clipPlane;

out DATA
{
    vec2 tc;
} vs_out;

void main() {
    vec4 worldPosition = ml_matrix * position;

    gl_ClipDistance[0] = dot(worldPosition, clipPlane);

	gl_Position = pr_matrix * vw_matrix * worldPosition;
	vs_out.tc = tc;
}
