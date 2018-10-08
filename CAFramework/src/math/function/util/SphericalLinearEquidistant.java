package math.function.util;

import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Quaternion;
import math.function.FunctionR1Vec3;
import math.function.InterpolationQuaternion;

public class SphericalLinearEquidistant extends FunctionR1Quaternion implements InterpolationQuaternion {
	private Quaternion[] points;
	private final int numPoints;
	private final float stepSize;
	
	public SphericalLinearEquidistant(Quaternion[] points, float tmin, float tmax) {
		super(tmin, tmax);
		this.points = points;
		numPoints = points.length;
		stepSize = (this.getTMax() - this.getTMin())  / (numPoints-1);
	}

	/**
	 * 
	 */
	@Override
	public Quaternion eval(float t) {
		int index = (int) ( (t-this.getTMin()) / stepSize );
		if (index == numPoints -1 ) 
		{
			return points[index];
		} 
		else 
		{
			float weight = t/stepSize-index;	// == (t - index * stepSize)/ stepSize
			return Quaternion.slerp(weight, points[index], points[index+1]);
		}
	}

	public Quaternion[] getPoints() {
		return points;
	}

	public float[] getGrid() {
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
