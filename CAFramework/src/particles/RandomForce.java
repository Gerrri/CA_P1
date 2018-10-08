package particles;

import java.util.Random;

import math.Vec3;

public class RandomForce extends Force {
	float maxLength;  	
	Random rand;
	
	/**
	 * Constructor
	 */
	public RandomForce(Particle p, float maxLength) {
		super(p, null);
		this.maxLength = maxLength;
		this.rand = new Random();
	}
	
	public void eval(float time) {
		// create random point on sphere
		float a = 2.0f* ((float) Math.random()) -1.0f;
		float b = 2.0f* ((float) Math.random()) -1.0f;
		float s =a*a + b*b;
		
		while (s >= 1) {
			a = 2.0f* ((float) Math.random()) -1.0f;
			b = 2.0f* ((float) Math.random()) -1.0f;
			s = a*a + b*b;
		}
		
		float sq = (float) Math.sqrt(1-s);
		
		Vec3 f =  new Vec3(2.0f * a * sq * maxLength, 
				2.0f * b * sq * maxLength, 
				(1.0f - 2.0f * s) * maxLength);
		
		p.force.add(f);
	}
}
