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
import math.Vec4;
import animation.AbstController;

/**
 * Controller with output channel of type Vec3.
 * 
 * The output value is a color vector. The controller mixes the original color
 * with a specified target color over a certain time period. The target color
 * and time period (duration) is specified in the constructor.
 * 
 * @author Ursula Derichs
 * @version 1.0
 * 
 */
public class ColorController extends AbstController {
	/**
	 * original color, which is mixed with the target color over a specified
	 * time period
	 */
	private Vec4 originalColor;
	
	/**
	 * target color
	 */
	private Vec4 targetColor;

	/**
	 * channel to the object, which will be changed over time, the object
	 * itself must be of type Vec3.
	 */
	private Channel colorChannel;

	/**
	 * Constructs ColorController.
	 * 
	 * @param name
	 *            name of the controller (mainly for debug purposes)
	 * @param channel
	 *            channel to the color (must be of type Vec3)
	 * @param targetCol
	 *            target color (which is fully active after the time period
	 *            specified by <duration>
	 * @param duration
	 *            time period for the color mixing
	 */
	public ColorController(String name, Channel channel, Vec3 targetCol, float duration) {
		super(AbstController.RepeatType.CLAMP, 0, duration);
		setName(name);
		originalColor = new Vec4((Vec4) channel.getData());
		targetColor = new Vec4(targetCol,1);
		this.colorChannel = channel;
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
		
		// calculate new color
		if (localInitialDuration > 0)
		{
			Vec4 newColor = Vec4.mix (originalColor, targetColor, localTime/localInitialDuration);
			
			// set the channel object to calculated output value.
			Vec4 vRef = (Vec4) colorChannel.getData();
			vRef.set(newColor);
		}
		
		return true;
	}	
}
