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

import renderer.AbstRenderer;
import util.Vec3Array;
import math.Vec3;

/**
 * Class provides storage and management for triangle meshes. The class needs a
 * MeshData object to operate on the vertex data. Other functions, such as face
 * and vertex normal calculations, are done by the triangle mesh.
 * 
 * @author Ulla
 * @version 1.0
 */
public class TriangleMesh extends AbstGeometry {
	/**
	 * base mesh object
	 */
	protected AbstMeshData mesh;

	/**
	 * number of vertices of mesh object
	 */
	protected int nVertices;

	/**
	 * number of triangles
	 */
	protected int nTriangles;

	/**
	 * vertex indices for each face (a face is a triangle consisting of three
	 * vertices). The first three indices specify the vertices of the first
	 * triangle. The next three indices specify the vertices of the second
	 * triangle etc.
	 */
	protected int[] triangleIndices;


	/**
	 * texture uv coordinate pairs
	 */
	protected Vec3Array UV;


	/**
	 * face normals
	 */
	protected Vec3Array faceNormals;

	/**
	 * face/vertex normals
	 */
	protected Vec3Array vertexNormals;
	
	protected boolean wireframe;
	

	/**
	 * Constructor assigns the given arrays as references.
	 * 
	 * @param mesh
	 *            vertex information
	 * @param triangleIndices
	 *            vertex indices to form triangles
	 * @param vertexNormals
	 *            vertex normals (if == null, they will be calculated)
	 * @param UV
	 *            texture coordinates
	 */
	public TriangleMesh(AbstMeshData mesh, 
			int[] triangleIndices, 
			Vec3Array vertexNormals,  
			Vec3Array UV) 
	{
		super("TriangleMesh");
		this.nVertices = mesh.getNumVertices();
		this.nTriangles = triangleIndices.length/3;
		this.mesh = mesh;
		this.triangleIndices = triangleIndices;
		this.UV = UV;
		this.vertexNormals = vertexNormals;
		if (vertexNormals == null || vertexNormals.length() == 0)
			updateNormals();
		this.wireframe = false;
	}
	
	public TriangleMesh(Vec3Array vertices, int[] triangleIndices) 
	{
		this(new StaticMesh(vertices), triangleIndices, null);
	}
	
	public TriangleMesh(AbstMeshData mesh, int[] triangleIndices) 
	{
		this(mesh, triangleIndices, null);
	}
	
	public TriangleMesh(AbstMeshData mesh, int[] triangleIndices, Vec3Array normals) 
	{
		this(mesh, triangleIndices, normals, null);
	}

	public TriangleMesh(String name)
	{
		super(name);
		this.nVertices = 0;
		this.nTriangles = 0;
		this.mesh = null;
		this.triangleIndices = null;
		this.UV = null;
		this.vertexNormals = null;
		this.wireframe = false;
	}
	public boolean isWireframe()
	{
		return wireframe;
	}

	public void setWireframe(boolean wireframe)
	{
		this.wireframe = wireframe;
	}

	/**
	 * Copy constructor copies given object.
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public TriangleMesh(TriangleMesh obj) {
		super(obj);

		this.nVertices = obj.nVertices;
		this.nTriangles = obj.nTriangles;
		if (obj.mesh != null)
		{
			try {
				this.mesh = ((AbstMeshData) obj.mesh.clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			this.mesh = null;

		this.triangleIndices = (obj.triangleIndices != null) ? obj.triangleIndices.clone() : null;
		this.UV = (obj.UV != null) ? obj.UV : null;
		this.faceNormals = (obj.faceNormals != null) ?  new Vec3Array(obj.faceNormals): null;
		this.vertexNormals = (obj.vertexNormals != null) ? new Vec3Array(obj.vertexNormals) : null;
	}
	
	@Override
	public Object clone()  {
		return new TriangleMesh (this);
	}

	/**
	 * Returns the number of vertices in the mesh.
	 * 
	 * @return number of vertices
	 */
	public int numVertices() {
		return nVertices;
	}

	/**
	 * Returns the number of triangles in the mesh.
	 * 
	 * @return number of triangles
	 */
	public int numTriangles() {
		return nTriangles;
	}

	/**
	 * Return the vertex points.
	 * 
	 * @return array of vertex points
	 */
	public Vec3Array vertices() {
		return mesh.vertices();
	}

	/**
	 * Return the face normals.
	 * 
	 * @return array of face normals
	 */
	public Vec3Array faceNormals() {
		return faceNormals;
	}

	/**
	 * Return the face vertex normals.
	 * 
	 * @return array of face vertex normals
	 */
	public Vec3Array vertexNormals() {
		return vertexNormals;
	}


	/**
	 * Return the triangle interconnection indices.
	 * 
	 * @return array of triangle vertex indices
	 */
	public int[] triangleIndices() {
		return triangleIndices;
	}


	/**
	 * Return the texture uv coordinates.
	 * 
	 * @return array of texture uv coordinate pairs
	 */
	public Vec3Array uv() {
		return UV;
	}

	/**
	 * Checks if mesh has texture coordinates.
	 * 
	 * @return true, if texture coordinates exist
	 */
	public boolean hasUVs() {
		return UV != null;
	}

	/**
	 * Return the base mesh for this triangle mesh.
	 * 
	 * @return pointer to base mesh object
	 */
	public AbstMeshData meshData() {
		return mesh;
	}
	
	public void setMeshData( AbstMeshData mesh) {
		this.mesh = mesh;
	}

	/**
	 * Passes the triangle mesh to the renderer
	 * 
	 * @param renderer
	 *            renderer to draw this object
	 * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
	 */
	public void onDraw(AbstRenderer renderer) {
		renderer.draw(this);
	}


	/**
	 * Update the normal vectors after mesh deformation. All face and face
	 * vertex normals are updated.
	 */	
	public void updateNormals() 
	{	
		if (nTriangles <= 0 || nVertices <=0)
			return;
		
		this.faceNormals = new Vec3Array(nTriangles);
		this.vertexNormals = new Vec3Array (nVertices);
		Vec3Array vertex = mesh.vertices();

		// initialize Vertex-Normals	
		vertexNormals.set(0.0f);

		// Calculate face normals and add them to F/V normals
		for (int i = 0, index = 0; i < nTriangles; i++, index+=3) 
		{
			Vec3 d1 = Vec3.sub(vertex.at(triangleIndices[index + 1]),
					vertex.at(triangleIndices[index]));
			Vec3 d2 = Vec3.sub(vertex.at(triangleIndices[index + 2]),
					vertex.at(triangleIndices[index]));

			Vec3 faceNormal = Vec3.cross(d1, d2);
			faceNormals.set(i, faceNormal);
			vertexNormals.add(triangleIndices[index], faceNormal);
			vertexNormals.add(triangleIndices[index+1], faceNormal);
			vertexNormals.add(triangleIndices[index+2], faceNormal);
		}
		
		vertexNormals.normalize();
	}
	
	/**
	 * Update the normal vectors after mesh deformation. All face and face
	 * vertex normals are updated.
	 */	
	public void calcVertexNormals() 
	{	
		assert faceNormals != null: "faceNormals must be available";
		
		if (nVertices <=0)
			return;
		
		this.vertexNormals = new Vec3Array (nVertices);

		// initialize Vertex-Normals	
		vertexNormals.set(0.0f);

		// Calculate face normals and add them to F/V normals
		for (int i = 0, index = 0; i < nTriangles; i++, index+=3) 
		{
			vertexNormals.add(triangleIndices[index], faceNormals.at(i));
			vertexNormals.add(triangleIndices[index+1], faceNormals.at(i));
			vertexNormals.add(triangleIndices[index+2], faceNormals.at(i));
		}
		
		vertexNormals.normalize();
	}
	

	/**
	 * If the object is not hidden, an update of the mesh data is requested
	 * (e.g. a deforming/morphing of the mesh data after an update of the
	 * morphing weights). If the update request returns that it has updated its
	 * data, the face normals are recalculated.
	 */
	public boolean updateWorldData() {
		if (!hidden())
		{
			if (mesh instanceof SkinCluster)
			{
				((SkinCluster)mesh).updateWorldData(getGlobalTransform());
				updateNormals();
				return true;
			}
			else
				mesh.updateWorldData();
		}
		return false;
	}

}
