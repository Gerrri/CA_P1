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

/**
 * Class for a group of particles.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class ParticleGroup extends AbstGeometry
{
	/**
	 * List of particles positions
	 */
	Vec3Array particlePositions;

	/**
	 * Size of points to be rendered
	 */
	float pointSize;

	/**
	 * Channel name of particles positions
	 */
	public static String POSITIONS = "Positions";

	/**
	 * Generates (empty) particles group
	 */
	public ParticleGroup()
	{
		super("ParticleGroup");
		particlePositions = null;
		pointSize = 3;
	}

	/**
	 * Constructs a particle group and set positions of all particles.
	 * 
	 * @param particles
	 *            positions of particles
	 */
	public ParticleGroup(Vec3Array particles)
	{
		super("ParticleGroup");
		particlePositions = particles;
		pointSize = 3;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public ParticleGroup(ParticleGroup obj)
	{
		super(obj);
		this.particlePositions = obj.particlePositions;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		ParticleGroup obj = (ParticleGroup) super.clone();
		return obj;
	}

	/**
	 * Passes the point object to the renderer
	 * 
	 * @param renderer
	 *            the renderer that shall draw the line
	 * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
	 */
	@Override
	public void onDraw(AbstRenderer renderer)
	{
		renderer.draw(this);
	}

	public float getPointSize()
	{
		return pointSize;
	}

	public void setPointSize(float size)
	{
		this.pointSize = size;
	}

	public Vec3Array getParticlePositions()
	{
		return particlePositions;
	}

	public void setParticlePositions(Vec3Array particlePositions)
	{
		this.particlePositions = particlePositions;
	}

}
