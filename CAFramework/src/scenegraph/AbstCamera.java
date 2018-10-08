/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */
package scenegraph;

import renderer.AbstRenderer;
import math.Mat4;
import math.Vec3;
import math.Mat3;

/**
 * Base class for camera handling. A camera is an object in the scene, which is
 * not displayed, but which determines the perspective or orthographic view of
 * the scene. The camera has a position (location), an orientation (up,
 * direction, focus, right vectors) and clipping details (near and far clipping
 * plane, aspect ratio). This class allows the manipulation of all these
 * characteristics.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstCamera extends AbstSpatial {

	/**
	 * distance to near clipping plane
	 */
	private float zNear;

	/**
	 * distance to far clipping plane
	 */
	private float zFar;

	/**
	 * aspect ratio
	 */
	private float aspect;

	/**
	 * camera location
	 */
	private Vec3 location;

	/**
	 * view direction
	 */
	private Vec3 direction;

	/**
	 * focus/point of interest
	 */
	private Vec3 focus;

	/**
	 * up direction
	 * 
	 */
	private Vec3 up;

	/**
	 * right direction
	 */
	private Vec3 right;

	/**
	 * distance to center of interest point
	 * 
	 */
	private float distance;

	/**
	 * Default constructor. The camera is positioned at the coordinate (0/0/8),
	 * looking along the Z axis and the Y axis being the 'up' vector. The Z
	 * range is set to [0.1,20].
	 */

	public AbstCamera() {
		this(0, 0, 8f); // camera position moved along negative z axis
	}

	public AbstCamera(float x, float y, float z) {
		zNear = 0.1f;
		zFar = 100.0f;
		aspect = 1.0f;

		location = new Vec3(x, y, z);
		focus = new Vec3(0, 0, 0); // origin as default center of interest
		up = new Vec3(0, 1, 0); // y-axis as default up orientation

		lookAt(location, focus, up); // orient the camera towards the origin
	}

	public AbstCamera(String name) {
		this();
		setName(name);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstCamera obj = (AbstCamera) super.clone();
		obj.location = new Vec3(location);
		obj.direction = new Vec3(direction);
		obj.focus = new Vec3(focus);
		obj.up = new Vec3(up);
		obj.right = new Vec3(right);
		return obj;
	}

	
	/**
	 * Set the renderer.
	 * 
	 * @param renderer
	 *            the renderer
	 */

	public abstract void setCamera(AbstRenderer renderer);

	/**
	 * Convenience routine to set the rotation and translation parameters of the
	 * camera for a given location, focus and up direction.
	 * 
	 * @param camera
	 *            location of the camera
	 * @param focus
	 *            center of location to look at
	 * @param up
	 *            up direction to set
	 */

	public void lookAt(Vec3 camera, Vec3 focus, Vec3 up) {
		Vec3 axisX, axisY, axisZ;
		Vec3 eulerAngles;

		Mat3 coordinateFrame = new Mat3(); 	// stores the inverse of the camera
											// coordinates

		axisZ = Vec3.sub(camera, focus); 	// Z axis: in the opposite direction as
											// camera view
		this.focus = focus;
		distance = Vec3.length(axisZ);
		if (distance > 0.0)
			axisZ.normalize();

		// Up-Vector is Y axis
		axisY = new Vec3(up);

		// X axis can be derived from Y and Z axis
		axisX = Vec3.normalize(Vec3.cross(axisY, axisZ));

		// select arbitrary x Vector, if cross product is 0
		if (axisX.x == 0 && axisX.y == 0 && axisX.z == 0)
			axisX = Vec3.xAxis();

		// recalculate Y axis, as the up vector might not be perpendicular to
		// X and Z
		axisY = Vec3.normalize(Vec3.cross(axisZ, axisX));

		// Set inverse (=transposed) rotation matrix
		coordinateFrame.m00 = axisX.x;
		coordinateFrame.m01 = axisX.y;
		coordinateFrame.m02 = axisX.z;

		coordinateFrame.m10 = axisY.x;
		coordinateFrame.m11 = axisY.y;
		coordinateFrame.m12 = axisY.z;

		coordinateFrame.m20 = axisZ.x;
		coordinateFrame.m21 = axisZ.y;
		coordinateFrame.m22 = axisZ.z;

		// extract Euler angles and set camera parameters
		eulerAngles = coordinateFrame.getEulerAngles();
		setRotation(eulerAngles);
		setTranslation(camera);
		updateTransform();
	}

	/**
	 * rotates camera to the left with the given angle
	 * 
	 * @param angle
	 *            rotation angle in radians
	 */
	public void left(float angle) {
		// rotation axis is the up vector
		location.transform(1, Mat4.rotation(up, angle));
		lookAt(location, focus, up);
	}

	/**
	 * rotates camera upwards with the given angle
	 * 
	 * @param angle
	 *            rotation angle in radians
	 */
	public void up(float angle) {
		// rotation axis is right = location x up
		Vec3 rotationAxis = Vec3.cross(location, up).normalize();
		Mat4 rotationMatrix = Mat4.rotation(rotationAxis, angle);

		// use rotation matrix to transform location and up vector
		location.transform(1, rotationMatrix);
		up = up.transform(0, rotationMatrix);
		lookAt(location, focus, up);
	}

	/**
	 * Move forward towards focus
	 * 
	 * @param amount
	 *            stepsize for the forward movement
	 */
	public void forward(float amount) {
		location.add(Vec3.mul(direction, amount));
		lookAt(location, focus, up);
	}

	/**
	 * Move the camera to the left side, without changing it's orientation
	 * 
	 * @param amount
	 *            stepsize for the movement to the left
	 */
	public void shift(float amount) {
		Vec3 left = Vec3.cross(direction, up);
		left.mul(amount);
		location.add(left);
		focus.add(left);
		lookAt(location, focus, up);
	}

	/**
	 * sets the focus (point of interest) of the camera and adjusts the
	 * orientation.
	 * 
	 * @param focus
	 */
	public void focus(Vec3 focus) {
		this.focus = focus;
		lookAt(location, focus, up);
	}

	/**
	 * @return focus (point of interest) of the camera
	 */
	public Vec3 getFocus() {
		return focus;
	}



	/**
	 * Set the distances of the near and far clipping planes.
	 * 
	 * @param zNear
	 *            near clipping plane value
	 * @param zFar
	 *            far clipping plane value
	 */

	public void setZRange(float zNear, float zFar) {
		this.zNear = zNear;
		this.zFar = zFar;

		setUpdate(true);
	}

	/**
	 * Retrieves the distance to the near clipping plane.
	 * 
	 * @return zNear near clipping plane value
	 */

	public float getZNear() {
		return zNear;
	}

	/**
	 * Retrieves the distance to the far clipping plane.
	 * 
	 * @return zFar far clipping plane value
	 */
	public float getZFar() {
		return zFar;
	}

	/**
	 * Set the pixel aspect ratio. The aspect ratio is the ratio of width to
	 * height
	 * 
	 * @param dAspect
	 *            pixel aspect ratio
	 */

	public void setAspectRatio(float dAspect) {
		this.aspect = dAspect;
		setUpdate(true);
	}

	/**
	 * @return aspect aspect ratio (ratio of width to height)
	 */

	public float aspectRatio() {
		return aspect;
	}

	/**
	 * Returns the camera location.
	 * 
	 * @return location of the camera in world space
	 */

	public Vec3 location() {
		return location;
	}

	/**
	 * Returns the view direction vector.
	 * 
	 * @return view direction vector in world space
	 */

	public Vec3 direction() {
		return direction;
	}

	/**
	 * Returns the up direction vector.
	 * 
	 * @return up direction vector in world space
	 */

	public Vec3 getUp() {
		return up;
	}

	/**
	 * Returns the right direction vector.
	 * 
	 * @return right direction vector in world space
	 */

	public Vec3 right() {
		return right;
	}

	/**
	 * Returns the distance to focus (point of interest).
	 * 
	 * @return distance to focus
	 */

	public float distance() {
		return distance;
	}

	/**
	 * Transforms the camera vectors and positions locally (apply local
	 * transformation matrix consisting of translation, scale and rotation). The
	 * camera might be stand-alone object outside the scene or an object
	 * embedded within the scene.
	 */
	public void updateTransform() {
		super.updateTransform();

		// set camera parameters here, so that all cameras (the ones, which are
		// placed in the world and the ones, which are not placed in the world) have
		// valid parameters
		location = new Vec3();
		location = location.transform(1, localTransform);
		direction = Vec3.zAxis().negate().transform(0, localTransform)
				.normalize();
		up = Vec3.yAxis().transform(0, localTransform).normalize();
		right = Vec3.xAxis().transform(0, localTransform).normalize();

		// set world transformation (to guarantee that stand-alone cameras
		// will also work)
		globalTransform = localTransform;
	}

	/**
	 * Transforms the camera vectors and positions globally (apply global
	 * transformation matrix)
	 * 
	 * @param world
	 */
	public void updateWorldTransform(Mat4 world) {
		super.updateWorldTransform(world);

		// Set final camera values for the world camera

		location = new Vec3();
		location = location.transform(1, globalTransform);
		direction = Vec3.zAxis().negate().transform(0, globalTransform)
				.normalize();
		up = Vec3.yAxis().transform(0, globalTransform).normalize();
		right = Vec3.xAxis().transform(0, globalTransform).normalize();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}