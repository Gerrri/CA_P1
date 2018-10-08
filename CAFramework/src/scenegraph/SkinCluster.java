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

import java.util.ArrayList;
import java.util.HashMap;

import util.Vec3Array;
import math.Mat4;

/**
 * Class for a set of vertices connected to a skeleton. A skeleton is a
 * hierarchy of joints and bones. The skeleton is animated and the skin (the
 * vertices of the skin) shall move together with the skeleton. For this reason
 * each vertex is connected to a number of joints in the skeleton. Usually the
 * vertex is connected to its nearest surrounding joints. Whenever the joints
 * move, the vertex is moving accordingly. The joints shall have different
 * influences on a vertex. The influence is realized as a weight with a value in
 * [0,1].
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class SkinCluster extends AbstMeshData
{

	/**
	 * number of weights per vertex. Each vertex must have the same amount of
	 * weights.
	 */
	private int numWeights;

	/**
	 * weights array per vertex per influencing joint (numVertices x numWeights)
	 */

	private float[][] weights;

	/**
	 * array with indices of the influencing joints per vertex (numVertices x
	 * numWeights). This index will be used for retrieving the correct
	 * jointMatrix for the Render Engine
	 */
	private int[][] jointIndices;

	/**
	 * complete list of joints that have an influence on the mesh
	 */
	private ArrayList<Joint> joints;

	/**
	 * for each joint it`s position within the skin at bind time is calculated
	 * in coordinates changes matrix [local joint coordinates <- mesh
	 * coordinates]
	 */
	private ArrayList<Mat4> jointBindInverses;

	/**
	 * list of transformation matrices per joint of the current animation frame
	 */
	private Mat4[] jointMatrices;

	/**
	 * Constructor.
	 * 
	 * @param vertices
	 *            skin mesh
	 * @param weights
	 *            field of weights of influencing joints per vertex (numVertices
	 *            x numWeights)
	 * @param jointIndices
	 *            field of indices of influencing joints per vertex (numVertices
	 *            x numWeights)
	 * @param numWeights
	 *            number of weights per vertex (is the same for all vetices).
	 */
	public SkinCluster(Vec3Array vertices, float[][] weights,
			int[][] jointIndices, int numWeights)
	{
		super(vertices);
		this.weights = weights;
		this.jointIndices = jointIndices;
		this.numWeights = numWeights;
		this.joints = null;
		this.jointBindInverses = null;
		this.jointMatrices = null;
	}

	@Override
	public SkinCluster clone()
	{
		// arrays must be copied
		SkinCluster copy = new SkinCluster(vertices, weights, jointIndices,
				numWeights);
		copy.setJoints(joints);
		return copy;
	}

	/**
	 * For each joint the actual joint`s transformation matrix (the matrix with
	 * the actual position/orientation/scale of the joint in world coordinates)
	 * is "transfered" to a transformation matrix for the vertices in skin mesh
	 * coordinates.
	 * 
	 * @return true
	 */
	public boolean updateWorldData(Mat4 meshTransform)
	{

		if (joints != null)
		{
			Mat4 inv = Mat4.inverse(meshTransform);
			for (int i = 0; i < joints.size(); i++)
			{
				Joint joint = joints.get(i);
				Mat4 transform = joint.getGlobalTransform();
				jointMatrices[i] = Mat4.mul(transform,
						jointBindInverses.get(i));
				jointMatrices[i] = Mat4.mul(inv, jointMatrices[i]);
			}
		}

		return true;
	}

	/**
	 * Reset all joints to their positions/orientations at bind time.
	 */
	public void setBindPose()
	{
		if (joints != null)
		{
			for (int i = 0; i < joints.size(); i++)
			{
				Joint joint = joints.get(i);
				Mat4 trans = new Mat4(jointBindInverses.get(i));
				joint.setLocalTransform(trans);
			}
		}
	}

	/**
	 * Set current position/orientation as binding pose.
	 */
	public void recalculateBindings(HashMap<Joint, Mat4> rotInverses)
	{
		for (int i = 0; i < joints.size(); i++)
		{
			Joint joint = joints.get(i);
			Mat4 rotInverse = rotInverses.get(joint);
			if (rotInverse != null)
			{
				jointBindInverses.set(i,
						Mat4.mul(rotInverse, jointBindInverses.get(i)));
			}
		}
	}

	public boolean updateWorldData()
	{
		return false;
	}

	public StaticMesh baseMesh()
	{
		return null;
	}

	/**
	 * @return true, if joints are connected to this skin false, if no joints
	 *         are connected
	 */
	public boolean hasJoints()
	{
		return joints != null;
	}

	/**
	 * Sets the list of joints and stores the current position of the joints
	 * within the skin in world (mesh) coordinates
	 * 
	 * @param joints
	 */
	public void bindJoints(ArrayList<Joint> joints)
	{
		this.joints = joints;
		if (joints != null)
			this.jointMatrices = new Mat4[joints.size()];

		int numJoints = joints.size();
		jointBindInverses = new ArrayList<Mat4>(numJoints);

		for (Joint joint : joints)
		{
			jointBindInverses.add(Mat4.inverse(joint.getGlobalTransform()));
		}
	}

	public void rebindJoints(Mat4 mesh_transform)
	{
		if (joints != null)
		{
			for (int i = 0; i < joints.size(); i++)
			{
				Joint joint = joints.get(i);
				Mat4 inverseBoneMatrix = Mat4
						.inverse(joint.getGlobalTransform())
						.mul(mesh_transform);
				jointBindInverses.get(i).set(inverseBoneMatrix);
			}
		}
	}

	/**
	 * set list of all joints that have an influence on the skin. Initializes
	 * the joint matrices for the render engine.
	 * 
	 * @param joints
	 *            list of joints that influence the skin
	 */
	public void setJoints(ArrayList<Joint> joints)
	{
		this.joints = joints;
		if (joints != null)
			this.jointMatrices = new Mat4[joints.size()];
	}

	/**
	 * Sets list of joints positions/orientations at binding time in mesh
	 * coordinates.
	 * 
	 * @param jointBindInverses
	 *            list of joint`s positions/orientations at binding time in mesh
	 *            coordinates
	 */
	public void setJointBindings(ArrayList<Mat4> jointBindInverses)
	{
		this.jointBindInverses = jointBindInverses;
	}

	/**
	 * @return list of all joints that influence this skin
	 */
	public ArrayList<Joint> getJoints()
	{
		return joints;
	}

	/**
	 * @return list of joint movement matrices (transformation of joint movement
	 *         transferred to movement in mesh coordinates)
	 */
	public Mat4[] getJointMatrices()
	{
		return jointMatrices;
	}

	/**
	 * @return number of weights (same number of all vertices)
	 */
	public int getNumWeights()
	{
		return numWeights;
	}

	/**
	 * @return field of weights per vertex
	 */
	public float[][] getWeights()
	{
		return weights;
	}

	/**
	 * @return field of joint indices per vertex
	 */
	public int[][] getJointIndices()
	{
		return jointIndices;
	}
}
