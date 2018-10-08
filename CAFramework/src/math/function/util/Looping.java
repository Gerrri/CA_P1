package math.function.util;

import math.MathUtil;
import math.Vec3;
import math.function.FunctionR1Vec3;

public class Looping extends FunctionR1Vec3 {
	private final float turnPoint; 
	private float radius; 
	
	public Looping(float radius) {
		super(0, 4 * MathUtil.PI);
		this.radius = radius;
		turnPoint = 2 * MathUtil.PI;
	}
	
	public Looping() {
		this (1);
	}
		
	@Override
	public Vec3 eval(float x) {
		if (x <= turnPoint)
			return new Vec3 ((float) -Math.cos(x)+radius, (float) Math.sin(x), 0);
		else
			return new Vec3 ((float) Math.cos(x)-radius, (float) Math.sin(x), 0);
    }
	
	public String toString() {
		return "LoopFunc(x)";
	}
}
