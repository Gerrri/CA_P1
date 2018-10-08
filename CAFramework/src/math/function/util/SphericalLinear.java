package math.function.util;

import math.Mat4;
import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Quaternion;
import math.function.InterpolationQuaternion;


/**
 * Linear interpolation of quaternions through given points.
 * @author Ulla
 *
 */
public class SphericalLinear extends FunctionR1Quaternion implements InterpolationQuaternion {

	private Quaternion[] quats;
	private float[] grid;
	
	/**
	 * Given the N float values, the function interpolates these values linearly over the interval [tmin, tmax]
	 * @param angles The angles to be interpolated 
	 * @param grid 
	 */
	public SphericalLinear(Vec3[] angles, float[] grid) {
		super(grid[0], grid[grid.length-1]);
		
		Mat4 rotateMatrix;
		quats = new Quaternion[angles.length];
		
		for (int i= 0; i < angles.length; i++)
		{
			Vec3 angle = angles[i];
			rotateMatrix = Mat4.rotationZ(angle.z);
			rotateMatrix.mul(Mat4.rotationY(angle.y));
			rotateMatrix.mul(Mat4.rotationX(angle.x));
			this.quats[i] = new Quaternion (rotateMatrix);
		}	
		this.grid = grid;		
	}
	
	public SphericalLinear(Quaternion[] rots, float[] grid) {
		super(grid[0], grid[grid.length-1]);
		this.quats = rots;	
		this.grid = grid;		
	}

	/**
	 * 
	 */
	@Override
	public Quaternion eval(float t) {
		int index =  binarysearch(t);
		if (index == -1)
		{
			// return start value: does it make sense?
			return quats[0];
		}
		if (index == grid.length-1) {
			return quats[grid.length-1];
		} else {
			float deltaT = grid[index+1] - grid[index];
			float weight = (t- grid[index]) / deltaT;
			return Quaternion.slerp(weight, quats[index], quats[index+1]);
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
	
	public Quaternion[] getPoints() {
		return quats;
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
