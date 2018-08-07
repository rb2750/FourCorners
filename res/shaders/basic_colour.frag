#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec3 position;
} fs_in;

uniform vec4 colour;
uniform float borderSize;
uniform vec4 borderColour;

void main() {
    if(borderSize == 0)
    {
        color = colour;
        return;
    }

    if(fs_in.position.x > /*-1 + */borderSize && fs_in.position.x < 1-borderSize && fs_in.position.y > /*-1+*/borderSize && fs_in.position.y < 1-borderSize)
    {
        color= colour;
    }
    else
    {
        color=borderColour;
    }
//	color = colour;
}
