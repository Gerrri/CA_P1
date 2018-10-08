/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2014 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * 3x3 Matrix constructor and operations
 */

public final class Mat3
{
	
	/**
	 * Matrix elements
	 */
	public float m00, m01, m02;
	public float m10, m11, m12;
	public float m20, m21, m22;

	
    /**
     * Element-wise Matrix constructor
     * @param m00
     * @param m01
     * @param m02
     * @param m10
     * @param m11
     * @param m12
     * @param m20
     * @param m21
     * @param m22
     */
    public Mat3(float m00, float m01, float m02, float m10, float m11, float m12, float m20, 
            float m21, float m22)
    {
		this.m00 = m00; this.m01 = m01; this.m02 = m02;
		this.m10 = m10; this.m11 = m11; this.m12 = m12;
		this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    /**
     * Identity matrix constructed as default
     */
    public Mat3()
    {
		this( 1.0f, 0.0f, 0.0f,
			  0.0f, 1.0f, 0.0f,
			  0.0f, 0.0f, 1.0f );
    }

    /**
     * Matrix copied from another matrix
     * @param mat matrix which is copied.
     */
    public Mat3(Mat3 mat)
    {
		this( mat.m00, mat.m01, mat.m02,
			  mat.m10, mat.m11, mat.m12,
			  mat.m20, mat.m21, mat.m22 );
    }

	/**
	 * Matrix copied from a 4x4 matrix (last colum and row is ignored)
	 * 
	 * @param mat
	 *            4x4 matrix to be copied
	 */
    public Mat3(Mat4 mat)
    {
		this( mat.m00, mat.m01, mat.m02,
			  mat.m10, mat.m11, mat.m12,
			  mat.m20, mat.m21, mat.m22 );
    }

    /**
     * Matrix build of axis vectors
     * @param xAxis x axis
     * @param yAxis y axis
     * @param zAxis y axis
     */
    public Mat3(Vec3 xAxis, Vec3 yAxis, Vec3 zAxis)
    {
		this( xAxis.x, yAxis.x, zAxis.x,
			  xAxis.y, yAxis.y, zAxis.y,
			  xAxis.z, yAxis.z, zAxis.z );
    }


	/**
	 * Generates rotation matrix for given axis and rotation angle
	 * 
	 * @param axis
	 *            rotation axis
	 * @param angle
	 *            rotation angle
	 * @return rotation matrix
	 */
    public static Mat3 rotation(Vec3 axis, float angle)
    {
        float x2 = axis.x * axis.x;
        float xy = axis.x * axis.y;
        float xz = axis.x * axis.z;
        float y2 = axis.y * axis.y;
        float yz = axis.y * axis.z;
        float z2 = axis.z * axis.z;
        float sinA = (float) Math.sin(angle);
        float cosA = (float) Math.cos(angle);
        float cosDif = 1.0F - cosA;
		return new Mat3( 
			cosA + x2 * cosDif,           xy * cosDif - axis.z * sinA,  xz * cosDif + axis.y * sinA,
			xy * cosDif + axis.z * sinA,  cosA + y2 * cosDif,           yz * cosDif - axis.x * sinA,
			xz * cosDif - axis.y * sinA,  yz * cosDif + axis.x * sinA,  cosA + z2 * cosDif           );
    }

    
	/**
	 * Calculates the euler angles of a given rotation matrix: these are three
	 * angles to rotate a vector around x-axis, y-axis and z-axis to yield the
	 * same result as the rotation matrix multiplication with that vector.
	 * 
	 * @return Vec3 containing the three euler angles
	 */
    public Vec3 getEulerAngles()
    {
    	m02 = MathUtil.clamp(m02, -1f, 1f);
        float y = (float)Math.asin(-m02);
        float x;
        float z;
        if(y < MathUtil.PI_HALF)
        {
            if(y > -MathUtil.PI_HALF)
            {
                x = (float)Math.atan2(m12, m22);
                z = (float)Math.atan2(m01, m00);
            } else
            {
        	    // no unique solution, choose z := 0
        	    x = (float)-Math.atan2(m10,m11);
        	    z = 0f;
            }
        } else
        {
        	// no unique solution, choose z := 0
        	x = (float)Math.atan2(m10, m11);
        	z = 0f;
        }
        return new Vec3(x, y, z);
    }

	/**
	 * Multiplication of this matrix with another matrix
	 * 
	 * @param mat
	 *            matrix multiplication operand
	 * @return resulting mulitplication matrix
	 */
    public Mat3 mul(Mat3 mat)
    {
		float tmp00 = this.m00,  tmp01 = this.m01,  tmp02 = this.m02;
		float tmp10 = this.m10,  tmp11 = this.m11,  tmp12 = this.m12;
		float tmp20 = this.m20,  tmp21 = this.m21,  tmp22 = this.m22;
		
		this.m00 = tmp00 * mat.m00 + tmp01 * mat.m10 + tmp02 * mat.m20;
		this.m01 = tmp00 * mat.m01 + tmp01 * mat.m11 + tmp02 * mat.m21;
		this.m02 = tmp00 * mat.m02 + tmp01 * mat.m12 + tmp02 * mat.m22;

		this.m10 = tmp10 * mat.m00 + tmp11 * mat.m10 + tmp12 * mat.m20;
		this.m11 = tmp10 * mat.m01 + tmp11 * mat.m11 + tmp12 * mat.m21;
		this.m12 = tmp10 * mat.m02 + tmp11 * mat.m12 + tmp12 * mat.m22;

		this.m20 = tmp20 * mat.m00 + tmp21 * mat.m10 + tmp22 * mat.m20;
		this.m21 = tmp20 * mat.m01 + tmp21 * mat.m11 + tmp22 * mat.m21;
		this.m22 = tmp20 * mat.m02 + tmp21 * mat.m12 + tmp22 * mat.m22;
		
		return this;
    }

	/**
	 * Multiplation of two matrices
	 * 
	 * @param left
	 *            left multiplation operand
	 * @param right
	 *            right multiplation operand
	 * @return resulting multiplication matrix
	 */
    public static Mat3 mul(Mat3 left, Mat3 right)
    {
        Mat3 result = new Mat3(left);
        return result.mul(right);
    }


    /**
     * Transpose this matrix
     * @return transposed matrix
     */
    public Mat3 transpose()
    {
		float tempVal = this.m01;
		this.m01 = this.m10;
		this.m10 = tempVal;

		tempVal  = this.m02;
		this.m02 = this.m20;
		this.m20 = tempVal;

		tempVal  = this.m12;
		this.m12 = this.m21;
		this.m21 = tempVal;
		
		return this;
    }

    /**
     * Transpose given matrix 
     * @param mat matrix to be transposed
     * @return transposed matrix
     */
    public static Mat3 transpose(Mat3 mat)
    {
        Mat3 result = new Mat3(mat);
        
        return result.transpose();
    }
    
    
    /**
     * Inverse given matrix
     * @return inversed matrix
     */
	public Mat3 inverse()
	{

		Mat4 mat = new Mat4( this );
		
	    this.m00 = mat.m11 * mat.m22  - 
	                 mat.m12 * mat.m21;

	    this.m01 =  mat.m02 * mat.m21 - 
	                  mat.m01 * mat.m22;

	    this.m02 = mat.m01 * mat.m12 -  
	                 mat.m02 * mat.m11;

	    this.m10 =  mat.m12 * mat.m20 - 
	                  mat.m10 * mat.m22;

	    this.m11 = mat.m00 * mat.m22  - 
	                 mat.m02 * mat.m20;

	    this.m12 =  mat.m02 * mat.m10 - 
	                  mat.m00 * mat.m12;

	    this.m20 = mat.m10 * mat.m21  - 
	                 mat.m11 * mat.m20;

	    this.m21 =  mat.m01 * mat.m20  - 
	                  mat.m00 * mat.m21;

	    this.m22 = mat.m00 * mat.m11 - 
	                 mat.m01 * mat.m10;


	    float det = mat.m00 * mat.m11 * mat.m22 + 
	    		mat.m01 * mat.m12 * mat.m20 + 
	    		mat.m02 * mat.m10 * mat.m21 -
	    		mat.m20 * mat.m11 * mat.m02 - 
	    		mat.m21 * mat.m12 * mat.m00 - 
	    		mat.m22 * mat.m10 * mat.m01;
	    
	    
	    if( det == 0 )
        {
	    	System.err.println( "Matrix is not invertible!" );
	    	return this;
        }
	    else
	    	return this.mul( 1.0f/det );
	}
	
	
	/**
	 * Multiplication of this matrix with a factor
	 * 
	 * @param value
	 *            multiplication factor
	 * @return resulting multiplied matrix
	 */
	public Mat3 mul( float value )
	{
		this.m00 *= value; this.m01 *= value; this.m02 *= value; 
		this.m10 *= value; this.m11 *= value; this.m12 *= value; 
		this.m20 *= value; this.m21 *= value; this.m22 *= value; 
		
		return this;
	}

	/**
	 * Places matrix in a newly created float buffer, suitable for lwjgl graphic
	 * operations
	 * 
	 * @return float buffer, containing the matrix in a serialized form.
	 */
    public FloatBuffer toFloatBuffer()
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        storeInBuffer(this, buffer);
        buffer.flip();
        return buffer;
    }

	/**
	 * Places matrix in a provided float buffer, suitable for lwjgl graphic
	 * operations
	 * 
	 * @param buffer
	 *            float buffer to store the elements
	 * @return buffer, containing the matrix in a serialized form.
	 */
    public FloatBuffer toFloatBuffer(FloatBuffer buffer)
    {
		assert buffer.capacity() == 9 : "FloatBuffer should have a capacity of 9 but has " + buffer.capacity();
		
		Mat3.storeInBuffer( this, buffer );
		buffer.flip();
		
		return buffer;
    }

	/**
	 * changing matrix to string
	 * 
	 * @return string with matrix
	 */
    public String toString()
    {
		return "[\t" + this.m00 + "\t" + this.m01 + "\t" + this.m02 + "]\n" + 
				   "[\t" + this.m10 + "\t" + this.m11 + "\t" + this.m12 + "]\n" + 
				   "[\t" + this.m20 + "\t" + this.m21 + "\t" + this.m22 + "]\n";
    }

    
	/**
	 * Serialize the given matrix into a buffer
	 * 
	 * @param mat
	 *            matrix to be serialized
	 * @param buffer
	 *            buffer to store the matrix elements
	 */
    private static void storeInBuffer(Mat3 mat, FloatBuffer buffer)
    {
		buffer.put( mat.m00 );
		buffer.put( mat.m10 );
		buffer.put( mat.m20 );
		buffer.put( mat.m01 );
		buffer.put( mat.m11 );
		buffer.put( mat.m21 );
		buffer.put( mat.m02 );
		buffer.put( mat.m12 );
		buffer.put( mat.m22 );
    }

    public static void runTest()
    {
    	
    }

}
