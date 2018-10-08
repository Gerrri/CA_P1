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
import math.Vec3;
import math.Vec4;

/**
 * Class to store and provide access to the color state attributes.
 * 
 * @author Ursula Derichs
 * @version 1.0
 */
public class ColorState extends AbstRenderState {
	/**
	 * name of channel to modify color
	 */
	public static String COLOR = "Color";

	/**
	 * Color vector (including alpha component as fourth coordinate)
	 */
	private Vec4 color;

	/**
	 * Constructs color state with white as default color.
	 */
	public ColorState() {
		this(Color.white());
	}

	/**
	 * Constructs color state with given color and sets alpha to 1 (fully
	 * opaque)
	 * 
	 * @param color
	 *            color vector (Vec3)
	 */
	public ColorState(Vec3 color) {
		this(color, 1);
	}

	/**
	 * Constructs color state with given color and alpha value
	 * 
	 * @param color
	 *            color vector (Vec3)
	 * @param alpha
	 *            alpha value
	 */
	public ColorState(Vec3 color, float alpha) {
		this.color = new Vec4(color, alpha);
		channels.add(new Channel(COLOR, this.color));
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ColorState obj = (ColorState) super.clone();
		obj.color = new Vec4(color);
		channels.add(new Channel(COLOR, obj.color));
		return obj;
	}

	/**
	 * Constructs color state with color (containing alpha value as 4.
	 * coordinate)
	 * 
	 * @param color
	 *            color vector (Vec4)
	 */
	public ColorState(Vec4 color) {
		this.color = color;
		channels.add(new Channel(COLOR, this.color));
	}

	/**
	 * Constructs color state for given rgb values, with alpha =1 as default
	 * 
	 * @param r
	 *            red component of color
	 * @param g
	 *            green component of color
	 * @param b
	 *            blue component of color
	 */
	public ColorState(float r, float g, float b) {
		this(new Vec4(r, g, b, 1f));
	}

	/**
	 * Returns the type of this render state.
	 * 
	 * @return the constant AbstRenderState::RS_COLOR
	 */
	public renderType type() {
		return renderType.COLOR;
	}

	/**
	 * Return the color stored in this color state
	 * 
	 * @return the color value of this state
	 */
	public Vec4 getColor() {
		return color;
	}

	/**
	 * Set the color of the render state.
	 * 
	 * @param color
	 *            the new color value
	 */
	public void setColor(Vec4 color) {
		this.color.set(color);
	}

	/**
	 * Passes the color state to the renderer.
	 */
	public void onDraw(AbstRenderer renderer) {
		renderer.setColorState(this);
	}
}
