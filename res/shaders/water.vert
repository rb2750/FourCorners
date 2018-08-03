#version 330 core

layout (location = 0) in vec2 position;

out DATA
{
    vec4 clipSpace;
    vec2 tc;
    vec3 toCameraVector;
    vec3 worldPos;
} vs_out;

uniform mat4 pr_matrix = mat4(1.0);
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec3 cameraPosition;

const float tiling = 4.0;

void main() {
    vec4 worldPosition = ml_matrix * vec4(position.x, 0.0, position.y, 1.0);
    vs_out.clipSpace = pr_matrix * vw_matrix * worldPosition;
	gl_Position = vs_out.clipSpace;
	vs_out.tc = vec2(position.x / 2.0 + 0.5, position.y / 2.0 + 0.5) * tiling;
	vs_out.toCameraVector = cameraPosition - worldPosition.xyz;
	vs_out.worldPos = worldPosition.xyz;
}
