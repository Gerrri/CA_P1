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

import util.Vec3Array;

/**
 * Cube mesh.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */



public class Cube extends TriangleMesh {

	private static float[][] cubeVertices = { 
		{-0.5f, -0.5f, 0.5f}, 
		{0.5f, -0.5f, 0.5f},
		{-0.5f, 0.5f, 0.5f}, 
		{0.5f, 0.5f, 0.5f}, 
		{-0.5f, 0.5f, -0.5f}, 
		{0.5f, 0.5f, -0.5f}, 
		{-0.5f, -0.5f, -0.5f}, 
		{0.5f, -0.5f, -0.5f }};
	
	private static float[][] cubeFaceNormals = {
		 {0, 0, 1},
		 {0, 1, 0},
		 {0, 0, -1},
		 {0, -1, 0},
		 {1, 0, 0},
		 {-1, 0, 0}};
	
	private static int[][] cubeIndices = {
		{0, 1, 3, 2},
		{2, 3, 5, 4},
		{4, 5, 7, 6},
		{6, 7, 1, 0},
		{1, 7, 5, 3},
		{6, 0, 2, 4}};
	
	final int CUBE_NUM_VERT = 8;
	final int CUBE_NUM_FACES = 6;
	final int CUBE_NUM_EDGE_PER_FACE = 4;
	final int CUBE_VERT_PER_OBJ = CUBE_NUM_FACES * CUBE_NUM_EDGE_PER_FACE;

	/**
	 * Size 
	 */
	private float size;

	/**
	 * Constructs object with default name
	 */
	public Cube() {
		this("Cube", 1);
	}
	
	public Cube(String name) {
		this(name, 1);
	}

	public Cube(float size) {
		this("Cube", size);
	}
	/**
	 * Constructs a unit cube
	 * 
	 * @param name
	 *            cube's name
	 */
	public Cube(String name, float size) {
		super(name);
		this.size = size;
		nTriangles = CUBE_NUM_FACES * 2;
		nVertices = CUBE_VERT_PER_OBJ;
		triangleIndices = new int[nTriangles*3];
		Vec3Array vertices = new Vec3Array(nVertices);
		vertexNormals = new Vec3Array(nVertices);
		int index = 0;
		for (int i=0; i < cubeIndices.length; i++)
		{
			for (int j=0; j < CUBE_NUM_EDGE_PER_FACE; j++)
			{
				int vi = cubeIndices[i][j];
				vertices.push_back(cubeVertices[vi][0]*size, cubeVertices[vi][1]*size, cubeVertices[vi][2]*size);
				vertexNormals.push_back(cubeFaceNormals[i][0], cubeFaceNormals[i][1], cubeFaceNormals[i][2]);
			}
			int offset = i*CUBE_NUM_EDGE_PER_FACE;
			triangleIndices[index++] = offset;
			triangleIndices[index++] = offset+1;
			triangleIndices[index++] = offset+2;
			triangleIndices[index++] = offset;
			triangleIndices[index++] = offset+2;
			triangleIndices[index++] = offset+3;
		}
		mesh = new StaticMesh (vertices); 
	}

	/**
	 * Copy constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public Cube(Cube obj) {
		super(obj);
		this.size = obj.size;
	}
	

	/**
	 * Resize cube
	 * 
	 * @param size
	 *            size of the cube
	 */
	public void setSize(float size) {
		this.size = size;
		mesh.vertices().mul(size);
	}


	/**
	 * @return size of cube
	 */
	public float getSize() {
		return size;
	}

}
