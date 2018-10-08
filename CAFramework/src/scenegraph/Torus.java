/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package scenegraph;
import math.Vec3;
import util.Vec3Array;

/**
 * Torus mesh.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */


public class Torus extends Sphere {

	float outerRadius;
	
	public Torus() {
		this("Torus", 0.5f, 1f, 36, 20);
	}

	
	/**
	 * Constructs a torus mesh
	 * 
	 * @param name - name of the torus object
	 * @param innerRadius - inner radius of the torus ring
	 * @param outerRadius - outer radius of the torus
	 * @param sides - number of torus sides
	 * @param rings - number of torus rings
	 */
	public Torus(String name, float innerRadius, float outerRadius, int sides, int rings) {
		super(name, innerRadius, sides, rings);
		this.outerRadius = outerRadius;
		
		if (sides < 2 || rings < 2)
		{
			System.out.println ("Torus cannot be generated with " + sides + " sides and " + rings + " rings.");
			return; 
		}

	    /* precompute values on unit circle */
		CircleTable psiVal = generateCircleTable (rings, false);
		CircleTable phiVal = generateCircleTable (-sides, false);

	    /* Allocate vertex and normal buffers, bail out if memory allocation fails */
		nVertices = (sides +1) * (rings+1);
	    Vec3Array vertices = new Vec3Array (nVertices);
	    vertexNormals  = new Vec3Array(nVertices);

	    for( int j=0; j<=rings; j++ )
	    {
	        for( int i=0; i<=sides; i++ )
	        {
	        	vertices.push_back(
	        			psiVal.cost[j] * (outerRadius + phiVal.cost[i] * innerRadius),
	        			psiVal.sint[j] * (outerRadius + phiVal.cost[i] * innerRadius),
	        							 		    	phiVal.sint[i] * innerRadius);
	        	
	        	vertexNormals.push_back( 
	        			psiVal.cost[j] * phiVal.cost[i],
	        			psiVal.sint[j] * phiVal.cost[i],
	        			phiVal.sint[i]);
	        }
	    }

        this.mesh = new StaticMesh(vertices);
		
		nTriangles = stacks * slices * 2;
		triangleIndices = new int [nTriangles*3];

		int index = 0;
		int offset;
		
		for (int k = 0; k < rings; k++)
		{
			offset = k*(sides+1);
			for (int j = 0; j < sides; j++)
			{
				triangleIndices[index++] = offset+j;
				triangleIndices[index++] = offset+j+1;
				triangleIndices[index++] = offset+sides+j+2;
				triangleIndices[index++] = offset+j;
				triangleIndices[index++] = offset+sides+j+2;
				triangleIndices[index++] = offset+sides+j+1;
			}
		}
	}
		

	
	/**
	 * Copy constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public Torus(Torus obj) {
		super(obj);
		this.outerRadius = obj.outerRadius;
	}
	

//	/**
//	 * Resize height of torus
//	 * 
//	 * @param height
//	 *            height of torus
//	 */
//	public void resizeHeight(float height) {
//		this.height = height;
//	    final float heightStep = height / (float)stacks;
//		float currentHeight = heightStep;
//	    int index = 2*slices + 3;
//	    Vec3Array vertices = mesh.vertices();
//	    for (int i=1; i<=stacks; i++ )
//	    {
//	        for (int j=0; j<=slices; j++)
//	        {
//	        	Vec3 vertex = vertices.at(index);
//	        	vertex.z = currentHeight;
//				vertices.set (index++, vertex);
//	        }
//	        currentHeight += heightStep;
//		}
//	    
//	    for (int j=0; j<=slices+1; j++)
//        {
//	    	Vec3 vertex = vertices.at(index);
//        	vertex.z = height;
//			vertices.set (index++, vertex);
//        }
//	}

	/**
	 * Resize radius of torus
	 * 
	 * @param newRadius
	 *            new radius of torus
	 */
	public void resizeRadius(float newRadius)
	{
		float fac = newRadius / this.radius;
		int index = 1;
		Vec3Array vertices = mesh.vertices();
		
		for (int j = 1; j < nVertices-1; j++)
		{
			Vec3 vertex = vertices.at(index);
			vertex.x = vertex.x * fac;
			vertex.y = vertex.y * fac;
			vertices.set(index++, vertex);
		}
		
		this.radius = newRadius;
	}

	/**
	 * @return size of torus
	 */
	public float getOuterRadius() {
		return outerRadius;
	}
}
