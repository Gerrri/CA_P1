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
 * This is the base class for all objects in the scene, which need to be drawn.
 * These can be basic geometric shapes or meshes with thousands of vertices or
 * polygon lines and curves.
 * 
 * @author Ursula Derichs
 * @version 1.0
 * 
 */
public abstract class AbstGeometry extends AbstSpatial {

	/**
	 * Constructor passes object's name to the base class
	 * 
	 * @param name
	 *            name of the object
	 */
	AbstGeometry(String name) {
		super(name);
	}

	/**
	 * Copy Constructor passes the object to be copied to the base class
	 * 
	 * @param obj
	 *            object to be copied
	 */
	AbstGeometry(AbstGeometry obj) {
		super(obj);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		 return super.clone();
	}

}
