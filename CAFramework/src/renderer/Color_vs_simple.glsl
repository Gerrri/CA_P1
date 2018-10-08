/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */


#version 150
#extension GL_ARB_explicit_attrib_location : enable


layout(location=0) in vec3 vPosition;
layout(location=1) in vec3 vNormal;

out vec3 vColor;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;


void main(void) 
{
	gl_Position = projectionMatrix * modelViewMatrix * vec4(vPosition, 1.0);
	vColor = vNormal;
}