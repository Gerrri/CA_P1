package Aufgabenblatt_5.Asbach_Dustin.ufo;

import animation.AbstController;
import math.Vec3;
import math.function.FunctionR1Vec3;
import scenegraph.Channel;

public class FunctionR1Vec3Controller extends AbstController {
	/**
	 * Time-parameterized function (R1 -> R3))
	 */
	protected FunctionR1Vec3 func;

	/**
	 * channel to the object, which shall be changed over time, the object
	 * itself must be of type Vec3.
	 */
	protected Channel channel;
	
	/**
	 * Creates a controller where the controlled channel is of type Vec4.
	 * It is controlled by a curve f: I -> R^3 
	 * @param loopMode	As in AbstController
	 * @param localMinTime	Local start time of animation
	 * @param localMaxTime	Local end time of animation
	 * @param globalStartTime	Global start time of animation
	 * @param rate	Factor for mapping global to local time
	 * @param channel	The channel which shall be controlled. Must be of type Vec3
	 * @param func	The function which controls the channel
	 */
	public FunctionR1Vec3Controller(RepeatType loopMode,  
			float localMinTime, 
			float localMaxTime, 
			float globalStartTime, 
			float rate, 
			Channel channel, 
			FunctionR1Vec3 func) {
		super(loopMode, localMinTime, localMaxTime, globalStartTime, rate);
		this.channel = channel;
		this.func = func;
	}
	
	/**
	 * Creates a controller where the controlled channel is of type Vec4.
	 * The specific parameters are set to default values.
	 * It is controlled by a curve f: I -> R^3 
	 * @param loopMode As in AbstController
	 * @param channel The channel which shall be controlled. Must be of type Vec3
	 * @param func The function which controls the channel
	 */
	public FunctionR1Vec3Controller( RepeatType loopMode, Channel channel, FunctionR1Vec3 func ) {
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
		Vec3 n = func.eval(ctrlTime);
		((Vec3) channel.getData()).set(n);
		return true;
	}

	/**
	 * @return the time-parameterized function
	 */
	public FunctionR1Vec3 getFunc() {
		return func;
	}

	/**
	 * @param func
	 *            sets the time-parameterized function
	 */
	public void setFunc(FunctionR1Vec3 func) {
		this.func = func;
	}

}
