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

/**
 * 3-dimensional vector operations
 */
public final class Vec3
{
	/**
	 * The three coordinates of the vector
	 */
	public float x;
	public float y;
	public float z;
	
	
	/**
	 * Element-wise construction of vector
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	/**
	 * Creates a new vector containing [0, 0, 0]
	 */
	public Vec3()
	{
		this( 0.0f, 0.0f, 0.0f );
	}
	
	
	/**
	 * Creates a new vector containing [value, value, value]
	 * @param value
	 */
	public Vec3( float value )
	{
		this( value, value, value );
	}
	
	
	/**
	 * Creates a new vector containing [vec.x, vec.y, 0]
	 * 
	 * @param vec
	 *            2-dimensional vector which is extended
	 */
	public Vec3( Vec2 vec )
	{
		this( vec.x, vec.y, 0.0f );
	}
	
	
	/**
	 * Creates a new vector containing [vec.x, vec.y, z]
	 * 
	 * @param vec
	 *            2-dimensional vector which is extended
	 * @param z
	 *            value of third dimension
	 */
	public Vec3( Vec2 vec, float z )
	{
		this( vec.x, vec.y, z );
	}
	
	
	/**
	 * Creates a vector by copying another vector
	 * @param vec vector to be copied
	 */
	public Vec3( Vec3 vec )
	{
		this( vec.x, vec.y, vec.z );
	}
	
	
	/**
	 * clone method for Vec3
	 * @return cloned vector
	 */
	public Vec3 clone()
	{
		return new Vec3 (this.x, this.y, this.z);
	}
	
	/**
	 * Creates a new vector containing [vec.x, vec.y, vec.z], omitting 4th
	 * dimension
	 * 
	 * @param vec
	 *            4-dimensional vector to be transformed to 3-dimensional vector
	 */
	public Vec3( Vec4 vec )
	{
		this( vec.x, vec.y, vec.z );
	}
	
	
	/**
	 * Copies all values of another vector into this one
	 * @param vec
	 * @return copied vector
	 */
	public Vec3 set ( Vec3 vec )
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}
	
	/**
	 * Copies coordinates element-wise into this vector
	 * @param x
	 * @param y
	 * @param z
	 * @return copied vector
	 */
	public Vec3 set ( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	

	/**
	 * Returns random vector on Sphere S^2
	 * @return Point on S^2
	 */
	public static Vec3 createRandomPointOnSphere() {
		float a = 2.0f* ((float) Math.random()) -1.0f;
		float b = 2.0f* ((float) Math.random()) -1.0f;
		float s =a*a + b*b;
		
		while (s >= 1) {
			a = 2.0f* ((float) Math.random()) -1.0f;
			b = 2.0f* ((float) Math.random()) -1.0f;
			s = a*a + b*b;
		}
		
		float sq = 2.0f * (float) Math.sqrt(1-s);
		
		return new Vec3( a * sq,  b * sq, 1.0f - 2.0f * s);
	}
	
	/**
	 * Creates random point on ellipse
	 * @param v Principal axe of ellipse
	 * @param w Principal axe of ellipse
	 * @return Point on ellipse
	 */
	public static Vec3 createRandomPointOnEllipse(Vec3 v, Vec3 w) {
		float angle = (float) Math.random() * MathUtil.PI_DOUBLE;
		float a = (float) Math.sin(angle);
		float b = (float) Math.cos(angle);
		
		return Vec3.mul(v, a).add(Vec3.mul(w, b));
	}
	
	/**
	 * Creates random vector on elliptic disc, where the principal 
	 * axes are specified by vector v und w
	 * @param v Principal axe
	 * @param w Principal axe
	 * @return random point on disc
	 */
	public static Vec3 createRandomPointOnDisc(Vec3 v, Vec3 w) {
		float a = 2.0f* ((float) Math.random()) -1.0f;
		float b = 2.0f* ((float) Math.random()) -1.0f;
		float s =a*a + b*b;
		
		while (s >= 1)  {
			a = 2.0f* ((float) Math.random()) -1.0f;
			b = 2.0f* ((float) Math.random()) -1.0f;
			s = a*a + b*b;
		}
		
		return Vec3.mul(v, a).add(Vec3.mul(w, b));
	}
	
	/**
	 * Creates random unit vector of cone in direction d and with given angle
	 * IMPORTANT: as parameter the tangent of the angle is demanded!
	 * @param d Direction of cone, must be normalized vector
	 * @param angle tangent of the angle of the cone
	 * @return random point on cone
	 */
	public static Vec3 createRandomPointCone(Vec3 d, float angle) {
		// Create orthogonal vectors
//		Vec3 v;
//		if (d.z != 0) {
//			v = new Vec3(0,-d.z, d.y).normalize();
//		} else {
//			v = new Vec3(-d.y, d.x, 0).normalize();
//		}
//		
//		Vec3 w = Vec3.cross(d, v).normalize();
//		
//		// Create random point on circle
//		Vec3 pcirc = createRandomPointOnEllipse(new Vec3(1,0,0), new Vec3(0,1,0));
//		
//		float a = (float) Math.random() * angle;
//		
//		return Vec3.mul(v, a * pcirc.x).add(Vec3.mul(w,a * pcirc.y)).add(d).normalize();
		
		Vec3 zAxis = Vec3.zAxis(); 
		// Create random point on circle
		Vec3 pcirc = createRandomPointOnEllipse(new Vec3(1,0,0), new Vec3(0,1,0));
		float a = (float) Math.random() * angle;
		Vec3 r = new Vec3 (pcirc.x * a, pcirc.y * a, -1);
		r.normalize();
		
		double dv = Vec3.dot(d,  zAxis);
		dv /= d.length();
		float rotAngle = (float) Math.acos(dv);
		Vec3 rotAxis = Vec3.cross(d, zAxis );
		Mat3 rotMat = Mat3.rotation(rotAxis, rotAngle);
		r.transform(rotMat);
		return r;
	}
	
	
	public static Vec3 createRandomVec3UnitCube() {
		double x = Math.random();
		double y = Math.random();
		double z = Math.random();
		return  new Vec3((float) x, (float) y, (float) z);
	}
	

	public static Vec3 createRandomVec3Ball(float radius) {
		double x = Math.random();
		double y = Math.random();
		double z = Math.random();
		x -= 0.5;
		y -= 0.5;
		z -= 0.5;
		
		Vec3 vec = new Vec3((float) x, (float) y, (float) z);
		vec.normalize();
		vec.mul(radius);
		return vec;		
	}
	
	/**
	 * Creates a new vector containing [1, 0, 0]
	 * @return x unit vector
	 */
	public static Vec3 xAxis()
	{
		return new Vec3( 1.0f, 0.0f, 0.0f );
	}
	

	/**
	 * Creates a new vector containing [0, 1, 0]
	 * @return y unit vector
	 */
	public static Vec3 yAxis()
	{
		return new Vec3( 0.0f, 1.0f, 0.0f );
	}
	

	/**
	 * Creates a new vector containing [0, 0, 1]
	 * @return z unit vector
	 */
	public static Vec3 zAxis()
	{
		return new Vec3( 0.0f, 0.0f, 1.0f );
	}
	
	
	/**
	 * Creates vector by negating another vector
	 * @return negated vector
	 */
	public Vec3 negate()
	{
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	/**
	 * Creates vector by negating another vector
	 * @return negated vector
	 */
	public static Vec3 negate(Vec3 vec)
	{
		return new Vec3 (-vec.x, -vec.y, -vec.z);
	}
	
	/**
	 * Adds another vector to this vector
	 * @param vec vector to be added
	 * @return added vector
	 */
	public Vec3 add( Vec3 vec )
	{
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		
		return this;
	}
	
	
	/**
	 * Adds two vectors
	 * @param left	left operand of the vector addition
	 * @param right	right operand of the vector addition
	 * @return result of the vecor addition
	 */
	public static Vec3 add( Vec3 left, Vec3 right )
	{
		return new Vec3( left.x + right.x,
						 left.y + right.y,
						 left.z + right.z );
	}
	
	/**
	 * Subtracts another vector from this vector
	 * @param right vector to subtract
	 * @return subtracted vector
	 */
	public Vec3 sub( Vec3 right )
	{
		this.x -= right.x;
		this.y -= right.y;
		this.z -= right.z;
		
		return this;
	}
	
	/**
	 * Subtracts one vector from another
	 * @param left	left operand of the vector subtraction
	 * @param right	right operand of the vector subtraction
	 * @return result of the vecor subtraction
	 */
	public static Vec3 sub( Vec3 left, Vec3 right )
	{
		return new Vec3( left.x - right.x,
						 left.y - right.y,
						 left.z - right.z );
	}
	
	
	/**
	 * Multiplies this vector with a factor
	 * @param value multiplication factor
	 * @return multiplied vector
	 */
	public Vec3 mul( float value )
	{
		this.x *= value;
		this.y *= value;
		this.z *= value;
		
		return this;
	}
	
	/**
	 * Multiplies given vector with a factor
	 * @param vec vector to be multiplied with factor
	 * @param value multiplication factor
	 * @return multiplied vector
	 */
	public static Vec3 mul( Vec3 vec, float value )
	{
		return new Vec3( vec.x * value,
						 vec.y * value,
						 vec.z * value );
	}
	
	
	/**
	 * Component wise multiplication of this vector with another vector. <br>
	 * result = [x1*x2, y1*y2, z1*z2]
	 * 
	 * @return multiplication result
	 */
	public Vec3 mul( Vec3 vec )
	{
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;
		
		return this;
	}
	
	
	/**
	 * Component wise multiplication of two vectors. <br>
	 * result = [x1*x2, y1*y2, z1*z2, w1*w2]
	 * 
	 * @param left
	 *            left multiplication operand
	 * @param right
	 *            right multiplication operand
	 * @return multiplication result
	 */
	public static Vec3 mul( Vec3 left, Vec3 right )
	{
		return new Vec3( left.x * right.x,
						 left.y * right.y,
						 left.z * right.z );
	}
	
	

	/**
	 * transforms this vector by a given 3x3 matrix M<br>
	 * this = M * this
	 * @param mat transformation matrix
	 * @return transformed vector
	 */
	public Vec3 transform( Mat3 mat )
	{
		float x = this.x;
		float y = this.y;
		float z = this.z;

		this.x = mat.m00 * x + mat.m01 * y + mat.m02 * z;
		this.y = mat.m10 * x + mat.m11 * y + mat.m12 * z;
		this.z = mat.m20 * x + mat.m21 * y + mat.m22 * z;
		
		return this;
	}
	
	
	/**
	 * transforms a vector v by a given 3x3 matrix M</br>
	 * v' = M * v
	 * @param vec vector to be transformed
	 * @param mat transformation matrix
	 * @return transformed vector
	 */
	public static Vec3 transform( Vec3 vec, Mat3 mat )
	{
		return new Vec3( mat.m00 * vec.x + mat.m01 * vec.y + mat.m02 * vec.z,
						 mat.m10 * vec.x + mat.m11 * vec.y + mat.m12 * vec.z,
						 mat.m20 * vec.x + mat.m21 * vec.y + mat.m22 * vec.z );
	}
	
	
	/**
	 * transforms this vector (expanded with fourth component w) by a given 4x4 matrix M</br>
	 * this = M * this', this' is the extended vector
	 * This method can be used to transform points (w=1) or vectors (w=0)
	 * @param w value of fourth dimension (=0 for vector, <>0 for points)
	 * @param mat transformation matrix
	 * return transformed vector
	 */
	public Vec3 transform( float w, Mat4 mat )
	{
		float x = this.x;
		float y = this.y;
		float z = this.z;

		this.x = mat.m00 * x + mat.m01 * y + mat.m02 * z + mat.m03 * w;
		this.y = mat.m10 * x + mat.m11 * y + mat.m12 * z + mat.m13 * w;
		this.z = mat.m20 * x + mat.m21 * y + mat.m22 * z + mat.m23 * w;
		
		return this;
	}
	
	/**
	 * transforms the vector v (expanded with fourth component w) by a given 4x4 matrix M</br>
	 * v = M * v', v' is the extended vector
	 * This method can be used to transform points (w=1) or vectors (w=0)
	 * @param vec vector to be transformed
	 * @param w value of fourth dimension (=0 for vector, <>0 for points)
	 * @param mat transformation matrix
	 * return transformed vector
	 */
	/**
	 * transforms a vector v by a given matrix M</br>
	 * v' = M * v
	 */
	public static Vec3 transform( Vec3 vec, float w, Mat4 mat )
	{
		return new Vec3( mat.m00 * vec.x + mat.m01 * vec.y + mat.m02 * vec.z + mat.m03 * w,
				 mat.m10 * vec.x + mat.m11 * vec.y + mat.m12 * vec.z + mat.m13 * w,
				 mat.m20 * vec.x + mat.m21 * vec.y + mat.m22 * vec.z + mat.m23 * w );
    }
	
	/**
	 * Dot product of this vector with another vector
	 * @param right second operand of dot product operation
	 * @return dot product
	 */
	public float dot( Vec3 right )
	{
		return this.x * right.x + this.y * right.y + this.z * right.z;
	}
	
	
	/**
	 * Dot product of two vectors
	 * @param left left second operand of dot product operation
	 * @param right right second operand of dot product operation
	 * @return dot product
	 */
	public static float dot( Vec3 left, Vec3 right )
	{
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}
	
	
	/**
	 * Calculates length of this vector
	 * @return length
	 */
	public float length()
	{
		return (float) Math.sqrt( this.dot(this) );
	}
	
	/**
	 * Calculates length of given vector
	 * @param vec vector which length shall be calculated
	 * @return length
	 */
	public static float length( Vec3 vec )
	{
		return (float) Math.sqrt( dot(vec,vec) );
	}
	
	
	/**
	 * Nomalizes this vector (by dividing the vector by it*s length)
	 * @return normalized vector
	 */
	public Vec3 normalize()
	{
		float length = length (this);
		if (length < MathUtil.EPS)
			return this;
		
		float lengthInv = 1.0f / length;
		
		this.x *= lengthInv;
		this.y *= lengthInv;
		this.z *= lengthInv;
		
		return this;
	}
	
	
	/**
	 * Nomalizes given vector (by dividing the vector by it*s length)
	 * @param vec vector to be normalized
	 * @return normalized vector
	 */
	public static Vec3 normalize( Vec3 vec )
	{
		Vec3 result = new Vec3( vec );
		return result.normalize();
	}
	
	
	/**
	 * Cross product of this vector with another vector
	 * @param right second operand of cross product operation
	 * @return cross product vector
	 */
	public Vec3 cross( Vec3 right )
	{
		
		return new Vec3 (	this.y * right.z - this.z * right.y,
							this.z * right.x - this.x * right.z,
							this.x * right.y - this.y * right.x);
	}
	
	/**
	 * Cross product of two vectors
	 * @param left first operand of cross product operation
	 * @param right second operand of cross product operation
	 * @return cross product vector
	 */
	public static Vec3 cross( Vec3 left, Vec3 right )
	{
		return new Vec3( left.y * right.z - left.z * right.y,
						 left.z * right.x - left.x * right.z,
						 left.x * right.y - left.y * right.x );
	}
	
	
	/**
	 * Calculates reflection vector to this vector and a given surface normal
	 * @param n surface normal
	 * @return reflection vector
	 */
	public Vec3 reflect( Vec3 n)
	{
		float term = 2 * dot(this, n);
		
		return sub (this, mul(n, term));
	}
	
	
	
	/**
	 * Returns a vector containing the component-wise minima</br></br>
	 * this = [0, 5, 1]</br>
	 * vec = [3, 0, 2]</br></br>
	 * this.min( vec ) = [0, 0, 1]
	 * @param vec second vector for this comparison
	 * @return vector with component wise minima
	 */
	public Vec3 min( Vec3 vec )
	{
		this.x = this.x < vec.x ? this.x : vec.x;
		this.y = this.y < vec.y ? this.y : vec.y;
		this.z = this.z < vec.z ? this.z : vec.z;
		
		return this;
	}
	
	
	/**
	 * Returns a new vector containing the component-wise minima of two vectors</br></br>
	 * v1 = [0, 5, 1]</br>
	 * v2 = [3, 0, 2]</br></br>
	 * min( v1, v2 ) = [0, 0, 1]
	 * @param left left vector for comparison
	 * @param right second vector for comparison
	 * @return vector with component wise minima 
	 */
	public static Vec3 min( Vec3 left, Vec3 right )
	{
		return new Vec3( left.x < right.x ? left.x : right.x,
						 left.y < right.y ? left.y : right.y,
						 left.z < right.z ? left.z : right.z );
	}
	
	
	/**
	 * Returns a vector containing the component-wise maxima</br></br>
	 * this = [0, 5, 1]</br>
	 * v2 = [3, 0, 2]</br></br>
	 * this.max( v2 ) = [3, 5, 2]
	 * @param vec second vector for this comparison
	 * @return vector with component wise minima
	 */
	public Vec3 max( Vec3 vec )
	{
		this.x = this.x > vec.x ? this.x : vec.x;
		this.y = this.y > vec.y ? this.y : vec.y;
		this.z = this.z > vec.z ? this.z : vec.z;
		
		return this;
	}
	
	
	/**
	 * Returns a new vector containing the component-wise maxima of two vectors</br>
	 * v1 = [0, 5, 1]</br>
	 * v2 = [3, 0, 2]</br></br>
	 * max( v1, v2 ) = [3, 5, 2]
	 * @param left left vector for comparison
	 * @param right second vector for comparison
	 * @return vector with component wise minima 
	 */
	public static Vec3 max( Vec3 left, Vec3 right )
	{
		return new Vec3( left.x > right.x ? left.x : right.x,
						 left.y > right.y ? left.y : right.y,
						 left.z > right.z ? left.z : right.z );
	}
	
	
	/**
	 * Clamps each component of this vector to the given min/max values.
	 * @param min minimum vector
	 * @param max maximum vector
	 * @return clamped vector
	 */
	public Vec3 clamp( Vec3 min, Vec3 max )
	{
		this.x = MathUtil.clamp( this.x, min.x, max.x );
		this.y = MathUtil.clamp( this.y, min.y, max.y );
		this.z = MathUtil.clamp( this.z, min.z, max.z );
		
		return this;
	}
	
	
	/**
	 * Returns a new vector, with each component of a given vector clamped to the given min/max values.
	 * 
	 * @param vec	vector to be clamped
	 * @param min   minimum vector
	 * @param max   maximum vector
	 * @return clamped vector
	 */
	public static Vec3 clamp( Vec3 vec, Vec3 min, Vec3 max )
	{
		return new Vec3( MathUtil.clamp( vec.x, min.x, max.x ),
						 MathUtil.clamp( vec.y, min.y, max.y ),
						 MathUtil.clamp( vec.z, min.z, max.z ) );
	}
	
	
	/**
	 * Discards the sign of each component of this vector
	 * @return magnitude of this vector
	 */
	public Vec3 abs()
	{
		this.x = Math.abs( this.x );
		this.y = Math.abs( this.y );
		this.z = Math.abs( this.z );
		
		return this;
	}
	
	
	/**
	 * Returns a new vector, with each component containing the absolute values of a given vector
	 * @param vec vector to take the absolute values from
	 * @return vector with absolute values
	 */
	public static Vec3 abs( Vec3 vec )
	{
		return new Vec3( Math.abs(vec.x), 
						 Math.abs(vec.y), 
						 Math.abs(vec.z) );
	}
	
	
	/**
	 * Linear, component-wise interpolation between this and another vectors.
	 * @param vec the second vector
	 * @param alpha the interpolation factor
	 * @return alpha*this + (1-alpha) * vec
	 */
	public Vec3 mix( Vec3 vec, float alpha )
	{
		float alphaDiff = 1.0f - alpha;

		this.x = alpha * vec.x + alphaDiff * this.x;
		this.y = alpha * vec.y + alphaDiff * this.y;
		this.z = alpha * vec.z + alphaDiff * this.z;
		
		return this;
	}
	
	
	/**
	 * Linear, component-wise interpolation between two given vectors.
	 * @param left the first vector
	 * @param right the second vector
	 * @param alpha the interpolation factor
	 * @return alpha*left + (1-alpha) * right
	 */
	public static Vec3 mix( Vec3 left, Vec3 right, float alpha )
	{
		Vec3 result = new Vec3( left );
		
		return result.mix( right, alpha );
	}
	
	
	/**
	 * Component-wise linear interpolation between this vectors and another
	 * vector with different interpolation factors for the three dimensions
	 * 
	 * @param vec
	 *            second vector for interpolation
	 * @param alpha
	 *            vector with different interpolation factors for each dimension
	 * @return alpha*this + (1-alpha) * vec
	 */
	public Vec3 mix( Vec3 vec, Vec3 alpha )
	{
		this.x = alpha.x * vec.x + (1.0f - alpha.x) * this.x;
		this.y = alpha.y * vec.y + (1.0f - alpha.y) * this.y;
		this.z = alpha.z * vec.z + (1.0f - alpha.z) * this.z;
		
		return this;
	}
	
	
	/**
	 * Component-wise linear interpolation between two vectors with different
	 * interpolation factors for the three dimensions
	 * 
	 * @param left
	 *            first vector for interpolation
	 * @param right
	 *            second vector for interpolation
	 * @param alpha
	 *            vector with different interpolation factors for each dimension
	 * @return alpha*this + (1-alpha) * vec
	 */
	public static Vec3 mix( Vec3 left, Vec3 right, Vec3 alpha )
	{
		Vec3 result = new Vec3( left );
		
		return result.mix( right, alpha );
	}
	
	/**
	 * Places vector in a newly created float buffer, suitable for lwjgl graphic
	 * operations
	 * 
	 * @return float buffer, containing the vector values.
	 */
	public FloatBuffer toFloatBuffer()
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		buffer.put( x);
		buffer.put( y);
		buffer.put( z);
		buffer.put(1.0f);  //TODO: this is a quick fix, which must be solved in a better way

		buffer.flip();
		
		return buffer;
	}
		
	/**
	 * changing matrix to string
	 * 
	 * @return string with matrix
	 */
	public Vec3 toRad()
	{
		x = MathUtil.toRad(x);
		y = MathUtil.toRad(y);
		z = MathUtil.toRad(z);
		return this;
	}
	
	
	/**
	 * changing matrix to string
	 * 
	 * @return string with matrix
	 */
	public String toString()
	{
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
	
	
	/**
	 * Compares this vector with another object (if the object is also a Vec3
	 * make an element-wise comparison)
	 * 
	 * @param obj
	 *            another object for the comparison
	 * @return true: object is equal and has the same values as this vector
	 *         false: object is unequal
	 */
	@Override
    public boolean equals( Object obj )
	{
        if( obj == this )
            return true;
        
        if( obj == null || obj.getClass() != this.getClass() )
            return false;

        Vec3 vec = (Vec3) obj;
        return this.x == vec.x
            && ( this.y == vec.y )
            && ( this.z == vec.z );
    }
	
	
	public static void runTest()
	{
		Vec3 first  = new Vec3( 1.0f, 0.0f, 0.0f );
		Vec3 second = new Vec3( 0.0f,  0.0f, -1.0f );
		
		System.out.println();
		System.out.println( "first:  " + first );
		System.out.println( "second: " + second );
		System.out.println( "first + second: " + add(first, second) );
		System.out.println( "second - first: " + sub(second, first) );
		System.out.println( "max: " + max(first, second) );
		System.out.println( "min: " + min(first, second) );
		System.out.println( "cross(first, second): " + cross(first, second) );
		System.out.println( "abs(first): " + abs(first) );
		System.out.println( "dot(first, second): " + dot(first, second) );
	}
}
