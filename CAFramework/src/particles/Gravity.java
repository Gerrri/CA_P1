/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package particles;

import math.Vec3;

/**
 * Gravity force
 * @author Stefan
 *
 */
public class Gravity extends ExternalForce {
	private Vec3 g;
	
	/**
	 * Defines gravity force on particle p.
	 * @param p The particle which is influenced
	 * @param g The gravity vector, usually (0, -9.81, 0).
	 */
	public Gravity(Particle p, Vec3 g) {
		super(p);
		this.g = g;
	}
	
	public Gravity(Vec3 g) {
		super(null);
		this.g = g;
	}
	
	public void eval(float time) {
		eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		assert (p != null) : "Cannot evaluate force, because particle is undefined";
		Vec3 f = Vec3.mul(g, p.mass);	
		p.force.add(f);
	}
	
}
