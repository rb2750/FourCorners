#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;
layout (location = 2) in vec3 normal;

uniform mat4 pr_matrix = mat4(1.0);
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec3 lightPosition;
uniform vec4 clipPlane;

out DATA
{
    vec2 tc;
    vec3 normal;
    vec3 toLightVector;
    vec3 toCameraVector;
} vs_out;

void main() {
    vec4 worldPosition = ml_matrix * position;

    gl_ClipDistance[0] = dot(worldPosition, clipPlane);

	gl_Position = pr_matrix * vw_matrix * worldPosition;
	vs_out.tc = tc;

	vs_out.normal = (ml_matrix * vec4(normal, 0.0)).xyz;
	vs_out.toLightVector = lightPosition - worldPosition.xyz;
	vs_out.toCameraVector = (inverse(vw_matrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}
