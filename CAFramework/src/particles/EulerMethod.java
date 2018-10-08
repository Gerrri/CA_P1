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

public class EulerMethod extends DifferentialSolver {

	public EulerMethod (int steps) {
		super(steps);
	}
	
	@Override
	public void solve (ParticleSystem psystem, float tstart, float tend) {

		int dimension = psystem.getDimension();
		float[] state = new float[dimension];
		float[] deriv = new float[dimension];
		
		float deltaTime = tend - tstart;		
		float h = deltaTime / steps;

		for (int i = 0 ; i < steps; i++) {
			float t = tstart + h*i;
			psystem.getState(state);
			psystem.getDerivative(deriv,t);
			
			scale(deriv,h);
			add(state, deriv, state);
			psystem.setState(state);
		}
	}	
}