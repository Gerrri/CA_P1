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

import java.util.ArrayList;

import math.MathUtil;
import math.Vec3;


/**
 * Base class for emitters in a particle system. 
 * @author Ulla
 *
 */
public class Emitter {
	/**
	 * accumulated floating point remainders
	 */
	protected float acc;
	protected float emissionRate;
	protected float lifespan;
	protected float terminationTime;
	protected float velAngle;
	protected Vec3 velDir; 
	protected float speed;
	protected float minSpeed;
	protected float maxSpeed;
	protected boolean randomSpeed;

	/**
	 * Constructor of class. 
	 * @param emissionRate
	 * @param lifespan
	 */
	public Emitter(float emissionRate, float lifespan) {
		this.emissionRate = emissionRate;
		this.lifespan = lifespan;
		this.randomSpeed = true;
		this.minSpeed = 0;
		this.maxSpeed = 0;
		this.speed = 0;
		this.velAngle = 0;
		this.terminationTime = Float.POSITIVE_INFINITY;
	}
	

	public ArrayList<Particle> emit(float tlast, float now) {

		float delta = now - tlast;
		float v = emissionRate*delta;
		acc += v;
		int numberParticles = (int) Math.floor(acc);
		acc -= numberParticles;

		return generateParticles(numberParticles, now);
	}	
	
	
	private ArrayList<Particle> generateParticles(int numberParticles, float time)
	{
		if (time < terminationTime)
		{
			ArrayList <Particle> particles = new ArrayList<Particle> (numberParticles);
			for (int i=0; i < numberParticles; i++)
			{
				Particle newParticle = new Particle();
				newParticle.pos = getInitialPosition();
				newParticle.birthtime = time;
				newParticle.lifespan = lifespan;
				if( randomSpeed)
				{
					speed = MathUtil.unif(minSpeed, maxSpeed);
				}
				
				newParticle.vel = generateVelocityVec();
				particles.add(newParticle);
			}
			return particles;
		}
		return null;
	}
	
	private Vec3 generateVelocityVec()
	{
		Vec3 velocity;
		
		//Vec3 pos = TestRepellerAttraktor.createRandomPointCone(new Vec3(0,1,0), (float) Math.tan(MathUtil.DEGTORAD * 80));
		if (velAngle > 0)
			velocity = Vec3.createRandomPointCone(velDir, velAngle);
		else
			velocity = Vec3.createRandomPointOnSphere();
		
		velocity.mul(speed);
		return velocity;
	}
	
	public Vec3 getInitialPosition()
	{
		return Vec3.createRandomVec3UnitCube();
	}
	
	
	public void setTermination(float time)
	{
		terminationTime = time;
	}
	
	public void setVelocityCone (Vec3 direction, float angle)
	{	
		this.velDir = direction;
		this.velAngle = (float) Math.tan(angle);
	}
	
	
	public void setSpeedInterval (float a, float b)
	{
		this.minSpeed = a;
		this.maxSpeed = b;
		this.speed = MathUtil.unif (minSpeed, maxSpeed);
		this.randomSpeed = true; 
	}
	
	public void setConstantSpeed (float speed)
	{
		this.speed = speed;
		this.randomSpeed = false;
		this.minSpeed = this.maxSpeed = speed;
	}
	
}
