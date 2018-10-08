package math.function;

import scenegraph.AbstCamera;
import math.Quaternion;
import math.Vec3;

abstract public class FunctionR1Quaternion implements Cloneable{
	private float tmin; 	// smallest allowed parameter
	private float tmax;		// biggest allowed parameter
	
	
	/**
	 * Sets the left and right border of the interval
	 * @param tmin The left border
	 * @param tmax The right border
	 */
	public FunctionR1Quaternion(float tmin, float tmax) {
		this.tmin = tmin;
		this.tmax = tmax;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		FunctionR1Quaternion obj = (FunctionR1Quaternion) super.clone();
		return obj;
	}
	/**
	 * Sets the left border of the interval
	 * @param tmin The new left border of the interval
	 */
	public void setTMin(float tmin) {
		this.tmin = tmin;
	}
	
	/**
	 * Set the right border of the interval
	 * @param tmax The new right border of the interval
	 */
	public void setTMax(float tmax) {
		this.tmax = tmax;
	}
	
	/**
	 * Get the left border of the interval
	 * @return The left border of the interval
	 */
	public float getTMin() {
		return tmin;
	}
		
	
	/**
	 * Get the right border of the interval
	 * @return The right border of the interval
	 */
	public float getTMax() {
		return tmax;
	}
	
	/**
	 * Evalutes the function for a specific parameter t
	 * @param t The parameter for which the function is evaluated
	 * @return The value of the function for this parameter
	 */
	abstract public Quaternion eval(float t);


}
