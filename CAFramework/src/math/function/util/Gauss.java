package math.function.util;


import math.Vec3;
import math.function.FunctionR1Vec3;

public class Gauss extends FunctionR1Vec3 {

	public Gauss() {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
	
	@Override
	public Vec3 eval(float z) {
		float result;
		if (z < -8.0) 
			result = 0;
		if (z > 8.0) 
			result = 1;
		
		double sum = 0.0, term = z;
		
		for (int i = 3; sum + term != sum; i += 2) {
			sum = sum + term;
			term = term * z * z / i;
		}
		
		result = (float) (0.5 + sum * phi(z));
		return (new Vec3(z, result, 0));
	}
	
	
	private double phi(double x) {
		return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
	}

	public static void main(String[] args) {
		float z = Float.parseFloat(args[0]);
		float mu = Float.parseFloat(args[1]);
		float sigma = Float.parseFloat(args[2]);
		Gauss f = new Gauss();
		Vec3 vec = f.eval(z-mu);
		System.out.println(vec.y/sigma);
	}
}
	
	
