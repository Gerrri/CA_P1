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
 * Base class for mesh data storage. A mesh data object provides storage for the
 * vertex data that is used by a triangle mesh. The vertex data can either be
 * static or dynamic (e.g. for animation) and it is possible to build
 * hierarchies of mesh data objects to make the (dynamically created) vertices
 * the input of another mesh data object, which might manipulate them further.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstMeshData extends AbstSceneObject {

	/**
	 * number of vertices
	 */
	protected int nVertices;
	protected Vec3Array vertices;

	/**
	 * Sets vertices and initializes the vertex count of the mesh
	 * 
	 * @param vertices
	 *            field of vertices
	 */
	public AbstMeshData(Vec3Array vertices) {
		this.nVertices = vertices.length();
		this.vertices = vertices;
	}

	/**
	 * Clone method must be implemented in each derived class
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstMeshData obj = (AbstMeshData) super.clone();
		return obj;
	}

	/**
	 * Returns the number of vertices.
	 * 
	 * @return number of vertices
	 */
	public int getNumVertices() {
		return nVertices;
	}
	

	/**
	 * Returns the vertex data.
	 * 
	 * @return vertex data
	 */

	public Vec3Array vertices()
	{
		return vertices;
	}

	/**
	 * Updates internal data for new frame. This data is considered to be
	 * consistent for the duration of (at least) one frame; thus the application
	 * can pass consecutive frame numbers, which in turn can be used by the
	 * object to prevent multiple recalculations, should this function be called
	 * more than once per frame. A frame number of -1 will enforce recalculation
	 * in every case.
	 * 
	 * @return a boolean value indicating whether the mesh data has changed
	 *         since the previous call to this function
	 */

	public abstract boolean updateWorldData();

	/**
	 * Returns the base mesh data object. This function can be used to traverse
	 * hierarchies of mesh data objects. In the case of an object that has
	 * several mesh data objects as inputs, one of these meshes is considered to
	 * be the primary mesh, which is returned by this function.
	 * 
	 * @return base mesh object, or 0
	 */

	public abstract AbstMeshData baseMesh();

}
