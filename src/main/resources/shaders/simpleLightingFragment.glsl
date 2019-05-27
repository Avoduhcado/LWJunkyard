#version 330

const int MAX_CASCADES = 4;

in vec2 fragTextureCoords;
in vec3 fragVertexNormal;
in vec3 fragVertexPosition;
//in vec4 shadowCoords;
in vec4 fragLightViewVertexPos[MAX_CASCADES];

out vec4 outColor;

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

// Percentage closer filtering for shadow smoothing
const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0); 

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
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;
//uniform sampler2D shadowMap;
uniform sampler2D shadowMaps[MAX_CASCADES];
uniform float cascadeFarPlanes[MAX_CASCADES];
uniform int shadowMapSize;

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

//float calcLightFactor() {
//	float texelSize = 1.0 / float(shadowMapSize);
//	float total = 0.0;
	
//	for(int x = -pcfCount; x < pcfCount; x++) {
//		for(int y = -pcfCount; y < pcfCount; y++) {
//			float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
//			if(shadowCoords.z > objectNearestLight + 0.0002) {
//				total += 1.0;
//			}
//		}
//	}
	
//	total /= totalTexels;
	
//	return 1.0 - (total * shadowCoords.w);
//}

float calcShadow(vec4 position, int idx) {
    vec3 projCoords = position.xyz;
    // Transform from screen coordinates to texture coordinates
    projCoords = projCoords * 0.5 + 0.5;
    float bias = 0.005;

    float shadowFactor = 0.0;
    vec2 inc = 1.0 / textureSize(shadowMaps[idx], 0);

    for(int row = -1; row <= 1; ++row) {
        for(int col = -1; col <= 1; ++col) {
            float textDepth = texture(shadowMaps[idx], projCoords.xy + vec2(row, col) * inc).r;
            shadowFactor += projCoords.z - bias > textDepth ? 1.0 : 0.0;        
        }    
    }
    shadowFactor /= 9.0;

    if(projCoords.z > 1.0) {
        shadowFactor = 1.0;
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

	int idx;
	for(int i = 0; i < MAX_CASCADES; i++) {
	    if(abs(fragVertexPosition.z) < cascadeFarPlanes[i]) {
	        idx = i;
	        break;
	    }
	}
	float shadow = calcShadow(fragLightViewVertexPos[idx], idx);
	//float lightFactor = calcLightFactor();

	//outColor = (ambientC * vec4(ambientLight, 1) + diffuseSpecularComp) * lightFactor;
	//outColor = ambientC * vec4(ambientLight, 1) + (diffuseSpecularComp * lightFactor);
	outColor = ambientC * vec4(ambientLight, 1) + (diffuseSpecularComp * shadow);
	
}
