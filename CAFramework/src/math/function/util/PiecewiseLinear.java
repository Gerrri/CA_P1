package math.function.util;

import math.function.FunctionR1R1;


/**
 * Linear interpolation through specified points of a curve.
 * @author Ulla
 *
 */
public class PiecewiseLinear extends FunctionR1R1  {
	
	private final float[] points;
	private final float[] grid;
	
	/**
	 * Given the N float values, the function interpolates these values linearly over the interval [tmin, tmax]
	 * @param points The interpolated values 
	 * @param grid 
	 */
	public PiecewiseLinear(float[] points, float[] grid) {
		super(grid[0], grid[grid.length-1]);
		this.points = points;
		this.grid = grid;
				
	}

	/**
	 * 
	 */
	@Override
	public float eval(float t) {
		int index =  binarysearch(t);
		
		if (index == -1)
		{
			System.err.println(t + "is outside of defined interval for piecewise linear interpolation");
			// return start value: could be any other value
			
			if (t < grid[0])
				return points[0];
			else
				return points[grid.length-1];
		}
		if (index == grid.length-1) {
			return points[grid.length-1];
		} else {
			float deltaT = grid[index+1] - grid[index];
			float weight = (t- grid[index]) / deltaT;
			return (1.0f-weight) * points[index] + weight * points[index+1];
		}
	}
	
	/**
	 * Find the index of the interval with t \in [t_i,t_i+1) 
	 * @param t
	 * @return index of interval
	 */
	private int binarysearch(float t) {
		int low = 0;
		int high = grid.length -1 ;
		
		if (t < grid[0] || t > grid[grid.length-1]) 
			return -1;

		int mid = 0;
		while (high - low > 1) {
			mid = low + (high - low) / 2;
			if  (t <= grid[mid]) { 
				high = mid; 
			} else if (t > grid[mid]) {
				low = mid;
			} 				
		}
		
		return low;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
