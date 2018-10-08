package math.function.util;

import math.Vec3;
import math.function.FunctionR1Vec3;


public class NormalFunc extends FunctionR1Vec3
{
	private double mu;
	private double sigma;

	public NormalFunc(double mu, double sigma) {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.mu = mu;
		this.sigma = sigma;
	}

	@Override
	public Vec3 eval(float x) {
		double num = Math.exp( -(x - mu) * (x - mu) / (2 * sigma * sigma));
		double den = sigma * Math.sqrt(2 * Math.PI);
		return (new Vec3(x, (float) (num / den), 0f));
	}

	
	public String toString() {
		return "N(" + mu + ", " + sigma + ")";
	}
}
