#version 330 core

const int MAX_POINT_LIGHTS = 4;
const int MAX_SPOT_LIGHTS = 4;

layout (location = 0) out vec4 color;

in DATA
{
    vec4 clipSpace;
    vec2 tc;
    vec3 toCameraVector;
    vec3 worldPos;
} fs_in;

struct BaseLight
{
    vec3 colour;
    float intensity;
};

struct DirectionalLight
{
    BaseLight base;
    vec3 direction;
};

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    BaseLight base;
    Attenuation atten;
    vec3 position;
    float range;
};

struct SpotLight
{
    PointLight pointLight;
    vec3 direction;
    float cutoff;
};

uniform sampler2D reflectionTex;
uniform sampler2D refractionTex;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;

uniform float moveFactor;

uniform float nearPlane;
uniform float farPlane;

uniform vec3 ambientLight;
uniform vec3 eyePos;

uniform DirectionalLight dirLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

const float waveStrength = 0.04;
const float fresnelStrength = 1;
const float normalSoftener = 3.0;
const float shineDamper = 20.0;
const float reflectivity = 0.5;
const float distanceSoftener = 5.0;
const float distantDistortionSoftener = 20.0;
const float distantSpecularSoftener = 5.0;

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal, float waterDepth)
{
    float diffuseFactor = dot(normal, -direction);

    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specularColour = vec4(0, 0, 0, 0);

    if (diffuseFactor > 0)
    {
        diffuseColour = vec4(base.colour, 1.0) * base.intensity * diffuseFactor;

        vec3 directionToEye = normalize(eyePos - fs_in.worldPos);
        vec3 reflectDirection = normalize(reflect(direction, normal));

        float specularFactor = dot(directionToEye, reflectDirection);
        specularFactor = pow(specularFactor, shineDamper);

        if (specularFactor > 0)
        {
            specularColour = vec4(base.colour, 1.0) * reflectivity * specularFactor * clamp(waterDepth / distantSpecularSoftener, 0.0, 1.0);
        }
    }

    return diffuseColour + specularColour;
}

vec4 calcDirectionalLight(DirectionalLight dirLight, vec3 normal, float waterDepth)
{
    return calcLight(dirLight.base, -dirLight.direction, normal, waterDepth);
}

vec4 calcPointLight(PointLight pointLight, vec3 normal, float waterDepth)
{
    vec3 lightDirection = fs_in.worldPos - pointLight.position;
    float distanceToPoint = length(lightDirection);

    if (distanceToPoint > pointLight.range)
        return vec4(0, 0, 0, 0);

    lightDirection = normalize(lightDirection);

    vec4 colour = calcLight(pointLight.base, lightDirection, normal, waterDepth);
    float attenuation = pointLight.atten.constant +
                         pointLight.atten.linear * distanceToPoint +
                         pointLight.atten.exponent * distanceToPoint * distanceToPoint +
                         0.0001;

    return colour / attenuation;
}

vec4 calcSpotLight(SpotLight spotLight, vec3 normal, float waterDepth)
{
    vec3 lightDirection = normalize(fs_in.worldPos - spotLight.pointLight.position);
    float spotFactor = dot(lightDirection, spotLight.direction);

    vec4 colour = vec4(0, 0, 0, 0);

    if (spotFactor > spotLight.cutoff)
    {
        colour = calcPointLight(spotLight.pointLight, normal, waterDepth) *
                 (1.0 - (1.0 - spotFactor) / (1.0 - spotLight.cutoff));
    }

    return colour;
}

void main() {
    vec2 ndc = (fs_in.clipSpace.xy / fs_in.clipSpace.w) / 2.0 + 0.5;
    vec2 refractTexCoords = vec2(ndc.x, ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);

    float depth = texture(depthMap, refractTexCoords).r;
    float floorDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));
    float waterDepth = floorDistance - waterDistance;

    vec2 distortedTexCoords = texture(dudvMap, vec2(fs_in.tc.x + moveFactor, fs_in.tc.y)).rg * 0.1;
    distortedTexCoords = fs_in.tc + vec2(distortedTexCoords.x, distortedTexCoords.y + moveFactor);
    vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / distantDistortionSoftener, 0.0, 1.0);

    refractTexCoords += totalDistortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    reflectTexCoords += totalDistortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

    vec4 reflectColour = texture(reflectionTex, reflectTexCoords);
    vec4 refractColour = texture(refractionTex, refractTexCoords);

    vec4 normalMapColour = texture(normalMap, distortedTexCoords);
    vec3 normal = vec3(normalMapColour.r * 2.0 - 1.0, normalMapColour.b * normalSoftener, normalMapColour.g * 2.0 - 1.0);
    normal = normalize(normal);

    vec3 viewVector = normalize(fs_in.toCameraVector);
    float refractiveFactor = dot(viewVector, normal);
    refractiveFactor = pow(refractiveFactor, fresnelStrength);
    refractiveFactor = clamp(refractiveFactor, 0.0, 1.0);

    vec4 totalLight = vec4(ambientLight, 1);

    totalLight += calcDirectionalLight(dirLight, normal, waterDepth);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++)
        if (pointLights[i].base.intensity > 0)
            totalLight += calcPointLight(pointLights[i], normal, waterDepth);

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++)
        if (spotLights[i].pointLight.base.intensity > 0)
            totalLight += calcSpotLight(spotLights[i], normal, waterDepth);

	color = mix(reflectColour, refractColour, refractiveFactor);
	color = mix(color, vec4(0.0, 0.3, 0.5, 1.0), 0.2) * totalLight;
	color.a = clamp(waterDepth / distanceSoftener, 0.0, 1.0);
}
