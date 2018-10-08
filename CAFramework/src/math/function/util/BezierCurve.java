package math.function.util;

import math.Vec3;
import math.function.FunctionR1Vec3Differentiable;

/**
 * Bézier curve B:[0,1] -> R^3 for arbitrary number of control points. Evaluation of curve
 * with Algorithm of Casteljau.
 * @author stef
 *
 */
public class BezierCurve extends FunctionR1Vec3Differentiable {
	private final Vec3[] points;		
	private final Vec3[] deltaPoints;		// control points for first derivative
	private final Vec3[] deltaPointsSecond;	// control points for second derivative

	/**
	 * Control points of the Beziercurve
	 * @param points
	 */
	public BezierCurve(Vec3[] points) {
		super(0.0f, 1.0f);

		this.points = new Vec3[points.length];
		for (int i = 0; i < points.length; i++) {
			this.points[i] = new Vec3(points[i]);
		}
		
		deltaPoints = new Vec3[points.length - 1];
		for (int i = 0; i < deltaPoints.length; i++) {
			deltaPoints[i] = Vec3.sub(points[i+1], points[i]);
		}
		
		deltaPointsSecond = new Vec3[points.length - 2];
		for (int i= 0; i < deltaPointsSecond.length; i++)
		{
			Vec3 v1 = Vec3.add(points[i+2], points[i]);
			Vec3 v2 = Vec3.mul(points[i+1], 2);
			deltaPointsSecond[i]= Vec3.sub(v1,v2);
		}
			 
	}
	


	/**
	 * Calculate value of Bézier curve for parameter t with 
	 * algorithm of deCasteljau
	 * @param p
	 * @param t
	 * @return value for parameter t
	 */
	private Vec3 casteljau(Vec3[] p, float t) {
		
		float oneMinusT = 1.0f - t;
		Vec3[] ptemp = new Vec3[p.length - 1];
		
		if (p.length <= 1)
			return p[0]; 
		
		for (int j = 0; j < p.length - 1; j++) {
			ptemp[j] = Vec3.add(Vec3.mul(p[j], oneMinusT) ,  Vec3.mul(p[j+1], t) );
		}
		
		for ( int i = 1; i < p.length - 1; i++ ) {
			for ( int j = 0; j < p.length - 1 - i; j++ ) {
				ptemp[j] = Vec3.add(Vec3.mul(ptemp[j], oneMinusT) ,  Vec3.mul(ptemp[j+1], t) );
			}
		}
		
		return ptemp[0];
		
	}

	@Override
	public Vec3 diff(float t) {
		return casteljau(deltaPoints, t).mul(points.length - 1);
	}
	
	
	public Vec3 secondDiff(float t) {
		return casteljau(deltaPointsSecond, t).mul((points.length - 1) * points.length);
	}

	@Override
	public Vec3 eval(float t) {
		return casteljau(points, t);
	}

	@Override
	public Vec3 getTangent(float t)
	{
		return diff(t).normalize();
	}

	@Override
	public Vec3 getNormal(float t)
	{
		return secondDiff(t).normalize();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Vec3[] points = { new Vec3(-3.0f,0.0f, 0.0f), new Vec3(0.0f,3.0f, 0.0f), new Vec3(3.0f,0.0f, 0.0f)};
		Vec3[] points = { new Vec3(0.0f,0.0f, 0.0f), new Vec3(-1.0f,1.0f, 0.0f), new Vec3(1.0f,1.0f, 0.0f),  new Vec3(0.0f,0.0f, 0.0f)};
		BezierCurve c = new BezierCurve(points);
		
		int samples = 100;
		float delta = 1.0f / (samples -1);
		
		for (int i = 0; i < 100; i++) {
			float t = i*delta;
			Vec3 v = c.eval(t);
			System.out.println(v.x + ", " + v.y + ", " + v.z );
		}

	}

}
