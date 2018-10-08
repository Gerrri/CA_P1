/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package particles;

import animation.AbstController;

public class ParticleEulerController extends AbstController {
	/**
	 * Last update time point
	 */
	private float lastTime;

	/**
	 * The particle system which is controller
	 */
	private ParticleSystem psystem;
	
	/**
	 * The number of Euler steps for each update 
	 */
	private int steps;
	
	/**
	 * Constructor of class. 
	 * @param name Name of the controller
	 * @param psystem The particle system which is controller
	 * @param steps The number of Euler steps for each update
	 */
	public ParticleEulerController(String name, ParticleSystem psystem, int steps) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.psystem = psystem;
		this.lastTime = 0.0f;
		this.steps = steps;
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
		float deltaTime = localTime - lastTime;		
		
		float h = deltaTime / steps;
		
		for (int i = 0 ; i < steps; i++) {
			float t = lastTime + h*i;
			EulerStep.update(psystem, t, h);
		}
			
		lastTime = localTime;
		return true;
	}	
}
