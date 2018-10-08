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

import math.Vec3;
import scenegraph.Channel;
import animation.AbstController;

public class ConnectorAddVec3 extends AbstController {

	/**
	 * Channel from which we receive a vector
	 */
	private Channel inChannel;
	
	/**
	 * Output channel, gets the value of inChannel
	 */
	private Channel outChannel;
	
	/**
	 * Vector that gets added to inChannel value 
	 */
	Vec3 vec;
	
	/**
	 * Constructor of class. 
	 * @param name Name of the controller
	 */
	public ConnectorAddVec3(String name, Vec3 vec, Channel inChannel, Channel outChannel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.inChannel = inChannel;
		this.outChannel = outChannel;
		this.vec = vec;
	}
	
	/**
	 * Main method of the controller. Overrides AbstController.update().
	 * outChannel = inChannel + vec
	 */
	@Override
	public boolean update(float time) {
		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}

		// get vector from input channel
		Vec3 inReference = (Vec3) inChannel.getData();
		Vec3 value = new Vec3( inReference );
		value.add(vec);
		
		// set the channel object to the calculated output value.
		Vec3 outReference = (Vec3) outChannel.getData();		
		outReference.set(value);
		
		return true;
	}	
}
