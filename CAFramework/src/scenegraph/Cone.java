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
 * Cone mesh.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class Cone extends Sphere {

	// height of cone;
	float height;
	
	public Cone() {
		this("Cone", 0.5f, 2, 36, 10);
	}

	
	/**
	 * Constructs a cone mesh.
	 * 
	 * @param name - name of cone object
	 * @param radius - radius of cone base
	 * @param height - height of cone
	 * @param slices - number of cone slices 
	 * @param stacks - number of cone stacks
	 */
	public Cone(String name, float radius, float height, int slices, int stacks) {
		super(name, radius, slices, stacks);
		this.height = height;
		
		if (slices <= 0 || stacks < 1)
		{
			System.out.println ("Cone cannot be generated with " + slices + " slices and " + stacks + " stacks.");
			return; 
		}

	    /* Step in z and radius as stacks are drawn. */
	    float currentHeight = 0;
	    float currentRadius = radius;

	    final float heightStep = height / (float)stacks;
	    final float radiusStep = radius / (float)stacks ;
	    /* Scaling factors for vertex normals */
	    float len = (float) Math.sqrt( height * height + radius * radius );
	    final float cosn = height / len;
	    final float sinn = radius / len;

	    this.nVertices = (slices+1) * (stacks+2) + 1;	// need an extra stack to close the bottom of the cone 
		Vec3Array vertices = new Vec3Array(nVertices);
		this.vertexNormals = new Vec3Array(nVertices);

		CircleTable val = generateCircleTable (-slices, false);
		
		// Bottom center point
		vertices.push_back (0, 0, currentHeight);
		vertexNormals.push_back (0, 0, -1);
		
		// Bottom edge loop
		for (int j = 0; j <= slices; j++)
		{
			vertices.push_back(val.cost[j] * currentRadius, val.sint[j]	* currentRadius, currentHeight);
			vertexNormals.push_back(0, 0, -1);
		}
		
		// each Stack
	    for (int i=0; i<stacks+1; i++ )
	    {
	        for (int j=0; j<=slices; j++)
	        {
				vertices.push_back (val.cost[j] * currentRadius, val.sint[j] * currentRadius, currentHeight);
				vertexNormals.push_back (val.cost[j] * cosn, val.sint[j] * cosn, sinn);
	        }
	        currentHeight += heightStep;
	        currentRadius -= radiusStep;
		}
		
        this.mesh = new StaticMesh(vertices);
		
		nTriangles = stacks * slices * 2;
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
		for (int k = 0; k < (stacks-1); k++)
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
            triangleIndices[index++] = j;              /* 0 is top vertex, 1 is first for first stack */
            triangleIndices[index++] = j-slices-1;
            triangleIndices[index++] = j-slices;
        }
	}

	
	/**
	 * Copy constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public Cone(Cone obj) {
		super(obj);
		this.height = obj.height;
	}
	

	/**
	 * Resize height of cone
	 * 
	 * @param height
	 *            height of cone
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
	}

	/**
	 * Resize radius of cone base
	 * 
	 * @param newRadius
	 *            new radius of cone base
	 */
	public void resizeRadius(float newRadius)
	{
		final float radiusStep = newRadius / (float) stacks;
		final float oldRadiusStep = this.radius / (float) stacks;
		float currentRadius = newRadius;
		float oldRadius = this.radius;
		float fac = currentRadius / this.radius;
		int index = 1;
		Vec3Array vertices = mesh.vertices();
		
		for (int j = 0; j <= slices; j++)
		{
			Vec3 vertex = vertices.at(index);
			vertex.x = vertex.x * fac;
			vertex.y = vertex.y * fac;
			vertices.set(index++, vertex);
		}

		for (int i = 1; i <= stacks; i++)
		{
			for (int j = 0; j <= slices; j++)
			{
				Vec3 vertex = vertices.at(index);
				vertex.x = vertex.x * fac;
				vertex.y = vertex.y * fac;
				vertices.set(index++, vertex);
			}
			currentRadius -= radiusStep;
			oldRadius -= oldRadiusStep;
			fac = currentRadius / oldRadius;
		}
		
		this.radius = newRadius;
	}

	/**
	 * @return height of cone
	 */
	public float getHeight() {
		return height;
	}
}
