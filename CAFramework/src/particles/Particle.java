/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Animation
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package particles;

import scenegraph.Channel;
import math.Vec3;

/**
 * Class Particle, 
 * @author Stefan
 *
 */
public class Particle {

	public float mass;
	public Vec3 pos;
	public Vec3 vel;
	public Vec3 force;
	public float lifespan;
	public float birthtime;
	
	public Particle() {
		this.mass = 1.0f;
		this.pos = new Vec3();
		this.vel = new Vec3();
		this.force = new Vec3();
		this.lifespan = Float.POSITIVE_INFINITY;
		this.birthtime = Float.NEGATIVE_INFINITY;
	}
	public Particle(float mass, Channel pos, Vec3 vel) {
		this.mass = mass;
		this.pos = (Vec3) pos.getData();
		this.vel = vel;
		this.force = new Vec3();
		this.lifespan = Float.POSITIVE_INFINITY;
		this.birthtime = Float.NEGATIVE_INFINITY;
	}
	
	public Particle(float mass, Vec3 pos, Vec3 vel, Vec3 force, 
					float lifespan, float birthtime) {
		this.mass = mass;
		this.pos = pos;
		this.vel = vel;
		this.force =  force;
		this.lifespan = lifespan;
		this.birthtime = 0.0f;
	}
	
	boolean isDead(float time) {
		if (lifespan == Float.POSITIVE_INFINITY)
			return false;
		
		return(time - birthtime < lifespan) ? false : true;			
	}
	
	public void addForce(Vec3 f) {
		force.add(f);
	}
}
