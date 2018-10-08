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
import math.Vec3;
import math.function.FunctionR1Vec3;
import scenegraph.Channel;

/**
 * 
 * Controller for interactive camera positioning and orientation. The camera can
 * freely be moved within the scene to allow the inspection of a scene from
 * different angles and sides. According to the entered keyboard user input the
 * camera is moved and turned sideways and up and down. The following keyboard
 * commands are implemented:
 * <ul>
 * <li>w - move camera forward (zoom in)</li>
 * <li>s - move camera backward (zoom out)</li>
 * <li>a - move camera right</li>
 * <li>d - move camera left</li>
 * <li>q - move camera down</li>
 * <li>e - move camera up</li>
 * <li>-> - rotate camera right (rotate around up axis)</li>
 * <li><- - rotate camera left</li>
 * <li>&uarr; - rotate camera up</li>
 * <li>&darr;- rotate camera down</li>
 * 
 * <li>+ - increases the increment (the width of the movement)</li>
 * <li>- - reduces the increment</li>
 * <li>h - help with an overview of the commands</li>
 * <li>r - resets the camera to the original position and orientation</li>
 * </ul>
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public class CameraController extends AbstController {

	/**
	 * Channel to set the translation (position) of the camera
	 */
	private Channel transChannel;

	/**
	 * Channel to set the rotation (orientation) of the camera
	 */
	private Channel rotationChannel;

	/**
	 * Amount for camera movement, can be increased and decreased interactively
	 */
	private float increment;

	/**
	 * initial position and orientation of the camera (consisting of focus, up
	 * and location vectors)
	 */
	private Vec3 initialFocus;
	private Vec3 initialUp;
	private Vec3 initialLocation;

	/**
	 * Current view direction
	 */
	private Vec3 direction;

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
	
	private Channel objPos;
	private FunctionR1Vec3 func;

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
	 * @param focus
	 *            initial focus (point of interest) of the camera
	 * @param up
	 *            initial up vector of the camera
	 */
	public CameraController(Channel trans, Channel rot, Vec3 focus, Vec3 up) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		transChannel = trans;
		rotationChannel = rot;
		this.up = up;
		this.focus = focus;
		this.location = (Vec3) trans.getData();
		initialUp = new Vec3(up);
		initialFocus = new Vec3(focus);
		initialLocation = new Vec3(location);
		direction = (Vec3.sub(focus, location)).normalize();
		right = (Vec3.cross(up, direction)).normalize();
		increment = 5;
		func = null; 
	}
	
	public CameraController(Channel trans, Channel rot, Vec3 focus, Vec3 up, Channel objectPos, FunctionR1Vec3 func) {
		super(AbstController.RepeatType.CLAMP, func.getTMin(), func.getTMax());
		transChannel = trans;
		rotationChannel = rot;
		this.up = up;
		this.focus = focus;
		this.location = (Vec3) trans.getData();
		initialUp = new Vec3(up);
		initialFocus = new Vec3(focus);
		initialLocation = new Vec3(location);
		direction = (Vec3.sub(focus, location)).normalize();
		right = (Vec3.cross(up, direction)).normalize();
		increment = 5;
		this.objPos = objectPos;
		this.func = func;
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
	public boolean update(float time) {
		if (!super.update(time)) { // check if controller is enabled
			return false;
		}
		float incr;
    	float ctrlTime = getLocalTime(time);   
		boolean result = true;
		if (func != null)
		{
			location = func.eval(ctrlTime);
			focus = (Vec3) objPos.getData();
			turnCamera();
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))	// Maya-like navigation commands with Alt key
		{
			float mouseDX = Mouse.getDX();
			float mouseDY = Mouse.getDY();
//			System.out.println("Mouse coordinates: " + mouseX + "/" + mouseY + ", " + mouseDX + "/" + mouseDY);
			if (Mouse.isButtonDown(0))
			{
				turnUp(-mouseDY);
				turnRight(-mouseDX);
			}
			if (Mouse.isButtonDown(1))
			{
				forward (mouseDX- mouseDY);
			}
			if (Mouse.isButtonDown(2))
			{
				shiftLeft (mouseDX);
				shiftDown(mouseDY);
			}
		}
		else if ((incr = Mouse.getDWheel()) != 0)
		{
			forward (incr/20);
		}
			
		else if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			forward(increment * 0.01f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			backward(increment * 0.01f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			shiftLeft(increment * 0.01f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			shiftRight(increment * 0.01f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_Q))
		{
			shiftDown(increment * 0.01f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_E))
		{
			shiftUp(increment * 0.01f);
		}
		// the following keys can be used alternatively to the mouse commands
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			turnLeft(increment);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			turnRight(increment);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			turnUp(increment);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			turnDown(increment);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_ADD))
		{
			increment++;
			System.out.println("increment set to " + increment);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT))
		{
			increment--;
			System.out.println("increment set to " + increment);
		} 
//		else if (Keyboard.isKeyDown(Keyboard.KEY_H))
//		{
//			printHelp();
//		} 
		else if (Keyboard.isKeyDown(Keyboard.KEY_R))
		{   // reset eye and up vectors
			reset();
			increment = 5;
			System.out.println("camera reset, increment set to " + increment);
		} else
			result = false;


		return result;
	}

	/**
	 * move position one step forward in view direction (zoom in)
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void forward(float step) {
		Vec3 incr = Vec3.mul(direction, step);
		location.add(incr);
		((Vec3) transChannel.getData()).set(location);
	}

	/**
	 * move position one step backward in view direction (zoom out)
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void backward(float step) {
		forward(-step);
	}

	/**
	 * move camera one step to the left side
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void shiftLeft(float step) {
		Vec3 incr = Vec3.mul(right, step);
		focus.add(incr);
		location.add(incr);
		((Vec3) transChannel.getData()).set(location);
	}

	/**
	 * move camera one step to the right side
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void shiftRight(float step) {
		shiftLeft(-step);
	}

	/**
	 * move camera one step upwards
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void shiftUp(float step) {
		Vec3 incr = Vec3.mul(up, step);
		focus.add(incr);
		location.add(incr);
		((Vec3) transChannel.getData()).set(location);
	}

	/**
	 * move camera one step downwards
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void shiftDown(float step) {
		shiftUp(-step);
	}

	/**
	 * rotate camera one step to the left (around up vector)
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void turnLeft(float step) {
		turnRight(-step);
	}

	/**
	 * rotate camera one step to the right (around up vector)
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void turnRight(float step) {
		location.transform(1, Mat4.rotation(up, MathUtil.toRad(step)));
		turnCamera();
	}

	/**
	 * rotate camera one step upwards
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void turnUp(float step) {
//		Vec3 rotationAxis = Vec3.cross(location, up).normalize();
		Vec3 rotationAxis = right;
		Mat4 rotationMatrix = Mat4.rotation(rotationAxis, MathUtil.toRad(step));

		location.transform(1, rotationMatrix);
		up.transform(0, rotationMatrix);

		turnCamera();
	}

	/**
	 * rotate camera one step downwards
	 * 
	 * @param step
	 *            stepsize (amount of movement)
	 */
	private void turnDown(float step) {
		turnUp(-step);
	}

	/**
	 * resets the camera to it's initial position and orientation
	 */
	private void reset() {
		location.set(initialLocation);
		focus.set(initialFocus);
		up.set(initialUp);

		turnCamera();
	}

	/**
	 * Calculate coordinate system of the camera and set rotation matrix to the
	 * inverse (transposed) coordinate system of the camera. Set translation
	 * channel to the previously calculated location.
	 */
	private void turnCamera() {
		direction = Vec3.sub(focus, location).normalize();
		Vec3 zAxis = Vec3.negate(direction);
//		right = Vec3.cross(up, Vec3.negate(zAxis)).normalize();
		Vec3 xAxis = Vec3.cross(up, zAxis).normalize();
		Vec3 yAxis = (Vec3.cross(zAxis, xAxis)).normalize();

		Mat4 rotation = new Mat4(xAxis, yAxis, zAxis);

		((Mat4) rotationChannel.getData()).set(rotation);
		((Vec3) transChannel.getData()).set(location);
	}

	/**
	 * print a help message with the available camera controller commands
	 */
	private void printHelp() {
		System.out.println("press 'h' to print this message again.");
		System.out.println("press '+' or '-' to change the increment that");
		System.out.println("occurs with each position change.");
		System.out.println("press arrow keys to rotate");
		System.out.println("press 'w' to move forward");
		System.out.println("press 's' to move backward");
		System.out.println("press 'q' to move object upward / camera downward");
		System.out.println("press 'e' to move object downward / camera upward");
		System.out.println("press 'a' to move object to the left / camera to the right");
		System.out.println("press 'e' to move object to the right / camera to the left");
		System.out.println("press 'r' to reset the camera (eye and up).");
		System.out.println("Alternativley you can use Maya navigation commands by pressing");
		System.out.println("Alt key together with left/middle/right mouse button to rotate/pan/zoom.");
		System.out.println("press ESC to quit.");
	}
}
