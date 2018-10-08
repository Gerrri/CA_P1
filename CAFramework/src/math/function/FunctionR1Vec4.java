package math.function;

import math.Vec4;

public abstract class FunctionR1Vec4 {
	private float tmin; 	// smallest allowed parameter
	private float tmax;		// biggest allowed parameter
	
	
	/**
	 * Sets the left and right border of the interval
	 * @param tmin The left border
	 * @param tmax The right border
	 */
	public FunctionR1Vec4(float tmin, float tmax) {
		this.tmin = tmin;
		this.tmax = tmax;
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
	abstract public Vec4 eval(float t);



}
