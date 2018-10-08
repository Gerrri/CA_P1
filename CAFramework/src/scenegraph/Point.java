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
import math.Vec3;

/**
 * Class for points.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class Point extends AbstGeometry
{	
	private float size;
	
	/**
	 * Constructs a point.
	 */
	public Point()
	{
		this(1);
	}
	
	/**
	 * Constructs a point.
	 * 
	 * @param size point size
	 */
	public Point(float size)
	{
		super("Point");
		this.size = size;
	}

	
	/**
	 * Copy Constructor
	 * @param obj object to be copied
	 */
	public Point( Point obj) 
	{
		super(obj);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Point obj = (Point) super.clone();
		return obj;
	}
	
	
    /** 
     * Passes the point object to the renderer
     * @param renderer the renderer that shall draw the line
     * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
     */
    @Override
	public void onDraw( AbstRenderer renderer)
    {
    	renderer.draw( this );
    }
    

    
	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}
}
