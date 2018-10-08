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

import java.util.ArrayList;
import java.util.List;

import renderer.AbstRenderer;
import math.Mat4;

/**
 * Base class for all node objects in the scene graph. These can be spatial
 * nodes or render nodes, containing objects which contribute to the 'drawing'
 * on the screen. Drawing does not always mean that actual geometry will be
 * shown. Instead, these objects are also used to control a renderer's internal
 * state machine, e.g. to place light sources, to set the color for triangle
 * faces or to modify the active transformation. A 'hidden' flag is maintained;
 * setting this flag will deactivate the respective object in such a way that it
 * will not have any effect on the rendering process (e.g. geometric objects
 * will not be drawn).
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public abstract class AbstNode extends AbstSceneObject {

	/**
	 * Channel name to hidden attribute
	 */

	public static String HIDDEN = "Hidden";


	/**
	 * true, if object will be rendered, otherwise false
	 */
	protected PrimitiveData hide;

	/**
	 * Reference to parent object if one exists. Is NULL, if no parent object
	 * exists
	 */
	private Group parent;

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
	public AbstNode(String name) {
		this.name = name;
		update = true;

		channels = new ArrayList<Channel>();

		hide = new PrimitiveData(false);
		channels.add(new Channel(HIDDEN, hide));

	}

	/**
	 * Copy Constructor.
	 * 
	 * @param obj
	 *            object to be copied
	 */
	public AbstNode(AbstNode obj)  {
		name = obj.name;
		update = true;

		channels = new ArrayList<Channel>();

		hide = new PrimitiveData(false);
		channels.add(new Channel(HIDDEN, hide));
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstNode obj = (AbstNode) super.clone();
		obj.channels = new ArrayList<Channel>();
		obj.hide = new PrimitiveData(false);
		obj.channels.add(new Channel(HIDDEN, obj.hide));
		return obj;
	}

	/**
	 * Default Constructor assigns a default name for the object
	 */
	public AbstNode() {
		this("Scene Element");
	}

	/**
	 * Sets hidden flag (object will not be drawn)
	 * 
	 * @param hide
	 */
	public void setHide(boolean hide) {
		this.hide.b = hide;
	}

	/**
	 * Checks hidden flag
	 * 
	 * @return true, if object is hidden, otherwise false
	 */
	public boolean hidden() {
		return hide.b;
	}

	/**
	 * Sets scenegraph parent node, which must be a group node.
	 * 
	 * @param parent
	 */
	public void setParent(Group parent) {
		this.parent = parent;
	}

	/**
	 * @return parent in scenegraph
	 */
	public Group getParent() {
		return parent;
	}


	/**
	 * Updates internal data for new frame. The object will update its internal
	 * data. 
	 */
	public boolean updateWorldData() {
		return false;
	}
	
	/**
	 * The transformation matrices are only relevant for spatial nodes. The base
	 * class method here is only a dummy method, which is overridden in AbtSpatial. 
	 * 
	 * @param world
	 *            world transformation matrix
	 */
	// TODO: better way to solve this?
	public void updateWorldTransform(Mat4 world) {
	}
	

	public void updateTransform() {
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
			onDraw(renderer);
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
	 * Checks if the node matches the specified id.
	 * @param id
	 *            id of the node to be searched, can be specified as a regular expression
	 * @return this, if the id matches, otherwise null
	 */
	public AbstNode searchNode (String id)
	{
		if (name.matches(id))
			return this;
		else 
			return null;
	}
	
	public List<AbstNode> getNodes(){
		AbstNode current = this;
		ArrayList<AbstNode> result = new ArrayList<AbstNode>();
		if (current instanceof Joint){
			result.add(current);
			return result;
		} else
			return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}