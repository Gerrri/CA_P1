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

/**
 * Base class for state control of the render engine. A render state is not a
 * visible scene object. It controls the render engine defining the visible
 * object's appearance. Examples for such states are material parameters or the
 * active textures. Other types of states control the behavior of the renderer
 * itself.
 * 
 * Currently only material and color states are handled.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */

public abstract class AbstRenderState extends AbstNode {
	
	/**
	 * render states, currently only material and color implemented (textures,
	 * fog, etc. for future)
	 */
	public enum renderType {
		COLOR, MATERIAL, TEXTURE
	};

	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstRenderState obj = (AbstRenderState) super.clone();
		return obj;
	}
	/**
	 * Returns the type of the render state.
	 * 
	 * @return type of the render state
	 */
	public abstract renderType type();
}
