/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package examples.controller;

import math.Vec3;
import scenegraph.Channel;
import scenegraph.PrimitiveData;
import animation.AbstController;

/**
 * Controller with output channel of type Vec3. 
 * The output value is a point on a circle in the x-y-plane.
 * The radius of the circle can be specified by the Channel RADIUS. 
 * It holds data of type PRIMITIVEDATA, and there a float
 * @author Stefan
 *
 */
public class CirclePosRadiusController extends AbstController {
	
	/**
	 * Name for the channel Radius of the controller
	 */
	public static String RADIUS_FLOAT = "Radius";
	
	/**
	 * channel to the object, which will be changed over time, the object
	 * itself must be of type Vec3.
	 */
	private Channel extChannel;
	
	/**
	 * Constructor of class. 
	 * @param name Name of the controller
	 */
	public CirclePosRadiusController(String name, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.extChannel = channel;
		
		PrimitiveData radius  = new PrimitiveData(1.0f);
		channels.add(new Channel(RADIUS_FLOAT, radius));
	}
	
	/**
	 * Main method of the controller. Overrides AbstController.update()
	 */
	@Override
	public boolean update(float time) {
		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}

		// map global time to local time
		float localTime = getLocalTime(time);
		
		PrimitiveData data = (PrimitiveData) channels.get(0).getData(); 
		float radius = data.f;
		
		// calculate position
		Vec3 position = new Vec3(radius * (float)Math.sin(localTime), radius * (float)Math.cos(localTime), 0.0f);
		
		// set the channel object to calculated output value.
		Vec3 vRef = (Vec3) extChannel.getData();
		vRef.set(position);
		
		return true;
	}	
}
