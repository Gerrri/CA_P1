/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 *
 * Vertex Shader to calculate projected vertex position, adapt normal. If the vertex is part
 * of a skinned model 
 *
 */


#version 150
#define MAX_LIGHTS 8
#define MAX_JOINTS 40
#define MAX_WEIGHTS 4

in vec3 vPosition;
in vec3 vNormal;
in vec3 texIn;
in vec4 weights;
in vec4 weightIndices;

// Transformation matrices

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat3 normalMatrix;
uniform mat4 jointMatrices [MAX_JOINTS];
uniform int numWeights;
uniform int textured;


smooth out vec3 varyingNormal;
smooth out vec3 varyingEcPosition;
smooth out vec2 texOut;

void main(void) 
{  
    vec4 weight = weights;
    vec4 index = weightIndices;
    
	vec4 tempPosition =  vec4( 0.0, 0.0, 0.0, 0.0 );
    vec4 tempNormal  = vec4( 0.0, 0.0, 0.0, 0.0 );
    vec4 position = vec4(vPosition, 1.0);
    if (textured == 1)
    {
    	texOut = vec2(texIn);
    }

    vec4 normal = vec4 (vNormal.xyz, 0.0);

    
    if (numWeights > 0)
    {       
    	for( int i = 0; i < numWeights; i++ )
    	{

	        // Apply influence of bone i
            tempPosition = tempPosition + weight.x * (jointMatrices[int(index.x)] * position);
	
	        // Transform normal by bone i
	        tempNormal = tempNormal + weight.x * (jointMatrices[int(index.x)] * normal);


            // shift over the index/weight variables, this moves the index and 
            // weight for the current bone into the .x component of the index 
            // and weight variables
            index  = index.yzwx;
            weight = weight.yzwx;
    	}
    	normal = tempNormal;
        position = tempPosition;
    	
    }
    
    position = modelViewMatrix *  position;

    varyingEcPosition = (vec3(position)) / position.w;
   
	// Get surface normal in eye coordinates
    varyingNormal = normalMatrix * normal.xyz;
    varyingNormal = normalize(varyingNormal); 
    
    // Get Vertex position in eye coordinates
    
	gl_Position = projectionMatrix * position;

}