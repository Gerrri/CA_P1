package math.function.util;

import math.Vec3;
import math.function.FunctionR1Vec3;

/**
 * Helix with two different radii , in z-direction, with center in
 * @author Stefan
 *
 */
public class Helix extends FunctionR1Vec3
{
	private final float a,b; 	// Radii of helix
	private final float h;	// Pitch of helix (length of complete turn)
	private final Vec3 center;

	/**
	 * Helix with radius 1 in x-direction, radius 1 in y-direction, pitch 1 in z-direction 
	 * and centered around origin
	 */
	public Helix() {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.a = 1.0f;
		this.b = 1.0f;
		this.h = 1.0f;
		this.center = new Vec3( 0.0f, 0.0f, 0.0f );
	}
	
	/**
	 * Helix with radius a in x-direction, radius b in y-direction, pitch h in z-direction 
	 * and centered around point p
	 * @param a
	 * @param b
	 * @param h
	 */
	public Helix( float a, float b, float h, Vec3 center ) {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.a = a;
		this.b = b;
		this.h = h;
		this.center = center;
	}

	@Override
	public Vec3 eval( float t ) {
		float x = a * (float) Math.cos(t) + center.x;
		float y = b * (float) Math.sin(t)  + center.y;
		float z = h * t  + center.z;
		return ( new Vec3( x, y, z ) );
	}
	
}
