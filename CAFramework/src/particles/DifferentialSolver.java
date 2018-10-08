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


abstract public class DifferentialSolver
{
	
	/**
	 * The number of iteration steps for each update 
	 */
	protected int steps;
	
	public DifferentialSolver (int steps) {
		this.steps = steps;
	}

	
	abstract public void solve (ParticleSystem psystem, float tstart, float tend);
	/**
	 * Multiplies each component of an array with a factor, i.e a[i] = h * a[i]
	 * 
	 * @param a
	 *            Array which is modified
	 * @param h
	 *            Factor
	 */
	protected static void scale(float[] a, float h)
	{
		for (int i = 0; i < a.length; i++)
		{
			a[i] *= h;
		}
		return;
	}

	/**
	 * Adds two array a and b component wise and stores the result on array c
	 * 
	 * @param a
	 *            Input
	 * @param b
	 *            Input
	 * @param c
	 *            Result
	 */
	protected static void add(float[] a, float[] b, float[] c)
	{
		for (int i = 0; i < a.length; i++)
		{
			c[i] = a[i] + b[i];
		}
		return;
	}

}
