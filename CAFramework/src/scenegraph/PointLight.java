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

import math.Vec4;

/**
 * Class for storing and providing access to the Point light's attributes 
 * @author Ursula Derichs
 * @version 1.0
 *
 */
public class PointLight extends AbstLight
{
	/**
	 * Constructs point light with specified name
	 * @param name name of light object
	 */
	public PointLight(String name)
	{
		super(name);
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		PointLight obj = (PointLight) super.clone();
		return obj;
	}
	
 	/**
 	 * Returns the type of light
	 * @return the constant AbstLight::LT_POINT
	 */
	public lightType type()
	{
		return lightType.LT_POINT;
	}

	/**
	 * Return the light position. The returned position is given in world system
	 * coordinates. The (local) position is always (0/0/0) in object space.
	 * 
	 * @return position of light as vector
	 */
	public Vec4 location()
	{
		return Vec4.origin().transform(getGlobalTransform());
	}

	public static void main(String[] args)
	{
	}

}
