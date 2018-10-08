package particles;

import math.Vec3;

public class Whirl extends Force {
	private Vec3 pos;
	float a;  	// nominator
	float b;	// denominator
	
	/**
	 * Constructor
	 */
	public Whirl(Particle p, Vec3 pos, float a, float b) {
		super(p, null);
		this.pos = pos;
		this.a = a;
		this.b = b;
	}
	
	public void eval(float time) {
		Vec3 direction = Vec3.sub( p.pos, pos);
		
		float distance = direction.length();
		direction.normalize();
		float factor = a / (b + distance);
		
		Vec3 f = new Vec3(-direction.z, direction.y, direction.x).normalize();
		
		f = Vec3.mul(f, factor);	
		p.force.add(f);
	}
}
