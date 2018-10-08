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

import org.lwjgl.input.Keyboard;

import math.Mat4;
import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Quaternion;
import math.function.FunctionR1QuaternionUtil;
import math.function.FunctionR1Vec3;
import math.function.FunctionR1Vec3Util;
import math.function.FunctionVec3Vec3;
import math.function.util.AddOffsetVec3;
import scenegraph.Channel;

/**
 * Manipulates a Vec3 channel (e.g. translation channel) according to a
 * specified 3D function, which is modeling a path in 3D space. The 3D function
 * takes as argument the controller time and returns a 3-dimensional vector
 * (which corresponds to a point in 3D Space).
 * 
 * @author Ursula Derichs
 */
public class JointController extends AbstController
{

	/**
	 * Time-parameterized space function, taking time as argument and delivering
	 * Vec3 as result (R1 -> R3)
	 */
	private FunctionR1Vec3 translationsFunc;
	private FunctionR1Quaternion rotationsFunc;

	/**
	 * channel to manipulate position of object (of type Vec3)
	 */
	private Channel transChannel;
	/**
	 * channel to manipulate orientation of object (of type Mat4)
	 */
	private Channel rotChannel;

	/**
	 * @param translations
	 * @param rotations
	 * @param trans
	 * @param rot
	 * @param loopMode
	 * @param duration
	 */
	public JointController(FunctionR1Vec3 translations,
			FunctionR1Quaternion rotations, Channel trans, Channel rot,
			RepeatType loopMode, float duration)
	{
		super(loopMode, 0, duration);

		assert(translations == null)
				|| ((translations != null) && (trans != null) && translations
						.getTMax() == duration) : "Translations not correctly specified";
		assert(rotations == null)
				|| ((rotations != null) && (rot != null) && rotations
						.getTMax() == duration) : "Rotations not correctly specified";

		this.rotChannel = rot;
		this.transChannel = trans;
		this.translationsFunc = translations;
		this.rotationsFunc = rotations;
		this.enabled = true;
		this.globalLastTime = Float.NEGATIVE_INFINITY;

	}

	public JointController(FunctionR1Quaternion rotations, Channel rot,
			float duration)
	{
		this(null, rotations, null, rot, RepeatType.CYCLE, duration);
	}

	public JointController()
	{
		this(null, null, null, null, RepeatType.CYCLE, 0);
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		JointController obj = (JointController) super.clone();
		return obj;
	}

	/**
	 * Main method to manipulate the channel with the value derived from the 3D
	 * function
	 */
	@Override
	public boolean update(float time)
	{
		// check if controller needs to do something
		if (!super.update(time))
		{
			return false;
		}

		// map application time to controller time
		float ctrlTime = getLocalTime(time);

		// System.out.println("Time in ms: " + ctrlTime);

		if (translationsFunc != null)
		{
			((Vec3) transChannel.getData())
					.set(translationsFunc.eval(ctrlTime));
			if (Keyboard.isKeyDown(Keyboard.KEY_T))
				System.out.println("Local animation time: " + ctrlTime);
		}

		if (rotationsFunc != null)
		{
			Quaternion q = rotationsFunc.eval(ctrlTime);
			Mat4 m = ((Mat4) rotChannel.getData());
			m.set(q.toRotationMatrix());
		}

		return true; // channel updated
	}

	public FunctionR1Vec3 getTranslationsFunc()
	{
		return translationsFunc;
	}

	public void setTranslationsFunc(FunctionR1Vec3 translationsFunc)
	{
		this.translationsFunc = translationsFunc;
	}

	public FunctionR1Quaternion getRotationsFunc()
	{
		return rotationsFunc;
	}

	public void setRotationsFunc(FunctionR1Quaternion rotFunc)
	{
		this.rotationsFunc = rotFunc;
	}

	public Channel getRotChannel()
	{
		return rotChannel;
	}

	public void setRotChannel(Channel rotChannel)
	{
		this.rotChannel = rotChannel;
	}

	public Channel getTransChannel()
	{
		return transChannel;
	}

	public void setTransChannel(Channel transChannel)
	{
		this.transChannel = transChannel;
	}

	/**
	 * lifts the translation function so that the first translation value
	 * matches the specified parameter trans. This is achieved by composing the
	 * translation function with a function that adds the difference between
	 * trans and the value of the translation function at the left interval
	 * border.
	 * 
	 * @param trans
	 *            translation to be matched on the left interval border
	 */
	public void liftTranslation(Vec3 trans)
	{
		// get left interval border of translation function (starting time)
		float start = translationsFunc.getTMin();
		// evaluate translation at left interval border)
		Vec3 startTranslation = translationsFunc.eval(start);
		// Generate a function that adds the difference between trans and
		// startTranslation)
		FunctionVec3Vec3 offset = new AddOffsetVec3(trans, startTranslation);
		// compose this function to the translation function
		translationsFunc = FunctionR1Vec3Util.compose(offset, translationsFunc);
	}
	
	/**
	 */
	public void startRotation(Quaternion rot)
	{
		rotationsFunc = FunctionR1QuaternionUtil.multiply(rotationsFunc, rot, rotationsFunc.getTMin(), rotationsFunc.getTMax());
	}

	public static JointController getByName(
			ArrayList<JointController> controllers, String name)
	{
		for (JointController controller : controllers)
		{
			if (controller.name.matches(name))
				return controller;
		}

		return null;
	}

}