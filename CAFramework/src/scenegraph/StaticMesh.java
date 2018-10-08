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

import util.OBJGroup;
import util.Vec3Array;

/**
 * Vertex Storage for static meshes
 */
public class StaticMesh extends AbstMeshData
{

	/**
	 * Constructs static mesh by copying vertex data
	 * 
	 * @param vertex
	 *            vertex data
	 */

	public StaticMesh(float [] vertex)
	{
		this (vertex.length/3, vertex);

	}
	
	/**
	 * Constructs static mesh. The vertices of the mesh are created from a
	 * coordinates array. The first three coordinates are the first vertex, the
	 * next three coordinates are the second vertex etc.
	 * 
	 * @param nVertices
	 *            number of vertices that should be build out of the coordinates
	 *            array
	 * @param coordinates
	 *            array of x,y,z coordinates of the vertices
	 */
	public StaticMesh(int nVertices, float [] coordinates)
	{
		super(new Vec3Array(coordinates));
	}
	
	
	/**
	 * Constructs static mesh. The vertices come from the Object Loader (import
	 * interface for maya files)
	 * 
	 * @param group
	 *            Object Loader result, containing vertices
	 */
	public StaticMesh (OBJGroup group)
	{
		this (group.getPositions());
	}
	
	public StaticMesh (Vec3Array verts)
	{
		super(new Vec3Array(verts));
	}
	
	
	@Override
	public Object clone()  {
		return new StaticMesh (this.vertices);
	}



	/**
	 * Updates internal data for new frame. The object will update its internal
	 * data. This data is considered to be consistent for the duration of (at
	 * least) ohne frame; thus the application can pass consecutive frame
	 * numbers, which in turn can be used by the object to prevent multiple
	 * recalculations, should this function be called more than once per frame.
	 * A frame number of -1 will enforce recalculation in every case.
	 * 
	 * @return a boolean value indicating whether the mesh data changed since
	 *         the previous call to this function
	 */

	public boolean updateWorldData()
	{
		return false;
	}

	/**
	 * Returns the base mesh data object. This function can be used to traverse
	 * hierarchies of mesh data objects. In the case of an object that has
	 * several mesh data objects as inputs, one of these meshes is considered to
	 * be the primary mesh, which is returned by this function.
	 * 
	 * @return base mesh object, or 0
	 */

	public StaticMesh baseMesh()
	{ 
		return null;
	}
	
}
