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
 * Cylinder mesh.  
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
// TODO: make this class abstract and derive the cube, sphere, torus etc as
// child classes

public class Cylinder extends Sphere {

	float height;
	
	public Cylinder() {
		this("Cylinder", 1, 1, 36, 10);
	}

	
	/**
	 * Constructs a cylinder mesh
	 * 
	 * @param name - name of cylinder object
	 * @param radius - radius of cylinder
	 * @param height - height of cylinder
	 * @param slices - number of cylinder slices
	 * @param stacks - number of cylinder stacks
	 */
	public Cylinder(String name, float radius, float height, int slices, int stacks) {
		super(name, radius, slices, stacks);
		this.height = height;
		
		if (slices <= 0 || stacks < 1)
		{
			System.out.println ("Cylinder cannot be generated with " + slices + " slices and " + stacks + " stacks.");
			return; 
		}

	    /* Step in z as stacks are drawn. */
	    float currentHeight = -height/2f;
	    final float heightStep = height / (float) stacks;
		CircleTable val = generateCircleTable (-slices, false);

	    /* Allocate vertex and normal buffers, bail out if memory allocation fails */
		/* need two extra stacks for closing off top and bottom with correct normals */
	    nVertices = (slices+1) * (stacks + 3) + 2;
	    Vec3Array vertices = new Vec3Array(nVertices);   
	    vertexNormals  = new Vec3Array(nVertices);
	    
		// Bottom center point
		vertices.push_back (0, currentHeight, 0);
		vertexNormals.push_back (0, -1, 0);
		
	    /* other on top (get normals right) */
	    for (int j=0; j<=slices; j++)
	    {   
			vertices.push_back(val.cost[j] * radius,  currentHeight, val.sint[j] * radius);
			vertexNormals.push_back(0, -1, 0);
	    }

	    /* each stack */
	    for (int i=0; i<=stacks; i++ )
	    {
	        for (int j=0; j<=slices; j++)
	        {
				vertices.push_back(val.cost[j] * radius,  currentHeight, val.sint[j] * radius);
				vertexNormals.push_back(val.cost[j], 0, val.sint[j]);
	        }

	        currentHeight += heightStep;
	    }

	    currentHeight -= heightStep;
	    /* other on bottom (get normals right) */
	    for (int j=0; j<=slices; j++)
	    {
			vertices.push_back(val.cost[j] * radius,  currentHeight, val.sint[j] * radius);
			vertexNormals.push_back(0, 1, 0);
	    }

		// Top center point
		vertices.push_back (0, currentHeight, 0);
		vertexNormals.push_back (0, 1, 0);	

        this.mesh = new StaticMesh(vertices);
		
		nTriangles = stacks * slices * 2 + 2 * slices;
		triangleIndices = new int [nTriangles*3];

		int index = 0;
	    /* bottom of cone */
        for (int j=1;  j<= slices;  j++)
        {
            triangleIndices[index++] = 0;              /* 0 is top vertex, 1 is first for first stack */
            triangleIndices[index++] = j;
            triangleIndices[index++] = j+1;
        }
        
        int idx = 2 * slices + 3;
		for (int k = 0; k < stacks; k++)
		{
			for (int j = 0; j < slices; j++)
			{
				int leftTopCorner = idx - slices -1;
				int rightTopCorner = leftTopCorner + 1;
				int leftBottomCorner = idx;
				int rightBottomCorner = idx + 1;
				triangleIndices[index++] = leftTopCorner;
				triangleIndices[index++] = leftBottomCorner;
				triangleIndices[index++] = rightBottomCorner;
				triangleIndices[index++] = leftTopCorner;
				triangleIndices[index++] = rightBottomCorner;
				triangleIndices[index++] = rightTopCorner;
				idx++;
			}
			idx++;
		}
		
        for (int j=idx;  j < idx+slices ;  j++)
        {
            triangleIndices[index++] = j+1;              /* 0 is top vertex, 1 is first for first stack */
            triangleIndices[index++] = idx+slices+1;
            triangleIndices[index++] = j;
        }
	}

	
	/**
	 * Copy constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public Cylinder(Cylinder obj) {
		super(obj);
		this.height = obj.height;
	}
	

	/**
	 * Resize height of cylinder
	 * 
	 * @param height
	 *            height of cylinder
	 */
	public void resizeHeight(float height) {
		this.height = height;
	    final float heightStep = height / (float)stacks;
		float currentHeight = heightStep;
	    int index = 2*slices + 3;
	    Vec3Array vertices = mesh.vertices();
	    for (int i=1; i<=stacks; i++ )
	    {
	        for (int j=0; j<=slices; j++)
	        {
	        	Vec3 vertex = vertices.at(index);
	        	vertex.z = currentHeight;
				vertices.set (index++, vertex);
	        }
	        currentHeight += heightStep;
		}
	    
	    for (int j=0; j<=slices+1; j++)
        {
	    	Vec3 vertex = vertices.at(index);
        	vertex.z = height;
			vertices.set (index++, vertex);
        }
	}

	/**
	 * Resize radius of cylinder
	 * 
	 * @param newRadius
	 *            new radius of cylinder
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
	 * @return height of cylinder
	 */
	public float getHeight() {
		return height;
	}
}
