package math.function.util;

import math.MathUtil;
import math.Vec3;
import math.function.FunctionR1Vec3;

/**
 * Non-uniforma cubic Catmull-Rom curve. 
 * Implementation according to http://www.cemyuksel.com/research/catmullrom_param/catmullrom.pdf. 
 * @author Stefan
 *
 */
public class CatmullRomCurve extends FunctionR1Vec3 {
	private final Vec3[] points;
	private final float[] grid;
	
	/**
	 * Uniform Catmull-Rom curve. The control points points p[0], ... , p[n] are 
	 * are associated with the parameter values 0, ... , n
	 * @param points The control points of the curve
	 */
	public CatmullRomCurve(Vec3[] points) {
		super(0.0f, points.length-1);
		
		this.points = new Vec3[points.length+2];
		this.grid = new float[points.length+2];
		
		setBorders(points);
			
		// copy the rest of the points
		for (int i = 0; i < points.length; i++) {
			this.points[i+1] = new Vec3(points[i]);
		}
		
		// create grid
		for (int i = 0; i < this.grid.length; i++) {
			this.grid[i] = i-1;
		}
	}
	
	/**
	 * Non-uniform Catmull-Rom The control points points p[0], ... , p[n] are 
	 * are associated with the parameter values grid[0], ... , grid[n]
	 * @param points The control points
	 * @param grid 
	 */
	public CatmullRomCurve(Vec3[] points,float[] grid) {
		super(0.0f, points.length-1);
		
		this.points = new Vec3[points.length+2];
		this.grid = new float[points.length+2];
		
		setBorders(points);
		
		// copy the rest of the points
		for (int i = 0; i < points.length; i++) {
			this.points[i+1] = new Vec3(points[i]);
		}
		
		// copy the rest of the grid
		for (int i = 0; i < grid.length; i++) {
			this.grid[i+1] = grid[i];
		}
		this.grid[0] = grid[0] - (grid[1] - grid[0]);
		this.grid[this.grid.length-1] = grid[grid.length-1] + (grid[grid.length-1] - grid[grid.length-2]);
		
	}

	/**
	 * Catmull-Rom Curve with control over tension.
	 * alpha = 0: Uniform parameters
	 * alpha = 0.5: centripedal parametrization
	 * alpha = 1.0: chordal parametrization 
	 * @param points Control points
	 * @param alpha control parameter
	 */
	public CatmullRomCurve(Vec3[] points, float alpha) {
		// set arbitray interval borders, these will be overwritten
		super(0.0f, points.length-1);
		
		this.points = new Vec3[points.length+2];
		
		setBorders(points);
			
		// copy the rest of the points
		for (int i = 0; i < points.length; i++) {
			this.points[i+1] = new Vec3(points[i]);
		}
		
		// create the grid
		this.grid = setGrid(this.points, alpha);
		
		// Now adjust the correct borders.
		setTMin(this.grid[1]);
		setTMax(this.grid[grid.length-2]);
		
	}
	
	/**
	 * We have to add two control points, one at the beginning, one at the 
	 * end of the curve for the Catmull-Rom interpolation.
	 * If the first and the last of the original control points coincide, we assume
	 * that the curve shall be closed and chooes the appropriate two control points.
	 * If the first and the last of the original control points do not coincide, we 
	 * take the extension of the two first, resp. the two last original control points
	 * @param points The original control points
	 */
	private void setBorders(Vec3[] points) {
		// Test if first and last point coincide. In this case
		// we assume, that the curve shall be closed.
		boolean coincide = Math.abs( Vec3.dot(Vec3.sub(points[0], points[points.length-1]), Vec3.sub(points[0], points[points.length-1])) ) < MathUtil.EPS;
		if (coincide) {
			// We suppose, that the curve shall be closed
			// We use the second point as last control point
			this.points[points.length+1]= new Vec3(points[1]);
			
			// We use the second last control point as the first control point
			this.points[0] = new Vec3(points[points.length-2]);
		} else {
			// We suppose, that the curve shall be open
			// The control point p0 is in the direction from input control point p0 to p1
			this.points[0] = Vec3.add(points[0], Vec3.sub(points[0],points[1]));	
					
			// at the other end of the curve similar.
			this.points[this.points.length-1] =  Vec3.add(points[points.length-1], Vec3.sub(points[points.length-1],points[points.length-2]));
		}
	}
	
	/**
	 * Generates parameter values for given tension alpha
	 * @param points
	 * @param alpha
	 * @return grid
	 */
	private float[] setGrid(Vec3[] points, float alpha) {
		float[] grid = new float[points.length];
		
		grid[0] = 0.0f;
		
		for (int i = 1; i < grid.length; i++) {
			Vec3 p = points[i]; 
			Vec3 q = points[i-1];
			float delta = (float) Math.pow((p.x-q.x)*(p.x-q.x) + (p.y-q.y)*(p.y-q.y) + (p.z-q.z)*(p.z-q.z), alpha);
			grid[i] = grid[i-1] + delta;
		}
		
		return grid;
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
			if  (t < grid[mid]) { 
				high = mid; 
			} else if (t >= grid[mid]) {
				low = mid;
			} 				
		}
		
		return low;
	}
	
	/**
	 * Evaluation of the Non-Uniform Catmull-Rom Curve at parameter t
	 * @param t
	 * @return interpolated value for parameter t
	 */
	private Vec3 interpolate(float t) {
		// find interval with t \in [t_i, t_{i+1}]
		int i = binarysearch(t);
		
		if (i == points.length-2) {
			return points[points.length-2];
		}
		
		
		Vec3 L01 = Vec3.add(Vec3.mul(points[i-1], (grid[i]-t)/(grid[i]-grid[i-1])), Vec3.mul(points[i], (t-grid[i-1])/(grid[i]-grid[i-1])));
		Vec3 L12 = Vec3.add(Vec3.mul(points[i], (grid[i+1]-t)/(grid[i+1]-grid[i])), Vec3.mul(points[i+1], (t-grid[i])/(grid[i+1]-grid[i])));
		Vec3 L23 = Vec3.add(Vec3.mul(points[i+1], (grid[i+2]-t)/(grid[i+2]-grid[i+1])), Vec3.mul(points[i+2], (t-grid[i+1])/(grid[i+2]-grid[i+1])));
		
		Vec3 L012 = Vec3.add(Vec3.mul(L01, (grid[i+1]-t)/(grid[i+1]-grid[i-1])), Vec3.mul(L12, (t-grid[i-1])/(grid[i+1]-grid[i-1])));
		Vec3 L123 = Vec3.add(Vec3.mul(L12, (grid[i+2]-t)/(grid[i+2]-grid[i])), Vec3.mul(L23, (t-grid[i])/(grid[i+2]-grid[i])));
		
		Vec3 C12 =  Vec3.add(Vec3.mul(L012, (grid[i+1]-t)/(grid[i+1]-grid[i])), Vec3.mul(L123, (t-grid[i])/(grid[i+1]-grid[i])));
		
		return C12;
		
	}
	


	@Override
	public Vec3 eval(float t) {
		return interpolate(t);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Vec3[] points = {new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 1.0f, 0.0f), new Vec3(1.0f, 1.0f, 0.0f), new Vec3(1.0f, 0.0f, 0.0f)};
		//Vec3[] points = {new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 1.0f, 0.0f), new Vec3(1.0f, 1.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f)};
		Vec3[] points = {new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 1.0f, 0.0f), new Vec3(1.0f, 0.5f, 0.0f), new Vec3(0.7f, 0.5f, 0.0f), new Vec3(1.0f, 0.0f, 0.0f)};
		//Vec3[] points = {new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1.0f, 0.0f, 0.0f)};
		
		//float[] grid = {0.0f, 2.0f, 2.5f, 3.0f};
		
		//CatmullRomCurve curve = new CatmullRomCurve(points);
		//CatmullRomCurve curve = new CatmullRomCurve(points,grid);
		CatmullRomCurve curve = new CatmullRomCurve(points, 0.1f);
		
		int samples = 100;
		float delta = (curve.getTMax()-curve.getTMin()) / (samples - 1);
		
		for (int i = 0; i < samples; i++ ) {
			float t = curve.getTMin() + i * delta;
			Vec3 v = curve.eval(t);
			System.out.println(v.x + "," + v.y);
		}
	}
}
