package math.function.util;

import math.function.FunctionR1R1;


public class Offset extends FunctionR1R1 {
	private float offset;
	
	
	public Offset(float offset, float min, float max) {
		super(min+offset, max+offset);
		this.offset = offset;
	}
	
	@Override
	public float eval(float t) {
		return t - offset;
	}
	
}
	
	
