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

import math.MathUtil;
import math.Vec3;
import math.function.FunctionR1Vec3;


/**
 * Generates particles along a specified curve
 * @author Ulla
 *
 */
public class CurveEmitter extends Emitter {
	
	FunctionR1Vec3 f;
	/**
	 * Constructor of class. 
	 * @param emissionRate
	 * @param lifespan
	 * @param f - curve on which particles are emitted
	 */
	public CurveEmitter(float emissionRate, float lifespan, FunctionR1Vec3 f) {
		super(emissionRate, lifespan);
		this.f = f;
	}
	

	public Vec3 getStartPosition() {
		float t = MathUtil.unif(f.getTMin(), f.getTMax());
		return f.eval(t);
	}	

}
