#version 330

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform vec3 colour;
uniform sampler2D fontAtlas;

const float width = 0.51;
const float edge = 0.02;

const float borderWidth = 0.0;
const float borderEdge = 0.0;

const vec3 outlineColour = vec3(0.0, 0.0, 0.0);

void main(void) {
    float distance = 1.0 - texture(fontAtlas, fs_in.tc).a;
    float alpha = 1.0 - smoothstep(width, width + edge, distance);

    float distance2 = 1.0 - texture(fontAtlas, fs_in.tc).a;
    float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);

    float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
    vec3 overallColour = mix(outlineColour, colour, alpha / overallAlpha);

    color = vec4(overallColour, overallAlpha);
}
