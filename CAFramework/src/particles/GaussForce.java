package particles;

import java.util.Random;

import math.Vec3;

public class GaussForce extends ExternalForce {
	private Vec3 direction;
	float maxLength;  	
	Random rand;
	
	/**
	 * Constructor
	 */
	public GaussForce(Particle p, Vec3 pos, float maxLength) {
		super(p);
		this.direction = pos;
		this.maxLength = maxLength;
		this.rand = new Random();
	}
	
	public GaussForce(Vec3 pos, float maxLength) {
		this( null, pos, maxLength);
	}
	
	public void eval(float time) {
		eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		assert (p != null) : "Cannot evaluate force, because particle is undefined";
		
		float x = direction.x * (float) rand.nextGaussian();
		float y = direction.y * (float) rand.nextGaussian();
		float z = direction.z * (float) rand.nextGaussian();
		
		Vec3 v = new Vec3( x,y,z).normalize();
		float size = rand.nextFloat() * maxLength; 
		v.normalize().mul(size);
		
		p.force.add(v);
	}
}
