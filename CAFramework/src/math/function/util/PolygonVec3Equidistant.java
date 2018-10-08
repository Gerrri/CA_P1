package math.function.util;

import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Vec3;
import math.function.InterpolationVec3;


public class PolygonVec3Equidistant extends FunctionR1Vec3 implements InterpolationVec3 {
	private  Vec3[] points;
	private final int numPoints;
	private final float stepSize;
	
	public PolygonVec3Equidistant(Vec3[] points, float tmin, float tmax) {
		super(tmin, tmax);
		this.points = points;
		numPoints = points.length;
		stepSize = (this.getTMax() - this.getTMin())  / (numPoints-1);
	}

	/**
	 * 
	 */
	@Override
	public Vec3 eval(float t) {
		int index = (int) ( (t-this.getTMin()) / stepSize );
		if (index == numPoints -1 ) 
		{
			return points[index];
		} 
		else 
		{
			float weight = t/stepSize-index;	// == (t - index * stepSize)/ stepSize
			return Vec3.mix(points[index], points[index+1], weight);
		}
	}

	public Vec3[] getPoints() {
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
