package Aufgabenbaltt_13.Asbach_Dustin;

import math.Vec3;
import particles.ExternalForce;
import particles.Particle;

public class steinRepeller extends ExternalForce{
	private float a;  	// nominator
	private float b;	// denominator
	Vec3 center;
	float rad;
	
	/**
	 * Constructor
	 */
	public steinRepeller(Vec3 center, float rad, float a, float b) {
		super(null);
		this.center = center;
		this.rad = rad;
		this.a = a;
		this.b = b;
	}
	
	
	public void eval(float time) {
		eval(time, p);
	}
	
	public void eval(float time, Particle p) {
		assert (p != null) : "Cannot evaluate PlaneRepeller force, because particle is undefined";
		
		float distance = Vec3.sub(p.pos, center).length()-rad;
		float factor = a /(b + distance);
		
		Vec3 f = Vec3.mul(Vec3.sub(p.pos, center).normalize(), factor);	
		p.force.add(f);
	}	
}
