package math.function;

import math.Mat3;
import math.Vec3;

/**
 * Interface for objects, having a channel.
 * @author Stefan
 *
 */
public interface FrenetFrame {
	/**
	 * Returns the normalized tangent at a given time <t>. The tangent is the first derivative of the curve and indicates the direction of the curve at the given time.  
	 * 
	 * @return normalized tangent
	 */
	public Vec3 getTangent(float t);

	/**
	 * Returns the normalized normal at a given time <t>. The normal is the second derivative of the curve  at the given time and is perpendicular to the tangent.  
	 * 
	 * @return normalized tangent
	 */
	public Vec3 getNormal(float t);
	
	/**
	 * Returns the normalized tangent at a given time <t>. The tangent indicates the direction of the curve at the given time.  
	 * 
	 * @return normalized tangent
	 */
	public Vec3 getBinormal(Vec3 tangent, Vec3 normal);

	public Mat3 getFrenetFrame( float t);
}
