package math.function.util;

import math.function.FunctionR1R1;



/**
 * Linear interpolation of a real values over an interval. 
 * @author Stefan
 *
 */
public class PiecewiseLinearEquidistant extends FunctionR1R1 {
	float[] points;
	int numPoints;
	float stepsize;
	
	/**
	 * Given the N float values, the function interpolates these values linearly over the interval [tmin, tmax]
	 * @param points The interpolated values 
	 * @param tmin The left border of the interval of the function
	 * @param tmax The right border of the interval of the function
	 */
	public PiecewiseLinearEquidistant(float[] points, float tmin, float tmax) {
		super(tmin, tmax);
		this.points = points;
		numPoints = points.length;
		stepsize = (this.getTMax() - this.getTMin())  / (numPoints-1);
	}

	/**
	 * 
	 */
	public float eval(float t) {
		int index =  (int) ( (t-this.getTMin()) / stepsize );
		if (index == numPoints -1 ) {
			return points[index];
		} else {
			float weight = (t-index) / stepsize;
			return (1.0f- weight) * points[index] + weight * points[index+1];
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
