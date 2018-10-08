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


/**
 * Abstract class Force. 
 * Given a particle p, this object models one force on particle p.
 * The force may be dependent on other particles and time
 * @author Stefan
 *
 */
public abstract class ExternalForce extends Force {
	
	public ExternalForce (Particle p)
	{
		super(p, null);
	}
	
	/**
	 * Update force on particle p. Within this routine, the calculated
	 * force has to be added to the other forces of particle p via
	 * p.addForce().
	 * @param time
	 */
	abstract public void eval(float time, Particle particle);
	
	public void eval(float time, ArrayList<Particle> particles) {
		for (Particle particle: particles)
			this.eval(time, particle);
	}
	
}
