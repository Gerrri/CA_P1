package math.function.util;

import math.Vec3;
import math.function.FunctionR1Vec3;
import math.function.InterpolationVec3;


public class PolygonVec3 extends FunctionR1Vec3 implements InterpolationVec3 {


	private Vec3[] points;
	private float[] grid;
	float last = 0.0f;
	
	public PolygonVec3(Vec3[] points, float[] grid) {
		super(grid[0], grid[grid.length-1]);
		this.points = points;
		this.grid = grid;
		last = grid[0];
	}

	/**
	 * 
	 */
	@Override
	public Vec3 eval(float t) {
		int index =  binarysearch(t);
		
		if (index == -1)
		{
//			System.err.println(t + "is outside of defined interval for Polygon interpolation");
//			// return start value: could be any other value
			return points[0];
		}
		if (index == grid.length-1) {
			return points[grid.length-1];
		} 
		else {
		
			float deltaT = grid[index+1] - grid[index];
			float weight = (t- grid[index]) / deltaT;
				
			return Vec3.add(Vec3.mul(points[index], 1.0f-weight) ,  Vec3.mul(points[index+1], weight) );
		}
	}
	
	private int binarysearch(float t) {
		int low = 0;
		int high = grid.length -1 ;
		
		if (t < grid[0] || t > grid[grid.length-1]) 
			return -1;

		int mid = 0;
		while (high - low > 1) {
			mid = low + (high - low) / 2;
			if  (t <= grid[mid]) { 
				high = mid ; 
			} else if (t > grid[mid]) {
				low = mid;
			} 				
		}
		
		return low;
	}

	public Vec3[] getPoints() {
		return points;
	}

	public float[] getGrid() {
		return grid;
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
