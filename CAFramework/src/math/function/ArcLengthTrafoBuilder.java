package math.function;

import math.Vec3;
import math.function.util.PiecewiseLinear;
import math.function.util.PolygonVec3;

/**
 * Given a curve and an interval, the class creates a parameter transform, such that the transformed curve is parametrized by arc length
 * @author Stefan
 *
 */
public class ArcLengthTrafoBuilder {
	private final FunctionR1Vec3 f;
	private float tmin, tmax;
	private int numSamples;

	private PiecewiseLinear trafo;
		
	public ArcLengthTrafoBuilder(FunctionR1Vec3 f, int numSamples, float tmin, float tmax) {
		this.f = f;	
		this.numSamples = numSamples;
		this.tmin = tmin;
		this.tmax = tmax;
		
		trafo = getSimpleTrafo(numSamples, tmin, tmax); 
	}
	
	public PiecewiseLinear getSimpleTrafo(int num, float tmin, float tmax) {
		float[] arcLength = new float[num];
		float[] grid = new float[num];
		
		calcArcLength(f, num, tmin, tmax, arcLength, grid);	
		
		return new PiecewiseLinear(grid, arcLength);	
	}
	
	public FunctionR1Vec3 getArcLengthParamCurve() {
		return FunctionR1Vec3Util.compose(f, trafo);
	}
	
	public void setTMin (float tmin) {
		this.tmin = tmin;
		trafo =  getSimpleTrafo(numSamples, tmin, tmax); 
	}
	
	public void setTMax (float tmax) {
		this.tmax = tmax;
		trafo =  getSimpleTrafo(numSamples, tmin, tmax); 
	}
	
	public void setnumSamples (int numSamples) {
		this.numSamples = numSamples;
		trafo =  getSimpleTrafo(numSamples, tmin, tmax); 
	}
	
	public float getArcLength() {
		return trafo.getTMax();
	}
	

	private void calcArcLength(FunctionR1Vec3 f, int numSamples, float tmin, float tmax, 
								float[] arcLength, float[] grid) {		
		float delta_t = (tmax-tmin) / (numSamples-1);
		arcLength[0] = 0.0f;
		grid[0] = tmin;
		Vec3 fOld = f.eval(tmin);
		
		for (int i = 1; i < numSamples; i++) {
			grid[i] = tmin + i * delta_t;
			Vec3 fNext = f.eval(grid[i]);
			arcLength[i] = arcLength[i-1] +  Vec3.sub(fNext, fOld).length();
			fOld = fNext;
		}	
			
//		System.out.println();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Vec3[] points = { new Vec3(-3.0f,0.0f, 0.0f), new Vec3(0.0f,0.0f, 0.0f), new Vec3(3.0f,0.0f, 0.0f)};
		float[] grid = {0.0f, 2.0f, 6.0f};
		PolygonVec3 f = new PolygonVec3(points, grid);
		
		int numSamples = 30;
		ArcLengthTrafoBuilder builder = new ArcLengthTrafoBuilder(f, numSamples, f.getTMin(), f.getTMax());
		System.out.println("Arclength with " + numSamples + ": " + builder.getArcLength() );
		
		// print values of trafo
		PiecewiseLinear trafo = builder.getSimpleTrafo(numSamples,  f.getTMin(), f.getTMax());
		
		int factor = 1;
		int testSamples = 150;
		float delta = (trafo.getTMax() - trafo.getTMin()) / (factor*(testSamples-1));
		
		float told = trafo.eval(trafo.getTMin());
		for (int i = 0; i < factor*testSamples; i++) {
			float t = trafo.getTMin() + i * delta;
			float wert = trafo.eval(t);
			System.out.println(", " + (wert-told));
			told = wert;
			
		}
		
		System.out.println();

		
	}

}
