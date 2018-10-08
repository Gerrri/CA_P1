package math.function.util;

import math.function.FunctionR1Vec3;
import math.Vec3;


public class CircleXZ extends FunctionR1Vec3
{
	private float r; 	// Radius of circle
	private float centerX;
	private float centerZ;

	public CircleXZ(float r, float x, float z) {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.r = r;
		this.centerX = x;
		this.centerZ = z;
	}

	@Override
	public Vec3 eval(float t) {
		float x = centerX + r * (float) Math.cos(t);
		float y = 0;
		float z = centerZ + r * (float) Math.sin(t);
		return (new Vec3(x, y, z));
	}
	
	public String toString() {
		return "Circle on XZ plane around (" + centerX + ", " + centerZ + ") with radius " + r;
	}
}