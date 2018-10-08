package particles;

import math.Vec3;

public class FloorRepeller extends ExternalForce {
	private Vec3 normal;
	private float d;
	private float a;  	// nominator
	private float b;	// denominator
	
	/**
	 * Constructor
	 */
	public FloorRepeller(Particle p, Vec3 normal, Vec3 positionVec, float a, float b) {
		super(p);
		this.normal = Vec3.normalize(normal);
		d = Vec3.dot(positionVec, normal);
		if (d < 0)
			this.normal.mul(1);
			
		this.a = a;
		this.b = b;
	}
	
	public FloorRepeller(Vec3 normal, Vec3 positionVec, float a, float b) {
		this(null, normal, positionVec, a, b);
	}
	
	public void eval(float time) {
		eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		assert (p != null) : "Cannot evaluate PlaneRepeller force, because particle is undefined";
		
		float distance = Vec3.dot (p.pos, normal) - d;
		float factor = a /(b + distance);
		Vec3 f = Vec3.mul(new Vec3 (0,0.5f, 0.5f), factor);	
		p.force.add(f);
	}	
	
}


