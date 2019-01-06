/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */
package examples.skinning;

import java.util.ListIterator;
import math.Vec3;
import scenegraph.AbstNode;
import scenegraph.Group;
import scenegraph.Line;
import scenegraph.Sphere;

/**
 * Class that implements a group node in the scenegraph. A group node is a
 * collection of children nodes with the effect that a transformation and render
 * state performed on a group automatically propagates its effect to all of its
 * children.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class Joint extends Group {

	/**
	 * Constructs group with an empty children list
	 * 
	 * @param name
	 *            group's name
	 */
	public Joint(String name) {
		super(name);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Joint obj = (Joint) super.clone();
		return obj;
	}

	
	public Joint searchJoint (String id)
	{
		Joint found = null;
		if (name.matches(id))
			found =  this;
		else if (children != null)
		{
			ListIterator<AbstNode> childrenIterator = children.listIterator();
			AbstNode child;

			while (childrenIterator.hasNext() && found == null) 
			{
				child = childrenIterator.next();
				if (child instanceof Joint)
					found = ((Joint)child).searchJoint(id);
			}
		}
		return found;
	}
	


	
	public float getMaxLength(float max)
	{
		float len = localTranslate.length();
		if (len > max)
			max = len;
		
		if (children != null)
		{
			for (int i=0; i < children.size(); i++)
			{
				AbstNode node = children.get(i);
				if (node instanceof Joint)
				{
					max = ((Joint)node).getMaxLength(max);
				}
			}
		}
		return max;
	}
	
	public void attachSkeleton()
	{
		float len = getMaxLength(0) / 30;
		attachSkeleton(len);
//		attachSkeleton(1);
	}
	
	public void attachSkeleton(float scale)
	{	
		Sphere jointPart = new Sphere();
		jointPart.setScale(scale, scale, scale);	
		attachChild(jointPart);
		
		if (children != null)
		{
			for (int i=0; i < children.size(); i++)
			{
				AbstNode node = children.get(i);
				if (node instanceof Joint)
				{
					((Joint)node).attachSkeleton(scale);
					((Joint)node).attachBones(scale);
				}
			}
		}	
	}
	
	public void attachBones(float scale)
	{
		Sphere jointPart = new Sphere();
		jointPart.setScale(scale, scale, scale);	
		attachChild(jointPart);
		
		Group parent= getParent();
		if (parent != null && parent instanceof Joint)
		{
			Joint parentJoint = (Joint) parent;
			if (! parentJoint.getName().toLowerCase().endsWith ("reference"))
			{
				Vec3[] line = new Vec3[2];
				line[0] = new Vec3();
				line[1] = getTranslation();
				Line bonePart = new Line(line);
				parentJoint.attachChild(bonePart);
			}
		}
	}
	
	public void attachBones()
	{
		attachBones(1);
	}


}
