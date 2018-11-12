package Aufgabenblatt_5.Asbach_Dustin;

import math.Mat3;
import math.Vec3;
import math.function.FrenetFrame;

abstract public class FunctionR1Vec3 implements FrenetFrame, Cloneable {
	

	private float tmin; 	// smallest allowed parameter
	private float tmax;		// biggest allowed parameter
	
	
	/**
	 * Sets the left and right border of the interval
	 * @param tmin The left border
	 * @param tmax The right border
	 */
	public FunctionR1Vec3(float tmin, float tmax) {
		this.tmin = tmin;
		this.tmax = tmax;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		FunctionR1Vec3 obj = (FunctionR1Vec3) super.clone();
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
	abstract public Vec3 eval(float t);

	@Override
	public Vec3 getTangent(float t) {
		float h = 0.01f;
		Vec3 diff;
		if ((t-h) < tmin)
		{
			if ((t+h) > tmax)
				System.out.println( "Cannot determine tangent because interval is too small for numerical approximation");
			diff = Vec3.sub(eval(t+h), eval(t));
		}
		else if ((t+h) > tmax)
		{
			diff = Vec3.sub(eval(t), eval(t-h));
		}
		else
		{
			float half = h/2;
			diff = Vec3.sub(eval(t+half), eval(t-half));
		}
		diff.normalize();
		return diff;
	}

	@Override
	public Vec3 getNormal(float t) {
		float h = 0.0001f;
		Vec3 secondDiff;
		Vec3 left, right; 
		
		if ((t-h) < tmin)		// left interval border
			left = Vec3.sub(eval(tmin), Vec3.mul(getTangent(tmin), (tmin-t+h)));
		else 
			left = eval(t-h);
		
		
		if ((t+h) > tmax)		// right interval border
			right = Vec3.add(eval(tmax), Vec3.mul(getTangent(tmin), (t-tmax+h)));
		else
			right = eval(t+h);
		
		secondDiff = Vec3.add(left, right);
		secondDiff.sub(Vec3.mul(eval(t),2));
		secondDiff.normalize();
		return secondDiff;
	}

	@Override
	public Vec3 getBinormal(Vec3 tangent, Vec3 normal)
	{
		return Vec3.cross(tangent, normal);
	}

	@Override
	public Mat3 getFrenetFrame(float t) {
		Vec3 w = getTangent(t);
		Vec3 normal = getNormal(t);
		Vec3 u = Vec3.cross(normal, w); 
		u.normalize();
		if (u.y < 0)
		{
			u.negate();
		}
		Vec3 v = Vec3.cross(w, u);
		v.normalize();

		Mat3 m = new Mat3(w, u, v);
		return m;
	}

}
