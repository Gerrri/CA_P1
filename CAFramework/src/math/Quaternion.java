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

/**
 * Quaternion algebra
 *
 */
public class Quaternion {
	
	public float x;
	public float y;
	public float z;
	public float w;
	
	// Values which are smaller then this value are considered as zero in some methods (e.g. SLERP)
	public static final float ZERO_TOLERANCE = 1e-06f;
	
	
	public Quaternion( float x, float y, float z, float w ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	/**
	 * Creates new quaternion 
	 */
	public Quaternion() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	

	/**
	 * Creates new quaternion from vec with w = 0
	 * @param vec
	 */
	public Quaternion( Vec3 vec ) {
		this(vec.x,vec.y, vec.z, 0.0f);
	}
	
	/**
	 * 
	 * @param vec
	 * @param w
	 */
	public Quaternion( Vec3 vec, float w ) {
		this(vec.x,vec.y, vec.z, w);
	}
	
	/**
	 * Creates new quaternion from vec with w = 0
	 * @param vec
	 */
	public Quaternion( Vec4 vec ) {
		this(vec.x,vec.y, vec.z, vec.w);
	}
	
	/**
	 * Creates new quaternion from rotation matrix
	 * @param mat
	 */
	public Quaternion( Mat3 mat ) {

        float trace = mat.m00 + mat.m11 + mat.m22;

        if (trace >= 0) { 
        	// w has  biggest magnitude, |w| > max{|x|, |y|, |z }, and |w| >= 1/2
            float s = (float) Math.sqrt(trace+1); 
            w = 0.5f * s;
            s = 0.5f / s;                 
            x = (mat.m21 - mat.m12) * s;
            y = (mat.m02 - mat.m20) * s;
            z = (mat.m10 - mat.m01) * s;
        } else if ((mat.m00 > mat.m11) && (mat.m00 > mat.m22)) {
        	// x has  biggest magnitude, |x| >= 1/2
            float s = (float) Math.sqrt(1.0f + mat.m00 - mat.m11 - mat.m22); 
            x = s * 0.5f; 
            s = 0.5f / s;
            y = (mat.m10 + mat.m01) * s;
            z = (mat.m02 + mat.m20) * s;
            w = (mat.m21 - mat.m12) * s;
        } else if (mat.m11 > mat.m22) {
        	// y has  biggest magnitude, |y| >= 1/2
            float s = (float) Math.sqrt(1.0f + mat.m11 - mat.m00 - mat.m22); 
            y = s * 0.5f; 
            s = 0.5f / s;
            x = (mat.m10 + mat.m01) * s;
            z = (mat.m21 + mat.m12) * s;
            w = (mat.m02 - mat.m20) * s;
        } else {
        	// z has  biggest magnitude, |z| >= 1/2
            float s = (float) Math.sqrt(1.0f + mat.m22 - mat.m00 - mat.m11);
            z = s * 0.5f; 
            s = 0.5f / s;
            x = (mat.m02 + mat.m20) * s;
            y = (mat.m21 + mat.m12) * s;
            w = (mat.m10 - mat.m01) * s;
        }
	}
	
	/**
	 * Creates new quaternion from rotation matrix
	 * @param mat
	 */
	public Quaternion( Mat4 mat ) {
        float trace = mat.m00 + mat.m11 + mat.m22;

        if (trace >= 0) { 
        	// w has  biggest magnitude, |w| > max{|x|, |y|, |z }, and |w| >= 1/2
            float s = (float) Math.sqrt(trace+1); 
            w = 0.5f * s;
            s = 0.5f / s;                 
            x = (mat.m21 - mat.m12) * s;
            y = (mat.m02 - mat.m20) * s;
            z = (mat.m10 - mat.m01) * s;
        } else if ((mat.m00 > mat.m11) && (mat.m00 > mat.m22)) {
        	// x has  biggest magnitude, |x| >= 1/2
            float s = (float) Math.sqrt(1.0f + mat.m00 - mat.m11 - mat.m22); 
            x = s * 0.5f; 
            s = 0.5f / s;
            y = (mat.m10 + mat.m01) * s;
            z = (mat.m02 + mat.m20) * s;
            w = (mat.m21 - mat.m12) * s;
        } else if (mat.m11 > mat.m22) {
        	// y has  biggest magnitude, |y| >= 1/2
            float s = (float) Math.sqrt(1.0f + mat.m11 - mat.m00 - mat.m22); 
            y = s * 0.5f; 
            s = 0.5f / s;
            x = (mat.m10 + mat.m01) * s;
            z = (mat.m21 + mat.m12) * s;
            w = (mat.m02 - mat.m20) * s;
        } else {
        	// z has  biggest magnitude, |z| >= 1/2
            float s = (float) Math.sqrt(1.0f + mat.m22 - mat.m00 - mat.m11);
            z = s * 0.5f; 
            s = 0.5f / s;
            x = (mat.m02 + mat.m20) * s;
            y = (mat.m21 + mat.m12) * s;
            w = (mat.m10 - mat.m01) * s;
        }
	}
	
	/**
	 * Create new quaternion from old
	 * @param q
	 */
	public Quaternion(Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}
	
	/**
	 * Creates a quaternion from rotation axis and rotation angle
	 * @param axis Rotation axis
	 * @param angle Rotation angle in radians
	 * @return Rotation quaternion
	 */
	public static Quaternion fromAxisAngle( Vec3 axis, float angle ) {
		return fromNormalAxisAngle(Vec3.normalize(axis), angle);
	}
	
	/**
	 * Creates a quaternion form rotation axis vec and rotation angle. 
	 * @param axis Rotation axis, has to be normalized and not zero vector
	 * @param angle Rotation angle in radians
	 * @return Corresponding quaternion
	 */
	public static Quaternion fromNormalAxisAngle( Vec3 axis, float angle ) {
		assert !(axis.x == 0 && axis.y == 0 && axis.z == 0) : "Quaternion.fromNormalAxisAngle(): axis vector is zero.";
		float halfAngle = 0.5f * angle;
        float sinHalfAngle = (float) Math.sin(halfAngle);
        return new Quaternion(	sinHalfAngle * axis.x, 
        						sinHalfAngle * axis.y, 
        						sinHalfAngle * axis.z, 
        						(float) Math.cos(halfAngle) );
	}
	
	public static Quaternion fromEulerAngles( Vec3 angles ) {
		
//	    float c1 = (float)Math.cos(angles.y/2);
//	    float s1 = (float)Math.sin(angles.y/2);
//	    float c2 = (float) Math.cos(angles.x/2);
//	    float s2 = (float) Math.sin(angles.x/2);
//	    float c3 = (float) Math.cos(angles.z/2);
//	    float s3 = (float) Math.sin(angles.z/2);
//	    float c1c2 = c1*c2;
//	    float s1s2 = s1*s2;
//
//      return new Quaternion(	c1c2*s3 + s1s2*c3, 
//        						s1*c2*c3 + c1*s2*s3, 
//        						c1*s2*c3 - s1*c2*s3, 
//        						c1c2*c3 - s1s2*s3 ); 
		
//		Quaternion q1 = Quaternion.fromNormalAxisAngle(Vec3.xAxis(), angles.x);
//		Quaternion q2 = Quaternion.fromNormalAxisAngle(Vec3.yAxis(), angles.y);
//		Quaternion q3 = Quaternion.fromNormalAxisAngle(Vec3.zAxis(), angles.z);	
//	
//		return q3.mul(q1).mul(q2);
        
		Mat4 rotateMatrix = Mat4.rotationZ(angles.z);
		rotateMatrix.mul(Mat4.rotationX(angles.x));
		rotateMatrix.mul(Mat4.rotationY(angles.y));
		return new Quaternion (rotateMatrix);
	}
	
	
	
	/**
	 * Computer axis and angle of rotation of quaternion.
	 * Only makes sense, if this is a unit quaternion
	 * @param axis The rotation axis
	 * @return Rotation angle in radians
	 */
	public float toAxisAngle( Vec3 axis ) {
		assert axis != null : "Quaternion.toAxisAngle: axis is null-reference" ;
		if (w == 0) {
			// Because cos (angle/2) = w, it follows that the rotation angle is 0. 
			// Thus any axis will do. We choose the x-axes
			axis.x = 1.0f;
			axis.y = 0.0f;
			axis.z = 0.0f;
			return 0;
		} else {
			float angle = 2.0f * (float) Math.acos(w);
			// alternative : 2.0f * atan2( sqrt(x*x+y*y+z*z), w ) , perhaps mathematical more stable?
			
            if (axis != null) {
                float invLength = 1.0f / (float) Math.sqrt(1.0f-angle*angle);
                axis.x = x * invLength;
                axis.y = y * invLength;
                axis.z = z * invLength;
            }
            return angle;
		}
	}
	
	/**
	 * Creates rotation matrix from quaternion.
	 * Only makes sense for unit quaternion
	 * @return 3x3 rotation matrix
	 */
	public Mat3 toRotationMatrix() {
        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        float xs      = x * 2;
        float ys      = y * 2;
        float zs      = z * 2;
        float xx      = x * xs;
        float xy      = x * ys;
        float xz      = x * zs;
        float xw      = w * xs;
        float yy      = y * ys;
        float yz      = y * zs;
        float yw      = w * ys;
        float zz      = z * zs;
        float zw      = w * zs;
        
        return new Mat3(1 - (yy + zz), xy - zw,  xz + yw,
        				xy + zw, 1 - (xx + zz), yz - xw,
        				xz - yw, yz + xw, 1 - (xx + yy) );
	}
	
	
	/**
	 * Creates rotation matrix from non-unit quaternion.
	 * @return 3x3 rotation matrix
	 */
	public Mat3 nonUnitToRotationMatrix() {
		float xs = x * 2;
		float ys = y * 2;
		float zs = z * 2;
		float xxs = x * xs;
		float xys = x * ys;
		float xzs = x * zs;
		float xws = w * xs;
		float yys = y * ys;
		float yzs = y * zs;
		float yws = w * ys;
		float zzs = z * zs;
		float zws = w * zs;
		float ww = w * w;
		float xx = x * x;
		float yy = y * y;
		float zz = z * z;
      
        
        return new Mat3(ww + xx - yy - zz, xys - zws,  xzs + yws,
				xys + zws, ww - xx + yy - zz, yzs - xws,
				xzs - yws, yzs + xws, ww - xx  - yy + zz );
	}
	
	
	/**
	 * Creates rotation matrix from quaternion.
	 * Only makes sense for unit quaternion
	 * @param mat The resulting rotation matrix
	 */
	public void toRotationMatrix(Mat3 mat) {
		assert mat != null : "Quaternion.toRotationMatrix(): mat reference is null.";
        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        float xs      = x * 2;
        float ys      = y * 2;
        float zs      = z * 2;
        float xx      = x * xs;
        float xy      = x * ys;
        float xz      = x * zs;
        float xw      = w * xs;
        float yy      = y * ys;
        float yz      = y * zs;
        float yw      = w * ys;
        float zz      = z * zs;
        float zw      = w * zs;
        
        mat.m00  = 1 - ( yy + zz );
        mat.m01  =     ( xy - zw );
        mat.m02  =     ( xz + yw );
        mat.m10  =     ( xy + zw );
        mat.m11  = 1 - ( xx + zz );
        mat.m12  =     ( yz - xw );
        mat.m20  =     ( xz - yw );
        mat.m21  =     ( yz + xw );
        mat.m22  = 1 - ( xx + yy );
	}
	
	/**
	 * Creates quaternion from rotation matrix
	 * @param mat The rotation matrix
	 */
	public static Quaternion fromRotationMatrix( Mat3 mat ) {
		assert mat != null : "Quaternion.fromRotationMatrix(): mat reference is null.";
		return new Quaternion(mat);
	}
	
	/**
	 * Creates quaternion from rotation matrix
	 * @param mat The rotation matrix
	 */
	public static Quaternion fromRotationMatrix( Mat4 mat ) {
		assert mat != null : "Quaternion.fromRotationMatrix(): mat reference is null.";
		return new Quaternion(mat);
	}
	
	public Quaternion add( Quaternion  q )
	{
		this.x += q.x;
		this.y += q.y;
		this.z += q.z;
		this.w += q.w;
		
		return this;
	}
	
	public static Quaternion add( Quaternion left, Quaternion right )
	{
		return new Quaternion( left.x + right.x,
						 left.y + right.y,
						 left.z + right.z,
						 left.w + right.w );
	}

	
	public Quaternion sub( Quaternion right )
	{
		this.x -= right.x;
		this.y -= right.y;
		this.z -= right.z;
		this.w -= right.w;
		
		return this;
	}
	
	
	public static Quaternion sub( Quaternion left, Quaternion right )
	{
		return new Quaternion( left.x - right.x,
						 left.y - right.y,
						 left.z - right.z,
						 left.w - right.w );
	}
	
	
	public Quaternion mul( float value )
	{
		this.x *= value;
		this.y *= value;
		this.z *= value;
		this.w *= value;
		
		return this;
	}
	
	public static Quaternion mul( Quaternion q, float value )
	{
		return new Quaternion( q.x * value,
						 q.y * value,
						 q.z * value,
						 q.w * value );
	}
	
	/** 
	 * Quaternion multiplication: this*q
	 * @param q
	 * @return multiplicated quaternion
	 */
	public Quaternion mul( Quaternion q )
	{
		x = x * q.w + y * q.z - z * q.y + w * q.x;
	    y = -x * q.z + y * q.w + z * q.x + w * q.y;
	    z = x * q.y - y * q.x + z * q.w + w * q.z;
	    w = -x * q.x - y * q.y - z * q.z + w * q.w;
		return this;
	}

	/** 
	 * Quaternion multiplication of two quaternions p*q
	 * @param p
	 * @param q
	 * @return multiplication result of p*q 
	 */
	public static Quaternion mul( Quaternion p, Quaternion q )
	{
		return new Quaternion(p.x * q.w + p.y * q.z - p.z * q.y + p.w * q.x,
				-p.x * q.z + p.y * q.w + p.z * q.x + p.w * q.y,
				p.x * q.y - p.y * q.x + p.z * q.w + p.w * q.z,
				-p.x * q.x - p.y * q.y - p.z * q.z + p.w * q.w);
	}
	
	/** 
	 * Logarithm of quaternion q
	 * @param q
	 * @return logarithm result of q (Vec3)
	 */
	public static Quaternion log( Quaternion q )
	{
//		Vec3 v = new Vec3(q.x, q.y, q.z);
//		float len = v.length();
//		if (len > MathUtil.EPS)
//		{	
//			if ((q.w < -1) || (q.w > 1))
//			{
//				System.out.println ("Cannot determine log of q = " + q.toString());
//				return null;
//			}
//			float alpha = (float) Math.acos(q.w);
//			float sinAlpha = (float) Math.sin(alpha);
//			if (sinAlpha > MathUtil.EPS)
//				v.mul(alpha/sinAlpha);
//		}
//		return v;
		
		float normQ = q.length();
		Vec3 v = new Vec3 (q.x, q.y, q.z);
		float normV = v.length();
		
		// TODO: normQ and normV could be near zero, handle these special cases correctly
		if (normQ < MathUtil.EPS)
			System.err.println ("Log: Warning on norm of q");
		
		if (normV < MathUtil.EPS)
			System.err.println ("Log: Warning on norm of v");
		
		float w = (float) Math.log(normQ);
		float coeff = (float) Math.acos(q.w / normQ) / normV;
		v.mul(coeff);
		return new Quaternion (v, w);
	}
	
	
	/** 
	 * Exponential function of q 
	 * @param q	
	 * @return quaternion as exponential result of q 
	 */
	public static Quaternion exp( Quaternion q )
	{
//		Vec3 nVec = new Vec3(v);
//		float len = v.length();
//		float w = (float) Math.cos(len);
//		if (len > MathUtil.EPS)
//		{
//			float sinAlpha = (float) Math.sin(len);
//			nVec.mul(sinAlpha/len);
//		}
//		
//		return new Quaternion (nVec, w);
		
		Vec3 v = new Vec3 (q.x, q.y, q.z);
		float normV = v.length();
		float w = (float) Math.cos(normV);
		// TODO: normV could be near zero, handle special case correctly
		if (normV < MathUtil.EPS)
			System.err.println ("Exp: Warning on norm of v");
		float coeff = (float) Math.sin(normV) / normV;
		v.mul(coeff);
		Quaternion res = new Quaternion (v, w);
		if (q.w > MathUtil.EPS)
		{
			coeff = (float) Math.exp(q.w);
			res.mul(coeff);
		}
		return res;
	}	
	
	
	/**
	 * Dot product of two quaternions
	 * @param p
	 * @param q
	 * @return dot product
	 */
	public static float dot( Quaternion p, Quaternion q )
	{
		return p.x * q.x + p.y * q.y + p.z * q.z + p.w * q.w;
	}

	
	/**
	 * Lenght (norm) of quaternion
	 * @return length of quaternion
	 */
	public float length()
	{
		return (float) Math.sqrt( dot(this, this) );
	}
	
	/**
	 * Lenght (norm) of quaternion
	 * @param q quaternion for which length shall be calculated
	 * @return length of quaternion
	 */
	public static float length( Quaternion q )
	{
		return (float) Math.sqrt( dot(q, q) );
	}
	
	
	/**
	 * Normalization of quaternion
	 * @return normalized quaternion
	 */
	public Quaternion normalize()
	{
		float lengthInv = 1.0f / length( this );
		
		this.x *= lengthInv;
		this.y *= lengthInv;
		this.z *= lengthInv;
		this.w *= lengthInv;
		
		return this;
	}
	
	/**
	 * Conjugates this quaternion
	 */
	public void conjugate() {
		x = -x;
		y = -y;
		z = -z;
	}
	
	/**
	 * Conjugates this quaternion
	 */
	public static Quaternion conjugate( Quaternion q ) {
		return new Quaternion(-q.x, -q.y, -q.z, q.w);
	}


	/**
	 * Inverts this quaternion
	 */
	public void invert() {
		conjugate();
		mul(this, 1.0f / dot(this, this) );
	}
	
	/**
	 * Returns inverse of a quaternion
	 * @param q Quaternion for which we want the inverse
	 * @return The inverse of the quaternion q
	 */
	public static Quaternion invert( Quaternion q ) {
		Quaternion p = new Quaternion(q);
		p.invert();
		return p;
	}
	
	/**
	 * Compute three-dimensional transformation. vec is converted to quaternion and then
	 * this x vec x this* computed.
 	 * @param vec The vector which has to be transformed
	 * @return The transformed vector
	 */
	public Vec3 transfrom( Vec3 vec ) {
		Quaternion q =  Quaternion.mul(this, Quaternion.mul(new Quaternion(vec), conjugate(this)));
		return new Vec3(q.x, q.y, q.z);
	}
	
	/**
	 * Compute three-dimensional transformation. vec is converted to quaternion and then
	 * this x vec x this* computed. 
	 * Only makes sense, if vec.w = 0
 	 * @param vec The vector which has to be transformed
	 * @return The transformed vector
	 */
	public Vec4 transfrom( Vec4 vec ) {
		Quaternion q =  Quaternion.mul(this, Quaternion.mul(new Quaternion(vec), conjugate(this)));
		return new Vec4(q.x, q.y, q.z, q.w);
	}
	
	public String toString()
	{
		return this.w +  " + " + this.x + "i + " + this.y + "j + " + this.z + "k";
	}
	
	@Override
    public boolean equals( Object obj )
	{
        if( obj == this )
            return true;
        
        if( obj == null || obj.getClass() != this.getClass() )
            return false;

        Quaternion q = (Quaternion) obj;
        return this.x == q.x
            && (this.y == q.y)
            && (this.y == q.y)  
            && (this.w == q.w);
    }
	
	/**
	 * Quaternion linear interpolation(LERP), computes  q =  (1-t)q0 + tq1 and returns the normalization of q
	 * @param t
	 * @param q1
	 * @param q2
	 * @return interpolated result
	 */
	public static Quaternion lerp( float t, Quaternion q1, Quaternion q2 ) {
		return mul(q1, 1-t).add( mul(q2,t) ).normalize();
	}
	
	/**
	 * Quaternion spherical linear interpolation (SLERP)
	 * @param t
	 * @param q1
	 * @param q2
	 * @return interpolated result
	 */
	public static Quaternion slerp( float t, Quaternion q1, Quaternion q2 ) {
	    float angle, cosAngle, invSinAngle, scale1, scale2;
	    Quaternion q;

	    // calculate cosine
	    cosAngle = Quaternion.dot( q1, q2 );
	    
	    // The next step should not be necessary, if every given quaternion here
	    // has norm 1. But this can not be assured due to numerical errors            
	    // This also can happen, if the two quats are the same.
	    // So instead of renormalizing every wanna-be unit quaternion
	    // before using slerp, we just set here cosAngle to 1.
	    cosAngle = MathUtil.clamp(cosAngle,-1.0f,1.0f);
		
		// if q1 and q2 are not on the same hemisphere (cosAngle < 0 in this
		// case), take q1 and -q2 to interpolate between them
//		if (cosAngle < 0)
//		{
//			cosAngle = -cosAngle;
//			q2 = Quaternion.mul(q2, -1.0f);
//		}
	    
	    angle =(float) Math.acos(cosAngle);
	    
	    if (angle < ZERO_TOLERANCE) {
	    	q = new Quaternion(q1);
	    } else  {
	    	 invSinAngle =  1.0f / (float) Math.sin(angle);
	 	     
	         scale1 = (float) Math.sin((1.0 - t) * angle) * invSinAngle;
	         scale2 = (float) Math.sin(t * angle) * invSinAngle;
	         
	         q = Quaternion.add(Quaternion.mul(q1, scale1), Quaternion.mul(q2, scale2));
	    }
	    return q;
	}
	
	/**
	 * Quaternion spherical cubic interpolation (SQUAD)
	 * @param t
	 * @param q1
	 * @param q2
	 * @param a1	
	 * @param a2
	 * @return interpolated result
	 */
	public static Quaternion squad( float t, Quaternion q1, Quaternion q2, Quaternion a1, Quaternion a2 ) {
		Quaternion first = slerp (t, q1, q2);
		Quaternion second = slerp (t, a1, a2);
		return slerp (2*t*(1-t), first, second);
	}
	
	
	public boolean sameHemisphere(Quaternion q)
	{
		// calculate cosine
		float cosAngle = Quaternion.dot(this, q);
		// if cosine is positive, the angle is smaller than Pi/2,
		// if cosine is negative the angle is bigger then Pi/2, meaning that the
		// quaternion is on the other hemisphere
		return cosAngle >= 0.0f;
	}
	
	
	/**
	 * Test client for quaternion class
	 * @param args
	 */
	public static void main( String[] args ) {
		Quaternion q1 = new Quaternion();
		System.out.println( "q1 = " + q1 );
		
		Quaternion q2 = new Quaternion( 1.0f, 2.0f, 3.0f, 4.0f );
		System.out.println( "q2 = " + q2 );
		
		Quaternion q3 = new Quaternion (q2);
		System.out.println( "q3 = " + q3 );
				
		System.out.println("q1 == q2 ? " + q1.equals(q2));
		System.out.println("q2 == q3 ? " + q2.equals(q3));
		
		Quaternion q4 = new Quaternion ( new Vec3(2.0f, 3.0f, 4.0f) );
		System.out.println( "q4 = " + q4 );

		Quaternion q5 = new Quaternion ( new Vec4(2.0f, 3.0f, 4.0f, 5.0f) );
		System.out.println( "q5 = " + q5 );
		
		Quaternion q6 = new Quaternion ( new Vec3(2.0f, 3.0f, 4.0f), 5.0f );
		System.out.println( "q6 = " + q6 );
		
		Quaternion q7 = new Quaternion (0.5f, 0, (float) Math.sqrt(2)/2.0f, 0.5f);
		System.out.println("q7 = " + q7);

		System.out.println( "exp(log(q7)) = " + Quaternion.exp(Quaternion.log(q7)));
		System.out.println( "log(exp(q7)) = " + Quaternion.log(Quaternion.exp(q7)));
		
		
		System.out.println( "log (q4*q5) = " + Quaternion.log(Quaternion.mul(q4, q5)));
		System.out.println( "log (q4) + log (q5) = " + Quaternion.add(Quaternion.log(q4),
														Quaternion.log(q5)));
	}

}
