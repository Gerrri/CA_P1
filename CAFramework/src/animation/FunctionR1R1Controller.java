/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package animation;

import scenegraph.Channel;
import scenegraph.PrimitiveData;
import math.function.FunctionR1R1;

/**
 * Manipulates a (one-dimensional) float channel according to a specified
 * time-parameterized function (commonly an interpolation function). The
 * function takes as argument the controller time and returns a float value.
 * This mechanism is for example used for the adaptation of morphing weights
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class FunctionR1R1Controller extends AbstController {

	/**
	 * Time-parameterized function (R1 -> R1))
	 */
	private FunctionR1R1 func;

	/**
	 * channel to the object, which shall be changed over time, the object
	 * itself must be of type float.
	 */
	private Channel channel;

	/**
	 * Creates a controller where the controlled channel is of type Vec4.
	 * It is controlled by a curve f: I -> R 
	 * @param loopMode	As in AbstController
	 * @param localMinTime	Local start time of animation
	 * @param localMaxTime	Local end time of animation
	 * @param globalStartTime	Global start time of animation
	 * @param rate	Factor for mapping global to local time
	 * @param channel	The channel which shall be controlled. Must be of type float
	 * @param func	The function which controls the channel
	 */
	public FunctionR1R1Controller(RepeatType loopMode,  
			float localMinTime, 
			float localMaxTime, 
			float globalStartTime, 
			float rate, 
			Channel channel, 
			FunctionR1R1 func) {
		super(loopMode, localMinTime, localMaxTime, globalStartTime, rate);
		this.channel = channel;
		this.func = func;
	}

	/**
	 * Creates a controller where the controlled channel is of type Vec4.
	 * The specific parameters are set to default values.
	 * It is controlled by a curve f: I -> R^4 
	 * @param loopMode As in AbstController
	 * @param channel The channel which shall be controlled. Must be of type Vec4
	 * @param func The function which controls the channel
	 */
	public FunctionR1R1Controller(RepeatType loopMode, Channel channel, FunctionR1R1 func ) {
		this(loopMode, func.getTMin(), func.getTMax(), func.getTMin(), 1.0f, channel, func);
	}
	
	

	/**
	 * Main method to manipulate the channel with the value derived from the
	 * time-parameterized function
	 */
	@Override
	public boolean update(float time) {

		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}

		// map application time to controller time
		float ctrlTime = getLocalTime(time);

		// evaluate function at controller time and set the channel object to
		// the evaluated value.
		((PrimitiveData) channel.getData()).f = func.eval(ctrlTime);
		return true;
	}

	/**
	 * @return the time-parameterized function
	 */
	public FunctionR1R1 getFunc() {
		return func;
	}

	/**
	 * @param func
	 *            sets the time-parameterized function
	 */
	public void setFunc(FunctionR1R1 func) {
		this.func = func;
	}

}
