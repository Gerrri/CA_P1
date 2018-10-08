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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import math.Mat4;
import math.MathUtil;
import scenegraph.Channel;

/**
 * Manipulates a Vec3 channel (e.g. translation channel) according to a
 * specified 3D function, which is modeling a path in 3D space. The 3D function
 * takes as argument the controller time and returns a 3-dimensional vector
 * (which corresponds to a point in 3D Space).
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class InteractiveJointController extends AbstController {

	/**
	 * channel to manipulate orientation of object (of type Mat4)
	 */
    private Channel [] rotChannels;
    int currentChannel;
    int numChannels;

	/**
	 * @param translations
	 * @param rotations
	 * @param trans
	 * @param rot
	 * @param loopMode
	 * @param duration
	 */
	public InteractiveJointController(Channel [] jointRotations)
	{
		this.rotChannels = jointRotations;
		currentChannel = 0;
		numChannels = this.rotChannels.length;
		if (numChannels > 5 )
			System.out.println ("Only 5 joints can be controlled, the other joints will be ignored.");
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		InteractiveJointController obj = (InteractiveJointController) super.clone();
		return obj;
	}
  
	/**
	 * Main method controlling the camera movements. Depending on the keyboard
	 * button that was pressed by the user, the camera is moved or turned in the
	 * appropriate direction. Camera movements are only dependent on the user's
	 * keyboard commands (not on the application time).
	 * 
	 * @param time
	 *            not used
	 */
	@Override
	public boolean update(float time)
	{

		if (!super.update(time))
		{ // check if controller is enabled
			return false;
		}
		
		if (numChannels == 0)
			return false;

		if (Keyboard.isKeyDown(Keyboard.KEY_1))
		{
			currentChannel = 0;
		} 
		else if (numChannels > 1 && Keyboard.isKeyDown(Keyboard.KEY_2))
		{
			currentChannel = 1;
		} 
		else if (numChannels > 2 && Keyboard.isKeyDown(Keyboard.KEY_3))
		{
			currentChannel = 2;
		} 
		else if (numChannels > 3 && Keyboard.isKeyDown(Keyboard.KEY_4))
		{
			currentChannel = 3;
		} 
		else if (numChannels > 4 && Keyboard.isKeyDown(Keyboard.KEY_5))
		{
			currentChannel = 4;
		} 


		float mouseDX = Mouse.getDX();
		float mouseDY = Mouse.getDY();
		// System.out.println("Mouse coordinates: " + mouseX + "/" + mouseY + ",
		// " + mouseDX + "/" + mouseDY);
		if (Mouse.isButtonDown(0))
		{
			float radStepUp = MathUtil.toRad(-mouseDY / 5f);
			float radStepRight = MathUtil.toRad(mouseDX / 5f);
			Mat4 rotMatrix = ((Mat4) rotChannels[currentChannel].getData());
			rotMatrix.mul(Mat4.rotationX(radStepUp));
			rotMatrix.mul(Mat4.rotationY(radStepRight));
		}
		return true;
	}

}