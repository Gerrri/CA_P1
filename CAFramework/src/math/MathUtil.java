/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2013 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package math;

import java.util.Random;


/**
 * Mathematical operations and constants
 */
public class MathUtil
{
	public static final float PI        = (float)Math.PI;
	public static final float PI_HALF   = (float)(Math.PI * 0.5);
	public static final float PI_DOUBLE = (float)(Math.PI * 2);
	public static final float EPS		= (float) 1.E-6;
	public static final float RADTODEG	= (float)( 180.0f / Math.PI);
	public static final float DEGTORAD	= (float)( Math.PI / 180.0f);
	private static Random randomGen = new Random();
	   
	public static float clamp( float value, float min, float max )
	{
		float clamped = min > value ? min : value;
		return max < clamped ? max : clamped;
	}
	
	
	public static int clamp( int value, int min, int max )
	{
		int clamped = min > value ? min : value;
		return max < clamped ? max : clamped;
	}

	
	public static float toRad( float degrees )
	{
		return PI * degrees / 180.0f;
	}
	
	
	public static float toDeg( float radians )
	{
		return 180.0f * radians / PI;
	}
	
	
	public static float unif()
	{
		return (float) randomGen.nextFloat();
	}
	
	
	public static float gauss()
	{
		return (float) randomGen.nextGaussian();
	}
	
	public static float unif(float d1, float d2)
	{
		float val = unif();
		return (d2-d1) * val + d1;
	}
	
	public static float gauss(float d1, float d2)
	{
		float val = gauss();
		return (d2-d1) * val + d1;
	}

    /**
     * Given 3 points in a 2d plane, this function computes if the points going from A-B-C
     * are moving counter clock wise.
     * @param p0 Point 0.
     * @param p1 Point 1.
     * @param p2 Point 2.
     * @return 1 If they are CCW, -1 if they are not CCW, 0 if p2 is between p0 and p1.
     */
    public static int counterClockwise(Vec2 p0, Vec2 p1, Vec2 p2) {
        float dx1, dx2, dy1, dy2;
        dx1 = p1.x - p0.x;
        dy1 = p1.y - p0.y;
        dx2 = p2.x - p0.x;
        dy2 = p2.y - p0.y;
        if (dx1 * dy2 > dy1 * dx2) {
            return 1;
        }
        if (dx1 * dy2 < dy1 * dx2) {
            return -1;
        }
        if ((dx1 * dx2 < 0) || (dy1 * dy2 < 0)) {
            return -1;
        }
        if ((dx1 * dx1 + dy1 * dy1) < (dx2 * dx2 + dy2 * dy2)) {
            return 1;
        }
        return 0;
    }

    /**
     * Test if a point is inside a triangle.  1 if the point is on the ccw side,
     * -1 if the point is on the cw side, and 0 if it is on neither.
     * @param t0 First point of the triangle.
     * @param t1 Second point of the triangle.
     * @param t2 Third point of the triangle.
     * @param p The point to test.
     * @return Value 1 or -1 if inside triangle, 0 otherwise.
     */
    public static int pointInsideTriangle(Vec2 t0, Vec2 t1, Vec2 t2, Vec2 p) {
        int val1 = counterClockwise(t0, t1, p);
        if (val1 == 0) {
            return 1;
        }
        int val2 = counterClockwise(t1, t2, p);
        if (val2 == 0) {
            return 1;
        }
        if (val2 != val1) {
            return 0;
        }
        int val3 = counterClockwise(t2, t0, p);
        if (val3 == 0) {
            return 1;
        }
        if (val3 != val1) {
            return 0;
        }
        return val3;
    }

    /**
     * A method that computes normal for a triangle defined by three vertices.
     * @param v1 first vertex
     * @param v2 second vertex
     * @param v3 third vertex
     * @return a normal for the face
     */
    public static Vec3 computeNormal(Vec3 v1, Vec3 v2, Vec3 v3) {
        Vec3 a1 = Vec3.sub (v1, v2);
        Vec3 a2 = Vec3.sub (v3, v2);
        return a2.cross(a1).normalize();
    }

    /**
     * Returns the determinant of a 4x4 matrix.
     */
    public static float determinant(double m00, double m01, double m02,
            double m03, double m10, double m11, double m12, double m13,
            double m20, double m21, double m22, double m23, double m30,
            double m31, double m32, double m33) {

        double det01 = m20 * m31 - m21 * m30;
        double det02 = m20 * m32 - m22 * m30;
        double det03 = m20 * m33 - m23 * m30;
        double det12 = m21 * m32 - m22 * m31;
        double det13 = m21 * m33 - m23 * m31;
        double det23 = m22 * m33 - m23 * m32;
        return (float) (m00 * (m11 * det23 - m12 * det13 + m13 * det12) - m01
                * (m10 * det23 - m12 * det03 + m13 * det02) + m02
                * (m10 * det13 - m11 * det03 + m13 * det01) - m03
                * (m10 * det12 - m11 * det02 + m12 * det01));
    }

}
