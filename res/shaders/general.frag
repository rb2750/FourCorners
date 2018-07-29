#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
    vec3 normal;
    vec3 toLightVector;
    vec3 toCameraVector;
} fs_in;

uniform sampler2D tex;
uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;

void main() {
    vec3 unitNormal = normalize(fs_in.normal);
    vec3 unitLightVector = normalize(fs_in.toLightVector);

    float nDot1 = dot(unitNormal, unitLightVector);
    float brightness = max(nDot1, 0.2);
    vec3 diffuse = brightness * lightColour;

    vec3 unitVectorToCamera = normalize(fs_in.toCameraVector);
    vec3 lightDirection = -unitLightVector;
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    specularFactor = max(specularFactor, 0.0);
    float dampedFactor = pow(specularFactor, shineDamper);
    vec3 finalSpecular = dampedFactor * reflectivity * lightColour;

	color = vec4(diffuse, 1.0) * texture(tex, fs_in.tc) + vec4(finalSpecular, 1.0);
}
