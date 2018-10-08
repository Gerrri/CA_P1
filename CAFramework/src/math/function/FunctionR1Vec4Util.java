package math.function;


import math.Vec4;


public class FunctionR1Vec4Util {
	/**
     * Class only contains static methods.
     */
	private FunctionR1Vec4Util() {}
	
	/**
     * Composes curve f:R->R^3 with function p:R -> R.
     * <br/>
     * The functions are composed to f(p(t))
     * @param f Curve
     * @param p Function
     * @return the reparametrized function.
     */
	public static FunctionR1Vec4 compose(final FunctionR1Vec4 f, final FunctionR1R1 p) {
		
		return new FunctionR1Vec4(p.getTMin(), p.getTMax()) {
			
			@Override
			public Vec4 eval(float t) {
               return f.eval(p.eval(t));
            }
		};
	}

	
	/**
     * Restricts curve f to the interval [tMin, tMax]
     * <br/>
     * @param f Curve
     * @param tMin Left interval border
     * @param tMax Right interval border
     * @return the restricted function.
     */
	public static FunctionR1Vec4 restrict(final FunctionR1Vec4 f, float tMin, float tMax) {
		
		return new FunctionR1Vec4(tMin, tMax) {
			
			@Override
			public Vec4 eval(float t) {
               return f.eval(t);
            }
		};
	}

	
	/**
	 * Connects  f_i [a_i,b_i] -> R^3, with i = 0, ..., n. 
	 * We assume, that b_i = a_{i+1} for i = 0, ..., n-1. and b0 < b1 < ... ldots b_{n-1}.
	 * a_0 may be negative infinity, b_n may be positive infinity.
	 * For t in [a_0, b_0) the function f_0 is evaluated, for t in [a_1, b_1) the function f_1 is evaluated
	 * and so on.
	 */
	public static FunctionR1Vec4 connect(final FunctionR1Vec4... f) {
        return new FunctionR1Vec4(f[0].getTMin(), f[f.length-1].getTMax()) {
            
            @Override
			public Vec4 eval(float t) {
            	if (f[0].getTMin() == Float.NEGATIVE_INFINITY && t < f[0].getTMax()) {
            		return f[0].eval(t);
            	} else if (f[f.length-1].getTMax() == Float.POSITIVE_INFINITY && t >= f[f.length-1].getTMin()) {
            		return f[f.length-1].eval(t);
            	} else {
            		int i = 0;
                    while (i < f.length-1 && t >= f[i+1].getTMin() ) {
                    	i++;
                    }
                    return f[i].eval(t);
            	}
               
            }
        };
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
