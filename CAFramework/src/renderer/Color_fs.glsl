/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 * 
 * 
 * Simple Fragment Shader to calculate the pixel color per vertex. The vertex is usually part 
 * of a mesh, for which a certain material or  color is defined. The current material and color 
 * is available in the mamterial parameter uniforms. 
 * 
 * All lights in the scene illuminate the object in an additive manner. For point lights the 
 * Blinn-Phong formula is used to calculate the reflected color of the light on the actual vertex. 
 * For ambient lights  the diffuse and ambient parts of the lights are multiplied with the ambient 
 * and diffuse parts of the materials and summed up.
 */

#version 150			// support for older OpenGL versions
#define MAX_LIGHTS 8	// max. 8 lights in scene supported

// Input from Vertex Shader per vertex: normal, position and texture coordinate 
smooth in vec3 varyingNormal;
smooth in vec3 varyingEcPosition;
smooth in vec2 texOut;

// Lights 
uniform int noLights ; 					// number of lights to be considered 
uniform vec4 lightPos[MAX_LIGHTS] ; 	// position of each light
uniform vec3 lightAmbient[MAX_LIGHTS]; 	// ambient part of each light
uniform vec3 lightDiffuse[MAX_LIGHTS]; 	// diffuse part of each light
uniform vec3 lightSpecular[MAX_LIGHTS]; // specular part of each light
uniform vec3 lightFactors[MAX_LIGHTS]; 	// constant, linear and quadratic factors
										// for the attenuation of each light
uniform int lightType [MAX_LIGHTS];		// ambient or point light
const int POINT_LIGHT = 2;
const int AMBIENT_LIGHT = 0;
const int DISABLED_LIGHT = 5;			// light is currently switched off and must 
										// not be considered in calculation

// Material parameters for current object
uniform vec4 materialEmissive;
uniform vec4 materialAmbient;
uniform vec4 materialDiffuse;
uniform vec4 materialSpecular;
uniform float materialShininess;

// Active color (only used, when there is no light in the scene
// otherwise only materials are considered to calculate the vertex color)
uniform vec4 colorVec;					

//Texture for current object (blends with material)
uniform sampler2D tex;
uniform int textured;	// Is the current object textured?

// Output: Calculated color 
out vec4 vFragColor;

/*
 * For each vertex:
 * Adds up the reflected ambient/diffuse/specular color part of the light and The color of the reflected object material  object surfaces. Here Uses Blinn-Phong reflection model to calculate the reflected material surface colors per vertex.
 * const in vec3 eye - Position of the camera 
 * const in vec3 ecPosition3  - position of vertex
 * const in vec3 normal - normal at vertex 
 * inout ambient  - ambient part of calculated reflection (is added)
 * inout diffuse - diffuse part of calculated reflection (is added)
 * inout specular - specular part of calculated reflection (is added)
 */
void PointLight (const in int i, 
		const in vec3 eye,
		const in vec3 ecPosition3,
		const in vec3 normal,
		inout vec4 ambient,
		inout vec4 diffuse,
		inout vec4 specular)
{
	float nDotNL;
	float nDotNH;
	float pf;
	float attenuation;
	float d;
	vec3 L;
	vec3 halfVec;
	
	// L: normierter Vektor vom Punkt zur Lichtquelle
	L = vec3(lightPos[i]) - ecPosition3;
	d = length(L);
	L = normalize(L);
	
	attenuation = 1.0 / (lightFactors[i].x + lightFactors[i].y * d + lightFactors[i].z * d * d);
	
	// halfVec: Verwende Winkelhalbierende statt Reflektionsvector
	halfVec = normalize (L + eye);
	
	nDotNL = max (0.0, dot (normal, L));
	nDotNH = max (0.0, dot (normal, halfVec));

	if (nDotNL == 0.0)
	{
		pf = 0.0;
	}
	else
	{
		if (materialShininess == 0)
			pf = 0;
		else
			pf = pow (nDotNH, materialShininess);
	}
	
	ambient += vec4(lightAmbient[i],1.0) * attenuation;
	diffuse += vec4(lightDiffuse[i],1.0) * nDotNL * attenuation;
	specular += vec4(lightSpecular[i],1.0) * pf * attenuation;
}

// Ambient light is added 
void AmbientLight (const in int i, inout vec4 gamb)
{
	gamb += materialAmbient * vec4(lightAmbient[i], 1.0); 
	vec4 diffColor = materialDiffuse;
	if (textured == 1)
		diffColor = texture(tex, texOut);
	gamb += diffColor * vec4(lightDiffuse[i], 1.0); 
}

void main (void) 
{       
    
	if (noLights == 0)
	{
		vFragColor = colorVec;
		return;
	}
	
    vec3 eye = -normalize(varyingEcPosition) ; 
    vec4 amb = vec4(0.0);
    vec4 diff = vec4(0.0);
    vec4 spec = vec4(0.0);
    vec4 globalAmb = vec4(0.0);
	
	for (int i=0; i < noLights; i++)
	{
		if (lightType[i] == POINT_LIGHT)
	        PointLight( i, eye, varyingEcPosition, varyingNormal, amb, diff, spec);
		else if (lightType[i] == AMBIENT_LIGHT )
			AmbientLight (i, globalAmb);
    }
		
	vec4 matDiffuse = materialDiffuse; 
	if (textured == 1)
	{
		matDiffuse = texture(tex, texOut);
	}
    vFragColor = materialEmissive + globalAmb + 
        		amb * materialAmbient + 
        		diff * matDiffuse +
        		spec * materialSpecular*0.3; // The effect of the specular light is 
    										 // too strong for some reason, therefore it 
    										 // is weakened by the factor 0.3
     
 
//	vFragColor = 0.5 + 0.5 * normalize( varyingNormal ).xyzz;  // to show normal map

}