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
import util.Vec3Array;
import math.Vec3;

/**
 * Class for blend shape deforming (morphing). A blend shape takes a base mesh
 * and blends it with other target meshes based on weight values. The base mesh
 * is the mesh to be deformed; it will change its form as the targets and weight
 * parameters are modified. The base mesh has a list of target meshes that
 * effect its shape. Each target is associated with a weight value. When the
 * weight value increases, the target has more influence on the base mesh.
 * 
 * The base mesh and its targets need to have identical vertex counts.
 * 
 * The blended object is a combination of N targets (the sum of the base mesh
 * and the weighted differences between base mesh and each target).
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class Blendshape extends AbstMeshData {
	/**
	 * base mesh
	 */
	private AbstMeshData baseMesh;

	/**
	 * target meshes [numTargets]
	 */
	private AbstMeshData[] targets;

	/**
	 * number of target meshess
	 */
	private int numTargets;

	/**
	 * weights for each target [numTargets]
	 */
	private PrimitiveData[] weight;


	/**
	 * displacement vectors (differences between base mesh and target mesh)
	 * [numTargets]
	 */
	private Vec3[][] displacement;

	/**
	 * Constructor. The base mesh and the target meshes are stored. For each
	 * target the delta between the base mesh and the target mesh is calculated
	 * and the channels to the weights are created.
	 * 
	 * @param baseMesh
	 *            base mesh for the blend shape
	 * @param numTargets
	 *            number of target meshes
	 * @param targets
	 *            field of target meshes
	 */
	public Blendshape(AbstMeshData baseMesh, int numTargets,
			AbstMeshData[] targets) {
		super(new Vec3Array (baseMesh.vertices()));
		try {
			this.baseMesh = (AbstMeshData) baseMesh.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.numTargets = numTargets;
		this.targets = targets;

		// work with deltas between base mesh and target mesh
		if (targets != null)
			calcDisplacement();

		// create channels for the target weights
		createDataChannels();
	}
	
	@Override
	public Object clone() {
		return new Blendshape(baseMesh, numTargets, targets);
	}

	/**
	 * Returns the number of weights (which is the same as the number of
	 * targets)
	 * 
	 * @return number of weights
	 */
	public int getNumWeights() {
		return numTargets;
	}

	/**
	 * Returns the name of a weight. The name is "WEIGHT_<n>" 
	 * 
	 * @return name of weight
	 */
	public String weightName(int nWeight) {
		assert (nWeight >= 0 && nWeight < numTargets) : "Index for weight is out of range";

		return (targets[nWeight]).getName();
	}

	/**
	 * Sets a weight value.
	 * 
	 * @param nWeight
	 *            index of the weight to set
	 * @param fValue
	 *            new weight
	 */
	public void setWeight(int nWeight, float fValue) {
		assert (nWeight >= 0 && nWeight < numTargets) : "Index for weight is out of range";

		weight[nWeight].f = fValue;
	}


	/**
	 * @return base mesh
	 */
	public AbstMeshData baseMesh() {
		return baseMesh;
	}

	/**
	 * Calculates the blended mesh by adding the weighted target differences to
	 * the base mesh TODO check if weights have been updated (otherwise nothing
	 * to do here)
	 * 
	 * @return true
	 */
	public boolean updateWorldData() {
		baseMesh.updateWorldData();

		// initialize blended mesh with base mesh vertices
		vertices = new Vec3Array (baseMesh.vertices());

		// Iterate over all targets and blend
		for (int i = 0; i < numTargets; i++) {
			if (Math.abs(weight[i].f) < 1E-3)
				continue;

			for (int j = 0; j < nVertices; j++) 
				vertices.add (j, Vec3.mul(displacement[i][j], weight[i].f));
		}
		return true;
	}



	/**
	 * Internal function. This function calculates the displacement vectors
	 * between the base mesh and the target meshes used in the blend process.
	 */
	private void calcDisplacement() {
		displacement = new Vec3[numTargets][nVertices];
		Vec3Array base = baseMesh.vertices();

		for (int i = 0; i < numTargets; i++) {
			assert (targets[i].getNumVertices() == nVertices) : "Blend Meshes do not have the same amount of vertices";
			Vec3Array targetVertices = targets[i].vertices();
			for (int k = 0; k < nVertices; k++)
				displacement[i][k] = Vec3.sub(targetVertices.at(k), base.at(k));
		}
	}

	/**
	 * Internal function. This function creates the data channels used to
	 * control the weights of the target groups.
	 */
	private void createDataChannels() {
		String name;
		channels = new ArrayList<Channel>();
		if (numTargets > 0) {
			this.weight = new PrimitiveData[numTargets];
			for (int i = 0; i < numTargets; i++) {
				weight[i] = new PrimitiveData(0.0f);
				name = "WEIGHT_" + i;
				targets[i].setName (name);
				channels.add(new Channel(name, weight[i]));
			}
		}
	}
}
