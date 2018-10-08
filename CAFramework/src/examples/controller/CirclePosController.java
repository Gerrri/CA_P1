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

import scenegraph.Channel;
import math.Vec3;
import animation.AbstController;

/**
 * Controller with output channel of type Vec3. 
 * The output value is a point on a circle in the x-y-plane.
 * The radius of the circle can be specified in the constructor
 * @author Stefan
 *
 */
public class CirclePosController extends AbstController {
	/**
	 * Radius of the circle
	 */
	private float radius;

	/**
	 * channel to the object, which will be changed over time, the object
	 * itself must be of type Vec3.
	 */
	private Channel channel;
	
	/**
	 * Constructor of class. 
	 * @param name Name of the controller
	 */
	public CirclePosController(String name, float radius, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.radius = radius;
		this.channel = channel;
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
		
		// calculate position
		Vec3 position = new Vec3(radius * (float)Math.sin(localTime), radius * (float)Math.cos(localTime), 0.0f);
		
		// set the channel object to calculated output value.
		Vec3 vRef = (Vec3) channel.getData();
		vRef.set(position);
		
		return true;
	}	
}
