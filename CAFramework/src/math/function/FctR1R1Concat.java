package math.function;


/**
 * Concatenation of two functions f:[a,b] -> R, g : [c,d] -> R.
 * Result is new real function f o g : [c,d]-> R, f o g (t) = f(g(t))
 * @author Stefan
 *
 */
public class FctR1R1Concat extends FunctionR1R1{
	
	FunctionR1R1 f;
	FunctionR1R1 g;
	
	public FctR1R1Concat(FunctionR1R1 f, FunctionR1R1 g) {
		super(g.getTMin(), g.getTMax());
		this.f = f;
		this.g = g;
	}
	
	public float eval(float t) {
		return f.eval(g.eval(t));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
