/* 
 * Cologne University of Applied Sciences
 * Institute for Media and Imaging Technologies - Computer Graphics Group
 *
 * Copyright (c) 2012 Cologne University of Applied Sciences. All rights reserved.
 *
 * This source code is property of the Cologne University of Applied Sciences. Any redistribution
 * and use in source and binary forms, with or without modification, requires explicit permission. 
 */

package math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static math.Vec3.*;

/**
 * 4x4 Matrix operations
 */
public final class Mat4
{
	/**
	 * Matrix elements
	 */
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;
	
	
	/**
	 * Element-wise Matrix constructor
	 * 
	 * @param m00
	 * @param m01
	 * @param m02
	 * @param m03
	 * @param m10
	 * @param m11
	 * @param m12
	 * @param m13
	 * @param m20
	 * @param m21
	 * @param m22
	 * @param m23
	 * @param m30
	 * @param m31
	 * @param m32
	 * @param m33
	 */
	public Mat4( float m00, float m01, float m02, float m03,
				 float m10, float m11, float m12, float m13,
				 float m20, float m21, float m22, float m23,
				 float m30, float m31, float m32, float m33 )
	{
		this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
		this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
		this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
		this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
	}
	
	
	/**
	 * Identity matrix constructed as default
	 */
	public Mat4()
	{
		this( 1.0f, 0.0f, 0.0f, 0.0f,
			  0.0f, 1.0f, 0.0f, 0.0f,
			  0.0f, 0.0f, 1.0f, 0.0f,
			  0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	
	/**
	 * Extends 3x3 matrix to 4x4 matrix (with a fourth homogeneous coordinate)
	 * 
	 * @param mat
	 *            3x3 matrix to be extended
	 */
	public Mat4( Mat3 mat )
	{
		this( mat.m00, mat.m01, mat.m02, 0.0f,
			  mat.m10, mat.m11, mat.m12, 0.0f,
			  mat.m20, mat.m21, mat.m22, 0.0f,
			  0.0f,    0.0f,    0.0f,    1.0f );
	}
	
	
	/**
	 * Creates a 4x4 matrix by copying another 4x4 matrix
	 * 
	 * @param mat
	 *            matrix to be copied
	 */
	public Mat4( Mat4 mat )
	{
		this( mat.m00, mat.m01, mat.m02, mat.m03,
			  mat.m10, mat.m11, mat.m12, mat.m13,
			  mat.m20, mat.m21, mat.m22, mat.m23,
			  mat.m30, mat.m31, mat.m32, mat.m33 );
	}
	
	
	/**
	 * Matrix build of axis vectors and homogeneous coordinates
	 * @param xAxis		x axis
	 * @param yAxis 	y axis
	 * @param zAxis		z axis
	 */
	public Mat4( Vec3 xAxis, Vec3 yAxis, Vec3 zAxis)
	{
		this( xAxis.x, yAxis.x, zAxis.x, 0.0f,
			  xAxis.y, yAxis.y, zAxis.y, 0.0f,
			  xAxis.z, yAxis.z, zAxis.z, 0.0f,
			  0.0f,    0.0f,    0.0f,    1.0f );
	}
	
	/**
	 * Creates matrix of homogeneous coordinates and combines it with translation vector
	 * @param xAxis		x axis
	 * @param yAxis 	y axis
	 * @param zAxis		z axis
	 */
	public Mat4( Vec3 xAxis, Vec3 yAxis, Vec3 zAxis, Vec3 translation )
	{
		this( xAxis.x, yAxis.x, zAxis.x, translation.x,
			  xAxis.y, yAxis.y, zAxis.y, translation.y,
			  xAxis.z, yAxis.z, zAxis.z, translation.z,
			  0.0f,    0.0f,    0.0f,    1.0f );
	}
	
	
	/**
	 * Copies values of another 4x4 matrix into this matrix
	 * @param mat
	 * @return this (for chaining methods)
	 */
	public Mat4 set( Mat4 mat )
	{
		this.m00 = mat.m00; this.m01 = mat.m01; this.m02 = mat.m02; this.m03 = mat.m03;
		this.m10 = mat.m10; this.m11 = mat.m11; this.m12 = mat.m12; this.m13 = mat.m13;
		this.m20 = mat.m20; this.m21 = mat.m21; this.m22 = mat.m22; this.m23 = mat.m23;
		this.m30 = mat.m30; this.m31 = mat.m31; this.m32 = mat.m32; this.m33 = mat.m33;
		
		return this;
	}
	
	/**
	 * Copies values of a 3x3 matrix into this matrix and inserts fourth homogeneous coordinate
	 * @param mat
	 * @return this (for method chaining)
	 */
	public Mat4 set( Mat3 mat )
	{
		this.m00 = mat.m00; this.m01 = mat.m01; this.m02 = mat.m02; this.m03 = 0.0f;
		this.m10 = mat.m10; this.m11 = mat.m11; this.m12 = mat.m12; this.m13 = 0.0f;
		this.m20 = mat.m20; this.m21 = mat.m21; this.m22 = mat.m22; this.m23 = 0.0f;
		this.m30 = 0.0f;    this.m31 = 0.0f;    this.m32 = 0.0f;    this.m33 = 1.0f;
		
		return this;
	}
	
	
	
	/**
	 * Builds rotation matrix for given rotation axis and angle (in radians)
	 * @param axis rotation axis
	 * @param radians rotation angle in radians 
	 * @return rotation matrix
	 */
	public static Mat4 rotation( Vec3 axis, float radians )
	{
		float x2 = axis.x * axis.x;
		float xy = axis.x * axis.y;
		float xz = axis.x * axis.z;
		float y2 = axis.y * axis.y;
		float yz = axis.y * axis.z;
		float z2 = axis.z * axis.z;
		float sinA = (float) Math.sin( radians );
		float cosA = (float) Math.cos( radians );
		float cosDif = 1.0f - cosA;
		
		return new Mat4( cosA + x2 * cosDif,           xy * cosDif - axis.z * sinA,  xz * cosDif + axis.y * sinA,  0.0f,
						 xy * cosDif + axis.z * sinA,  cosA + y2 * cosDif,           yz * cosDif - axis.x * sinA,  0.0f,
						 xz * cosDif - axis.y * sinA,  yz * cosDif + axis.x * sinA,  cosA + z2 * cosDif,           0.0f,
						 0.0f,                         0.0f,                         0.0f,                         1.0f );
	}
	
	/**
	 * Builds rotation matrix for a rotation around the x Axis 
	 * @param radians rotation angle (in radians)
	 * @return rotation matrix
	 */
	public static Mat4 rotationX( float radians )
	{    
		float cosA = (float) Math.cos( radians );
		float sinA = (float) Math.sin( radians );

		return new Mat4( 1.0f, 	0.0f,	0.0f,	0.0f,
						 0.0f,	cosA,	-sinA,	0.0f,
						 0.0f,	sinA,	cosA,	0.0f,
						 0.0f,	0.0f,	0.0f,	1.0f );
	}
	
	/**
	 * Builds rotation matrix for a rotation around the y Axis 
	 * @param radians rotation angle (in radians)
	 * @return rotation matrix
	 */
	public static Mat4 rotationY( float radians )
	{    
		float cosA = (float) Math.cos( radians );
		float sinA = (float) Math.sin( radians );

		return new Mat4( cosA, 	0.0f,	sinA,	0.0f,
						 0.0f,	1.0f,	0.0f,	0.0f,
						 -sinA,	0.0f,	cosA,	0.0f,
						 0.0f,	0.0f,	0.0f,	1.0f );
	}
	
	/**
	 * Builds rotation matrix for a rotation around the z Axis 
	 * @param radians rotation angle (in radians)
	 * @return rotation matrix
	 */
	public static Mat4 rotationZ( float radians )
	{    
		float cosA = (float) Math.cos( radians );
		float sinA = (float) Math.sin( radians );

		return new Mat4( cosA, 	-sinA,	0.0f,	0.0f,
						 sinA,	cosA,	0.0f,	0.0f,
						 0.0f,	0.0f,	1.0f,	0.0f,
						 0.0f,	0.0f,	0.0f,	1.0f );
	}
	
	
	/**
	 * Creates translation matrix 
	 * @param translation translation vector
	 * @return translation matrix
	 */
	public static Mat4 translation( Vec3 translation )
	{
		return new Mat4( 1.0f, 0.0f, 0.0f, translation.x,
						 0.0f, 1.0f, 0.0f, translation.y,
						 0.0f, 0.0f, 1.0f, translation.z,
						 0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	/**
	 * Creates translation matrix
	 * 
	 * @param x
	 *            x-Axis translation
	 * @param y
	 *            y-Axis translation
	 * @param z
	 *            z-Axis translation
	 * @return translation matrix
	 */
	public static Mat4 translation( float x, float y, float z )
	{
		return new Mat4( 1.0f, 0.0f, 0.0f, x,
						 0.0f, 1.0f, 0.0f, y,
						 0.0f, 0.0f, 1.0f, z,
						 0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	public Vec3 getTranslation()
	{
		return new Vec3( this.m03, this.m13, this.m23 );
	}
	
	public Vec3 getScale()
	{
		float sx = (float) Math.sqrt(m00*m00 + m01*m01+m02*m02);
		float sy = (float) Math.sqrt(m10*m10 + m11*m11+m12*m12);
		float sz = (float) Math.sqrt(m20*m20 + m21*m21+m22*m22);
		return new Vec3( sx, sy, sz );
	}
	
	public Mat4 getRotation(Vec3 scale)
	{
		return new Mat4( m00/scale.x,  m01/scale.x,  m02/scale.x,  0.0f,
				         m10/scale.y,  m11/scale.y,  m12/scale.y,  0.0f,
				         m20/scale.z,  m21/scale.z,  m22/scale.z,  0.0f,
				         0.0f,         0.0f,         0.0f,         1.0f );
	}
	
	/**
	 * Generates scaling matrix for same scaling in all directions (isotropic
	 * scaling)
	 * 
	 * @param scale
	 *            scale factor
	 * @return scaling matrix
	 */
	public static Mat4 scale( float scale )
	{
		return new Mat4( scale, 0.0f,  0.0f,  0.0f,
						 0.0f,  scale, 0.0f,  0.0f,
						 0.0f,  0.0f,  scale, 0.0f,
						 0.0f,  0.0f,  0.0f,  1.0f );
	}
	
	/**
	 * Generates scaling matrix for a given scaling vector (which can have
	 * different scaling factors for x,y,z axes)
	 * 
	 * @param scale
	 *            scale vector
	 * @return scaling matrix
	 */
	public static Mat4 scale( Vec3 scale )
	{
		return new Mat4( scale.x, 0.0f,    0.0f,    0.0f,
						 0.0f,    scale.y, 0.0f,    0.0f,
						 0.0f,    0.0f,    scale.z, 0.0f,
						 0.0f,    0.0f,    0.0f,    1.0f );
	}
	
	/**
	 * Generates scaling matrix for scaling factors of the x, y, z axes
	 * 
	 * @param x
	 *            x-Axis scaling factor
	 * @param y
	 *            y-Axis scaling factor
	 * @param z
	 *            z-Axis scaling factor
	 * @return scaling matrix
	 */
	public static Mat4 scale( float x, float y, float z )
	{
		return new Mat4( x,    0.0f, 0.0f, 0.0f,
						 0.0f, y,    0.0f, 0.0f,
						 0.0f, 0.0f, z,    0.0f,
						 0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	
	/**
	 * Generates lookAt matrix for camera view.
	 * 
	 * @param eye
	 *            location of the camera
	 * @param focus
	 *            point of interest (focus of the camera)
	 * @param up
	 *            up direction of the camera
	 * @return lookAt matrix
	 */
	public static Mat4 lookAt( Vec3 eye, Vec3 focus, Vec3 up )
	{
		Vec3 viewUp    = normalize( up );
		Vec3 direction = sub( focus, eye ).normalize();
		Vec3 viewRight = cross( direction, viewUp ).normalize();
		
        viewUp = cross( viewRight, direction ).normalize();
        
        Vec3 translation = new Vec3( -dot(viewRight, eye), -dot(viewUp, eye), dot(direction, eye) );
		Mat4 view        = new Mat4( viewRight, viewUp, direction.mul(-1.0f), new Vec3() );
        
        view.transpose();
        
        view.m03 = translation.x;
        view.m13 = translation.y;
        view.m23 = translation.z;
        
        return view;
	}
	
	
	/**
	 * Creates perspective (projection) matrix.
	 * 
	 * @param verticalFov
	 *            vertical field of view angle in degrees
	 * @param width
	 *            width of projected image
	 * @param height
	 *            height of projected image
	 * @param zNear
	 *            near plane of the view frustum
	 * @param zFar
	 *            far plane of the view frustum
	 * @return perspective matrix
	 */
	public static Mat4 perspective( float verticalFov, float width, float height, float zNear, float zFar)
	{
		float ymax, xmax;
	    float temp, temp2, temp3, temp4;
	    
	    ymax = zNear * (float)Math.tan( verticalFov * Math.PI / 360.0);
	    xmax = ymax * width / height;
	    
	    float left, right, bottom, top;
	    left   = -xmax;
	    right  = xmax;
	    bottom = -ymax;
	    top    = ymax;

	    temp  = 2.0f  * zNear;
	    temp2 = right - left;
	    temp3 = top   - bottom;
	    temp4 = zFar  - zNear;
	    
	    Mat4 proj = new Mat4();
	    
	    proj.m00 = temp / temp2;
	    proj.m11 = temp / temp3;
	    proj.m02 = (right + left) / temp2;
	    proj.m12 = (top + bottom) / temp3;
	    proj.m22 = (-zFar - zNear) / temp4;
	    proj.m32 = -1.0f;
	    proj.m23 = (-temp * zFar) / temp4;
	    proj.m33 = 0.0f;
		
		return proj;
	}
	
	/**
	 * Creates perspective (projection) matrix
	 * 
	 * @param left		left corner of the projection plane
	 * @param right		right corner of the projection plane
	 * @param top		top corner of the projection plance
	 * @param bottom	bottom corner of the projection plane
	 * @param near		near plane of the view frustum
	 * @param far       far plane of the view frustum
	 * @return perspective projection matric
	 */
	public static Mat4 perspective( float left, float right, float top, float bottom, float near, float far )
	{
		float xDiff = right - left;
		float yDiff = top - bottom;
		float zDiff = far - near;
		float n2    = near * 2.0f;
		
		return new Mat4( n2/xDiff, 0.0f,     (right+left)/xDiff,  0.0f,
						 0.0f,     n2/yDiff, (top+bottom)/yDiff,  0.0f,
						 0.0f,     0.0f,     -(far+near)/zDiff,  -2.0f*far*near/zDiff,
						 0.0f,     0.0f,     -1.0f,               0.0f );
	}
	
	
	/**
	 * Create orthographic (projection) matrix
	 * 
	 * @param left		left corner of the projection plane
	 * @param right		right corner of the projection plane
	 * @param top		top corner of the projection plance
	 * @param bottom	bottom corner of the projection plane
	 * @param near		near plane of the view frustum
	 * @param far       far plane of the view frustum
	 * @return orthographic projection matrix
	 */
	public static Mat4 orthographic( float left, float right, float top, float bottom, float near, float far )
	{
		float xDiff = right - left;
		float yDiff = top - bottom;
		float zDiff = far - near;
		
		return new Mat4( 2.0f/xDiff, 0.0f,       0.0f,        -(right+left)/xDiff,
						 0.0f,       2.0f/yDiff, 0.0f,        -(top+bottom)/yDiff,
						 0.0f,       0.0f,       -2.0f/zDiff, -(far+near)/zDiff,
						 0.0f,       0.0f,        0.0f,       1.0f );
	}
	
	/**
	 * Multiplication of this matrix with a factor
	 * 
	 * @param value
	 *            multiplication factor
	 * @return resulting multiplied matrix
	 */
	public Mat4 mul( float value )
	{
		this.m00 *= value; this.m01 *= value; this.m02 *= value; this.m03 *= value;
		this.m10 *= value; this.m11 *= value; this.m12 *= value; this.m13 *= value;
		this.m20 *= value; this.m21 *= value; this.m22 *= value; this.m23 *= value;
		this.m30 *= value; this.m31 *= value; this.m32 *= value; this.m33 *= value;
		
		return this;
	}
	
	
	/**
	 * Multiplication of this matrix with another matrix
	 * 
	 * @param rightMatrix
	 *            right matrix multiplication operand
	 * @return resulting multiplication matrix
	 */
	public Mat4 mul( Mat4 rightMatrix )
	{
		float tmp00 = this.m00,  tmp01 = this.m01,  tmp02 = this.m02,  tmp03 = this.m03;
		float tmp10 = this.m10,  tmp11 = this.m11,  tmp12 = this.m12,  tmp13 = this.m13;
		float tmp20 = this.m20,  tmp21 = this.m21,  tmp22 = this.m22,  tmp23 = this.m23;
		float tmp30 = this.m30,  tmp31 = this.m31,  tmp32 = this.m32,  tmp33 = this.m33;
		
		this.m00 = tmp00 * rightMatrix.m00 + tmp01 * rightMatrix.m10 + tmp02 * rightMatrix.m20 + tmp03 * rightMatrix.m30;
		this.m01 = tmp00 * rightMatrix.m01 + tmp01 * rightMatrix.m11 + tmp02 * rightMatrix.m21 + tmp03 * rightMatrix.m31;
		this.m02 = tmp00 * rightMatrix.m02 + tmp01 * rightMatrix.m12 + tmp02 * rightMatrix.m22 + tmp03 * rightMatrix.m32;
		this.m03 = tmp00 * rightMatrix.m03 + tmp01 * rightMatrix.m13 + tmp02 * rightMatrix.m23 + tmp03 * rightMatrix.m33;

		this.m10 = tmp10 * rightMatrix.m00 + tmp11 * rightMatrix.m10 + tmp12 * rightMatrix.m20 + tmp13 * rightMatrix.m30;
		this.m11 = tmp10 * rightMatrix.m01 + tmp11 * rightMatrix.m11 + tmp12 * rightMatrix.m21 + tmp13 * rightMatrix.m31;
		this.m12 = tmp10 * rightMatrix.m02 + tmp11 * rightMatrix.m12 + tmp12 * rightMatrix.m22 + tmp13 * rightMatrix.m32;
		this.m13 = tmp10 * rightMatrix.m03 + tmp11 * rightMatrix.m13 + tmp12 * rightMatrix.m23 + tmp13 * rightMatrix.m33;

		this.m20 = tmp20 * rightMatrix.m00 + tmp21 * rightMatrix.m10 + tmp22 * rightMatrix.m20 + tmp23 * rightMatrix.m30;
		this.m21 = tmp20 * rightMatrix.m01 + tmp21 * rightMatrix.m11 + tmp22 * rightMatrix.m21 + tmp23 * rightMatrix.m31;
		this.m22 = tmp20 * rightMatrix.m02 + tmp21 * rightMatrix.m12 + tmp22 * rightMatrix.m22 + tmp23 * rightMatrix.m32;
		this.m23 = tmp20 * rightMatrix.m03 + tmp21 * rightMatrix.m13 + tmp22 * rightMatrix.m23 + tmp23 * rightMatrix.m33;

		this.m30 = tmp30 * rightMatrix.m00 + tmp31 * rightMatrix.m10 + tmp32 * rightMatrix.m20 + tmp33 * rightMatrix.m30;
		this.m31 = tmp30 * rightMatrix.m01 + tmp31 * rightMatrix.m11 + tmp32 * rightMatrix.m21 + tmp33 * rightMatrix.m31;
		this.m32 = tmp30 * rightMatrix.m02 + tmp31 * rightMatrix.m12 + tmp32 * rightMatrix.m22 + tmp33 * rightMatrix.m32;
		this.m33 = tmp30 * rightMatrix.m03 + tmp31 * rightMatrix.m13 + tmp32 * rightMatrix.m23 + tmp33 * rightMatrix.m33;
		
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
	public static Mat4 mul( Mat4 left, Mat4 right )
	{
		Mat4 result = new Mat4( left );
		
		return result.mul( right );
	}
	
    /**
     * Transpose this matrix
     * @return transposed matrix
     */
	public Mat4 transpose()
	{
		float tempVal = this.m01;
		this.m01 = this.m10;
		this.m10 = tempVal;

		tempVal  = this.m02;
		this.m02 = this.m20;
		this.m20 = tempVal;

		tempVal  = this.m03;
		this.m03 = this.m30;
		this.m30 = tempVal;

		tempVal  = this.m12;
		this.m12 = this.m21;
		this.m21 = tempVal;

		tempVal  = this.m13;
		this.m13 = this.m31;
		this.m31 = tempVal;

		tempVal  = this.m23;
		this.m23 = this.m32;
		this.m32 = tempVal;
		
		return this;
	}
	
    /**
     * Transpose given matrix
     * @return matrix to be transposed
     */
	public static Mat4 transpose( Mat4 mat )
	{
		Mat4 result = new Mat4( mat );
		
		return result.transpose();
	}
	
	
	/**
     * Inverse this matrix
     * @return inversed matrix
     */
	public Mat4 inverse()
	{
		Mat4 temp = new Mat4( this );

	    this.m00 = temp.m11 * temp.m22 * temp.m33 - 
	               temp.m11 * temp.m32 * temp.m23 - 
	               temp.m12 * temp.m21 * temp.m33 + 
	               temp.m12 * temp.m31 * temp.m23 +
	               temp.m13 * temp.m21 * temp.m32 - 
	               temp.m13 * temp.m31 * temp.m22;

	    this.m01 = -temp.m01 * temp.m22 * temp.m33 + 
	                temp.m01 * temp.m32 * temp.m23 + 
	                temp.m02 * temp.m21 * temp.m33 - 
	                temp.m02 * temp.m31 * temp.m23 - 
	                temp.m03 * temp.m21 * temp.m32 + 
	                temp.m03 * temp.m31 * temp.m22;

	    this.m02 = temp.m01 * temp.m12 * temp.m33 - 
	               temp.m01 * temp.m32 * temp.m13 - 
	               temp.m02 * temp.m11 * temp.m33 + 
	               temp.m02 * temp.m31 * temp.m13 + 
	               temp.m03 * temp.m11 * temp.m32 - 
	               temp.m03 * temp.m31 * temp.m12;

	    this.m03 = -temp.m01 * temp.m12 * temp.m23 + 
	                temp.m01 * temp.m22 * temp.m13 +
	                temp.m02 * temp.m11 * temp.m23 - 
	                temp.m02 * temp.m21 * temp.m13 - 
	                temp.m03 * temp.m11 * temp.m22 + 
	                temp.m03 * temp.m21 * temp.m12;

	    this.m10 = -temp.m10 * temp.m22 * temp.m33 + 
	                temp.m10 * temp.m32 * temp.m23 + 
	                temp.m12 * temp.m20 * temp.m33 - 
	                temp.m12 * temp.m30 * temp.m23 - 
	                temp.m13 * temp.m20 * temp.m32 + 
	                temp.m13 * temp.m30 * temp.m22;

	    this.m11 = temp.m00 * temp.m22 * temp.m33 - 
	               temp.m00 * temp.m32 * temp.m23 - 
	               temp.m02 * temp.m20 * temp.m33 + 
	               temp.m02 * temp.m30 * temp.m23 + 
	               temp.m03 * temp.m20 * temp.m32 - 
	               temp.m03 * temp.m30 * temp.m22;

	    this.m12 = -temp.m00 * temp.m12 * temp.m33 + 
	                temp.m00 * temp.m32 * temp.m13 + 
	                temp.m02 * temp.m10 * temp.m33 - 
	                temp.m02 * temp.m30 * temp.m13 - 
	                temp.m03 * temp.m10 * temp.m32 + 
	                temp.m03 * temp.m30 * temp.m12;

	    this.m13 = temp.m00 * temp.m12 * temp.m23 - 
	               temp.m00 * temp.m22 * temp.m13 - 
	               temp.m02 * temp.m10 * temp.m23 + 
	               temp.m02 * temp.m20 * temp.m13 + 
	               temp.m03 * temp.m10 * temp.m22 - 
	               temp.m03 * temp.m20 * temp.m12;

	    this.m20 = temp.m10 * temp.m21 * temp.m33 - 
	               temp.m10 * temp.m31 * temp.m23 - 
	               temp.m11 * temp.m20 * temp.m33 + 
	               temp.m11 * temp.m30 * temp.m23 + 
	               temp.m13 * temp.m20 * temp.m31 - 
	               temp.m13 * temp.m30 * temp.m21;

	    this.m21 = -temp.m00 * temp.m21 * temp.m33 + 
	                temp.m00 * temp.m31 * temp.m23 + 
	                temp.m01 * temp.m20 * temp.m33 - 
	                temp.m01 * temp.m30 * temp.m23 - 
	                temp.m03 * temp.m20 * temp.m31 + 
	                temp.m03 * temp.m30 * temp.m21;

	    this.m22 = temp.m00 * temp.m11 * temp.m33 - 
	               temp.m00 * temp.m31 * temp.m13 - 
	               temp.m01 * temp.m10 * temp.m33 + 
	               temp.m01 * temp.m30 * temp.m13 + 
	               temp.m03 * temp.m10 * temp.m31 - 
	               temp.m03 * temp.m30 * temp.m11;

	    this.m23 = -temp.m00 * temp.m11 * temp.m23 + 
	                temp.m00 * temp.m21 * temp.m13 + 
	                temp.m01 * temp.m10 * temp.m23 - 
	                temp.m01 * temp.m20 * temp.m13 - 
	                temp.m03 * temp.m10 * temp.m21 + 
	                temp.m03 * temp.m20 * temp.m11;

	    this.m30 = -temp.m10 * temp.m21 * temp.m32 + 
	                temp.m10 * temp.m31 * temp.m22 + 
	                temp.m11 * temp.m20 * temp.m32 - 
	                temp.m11 * temp.m30 * temp.m22 - 
	                temp.m12 * temp.m20 * temp.m31 + 
	                temp.m12 * temp.m30 * temp.m21;

	    this.m31 = temp.m00 * temp.m21 * temp.m32 - 
	               temp.m00 * temp.m31 * temp.m22 - 
	               temp.m01 * temp.m20 * temp.m32 + 
	               temp.m01 * temp.m30 * temp.m22 + 
	               temp.m02 * temp.m20 * temp.m31 - 
	               temp.m02 * temp.m30 * temp.m21;

	    this.m32 = -temp.m00 * temp.m11 * temp.m32 + 
	                temp.m00 * temp.m31 * temp.m12 + 
	                temp.m01 * temp.m10 * temp.m32 - 
	                temp.m01 * temp.m30 * temp.m12 - 
	                temp.m02 * temp.m10 * temp.m31 + 
	                temp.m02 * temp.m30 * temp.m11;

	    this.m33 = temp.m00 * temp.m11 * temp.m22 - 
	               temp.m00 * temp.m21 * temp.m12 - 
	               temp.m01 * temp.m10 * temp.m22 + 
	               temp.m01 * temp.m20 * temp.m12 + 
	               temp.m02 * temp.m10 * temp.m21 - 
	               temp.m02 * temp.m20 * temp.m11;

	    float det = temp.m00 * this.m00 + temp.m10 * this.m01 + temp.m20 * this.m02 + temp.m30 * this.m03;
	    
	    
	    if( det == 0 )
        {
	    	System.err.println( "Matrix is not invertible!" );
	    	this.set( temp );
	    	
	    	return this;
        }
	    
	    return this.mul( 1.0f/det );
	}
	
	/**
     * Inverse given matrix
     * @return inversed matrix
     */
	public static Mat4 inverse( Mat4 mat )
	{
		Mat4 result = new Mat4();
		
	    result.m00 = mat.m11 * mat.m22 * mat.m33 - 
	                 mat.m11 * mat.m32 * mat.m23 - 
	                 mat.m12 * mat.m21 * mat.m33 + 
	                 mat.m12 * mat.m31 * mat.m23 +
	                 mat.m13 * mat.m21 * mat.m32 - 
	                 mat.m13 * mat.m31 * mat.m22;

	    result.m01 = -mat.m01 * mat.m22 * mat.m33 + 
	                  mat.m01 * mat.m32 * mat.m23 + 
	                  mat.m02 * mat.m21 * mat.m33 - 
	                  mat.m02 * mat.m31 * mat.m23 - 
	                  mat.m03 * mat.m21 * mat.m32 + 
	                  mat.m03 * mat.m31 * mat.m22;

	    result.m02 = mat.m01 * mat.m12 * mat.m33 - 
	                 mat.m01 * mat.m32 * mat.m13 - 
	                 mat.m02 * mat.m11 * mat.m33 + 
	                 mat.m02 * mat.m31 * mat.m13 + 
	                 mat.m03 * mat.m11 * mat.m32 - 
	                 mat.m03 * mat.m31 * mat.m12;

	    result.m03 = -mat.m01 * mat.m12 * mat.m23 + 
	                  mat.m01 * mat.m22 * mat.m13 +
	                  mat.m02 * mat.m11 * mat.m23 - 
	                  mat.m02 * mat.m21 * mat.m13 - 
	                  mat.m03 * mat.m11 * mat.m22 + 
	                  mat.m03 * mat.m21 * mat.m12;

	    result.m10 = -mat.m10 * mat.m22 * mat.m33 + 
	                  mat.m10 * mat.m32 * mat.m23 + 
	                  mat.m12 * mat.m20 * mat.m33 - 
	                  mat.m12 * mat.m30 * mat.m23 - 
	                  mat.m13 * mat.m20 * mat.m32 + 
	                  mat.m13 * mat.m30 * mat.m22;

	    result.m11 = mat.m00 * mat.m22 * mat.m33 - 
	                 mat.m00 * mat.m32 * mat.m23 - 
	                 mat.m02 * mat.m20 * mat.m33 + 
	                 mat.m02 * mat.m30 * mat.m23 + 
	                 mat.m03 * mat.m20 * mat.m32 - 
	                 mat.m03 * mat.m30 * mat.m22;

	    result.m12 = -mat.m00 * mat.m12 * mat.m33 + 
	                  mat.m00 * mat.m32 * mat.m13 + 
	                  mat.m02 * mat.m10 * mat.m33 - 
	                  mat.m02 * mat.m30 * mat.m13 - 
	                  mat.m03 * mat.m10 * mat.m32 + 
	                  mat.m03 * mat.m30 * mat.m12;

	    result.m13 = mat.m00 * mat.m12 * mat.m23 - 
	                 mat.m00 * mat.m22 * mat.m13 - 
	                 mat.m02 * mat.m10 * mat.m23 + 
	                 mat.m02 * mat.m20 * mat.m13 + 
	                 mat.m03 * mat.m10 * mat.m22 - 
	                 mat.m03 * mat.m20 * mat.m12;

	    result.m20 = mat.m10 * mat.m21 * mat.m33 - 
	                 mat.m10 * mat.m31 * mat.m23 - 
	                 mat.m11 * mat.m20 * mat.m33 + 
	                 mat.m11 * mat.m30 * mat.m23 + 
	                 mat.m13 * mat.m20 * mat.m31 - 
	                 mat.m13 * mat.m30 * mat.m21;

	    result.m21 = -mat.m00 * mat.m21 * mat.m33 + 
	                  mat.m00 * mat.m31 * mat.m23 + 
	                  mat.m01 * mat.m20 * mat.m33 - 
	                  mat.m01 * mat.m30 * mat.m23 - 
	                  mat.m03 * mat.m20 * mat.m31 + 
	                  mat.m03 * mat.m30 * mat.m21;

	    result.m22 = mat.m00 * mat.m11 * mat.m33 - 
	                 mat.m00 * mat.m31 * mat.m13 - 
	                 mat.m01 * mat.m10 * mat.m33 + 
	                 mat.m01 * mat.m30 * mat.m13 + 
	                 mat.m03 * mat.m10 * mat.m31 - 
	                 mat.m03 * mat.m30 * mat.m11;

	    result.m23 = -mat.m00 * mat.m11 * mat.m23 + 
	                  mat.m00 * mat.m21 * mat.m13 + 
	                  mat.m01 * mat.m10 * mat.m23 - 
	                  mat.m01 * mat.m20 * mat.m13 - 
	                  mat.m03 * mat.m10 * mat.m21 + 
	                  mat.m03 * mat.m20 * mat.m11;

	    result.m30 = -mat.m10 * mat.m21 * mat.m32 + 
	                  mat.m10 * mat.m31 * mat.m22 + 
	                  mat.m11 * mat.m20 * mat.m32 - 
	                  mat.m11 * mat.m30 * mat.m22 - 
	                  mat.m12 * mat.m20 * mat.m31 + 
	                  mat.m12 * mat.m30 * mat.m21;

	    result.m31 = mat.m00 * mat.m21 * mat.m32 - 
	                 mat.m00 * mat.m31 * mat.m22 - 
	                 mat.m01 * mat.m20 * mat.m32 + 
	                 mat.m01 * mat.m30 * mat.m22 + 
	                 mat.m02 * mat.m20 * mat.m31 - 
	                 mat.m02 * mat.m30 * mat.m21;

	    result.m32 = -mat.m00 * mat.m11 * mat.m32 + 
	                  mat.m00 * mat.m31 * mat.m12 + 
	                  mat.m01 * mat.m10 * mat.m32 - 
	                  mat.m01 * mat.m30 * mat.m12 - 
	                  mat.m02 * mat.m10 * mat.m31 + 
	                  mat.m02 * mat.m30 * mat.m11;

	    result.m33 = mat.m00 * mat.m11 * mat.m22 - 
	                 mat.m00 * mat.m21 * mat.m12 - 
	                 mat.m01 * mat.m10 * mat.m22 + 
	                 mat.m01 * mat.m20 * mat.m12 + 
	                 mat.m02 * mat.m10 * mat.m21 - 
	                 mat.m02 * mat.m20 * mat.m11;

	    float det = mat.m00 * result.m00 + mat.m10 * result.m01 + mat.m20 * result.m02 + mat.m30 * result.m03;
	    
	    
	    if( det == 0 )
        {
	    	System.err.println( "Matrix is not invertible!" );
	    	result.set( mat );
	    	
	    	return result;
        }
	    
	    return result.mul( 1.0f/det );
	}
	
	
	/**
	 * Fast inversion of an orthogonal transformation matrix
	 * whose columns are orthogonal unit vectors
	 * @return iversed matrix
	 */
	public Mat4 inverseOrtho()
	{		
		this.transpose();
		
		this.m03 = -( this.m00 * this.m30 + this.m01 * this.m31 + this.m02 * this.m32 );
		this.m13 = -( this.m10 * this.m30 + this.m11 * this.m31 + this.m12 * this.m32 ); 
		this.m23 = -( this.m20 * this.m30 + this.m21 * this.m31 + this.m22 * this.m32 );
		
		this.m30 = 0.0f;
		this.m31 = 0.0f;
		this.m32 = 0.0f;
		this.m33 = 1.0f;
		
		return this;
	}
	

	/**
	 * Fast inversion of an orthogonal transformation matrix
	 * whose columns are orthogonal unit vectors
	 * @return inversed matrix
	 */
	public static Mat4 inverseOrtho( Mat4 mat )
	{
		Mat4 result = new Mat4( mat );
		
		return result.inverseOrtho();
	}
	
	/**
	 * Places matrix in a newly created float buffer, suitable for lwjgl graphic
	 * operations
	 * 
	 * @return float buffer, containing the matrix in a serialized form.
	 */
	public FloatBuffer toFloatBuffer()
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		Mat4.storeInBuffer( this, buffer );
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
	public FloatBuffer toFloatBuffer( FloatBuffer buffer )
	{
		assert buffer.capacity() == 16 : "FloatBuffer should have a capacity of 16 but has " + buffer.capacity();
		
		Mat4.storeInBuffer( this, buffer );
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
		return "[" + this.m00 + "  " + this.m01 + "  " + this.m02 + "  " + this.m03 + "]\n" + 
			   "[" + this.m10 + "  " + this.m11 + "  " + this.m12 + "  " + this.m13 + "]\n" + 
			   "[" + this.m20 + "  " + this.m21 + "  " + this.m22 + "  " + this.m23 + "]\n" + 
			   "[" + this.m30 + "  " + this.m31 + "  " + this.m32 + "  " + this.m33 + "]";
	}
	
	/**
	 * Compares this matrix with another object (if the object is also a Mat4
	 * make an element-wise comparison)
	 * 
	 * @param obj
	 *            another object for the comparison
	 * @return true: object is equal and has the same values as this matrix
	 *         false: object is unequal
	 */
	@Override
    public boolean equals( Object obj )
	{
        if( obj == this )
            return true;
        
        if( obj == null || obj.getClass() != this.getClass() )
            return false;

        Mat4 vec = (Mat4) obj;
        return  (this.m00 == vec.m00) && (this.m01 == vec.m01) && (this.m02 == vec.m02) && (this.m03 == vec.m03) &&
        		(this.m10 == vec.m10) && (this.m11 == vec.m11) && (this.m12 == vec.m12) && (this.m13 == vec.m13) &&
        		(this.m20 == vec.m20) && (this.m21 == vec.m21) && (this.m22 == vec.m22) && (this.m23 == vec.m23) &&
        		(this.m30 == vec.m30) && (this.m31 == vec.m31) && (this.m32 == vec.m32) && (this.m33 == vec.m33);
    }
	
	/**
	 * Serialize the given matrix into a buffer
	 * 
	 * @param mat
	 *            matrix to be serialized
	 * @param buffer
	 *            buffer to store the matrix elements
	 */
	public static void storeInBuffer( Mat4 mat, FloatBuffer buffer )
	{
		buffer.put( mat.m00 );
		buffer.put( mat.m10 );
		buffer.put( mat.m20 );
		buffer.put( mat.m30 );
		buffer.put( mat.m01 );
		buffer.put( mat.m11 );
		buffer.put( mat.m21 );
		buffer.put( mat.m31 );
		buffer.put( mat.m02 );
		buffer.put( mat.m12 );
		buffer.put( mat.m22 );
		buffer.put( mat.m32 );
		buffer.put( mat.m03 );
		buffer.put( mat.m13 );
		buffer.put( mat.m23 );
		buffer.put( mat.m33 );
	}
}
