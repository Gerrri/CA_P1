/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies  - Computer Animation
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */



package examples.controller;

import math.Vec3;
import animation.AbstController;

public class SimpleCircleController extends AbstController {
	/**
	 * Radius of the circle
	 */
	private float radius;

	/**
	 * Constructor of class. 
	 * @param name Name of the controller
	 */
	public SimpleCircleController(String name, float radius) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.radius = radius;
	}
	
	/**
	 * Main method of the controller. It overrides AbstController.update
	 */
	@Override
	public boolean update(float time) {
		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}

		// map global time to local time
		float localTime = getLocalTime(time);
		
		// calculate position on circle, dependent on localTime
		Vec3 position = new Vec3(radius * (float)Math.sin(localTime), radius * (float)Math.cos(localTime), 0.0f);
		
		// Print global and local time and position on circle
		System.out.println(name + " Global Time: " + time + " , Local Time: " + localTime + " , Position:" + position);
		return true;
	}	
}
