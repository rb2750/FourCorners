#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D guiTex;

void main() {
	gl_FragColor = texture(guiTex, fs_in.tc);
}
