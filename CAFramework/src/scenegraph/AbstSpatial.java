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
import math.Mat3;
import math.Quaternion;
import math.Vec3;
import math.Mat4;

/**
 * Base class for all spatial components in the scene graph. These can be
 * components that will be visible in the scene or components that influence the
 * rendering and view of the scene (lights, cameras). They all have in common
 * that they have a position/orientation in the 3D scene, which is determined by
 * a translation vector, rotation matrix (or vector) and scale vector (which
 * together forms the local transformation matrix).
 * 
 * Examples for these objects are geometric objects, which are drawn on screen
 * as well as light sources and cameras.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstSpatial extends AbstNode {

	/**
	 * Channel names to spatial attributes
	 */
	public static String TRANSLATION = "Translation";
	public static String SCALE = "Scale";
	public static String ROTATION = "Rotation";

	/**
	 * local translate vector (determines position of object within a scenegraph
	 * group)
	 */
	protected Vec3 localTranslate;

	/**
	 * local scale vector (determines the dimensions of an object within a
	 * scenegraph group)
	 */
	protected Vec3 localScale;

	/**
	 * local rotation matrix (determines orientation of object within a
	 * scenegraph group). The rotation matrix is used for rendering, the
	 * rotation vector is only an internal representation of the matrix.
	 */
	protected Mat4 localRotateMatrix;


	/**
	 * local Transformation. This matrix determines the position and
	 * orientation within a scenegraph group
	 */
	protected Mat4 localTransform;

	/**
	 * Transformation from local to global frame. This matrix determines the
	 * position and orientation within the complete scene
	 */
	protected Mat4 globalTransform;

	/**
	 * Constructor sets translation vector to (0, 0, 0), scale to (1, 1, 1) and
	 * rotation matrix to identity matrix (this means no translation, no scaling
	 * and no rotation of original object). Local and global transformation
	 * matrices are set to the Identity. It also creates channels to all spatial
	 * attributes.
	 * 
	 * @param name
	 *            object's name
	 */
	public AbstSpatial(String name) {
		super(name);

		localTranslate = new Vec3();
		channels.add(new Channel(TRANSLATION, localTranslate));
		localScale = new Vec3(1, 1, 1);
		channels.add(new Channel(SCALE, localScale));
		localRotateMatrix = new Mat4();
		channels.add(new Channel(ROTATION, localRotateMatrix));
		localTransform = new Mat4();
		globalTransform = new Mat4();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstSpatial obj = (AbstSpatial) super.clone();
		obj.localTranslate = new Vec3(localTranslate);
		obj.channels.add(new Channel(TRANSLATION, obj.localTranslate));
		obj.localScale = new Vec3(localScale);
		obj.channels.add(new Channel(SCALE, obj.localScale));
		obj.localRotateMatrix = new Mat4(localRotateMatrix);
		obj.channels.add(new Channel(ROTATION, obj.localRotateMatrix));

		obj.localTransform = new Mat4(localTransform);
		obj.globalTransform = new Mat4(globalTransform);
		return obj;
	}

	/**
	 * Copy Constructor.
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public AbstSpatial(AbstSpatial obj) {
		super(obj.name);

		localTranslate = new Vec3(obj.localTranslate);
		channels.add(new Channel(TRANSLATION, localTranslate));
		localScale = new Vec3(obj.localScale);
		channels.add(new Channel(SCALE, localScale));
		localRotateMatrix = new Mat4(obj.localRotateMatrix);
		channels.add(new Channel(ROTATION, obj.localRotateMatrix));

		localTransform = new Mat4();
		globalTransform = new Mat4();
	}

	/**
	 * Default Constructor assigns a default name for the object
	 */
	public AbstSpatial() {
		this("Scene Element");
	}


	/**
	 * Sets local translation vector
	 * 
	 * @param v
	 *            vector to be set
	 */
	public void setTranslation(Vec3 v) {
		localTranslate.set(v);
	}

	/**
	 * Sets local translation vector
	 * 
	 * @param x
	 *            x coordinate of translation vector
	 * @param y
	 *            y coordinate of translation vector
	 * @param z
	 *            z coordinate of translation vector
	 */
	public void setTranslation(float x, float y, float z) {
		localTranslate.set(x, y, z);
	}

	/**
	 * @return translation vector
	 */
	public Vec3 getTranslation() {
		return localTranslate;
	}

	/**
	 * Sets scaling vector
	 * 
	 * @param s
	 */
	public void setScale(Vec3 s) {
		localScale.set(s);
	}

	/**
	 * Sets local scaling vector
	 * 
	 * @param x
	 *            x coordinate of scale vector
	 * @param y
	 *            y coordinate of scale vector
	 * @param z
	 *            z coordinate of scale vector
	 */
	public void setScale(float x, float y, float z) {
		localScale.set(x, y, z);
	}

	/**
	 * @return local scale vector
	 */
	public Vec3 getScale() {
		return localScale;
	}

	/**
	 * Sets rotation vector and construct rotation matrix for the rotation
	 * angles around x-, y, z- Axis. Vector contains Euler angles. The Euler
	 * angles are extrinsic (fixed axes: x, y, z-axis, fixed order: XYZ.)
	 * 
	 * @param r
	 *            rotation vector
	 */
	public void setRotation(Vec3 r) {
		setRotation(r.x, r.y, r.z);
	}

	/**
	 * Sets rotation vector and construct rotation matrix for the rotation
	 * angles around x-, y, z- Axis. Vector contains Euler angles. The Euler
	 * angles are extrinsic (fixed axes: x, y, z-axis, fixed order: XYZ.)
	 * 
	 * @param r1
	 *            rotation angle for x Axis
	 * @param r2
	 *            rotation angle for y Axis
	 * @param r3
	 *            rotation angle for z Axis
	 */
	public void setRotation(float r1, float r2, float r3) {
		localRotateMatrix.set(Mat4.rotationZ(r3));
		localRotateMatrix.mul(Mat4.rotationY(r2));
		localRotateMatrix.mul(Mat4.rotationX(r1));
	}
	
	public void setRotation(Quaternion q) {
		Mat3 mat = q.toRotationMatrix();
		localRotateMatrix.set(mat);
	}

	/**
	 * @return local rotation matrix
	 */
	public Mat4 getRotationMatrix() {
		return localRotateMatrix;
	}
	
	/**
	 * sets local rotation matrix
	 * @param mat rotation matrix for the orientation of the object
	 */
	public void setRotationMatrix(Mat4 mat) {
		localRotateMatrix.set(mat);
	}


	/**
	 * Builds local transformation matrix by concatenating translation, scaling
	 * and rotation matrices
	 */
	public void updateTransform() {
		localTransform = new Mat4(); // Identity matrix

		// The object is 1) scaled with scaling matrix S 2) rotated with
		// rotation matrix R and 3) moved with translation matrix T.
		// The combined transformation matrix M is M = TRS (reverse order!)
		localTransform.mul(Mat4.translation(localTranslate));
		localTransform.mul(localRotateMatrix);
		localTransform.mul(Mat4.scale(localScale));

		update = true;
	}

	/**
	 * Builds the world transformation matrix (called global transformation
	 * here). This determines the final position of an object in the overall
	 * scene
	 * 
	 * @param world
	 *            world transformation matrix
	 */
	public void updateWorldTransform(Mat4 world) {
		updateTransform();
		globalTransform = Mat4.mul(world, localTransform);
	}
	

	/**
	 * @return local transformation matrix
	 */
	public Mat4 getLocalTransform() {
		return localTransform;
	}
	
	
	/**
	 * Sets the current position/orientation/scale matrix of the object in local
	 * coordinates
	 * 
	 * @param trans
	 *            matrix with position/orientation/scale to be set
	 */
	public void setLocalTransform(Mat4 trans) {
		localTranslate.set(trans.getTranslation());
		localScale.set(trans.getScale());
		localRotateMatrix.set(trans.getRotation(localScale));
	}

	/**
	 * @return world(=global) transformation matrix
	 */
	public Mat4 getGlobalTransform() {
		return globalTransform;
	}
	
	public void setGlobalTransform( Mat4 trans) {
		this.globalTransform = trans;
	}

	/**
	 * Passes this object to the renderer (if it is not hidden). In detail: 1)
	 * Requests the renderer to calculate the new transformation matrix. The
	 * transformation matrix is the multiplication result of the transformation
	 * matrix of the surrounding group within the scenegraph (this matrix lays
	 * on top of the stack) and the local transformation matrix. 2) Requests the
	 * renderer to draw the object. 3) Finally requests the renderer to throw
	 * away again the transformation matrix.
	 * 
	 * @param renderer
	 *            the renderer for the scene
	 * @return true
	 */
	public boolean draw(AbstRenderer renderer) {
		if (!hidden()) {
			renderer.pushTransform();
			updateTransform();
			renderer.multTransform(localTransform);
			onDraw(renderer);
			renderer.popTransform();
		}

		return true;
	}

	/**
	 * needs to be overridden in derived classes, as objects have different
	 * rendering needs.
	 * 
	 * @param renderer
	 */
	public abstract void onDraw(AbstRenderer renderer);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}