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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import renderer.AbstRenderer;
import math.Mat4;

/**
 * Class that implements a group node in the scenegraph. A group node is a
 * collection of children nodes with the effect that a transformation and render
 * state performed on a group automatically propagates its effect to all of its
 * children.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class Group extends AbstSpatial {
	/**
	 * list of children
	 */
	protected List<AbstNode> children;

	/**
	 * Constructs group with an empty children list
	 * 
	 * @param name
	 *            group's name
	 */
	public Group(String name) {
		super(name);
		children = null;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Group obj = (Group) super.clone();
		if (this.children != null)
		{
			obj.children = Collections.synchronizedList(new ArrayList<AbstNode>(1));
			ListIterator<AbstNode> childrenIterator = this.children.listIterator();
			while (childrenIterator.hasNext()) {
				AbstNode node = childrenIterator.next();
				obj.children.add((AbstNode)node.clone());
			} 
		}
			
		return obj;
	}

	/**
	 * Adds a child object to the group at the specified position
	 * 
	 * @param child
	 *            child to be added
	 * @param pos
	 *            position, where the child is to be added
	 */
	public void attachChild(AbstNode child, int pos) {
		assert child != null : "Tried to attach null-Reference to Group-Node.";

		if (children == null) {
			children = Collections.synchronizedList(new ArrayList<AbstNode>(
					1));
		}

		if (pos >= 0 && pos <= children.size())
			children.add(pos, child);
		else
			children.add(child); // add child to the end

//		if (child.getParent() != this) {
//			if (child.getParent() != null) { // child has already another parent
//				child.getParent().detachChild(child); // and must be detached
//														// from this parent
//			}
//			child.setParent(this);
//		}
		
		child.setParent(this);
		setUpdate(true);
	}

	/**
	 * Adds a child at the end of the children list
	 * 
	 * @param child
	 *            child to be added
	 */
	public void attachChild(AbstNode child) {
		attachChild(child, -1);
	}

	/**
	 * Removes child from the children list
	 * 
	 * @param child
	 *            child to be removed
	 */
	public void detachChild(AbstNode child) {
		assert child != null : "Tried to detach null-Reference from Group-Node.";
		children.remove(child);
		setUpdate(true);
	}

	/**
	 * Removes child at the specified position
	 * 
	 * @param pos
	 *            position of child in children list, which shall be removed
	 */
	public void detachChild(int pos) {
		assert pos >= 0 && pos <= children.size() : "Tried to detach element with invalid position from Group-Node.";
		if (pos >= 0 && pos <= children.size())
			children.remove(pos);
		setUpdate(true);
	}

	/**
	 * Removes child with the specified name
	 * 
	 * @param name
	 *            name of child, which shall be removed
	 */
	public void detachChild(String name) {
		assert name != null : "Tried to detach element with empty name from Group-Node.";
		assert children != null : "Tried to detach element from empty list";
		ListIterator<AbstNode> childrenIterator = children.listIterator();
		while (childrenIterator.hasNext()) {
			if (childrenIterator.next().getName() == name)
				childrenIterator.remove();
		}
		setUpdate(true);
	}

	/**
	 * Gets child at the specified position
	 * 
	 * @param pos
	 *            position of queried child
	 * @return queried child
	 */
	public AbstNode getChild(int pos) {
		assert pos >= 0 && pos <= children.size() : "Tried to get element with invalid position from Group-Node.";
		if (pos >= 0 && pos <= children.size())
			return children.get(pos);
		else
			return null;
	}

	/**
	 * Gets child with the specified name
	 * 
	 * @param name
	 *            name of queried child
	 * @return queried child
	 */
	public AbstNode getChild(String name) {
		assert name != null : "Tried to get child with empty reference from Group-Node.";

		if (children == null)
			return null;

		ListIterator<AbstNode> childrenIterator = children.listIterator();
		AbstNode child;
		while (childrenIterator.hasNext()) {
			child = childrenIterator.next();
			if (child.getName().matches(name))
				return child;
		}
		return null; // child with specified name not found
	}

	/**
	 * @return list of all children
	 */
	public List<AbstNode> getChildren() {
		return children;
	}

	public void setChildren( List<AbstNode> children) {
		this.children = children;
	}

	/**
	 * Removes all children
	 */
	public void detachAllChildren() {
		if (children != null)
			children.clear();
	}
	
	/*
	 * Search the first scenegraph node which id matches the specified id. If the
	 * group's id does not match, recursively search through the children.
	 * @param id
	 *            id of the node to be searched, can be specified as a regular expression
	 * @return matching node
	 */
	public AbstNode searchNode (String id)
	{
		AbstNode found = null;
		if (name.matches(id))
			found =  this;
		else if (children != null)
		{
			ListIterator<AbstNode> childrenIterator = children.listIterator();
			AbstNode child;

			while (childrenIterator.hasNext() && found == null) 
			{
				child = childrenIterator.next();
				found = child.searchNode(id);
			}
		}
		return found;
	}
	
	//Begin Lorenz neu
	public List<AbstNode> getNodes() {
		// TODO Auto-generated method stub
		AbstNode found = this;
		ArrayList<AbstNode> result = new ArrayList<AbstNode>();
		result.add(found);
		
		if(children != null)
		{
			ListIterator<AbstNode> childrenIterator = children.listIterator();
			AbstNode child;
			while (childrenIterator.hasNext()){
				child = childrenIterator.next();	
				result.addAll(child.getNodes());
			}
		}
		return result;
	}
	//End Lorenz neu
	
	public Group searchGroup (String id)
	{
		Group found = null;
		if (name.matches(id))
			found =  this;
		else if (children != null)
		{
			ListIterator<AbstNode> childrenIterator = children.listIterator();
			AbstNode child;

			while (childrenIterator.hasNext() && found == null) 
			{
				child = childrenIterator.next();
				if (child instanceof Group)
					found = ((Group)child).searchGroup(id);
			}
		}
		return found;
	}
	
	
	public void setWireFrame(boolean flag)
	{
		if (children == null)
			return;
		
		ListIterator<AbstNode> childrenIterator = children.listIterator();
		AbstNode child;

		while (childrenIterator.hasNext()) 
		{
			child = childrenIterator.next();
			if (child instanceof TriangleMesh)
				((TriangleMesh)child).setWireframe(flag);
			else if (child instanceof Group )
				((Group)child).setWireFrame(flag);
		}
		
	}

	/**
	 * Conserves render state by pushing it to the renderer's stack, then
	 * iterate through the children and draw them (they could put own render
	 * states on the stack); at the end remove the render state for this group
	 * again, because the whole group has been drawn.
	 * 
	 * @param renderer
	 *            renderer that will retrieve render states and draws the
	 *            objects
	 * 
	 * @see scenegraph.AbstSpatial#onDraw(renderer.AbstRenderer)
	 * 
	 */
	public void onDraw(AbstRenderer renderer) {
		renderer.pushState();

		if (children != null) {
			for (ListIterator<AbstNode> itr = children.listIterator(); itr
					.hasNext();)
				itr.next().draw(renderer);
		}

		renderer.popState();
	}

	/**
	 * Iterate through the list of children and call their updateWorldData()
	 * method (to prepare all children before they get be drawn, they might
	 * change some of their attributes)
	 */
	public boolean updateWorldData() {
		boolean updated = false;
		if (!hidden() && (children != null)) {
			for (ListIterator<AbstNode> itr = children.listIterator(); itr
					.hasNext();) {
				updated = itr.next().updateWorldData() || updated;
			}
		}
		return updated;
	}

	/**
	 * Calculate global transformation matrix (which is a concatenation of all
	 * transformation matrices in the group chain). Iterate through the list of
	 * children and call their updateWorldTransform() method with the calculated
	 * global transformation matrix. The children will concatenate their local
	 * transformation matrix with the global transformation matrix to calculate
	 * the final position and orientation in the scene.
	 * 
	 * @param world
	 *            transformation matrix from the parent element
	 * @see scenegraph.AbstSpatial#updateWorldTransform(math.Mat4)
	 */
	public void updateWorldTransform(Mat4 world) {
		updateTransform();
		globalTransform = Mat4.mul(world, localTransform);

		if (children != null) {
			for (ListIterator<AbstNode> itr = children.listIterator(); itr
					.hasNext();)
				itr.next().updateWorldTransform(globalTransform);
		}
	}
}
