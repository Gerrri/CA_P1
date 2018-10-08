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


/**
 * Class for storing and providing access to the Ambient light's attributes 
 * @author Ursula Derichs
 * @version 1.0
 *
 */
public class AmbientLight extends AbstLight
{
	/**
	 * Constructs ambient light with specified name
	 * @param name name of light object
	 */
	public AmbientLight(String name)
	{
		super(name);
		ambient = Color.white();
		diffuse = Color.black();
		specular = Color.black();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AmbientLight obj = (AmbientLight) super.clone();
		return obj;
	}
	
 	/**
 	 * Returns the type of light
	 * @return the constant AbstLight::LT_POINT
	 */
	public lightType type()
	{
		return lightType.LT_AMBIENT;
	}

}
