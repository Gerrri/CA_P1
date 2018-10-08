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

import math.Mat4;
import math.Vec3;
import math.function.FunctionR1Vec3;
import scenegraph.Channel;


/**
 * The camera shall move along a certain path in the 3D scene. The path is
 * specified as a FunctionR1Vec3. A delta can be specified, which let`s the
 * camera move along the path with a certain time distance (the delta).
 * 
 * @author Derichs
 *
 */
public class FollowerCameraController extends AbstController {

	/**
	 * Channel to set the translation (position) of the camera
	 */
	private Channel transChannel;

	/**
	 * Channel to set the rotation (orientation) of the camera
	 */
	private Channel rotationChannel;



	/**
	 * Current up vector
	 */
	private Vec3 up;

	/**
	 * Current right vector
	 */
	private Vec3 right;

	/**
	 * Current focus (point of interest)
	 */
	private Vec3 focus;

	/**
	 * Position of the camera
	 */
	private Vec3 location;
	
	private FunctionR1Vec3 func;
	private float delta; 

	/**
	 * Constructor for the CameraController, which manipulates the translation
	 * and rotation vectors of the camera through the specified channels. The
	 * camera's initial position and orientation (in form of focus and up
	 * vector) is also specified and stored as initial position so that the
	 * original camera position and orientation can be restored, when the user
	 * presses the reset button.
	 * 
	 * @param trans
	 *            channel for the translation vector (associated object must be
	 *            of type Vec3)
	 * @param rot
	 *            channel for the rotation vector (associated object must be of
	 *            type Mat4)
	 * @param func
	 * 			  function for the camera path
	 * @param delta
	 * 			  camera follows the path with a time distance
	 */
	public FollowerCameraController(Channel trans, Channel rot, FunctionR1Vec3 func, float delta) {
		super(AbstController.RepeatType.CLAMP, func.getTMin(), func.getTMax());
		transChannel = trans;
		rotationChannel = rot;
		this.up = new Vec3 (0,1,0);
		this.location = (Vec3) trans.getData();
		this.func = func;
		this.delta = delta;
	}

	/**
	 * Main method controlling the camera movements along a specified curve.
	 * 
	 * @param time position depending on time (and a specified delta)
	 */
	@Override
	public boolean update(float time) {
		if (!super.update(time)) { // check if controller is enabled
			return false;
		}
    	float ctrlTime = getLocalTime(time);   
		boolean result = true;
		if (func != null)
		{
			float camTime = Math.max(ctrlTime-delta,  0);
			location = func.eval(camTime);
			focus = func.eval(ctrlTime);
			float distance = Vec3.sub(location, focus).length();
			if (distance > 0.5f)
				turnCamera();
		}

		return result;
	}


	/**
	 * Calculate coordinate system of the camera and set rotation matrix to the
	 * inverse (transposed) coordinate system of the camera. Set translation
	 * channel to the previously calculated location.
	 */
	private void turnCamera() {
		Vec3 zAxis = Vec3.sub(location, focus).normalize();
		right = Vec3.cross(up, zAxis).normalize();
		Vec3 xAxis = right;
		Vec3 yAxis = (Vec3.cross(zAxis, xAxis)).normalize();

		Mat4 rotation = new Mat4(xAxis, yAxis, zAxis);

		((Mat4) rotationChannel.getData()).set(rotation);
		((Vec3) transChannel.getData()).set(location);
	}


}
