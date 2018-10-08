package math.function.util;

import math.Vec3;
import math.function.FunctionR1Vec3Differentiable;

public class SinFunc extends FunctionR1Vec3Differentiable {
	
	private final float amplitude;
	private final float freq;
	private final float phase;
	
	public SinFunc() {
		this(1, 1, 0);
	}
	
	public SinFunc(float amplitude, float freq, float phase) {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.amplitude = amplitude;
		this.freq = freq;
		this.phase = phase;
	}
		
	@Override
	public Vec3 eval(float x) {
		return new Vec3 (x,  amplitude * (float)Math.sin(freq * x + phase), 0);
    }
	
	public Vec3 diff(float x) {
		return new Vec3 (x,  amplitude * freq * (float)Math.cos(freq * x + phase),0);
	}
	
	public String toString() {
		return "sin(x)";
	}
}
