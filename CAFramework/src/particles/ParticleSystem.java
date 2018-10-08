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
import java.util.Iterator;

import util.Vec3Array;

/**
 * Particle System class, responsible for updating the state of all particles
 * @author Stefan
 *
 */
public class ParticleSystem {
	
	/**
	 * Particles of the particle system
	 */
	private ArrayList<Particle> particles;
	
	/** 
	 * all forces in the particle system
	 */
	private ArrayList<Force> forces;
	
	private boolean dynamic; 
	
	
	/**
	 * Constructor
	 * @param particles
	 * @param forces
	 */
	public ParticleSystem(ArrayList<Particle> particles, ArrayList<Force> forces) {
		this.particles = particles;
		this.forces = forces;
		this.dynamic = false;
	}
	
	/**
	 * The dimension of the resulting one dimensionale ode
	 * @return dimension of differential system 
	 */
	public int getDimension() {
		return particles.size() * 6;
	}
	
	/**
	 * Returns the state, i.e. all positions and velocities of all particles in 
	 * a big float array
	 * @param state
	 */
	public void getState(float[] state) {
		for (int i = 0; i < particles.size(); i++) {
			int j = 6*i;
			state[j++] = particles.get(i).pos.x;
			state[j++] = particles.get(i).pos.y;
			state[j++] = particles.get(i).pos.z;
			state[j++] = particles.get(i).vel.x;
			state[j++] = particles.get(i).vel.y;
			state[j]   = particles.get(i).vel.z;
		}
		return;
	}
	
	/**
	 * Sets the states of all particles
	 * @param state Contains all the states of all particles. The ordering for 
	 * particle 0 is x0.x, x0.y, x0.z. v0.x, v0.y, v0.z, x1.x,
	 * and then all other particles follow in the same order
	 */
	public void setState(float[] state) {
		for (int i = 0; i < particles.size(); i++) {
			int j = 6*i;
			particles.get(i).pos.x = state[j++];
			particles.get(i).pos.y = state[j++];
			particles.get(i).pos.z = state[j++];
			particles.get(i).vel.x = state[j++];
			particles.get(i).vel.y = state[j++];
			particles.get(i).vel.z = state[j];
		}
		return;
	}
	
	/**
	 * Computes the right hand side of the Newton equation for time t.
	 * The result is put into a float array
	 * @param deriv	The value of the right hand side of the Newton equation at 
	 * time t
	 * @param t Time for which we can to compute the rhs
	 */
	public void getDerivative(float[] deriv, float t) {
		clearForces();
		updateForces(t);
		for (int i = 0; i < particles.size(); i++) {
			int j = 6*i;			
			deriv[j++] = particles.get(i).vel.x;
			deriv[j++] = particles.get(i).vel.y;
			deriv[j++] = particles.get(i).vel.z;
			
			float mass =  particles.get(i).mass;
			deriv[j++] = particles.get(i).force.x / mass;
			deriv[j++] = particles.get(i).force.y / mass;
			deriv[j]   = particles.get(i).force.z / mass;
		}
		return;
	}
	
	/**
	 * Sets all forces to zero
	 */
	public void clearForces() {
		for (Particle particle: particles) {
			particle.force.set(0, 0, 0);
		}
	}
	
	/**
	 * Updates the values of all forces at time t
	 * @param t
	 */
	public void updateForces(float t) {

		if (forces == null)
			return; 
		
		for (Force force: forces) {
			if (dynamic && force instanceof ExternalForce)
			{
				((ExternalForce)force).eval(t, particles);
			}
			else
				force.eval(t);
		}
	}
	
	public Vec3Array getParticlePositions() {
		Vec3Array positions = new Vec3Array (particles.size());
		for (int i = 0; i < particles.size(); i++) {
			positions.set(i, particles.get(i).pos);
		}
		positions.setLength(particles.size());
		return positions;
	}
	
	public void eliminateDeadParticles(float time) {
		
		Iterator<Particle> itr = particles.iterator();
		while (itr.hasNext())
		{
			if (itr.next().isDead(time))
				itr.remove();
		}
	}
	
	public void addParticles(ArrayList<Particle> newParticles) {
		assert (particles != null) : "Cannot add particles, as particle list is not initialized";
		if (newParticles != null)
		{
			particles.addAll(newParticles);
		}
	}
	
	public void setDynamic()
	{
		dynamic = true;
	}
	
}
