#version 330 core

layout (location = 0) in vec4 position;

out DATA
{
    vec3 position;
} vs_out;

uniform mat4 pr_matrix = mat4(1.0);
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec4 clipPlane;

void main() {
    vec4 worldPosition = ml_matrix * position;

    gl_ClipDistance[0] = dot(worldPosition, clipPlane);

	gl_Position = pr_matrix * vw_matrix * worldPosition;
	vs_out.position = position.xyz;
}
