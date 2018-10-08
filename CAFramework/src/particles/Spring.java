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
 * Force of elastic spring acting on a particle
 * @author Stefan
 *
 */
public class Spring extends Force {
	private Particle p2;
	float k;  	// spring constant
	float L;	// length of spring
	
	/**
	 * Constructor
	 * @param p	Particle on which force is applied
	 * @param p2 Other end of spring, particle which exerts force on p
	 * @param k Spring material constant
	 * @param L Initial length of spring
	 */
	public Spring(Particle p, Particle p2, float k, float L) {
		super(p, null);
		this.p2 = p2;
		this.k = k;
		this.L = L;
	}
	
	public void eval(float time) {
		Vec3 pMinusp2 = Vec3.sub(p.pos, p2.pos);
		float factor = -k *( pMinusp2.length() - L);
		pMinusp2.normalize();
		Vec3 f = Vec3.mul(pMinusp2, factor);	
		p.force.add(f);
	}
}
