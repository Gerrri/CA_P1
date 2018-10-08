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

import java.util.ArrayList;
import scenegraph.Channel;
import scenegraph.ChannelInterface;

/**
 * Base class for animation controllers. Animation controllers manipulate the
 * visible characteristics of an associated object within the scene over time. A
 * controller uses the object's channels to adapt the characteristics depending
 * on the time.
 * 
 * An animation can be scheduled to take place only once in a certain time
 * frame, or repeated times. In the latter case the animation could run in
 * wrapped loops (always start again from the beginning), or it could run in
 * cycled loops (running repeatedly forward and backward). This class is
 * managing the timing of the animation. It maps the time, which has elapsed
 * from the start of the animation (called the application time), to a time
 * within the animation's time interval (called the controller time). The time
 * interval of the animation sequence is [minTime, maxTime]. In clamp mode
 * (RT_CLAMP) the animation would start at minTime and end at maxTime. In wrap
 * mode (RT_WRAP) the animation would start at minTime, run until maxTime, and
 * then start from minTime again etc. In cycle mode (RT_OSCILLATE) the animation
 * would start at minTIme, run until maxTime, and then run backward from maxTime
 * to minTime, run forward again to maxTime etc.
 * 
 * The animation controller maps the application time to the controller time.
 * 
 * @author Ursula Derichs
 * @version 1.0
 * 
 */
public abstract class AbstController implements ChannelInterface, Cloneable {
	/**
	 * animation repeat type: 
	 * RT_CLAMP- runs animation only once within specified time frame 
	 * RT_CYCLE - runs animation repeatedly, always starting from the beginning 
	 * RT_OSCILLATE- runs animation repeatedly forward and backward
	 */
	public enum RepeatType {
		CLAMP, CYCLE, OSCILLATE,
	}

	protected RepeatType repeatType; 

	/**
	 * controller can be enabled/disabled; if disabled, it does not manipulate
	 * the associated channel
	 */
	protected boolean enabled = true;

	/**
	 * if enabled, makes path and control points visible on the screen
	 */
	protected boolean debug = true;

	/**
	 * start time of the animation sequence
	 */
	protected float localMinTime;

	/**
	 * end of the animation sequence
	 */
	protected float localMaxTime;

	/**
	 * last time the controller was invoked
	 */
	protected float globalLastTime;

	/**
	 * duration of the animation sequence (maxTime-minTme)
	 */
	protected float localInitialDuration;
	
	/**
	 * Start of the animation in global time
	 */
	protected float globalStartTime;
	
	/**
	 * multiplication rate between global and local time
	 */
	protected float rate;
	
	/**
	 * Name of the controller
	 */
	protected String name;
	
	/**
	 * List of data channels of the node
	 */
	protected ArrayList<Channel> channels;

	
	/**
	 * For some derived controllers, the timing is irrelevant, therefore an
	 * empty Constructor is provided.
	 */
	public AbstController() {
	}
	
	/**
	 * Constructor sets all time related parameters of the applications
	 * 
	 * @param repType
	 *            repeat type of the application
	 * @param localMinTime
	 *            start time of the animation sequence
	 * @param localMaxTime
	 *            end time of the animation sequence
	 */
	public AbstController(RepeatType repType, float localMinTime, float localMaxTime) {
		this.repeatType = repType;
		this.localMinTime = localMinTime;
		this.localMaxTime = localMaxTime;
		this.enabled = true;
		this.globalLastTime = Float.NEGATIVE_INFINITY;
		this.localInitialDuration = localMaxTime - localMinTime;
		this.globalStartTime = 0.0f;
		this.rate = 1.0f;
		this.channels = new ArrayList<Channel>();
	}

	/**
	 * Constructor sets all time related parameters of the applications
	 * 
	 * @param repType
	 *            repeat type of the application
	 * @param localMinTime
	 *            start time of the animation sequence
	 * @param localMaxTime
	 *            end time of the animation sequence
	 * @param globalStartTime
	 * 				global start time of the animation sequence
	 * @param	rate
	 * 				multiplication rate
	 */
	public AbstController(RepeatType repType, float localMinTime, float localMaxTime, float globalStartTime, float rate) {
		this.repeatType = repType;
		this.localMinTime = localMinTime;
		this.localMaxTime = localMaxTime;
		this.enabled = true;
		this.globalLastTime = Float.NEGATIVE_INFINITY;
		this.localInitialDuration = localMaxTime - localMinTime;
		this.globalStartTime = globalStartTime;
		this.rate = rate;
		this.channels = new ArrayList<Channel>();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstController obj = (AbstController) super.clone();
		return obj;
	}

	/**
	 * Enables or disables the controller
	 * 
	 * @param enabled
	 *            flag to enable or disable controller (is enabled by default)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return enable/disable status of Controller
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return duration of the animation sequence (which should equal to
	 *         maxTime-minTime)
	 */
	public float getInitialDuration() {
		return localInitialDuration;
	}

	/**
	 * Main method of the controller. Should be overridden in each subclass to
	 * manipulate certain object's characteristics for the given point in time.
	 * The base class update method should be called in the beginning of each
	 * overridden method to check if the controller needs to recalculate the
	 * characteristics it is responsible for (this is the case when the
	 * controller is enabled and a new time is given).
	 * 
	 * @param time
	 *            global time
	 * @return   true, if Controller needs to become active and calculate new
	 *               characteristics. 
	 *            false, if Controller is disabled or if the
	 *               given time is the same as in the last update() call
	 */
	public boolean update(float time) {

		if (enabled && (time == Float.NEGATIVE_INFINITY) || (time != globalLastTime))
		{
			globalLastTime = time;
			return true;
		}
		return false;
	}

	/**
	 * Maps global time to local time within the animation sequence interval
	 * [localMinTime, localMaxTime].The mapping is dependent
	 * on the repeat type. If one of the parameters localMinTime, localMaxTime oder globalStartTime are
	 * not finite, then special behavior is implemented. 
	 * 
	 * rate is a multiplication factor for the mapping between global and local time
	 *
	 * 1) RT_CLAMP - the animation runs only once from
	 * localMinTime to localMaxTime. It starts in global time at globalStartTime. If global time values
	 * are mapped to values greater then localMaxTime, the method returns localMaxTime. If global 
	 * time values are mapped to values smaller then localMintime, the method returns localMintime.
	 * 
	 * 2) RT_CYCLE - the animation is running in a repeated loop, always
	 * starting from the beginning. globalStartTime is mapped to localMinTime. 
	 * 
	 * 3) RT_OSCILLATE - the animation is running in a repeated loop always forward
	 * and backward. globalStartTime is mapped to localMinTime. 
	 * 
	 * Here is an example controller time calculation for the local time interval
	 * [20, 25], rate = 1.0, globalStartTime = 20
	 * 
	 * Application Time 0, 1, 2, 3, 4, 5, 6,..20, 21, 22,..24, 25, 26,..30, 31,...
	 * ------------------------------------------------------------------
	 * Controller Time 
	 * RT_CLAMP        20, 20, 20, 20, 20, 20, 20,..20, 21, 22,..24, 25, 25,.. 25, 25,.. 
	 * RT_CYCLE        20, 21, 22, 23, 24, 20, 21,..20, 21, 22,..24, 20, 21,.. 20, 21,.. 
	 * RT_OSCILLATE	   20, 21, 22, 23, 24, 25, 24,..20, 21, 22,..24, 25, 24,.. 20, 21 ...
	 * 
	 * @param time
	 *            application time in milliseconds
	 * @return transformed time for controller, in milliseconds
	 */
	public float getLocalTime(float time) {
		// First check special cases for infinite values
		if ( localMaxTime == Float.POSITIVE_INFINITY && isFinite(globalStartTime) ) {
			float s = rate * (time - globalStartTime);
			if (isFinite(localMinTime)) {
				s += localMinTime;
			}
			return s;
		}
		
		if (!isFinite(localMinTime) || !isFinite(localMaxTime) || !isFinite(globalStartTime)) {
			return rate * time;
		}
		
		// Now localMinTime, localMaxTime and globalStartTime are finite
			
		// clamp
		if ( repeatType == RepeatType.CLAMP ) {
			float s = rate * (time - globalStartTime) + localMinTime;
			if ( s < localMinTime ) {
//				System.out.println(time + " min " + s);
				return localMinTime;
			} else if ( s > localMaxTime ) {
//				System.out.println(time + " max " + s);
				return localMaxTime;
			} else { 
//				System.out.println(time + " " + s);
				return s;				
			}
		}

		// cycle
		float s = rate * (modArithmetic(time - globalStartTime, localInitialDuration/rate )) + localMinTime;
		
		if (repeatType == RepeatType.CYCLE) {
			return s;
		}
		
		// oscillate		
		if (modArithmetic((float) Math.floor( (time -globalStartTime) / ( localInitialDuration / rate)) , 2) == 1) {
			s = localMaxTime  - s; 
		}
		
		if (rate < 0.0f) {
			s = localMaxTime -s ; 
		}
		
//		System.out.println(time + " " + s);
		return s;
		
	}

	/**
	 * @return repeatType 
	 */
	public RepeatType getRepeatType() {
		return repeatType;
	}

	/**
	 * @param repeatType
	 */
	public void setRepeatType(RepeatType repeatType) {
		this.repeatType = repeatType;
	}
		
	/**
	 * @return maxTime	end time of the animation
	 */
	public float getMaxTime() {
		return localMaxTime;
	}
	
	/**
	 * @param localMaxTime		end time of the animation sequence
	 */
	public void setMaxTime(float localMaxTime) {
		this.localMaxTime = localMaxTime;
		this.localInitialDuration = this.localMaxTime - this.localMinTime;
	}

	/**
	 * @return localMinTime	start time of the animation
	 */
	public float getMinTime() {
		return localMinTime;
	}

	/**
	 * @param localMinTime	start time of the animation sequence
	 */
	public void setMinTime(float localMinTime) {
		this.localMinTime = localMinTime;
		this.localInitialDuration = this.localMaxTime - this.localMinTime;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Global start time
	 */
	public float getGlobalStartTime() {
		return globalStartTime;
	}

	/**
	 * Set global start time
	 * @param globalStartTime
	 */
	public void setGlobalStartTime(float globalStartTime) {
		this.globalStartTime = globalStartTime;
	}

	/** 
	 * @return rate 
	 */
	public float getRate() {
		return rate;
	}

	/**
	 * Set multiplication rate between global and local time 
	 * @param rate
	 */
	public void setRate(float rate) {
		this.rate = rate;
	}
	
	/**
	 * Arithmetic modulo operation, cmp. http://de.wikipedia.org/wiki/Division_mit_Rest#Modulo
	 * @param a
	 * @param b
	 * @return a mod b
	 */
	public float modArithmetic(float a, float b) {
		return  ( a % b + b) % b;
	}
	
	/**
	 * Test if a is finite
	 * @param a
	 * @return Returns true, iff a is finite.
	 */
	public boolean isFinite(float a) {
		return (a < Float.POSITIVE_INFINITY ) && ( a > Float.NEGATIVE_INFINITY ) && ( a != Float.NaN );
	}
	
	/**
	 * Returns all channels of the object, or null if the object has no channels
	 * 
	 * @return list of channels
	 */
	public ArrayList<Channel> getChannels() {
		return channels;
	}
	
	/**
	 * Returns the channel with a given name
	 * 
	 * @param cname
	 *            The name of the queried channel
	 * @return The channel with specified name if it exists, otherwise null
	 */
	public Channel getChannel(String cname) {
		for (int i = 0; i < channels.size(); i++) {
			if (channels.get(i).getName().equals(cname))
				return channels.get(i);
		}
		return null;
	}

}
