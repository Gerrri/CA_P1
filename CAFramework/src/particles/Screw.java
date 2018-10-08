package particles;

import math.Vec3;

public class Screw extends ExternalForce {
	private Vec3 pos;
	float a;  	// nominator
	float b;	// denominator
	
	/**
	 * Constructor
	 */
	public Screw(Particle p, Vec3 pos, float a, float b) {
		super(p);
		this.pos = pos;
		this.a = a;
		this.b = b;
	}
	
	public Screw(Vec3 pos, float a, float b) {
		this(null, pos, a, b);
	}
	
	public void eval(float time) {
		this.eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		assert (p != null) : "Cannot evaluate force, because particle is undefined";
		Vec3 direction = Vec3.sub( p.pos, pos);
	
		float distance = direction.length();
		direction.normalize();
		float factor = a / (b + distance);
		
		Vec3 f = new Vec3(-direction.z, direction.y, direction.x).normalize();
		
		f = Vec3.mul(f, factor);	
		p.force.add(f);
	}
}
