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

import org.lwjgl.input.Keyboard;

import animation.AbstController;
import scenegraph.Channel;
import scenegraph.PrimitiveData;

public class InputController extends AbstController {
		
	/**
	 * channel to the object, which will be changed over time, the object
	 * itself must be of type PrimitiveData - float.
	 */
	private Channel channel;
	
	/**
	 * The current state of the controller
	 */
	private float value;
	
	/**
	 * Increment will be added or subtracted from value, according to
	 * keyboard input
	 */
	private float increment;
	
	/**
	 * Constructor of class.
	 * @param name Name of the controller
	 * @param channel The channel which will be controlled. Must be of type PrimitiveData - float.
	 * @param increment	The value will be added or subtracted from state
	 */
	public InputController(String name, Channel channel, float increment) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		setName(name);
		this.channel = channel;
		this.increment = increment;		
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

		// Process keyboard input
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			value += increment;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			value -= increment;
		}
		
		// adjust output
		PrimitiveData data = (PrimitiveData) channel.getData();
		data.set(value);
		
		return true;
	}	
}
