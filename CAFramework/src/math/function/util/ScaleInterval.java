package math.function.util;

import math.function.FunctionR1R1;


public class ScaleInterval extends FunctionR1R1 {
	private float s0, s1, t0, t1, factor;
	
	public ScaleInterval(float t0, float t1) {
		this (t0, t1, 0, 1);
	}
	
	public ScaleInterval(float t0, float t1, float s0, float s1) {
		super(t0, t1);
		this.t0 = t0; 
		this.t1 = t1;
		this.s0 = s0;
		this.s1 = s1;
		this.factor = (s1-s0) / (t1-t0);
	}
	
	@Override
	public float eval(float t) {
		if (t <= t0) 
			return s0;
		if (t >= t1) 
			return s1;
		
		return (factor * (t-t0) + s0);
	}
	
}
	
	
