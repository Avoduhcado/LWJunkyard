#version 330

in vec2 fragTextureCoords;
in vec3 fragVertexNormal;
in vec3 fragVertexPosition;
in vec4 fragLightViewVertexPos;

out vec4 outColor;

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

struct Attenuation {
	float constant;
	float linear;
	float exponent;
};

struct PointLight {
	vec3 color;
	// Light position is assumed to be in view coordinates
	vec3 position;
	float intensity;
	Attenuation att;
};

struct SpotLight {
	PointLight pointLight;
	vec3 coneDirection;
	float cutOff;
};

struct DirectionalLight {
	vec3 color;
	vec3 direction;
	float intensity;
};

struct Material {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	float hasTexture;
	float reflectance;
};

uniform sampler2D colorTexture;
uniform sampler2D shadowMap;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoord) {
	if (material.hasTexture == 1) {
		ambientC = texture(colorTexture, textureCoord);
		diffuseC = ambientC;
		specularC = ambientC;
	} else {
		ambientC = material.ambient;
		diffuseC = material.diffuse;
		specularC = material.specular;
	}
}

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal) {
	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);

	// Diffuse Light
	float diffuseFactor = max(dot(normal, toLightDir), 0.0);
	diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

	// Specular Light
	vec3 cameraDirection = normalize(-position);
	vec3 fromLightDir = -toLightDir;
	vec3 reflectedLight = normalize(reflect(fromLightDir , normal));
	float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
	specularFactor = pow(specularFactor, specularPower);
	specularColor = specularC * lightIntensity  * specularFactor * material.reflectance * vec4(lightColor, 1.0);

	return (diffuseColor + specularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
	vec3 lightDirection = light.position - position;
	vec3 toLightDir = normalize(lightDirection);
	vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDir, normal);

	// Apply Attenuation
	float distance = length(lightDirection);
	float attenuationInv = 1;//light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
	return lightColor / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
	vec3 lightDirection = light.pointLight.position - position;
	vec3 toLightDir = normalize(lightDirection);
	vec3 fromLightDir = -toLightDir;
	float spotAlfa = dot(fromLightDir, normalize(light.coneDirection));

	vec4 color = vec4(0, 0, 0, 0);

	if(spotAlfa > light.cutOff) {
		color = calcPointLight(light.pointLight, position, normal);
		color *= (1.0 - (1.0 - spotAlfa) / (1.0 - light.cutOff));
	}
	return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
	return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

float calcShadow(vec4 position) {
    float shadowFactor = 1.0;
    vec3 projCoords = position.xyz;
    // Transform from screen coordinates to texture coordinates
    projCoords = projCoords * 0.5 + 0.5;
    if (projCoords.z < texture(shadowMap, projCoords.xy).r) 
    {
        // Current fragment is not in shade
        shadowFactor = 0;
    }

    return 1 - shadowFactor;
}

void main() {
	setupColors(material, fragTextureCoords);

	vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragVertexPosition, fragVertexNormal);
	for(int i = 0; i < MAX_POINT_LIGHTS; i++) {
		if(pointLights[i].intensity > 0) {
			diffuseSpecularComp += calcPointLight(pointLights[i], fragVertexPosition, fragVertexNormal);
		}
	}
	for(int i = 0; i < MAX_SPOT_LIGHTS; i++) {
		if(spotLights[i].pointLight.intensity > 0) {
			diffuseSpecularComp += calcSpotLight(spotLights[i], fragVertexPosition, fragVertexNormal);
		}
	}

	float shadow = calcShadow(fragLightViewVertexPos);
	outColor = clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecularComp * shadow, 0, 1);
	//outColor = vec4(shadow);
}
