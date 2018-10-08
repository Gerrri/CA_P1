package particles;

import math.Vec3;

public class PointAttractor extends ExternalForce {
	private Vec3 attractor;
	float a;  	// nominator
	float b;	// denominator
	
	/**
	 * Constructor
	 */
	public PointAttractor(Particle p, Vec3 pos, float a, float b) {
		super(p);
		this.attractor = pos;
		this.a = a;
		this.b = b;
	}
	
	public PointAttractor(Vec3 pos, float a, float b) {
		this(null, pos, a, b);
	}
	
	public void eval(float time) {
		eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		assert (p != null) : "Cannot evaluate PointAttractor force, because particle is undefined";
		
		Vec3 direction = Vec3.sub(attractor, p.pos);
		
		float distance = direction.length();
		direction.normalize();
		float factor = a / (b + distance);
		
		Vec3 f = Vec3.mul(direction, factor);	
		p.force.add(f);
	}
}

