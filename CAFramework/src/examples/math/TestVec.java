/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package examples.math;

import math.Vec3;


public class TestVec
{

	public static void main(String[] args) {
		
			Vec3 v1 = new Vec3 (2, 3, 4);
			Vec3 v2 = new Vec3 (2, 1, 1);
			Vec3 v3 = v1;
			
			Vec3 v4 = v1.add(v2);
			Vec3 v5 = v4.sub(v3);
			
			System.out.println (v1 + " + " + v2 + " = " + v4);
			System.out.println (v4 + " - " + v3 + " = " + v5);
	}
}
