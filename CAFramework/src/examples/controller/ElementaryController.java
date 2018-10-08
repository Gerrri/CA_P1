/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */


package examples.controller;

import animation.AbstController;

/**
 * Very simple controller. Just prints global and local time to console
 * @author Stefan
 *
 */
public class ElementaryController extends AbstController {
	
	/**
	 * Constructor of class. 
	 * @param name Name of the controller
	 */
	public ElementaryController(String name) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
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
		
		// Print global and local time
		System.out.println(name + " global Time: " + time + ", local time: " + localTime);
		return true;
	}	
}
