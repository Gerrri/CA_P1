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
 * Force of damped spring acting on a particle
 * @author Stefan
 *
 */
public class DampedSpring extends Force {
	private Particle p2;
	float ks;  	// spring constant
	float kd;	// damping constant
	float L;	// length of spring
	
	/**
	 * Constructor
	 * @param p	Particle on which force is applied
	 * @param p2 Other end of spring, particle which exerts force on p
	 * @param ks Spring material constant
	 * @param kd Damping constant
	 * @param L Initial length of spring
	 */
	public DampedSpring(Particle p, Particle p2, float ks, float kd, float L) {
		super(p, null);
		this.p2 = p2;
		this.ks = ks;
		this.kd = kd;
		this.L = L;
	}
	
	public void eval(float time) {
		Vec3 pMinusp2 = Vec3.sub(p.pos, p2.pos);
		float factorSpring = -ks *( pMinusp2.length() - L);
		
		Vec3 vMinusv2 = Vec3.sub(p.vel, p2.vel);
		float factorDamping = - kd* Vec3.dot(vMinusv2, pMinusp2) / pMinusp2.length();
		
		pMinusp2.normalize();
		Vec3 f = Vec3.mul(pMinusp2, factorSpring + factorDamping);	
		p.force.add(f);
	}
}
