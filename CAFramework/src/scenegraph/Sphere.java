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

import math.MathUtil;
import util.Vec3Array;

/**
 * Sphere mesh.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */


public class Sphere extends TriangleMesh {

	
	protected class CircleTable
	{
		public float [] sint;
		public float [] cost;
		public int numValues;
		public CircleTable (int numValues)
		{
			sint = new float[numValues];
			cost = new float[numValues];
			this.numValues = numValues;
		}
	}
	
	protected CircleTable table;
	
	protected float radius;


	/**
	 * Number of horizontal subdivisions (longitudes)
	 */
	protected int slices;

	/**
	 * Number of vertical subdivisions (latitudes)
	 */
	protected int stacks;

	public Sphere(String name) {
		this(name, 1, 36, 18);
	}
	
	public Sphere() {
		this("Sphere", 1, 36, 18);
	}

	public Sphere(float radius) {
		this("Sphere", radius, 36, 18);
	}
	
	/**
	 * Constructs a sphere
	 * 
	 * @param name - name of the sphere object
	 * @param radius - radius of the sphere
	 * @param slices - number of sphere slices
	 * @param stacks - number of sphere stacks
	 */
	public Sphere(String name, float radius, int slices, int stacks) {
		super(name);
		this.radius = radius;
		this.slices = slices;
		this.stacks = stacks;

		if (slices <= 0 || stacks < 2)
		{
			System.out.println ("Sphere cannot be generated with " + slices + " slices and " + stacks + " stacks.");
			return; 
		}
		
		this.nVertices = slices * (stacks-1) + 1 + stacks; 
		Vec3Array vertices = new Vec3Array(nVertices);
		this.vertexNormals = new Vec3Array(nVertices);

		CircleTable slicesVal = generateCircleTable (slices, false);
		CircleTable stackVal = generateCircleTable (stacks, true);
		
		// "north pole" 
		vertices.push_back (0, 0, radius);
		vertexNormals.push_back (0, 0, 1);
		
		// Generate vertices between poles
		float x, y, z;
		for (int i= 1; i < stacks; i++)
		{
			for (int j=0; j <= slices; j++)
			{
				x = slicesVal.cost[j] * stackVal.sint[i];
				y = slicesVal.sint[j] * stackVal.sint[i];
				z = stackVal.cost[i];
				vertices.push_back (radius * x, radius * y, radius *z);
			    vertexNormals.push_back (x, y, z);
			}
		}
		
		// "south pole" 
		vertices.push_back(0, 0, -radius);
		vertexNormals.push_back(0, 0, -1);
		
        mesh = new StaticMesh(vertices);
		
		nTriangles = (stacks-2) * slices * 2 + 2*slices;
		triangleIndices = new int [nTriangles*3];

		int index = 0;
	    // Top stack 
        for (int j=1;  j<= slices;  j++)
        {
            triangleIndices[index++] = 0; 
            triangleIndices[index++] = j;
            triangleIndices[index++] = j+1;
        }
        
        //Stack in between
        int idx = slices+2;
		for (int k = 1; k < (stacks - 1); k++)
		{
			for (int j = 1; j <= slices; j++)
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
		
		// Bottom stack
        for (int j=idx-slices;  j < idx ;  j++)
        {
            triangleIndices[index++] = j-1;
            triangleIndices[index++] = idx;
            triangleIndices[index++] = j;
        }
	}

	
	/**
	 * Copy constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public Sphere(Sphere obj) {
		super(obj);
		this.radius = obj.radius;
		this.slices = obj.slices;
		this.stacks = obj.stacks;
	}


	/**
	 * Resize sphere
	 * 
	 * @param radius
	 *            radius of sphere
	 */
	public void setRadius(float radius) {
		this.radius = radius;
		mesh.vertices().mul(radius);
	}


	/**
	 * @return radius of sphere
	 */
	public float getRadius() {
		return radius;
	}
	
	
	/**
	 * This table is calculated for all round objects 
	 * @param n - number of circle segments
	 * @param halfCircle - shall only half of the circle be calculated?
	 * @return table with sin and cos values for each segment on the circle. 
	 */
	protected CircleTable generateCircleTable(int n, boolean halfCircle)
	{
	    if (n == 0)
	    	return null; 
	    
	    /* Table size, the sign of n flips the circle direction */
	    int size = Math.abs(n);
	    CircleTable table = new CircleTable(size+1);

	    /* Determine the angle between samples */
	    float angle = MathUtil.PI / n;
	    if (!halfCircle)
	    	angle = angle * 2;
	    		

	    /* Compute cos and sin around the circle */
	    table.sint[0] = 0;
	    table.cost[0] = 1;

	    for (int i=1; i<size; i++)
	    {
	        table.sint[i] = (float) Math.sin(angle*i);
	        table.cost[i] = (float) Math.cos(angle*i);
	    }

	    table.sint[size] =  0;  /* sin PI  = sin 2*PI*/
	    table.cost[size] = halfCircle? -1 : 1;  /* cos PI  or cos(2*PI) */
	
		return table;
	}
}
