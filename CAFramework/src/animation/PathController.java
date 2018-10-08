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

import math.Vec3;
import math.function.FunctionR1Vec3;
import scenegraph.Channel;

/**
 * @Deprecated Replaced by class FunctionR1Vec3Controller
 * 
 * Manipulates a Vec3 channel (e.g. translation channel) according to a
 * specified 3D function, which is modeling a path in 3D space. The 3D function
 * takes as argument the controller time and returns a 3-dimensional vector
 * (which corresponds to a point in 3D Space).
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
@Deprecated
public class PathController extends AbstController {

	/**
	 * Time-parameterized space function, taking time as argument and delivering
	 * Vec3 as result (R1 -> R3)
	 */
    private FunctionR1Vec3 path;
    
    
	/**
	 * channel to manipulate 3-dimensional characteristics of an object; the
	 * object, which is available through the channel, must be of type Vec3
	 */
    private Channel channel;
  
	/**
	 * Creates a PathController for the specified channel, 3D function,
	 * animation repeat type and interval The animation interval will be
	 * adjusted to the 3D function interval, if necessary.
	 * 
	 * @param channel
	 *            channel, through which the (Vec3!) object can be manipulated
	 * @param path
	 *            the time-parameterized 3D space function
	 * @param loopMode
	 *            the animation repeat type
	 * @param min
	 *            start of the animation
	 * @param max
	 *            end of the animation
	 */
    public PathController(Channel channel, FunctionR1Vec3 path, RepeatType loopMode, float min, float max) {
        super(loopMode, min, max);
        this.channel = channel;
        this.path = path;
        if (path.getTMin() > localMinTime)
        	localMinTime = path.getTMin();		// adjust animation start 
        if (path.getTMax() < localMaxTime)
        	localMaxTime = path.getTMax();		// adjust animation end
    }

	/**
	 * Creates a PathController for the specified channel and 3D function The
	 * repeat type will be set to RT_CLAMP by default, the animation interval is
	 * the same as the 3D function interval.
	 * 
	 * @param channel
	 *            channel, through which the (Vec3!) object can be manipulated
	 * @param path
	 *            the time-parameterized 3D space function
	 */
    public PathController(Channel channel, FunctionR1Vec3 path) {
        this(channel, path, RepeatType.CLAMP, path.getTMin(), path.getTMax());
    }

	/**
	 * Creates a PathController for the specified channel, 3D function and
	 * animation duration. The repeat type will be set to RT_CLAMP by default,
	 * the animation start will be 0 and animation end will be set to the
	 * specified animation duration.
	 * 
	 * @param channel
	 *            channel, through which the (Vec3!) object can be manipulated
	 * @param path
	 *            the time-parameterized 3D space function
	 * @param initialDuration
	 *            duration of the animation
	 */
    public PathController(Channel channel, FunctionR1Vec3 path, float initialDuration) {
        this(channel, path, RepeatType.CLAMP, 0, initialDuration);
    }

	/**
	 * Creates a PathController for the specified channel, 3D function and
	 * animation repeat type. The repeat type will be set to RT_CLAMP by
	 * default, the animation interval is the same as the 3D function interval.
	 * 
	 * @param channel
	 *            channel, through which the (Vec3!) object can be manipulated
	 * @param path
	 *            the time-parameterized 3D space function
	 * @param loopMode
	 *            type of animation (clamp, wrap, cycle)
	 */
    public PathController(Channel channel, FunctionR1Vec3 path, RepeatType loopMode) {
        this(channel, path, loopMode, path.getTMin(), path.getTMax());
    }
  
	/**
	 * Main method to manipulate the channel with the value derived from the 3D
	 * function
	 */
	@Override
	public boolean update(float time) {
		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}
				
		// map application time to controller time
    	float ctrlTime = getLocalTime(time);
    	
    	// evaluate space function at controller time and set the channel object to the evaluated value.
    	((Vec3) channel.getData()).set(path.eval(ctrlTime));
    	
		return true;	// channel updated
	}
    
    /** 
     * @return 3D function this control follows
     */
    public FunctionR1Vec3 getPath() {
        return path;
    }

    /**
     * Sets the 3D function to follow
     * @param path
     */
    public void setPath(FunctionR1Vec3 path) {
        this.path = path;
    }
}
