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

import scenegraph.ParticleGroup;

import animation.AbstController;

/**
 * Manipulates particles of a particle system with a differential solver
 * algorithm.
 * 
 * @author Ulla
 *
 */
public class ParticleController extends AbstController {
	/**
	 * Last update time point
	 */
	private float lastTime;

	/**
	 * The particle system which is controller
	 */
	private ParticleSystem psystem;
	
	/**
	 * The number of Euler steps for each update 
	 */
	private DifferentialSolver psolver;
	
	private Emitter emitter;
	
	private ParticleGroup pgroup; 
	
	/**
	 * Constructor of class. 
	 * @param psystem
	 * @param solver
	 */
	public ParticleController(ParticleSystem psystem, DifferentialSolver solver) {
		super(AbstController.RepeatType.CLAMP, 0, Float.POSITIVE_INFINITY);
		setName("ParticleController");
		this.psystem = psystem;
		this.psolver = solver;
		this.emitter = null;
		this.pgroup = null; 
		this.lastTime = 0;
	}
	
	public ParticleController(ParticleGroup pgroup, Emitter emitter, ArrayList<Force> forces, DifferentialSolver solver) {
		super(AbstController.RepeatType.CLAMP, 0, Float.POSITIVE_INFINITY);
		setName("ParticleController");
		this.psolver = solver;
		this.emitter = emitter;
		this.pgroup = pgroup;
		this.lastTime = 0;
		ArrayList<Particle> particles = new ArrayList<Particle> ();
		this.psystem = new ParticleSystem(particles, forces);
		this.psystem.setDynamic();
	}
	
	/**
	 * Main method of the controller. Overrides AbstController.update()
	 */
	@Override
	public boolean update(float time) {
		// check if controller needs to do something
		if (!super.update(time)) {
			return false;
		}

		// map global time to local time
		float localTime = getLocalTime(time);
		
		if (localTime < localMinTime || localTime > localMaxTime)
			return false;
		
		// eliminate dead particles
		psystem.eliminateDeadParticles (localTime);
		
		// emit new particles and initialize them
		if (emitter != null)
		{
			ArrayList<Particle> newParticles = emitter.emit(lastTime, localTime);
			psystem.addParticles (newParticles);
		}
		
		// update the state of all particles
		psolver.solve( psystem, lastTime, localTime);
		
		if (pgroup != null)
			pgroup.setParticlePositions(psystem.getParticlePositions());
			
		lastTime = localTime;
		return true;
	}	
}
