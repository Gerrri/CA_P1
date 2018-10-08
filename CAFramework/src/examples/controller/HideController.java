package examples.controller;

import math.Vec3;
import scenegraph.Channel;
import scenegraph.PrimitiveData;
import animation.AbstController;
import animation.AbstController.RepeatType;

public class HideController extends AbstController
{
	/**
	 * channel to the object, which will be changed over time, the object itself
	 * must be of type Vec3.
	 */
	private Channel channel;

	/**
	 * Constructor of class.
	 * 
	 * @param name
	 *            Name of the controller
	 */
	public HideController(Channel channel)
	{
		super(AbstController.RepeatType.CLAMP, 0,
				Float.POSITIVE_INFINITY);
		setName("HideController");
		this.channel = channel;
	}

	/**
	 * Main method of the controller. Overrides AbstController.update()
	 */
	@Override
	public boolean update(float time)
	{
		// check if controller needs to do something
		if (!super.update(time))
		{
			return false;
		}

		if (getLocalTime(time) >= 0)
		{
			((PrimitiveData) channel.getData()).b = true;
		}
		
		return true;
	}
}
