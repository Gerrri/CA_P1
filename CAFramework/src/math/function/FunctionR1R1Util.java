package math.function;

/**
 * Utilities for manipulation of function R1-> R1.
 * @author Stefan
 *
 */
public class FunctionR1R1Util {
	
	/**
     * Class only contains static methods.
     */
	private FunctionR1R1Util() {}
	
    /**
     * Composes functions.
     * <br/>
     * The functions in the argument list are composed sequentially, in the
     * given order.  For example, compose(f1,f2,f3) acts like f1(f2(f3(x))).
     *
     * @param f List of functions.
     * @return the composite function.
     */
    public static FunctionR1R1 compose(final FunctionR1R1... f) {
        return new FunctionR1R1(f[0].getTMin(), f[f.length-1].getTMax()) {
            
            public float eval(float t) {
                float r = t;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].eval(r);
                }
                return r;
            }
        };
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
