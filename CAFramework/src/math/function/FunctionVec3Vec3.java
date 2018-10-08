package math.function;

import math.Vec3;

abstract public class FunctionVec3Vec3  {

	/**
	 * Evalutes the function for a specific Vec3 parameter v
	 * @param v The parameter for which the function is evaluated
	 * @return The value of the function for this parameter
	 */
	abstract public Vec3 eval(Vec3 v);
}
