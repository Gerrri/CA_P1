package particles;

import math.Vec3;

public class PointRepeller extends Force {
	private Vec3 repeller;
	float a;  	// nominator
	float b;	// denominator
	
	/**
	 * Constructor
	 */
	public PointRepeller(Particle p, Vec3 pos, float a, float b) {
		super(p, null);
		this.repeller = pos;
		this.a = a;
		this.b = b;
	}
	
	public void eval(float time) {
		Vec3 direction = Vec3.sub( p.pos, repeller);
		
		float distance = direction.length();
		direction.normalize();
		float factor = a /(b + distance);;
		
		Vec3 f = Vec3.mul(direction, factor);	
		p.force.add(f);
	}
}


