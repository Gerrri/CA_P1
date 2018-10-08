package particles;

/**
 * Class EulerStep computes on Euler iteration for a particle system, and updates
 * the state of the particle system
 * @author Stefan
 *
 */
public class EulerStep {
	
	/**
	 * Computes one Euler iteration for a particle system and updates
	 * the state of the particle system
	 * @param psystem The particle system
	 * @param t	Start time of Euler step
	 * @param h	Time step
	 */
	public static void update(ParticleSystem psystem, float t, float h) {
		float[] state = new float[psystem.getDimension()];
		float[] deriv = new float[psystem.getDimension()];
		
		psystem.getState(state);
		psystem.getDerivative(deriv,t);
		
		scale(deriv,h);
		add(state, deriv, state);
		psystem.setState(state);
	}
	
	/**
	 * Multiplies each component of an array with a factor, i.e a[i] = h * a[i]
	 * @param a Array which is modified
	 * @param h Factor
	 */
	private static void  scale(float[] a, float h) {
		for (int i = 0; i < a.length; i++) {
			a[i] *= h;
		}
		return;
	}
	
	/**
	 * Adds two array a and b component wise and stores the result on array c
	 * @param a Input
	 * @param b Input
	 * @param c Result
	 */
	private static void  add(float[] a, float[] b, float[] c) {
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] + b[i];
		}
		return;
	}
}
