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
import particles.ExternalForce;
import particles.Force;
import particles.Particle;
import math.Vec3;

/**
 * Gravity force
 * @author Stefan
 *
 */
public class Viscous extends ExternalForce {
	private float damping;
	
	/**
	 * Defines gravity force on particle p.
	 * @param p The particle which is influenced
	 * @param damping - damping constant
	 */
	public Viscous(Particle p, float damping) {
		super(p);
		this.damping = damping;
	}
	
	public Viscous(float damping) {
		this(null, damping);
	}
	
	public void eval(float time) {
		eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		Vec3 f = new Vec3(p.vel);
		f.mul(-damping);
		p.force.add(f);
	}
}
