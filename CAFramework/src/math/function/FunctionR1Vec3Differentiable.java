package math.function;

import math.Vec3;


abstract public class FunctionR1Vec3Differentiable extends FunctionR1Vec3 {
	
	public FunctionR1Vec3Differentiable(float tMin, float tMax) {
		super(tMin,tMax);
	}
	
	abstract public Vec3 diff(float t);
}
