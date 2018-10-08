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

/**
 * Class modeling the perspective camera, basically storing and providing access
 * to FOV (Field of View) (all other attributes are in parent class)
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class PerspectiveCamera extends AbstCamera {

	/**
	 * the field of view (in degree) for the perspective camera 
	 */
	private float fov;
	
	/**
	 * @return FOV (Field of View)
	 */
	public float getFov()
	{
		return fov;
	}

	/**
	 * Sets FOV
	 * @param fov
	 */
	public void setFov(float fov)
	{
		this.fov = fov;
	}

	/**
	 * Constructs perspective camera with default FOV (45 degree)
	 */
	public PerspectiveCamera()
	{
		fov = 45.0f;
	}
	
	/**
	 * Constructs perspective camera at a specified position with default FOV
	 * 
	 * @param x
	 *            x coordinate of the position
	 * @param y
	 *            y coordinate of the position
	 * @param z
	 *            z coordinate of the position
	 */
	public PerspectiveCamera(float x, float y, float z)
	{
		super(x, y, z);
		fov = 45.0f;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		PerspectiveCamera obj = (PerspectiveCamera) super.clone();
		return obj;
	}
	
	/**
	 * the camera is an invisible part of the scene, so there is nothing to draw
	 * 
	 * @param renderer
	 *            not used
	 * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
	 */
	public void onDraw( AbstRenderer renderer)
	{
		// nothing to draw
	}
	
	/**
	 * Passes the camera to the renderer (to change the renderer's view)
	 * 
	 * @param renderer
	 *            renderer to adapt to the camera's view
	 * @see scenegraph.AbstCamera#setCamera(renderer.AbstRenderer)
	 */
	public void setCamera( AbstRenderer renderer)
	{
		renderer.setCamera( this);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
