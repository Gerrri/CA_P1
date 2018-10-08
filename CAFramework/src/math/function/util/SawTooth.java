package math.function.util;
import math.Vec3;
import math.function.FunctionR1Vec3;

public class SawTooth extends FunctionR1Vec3 {
	
	private float period;
	private float amplitude;
	
	public SawTooth(float period, float amplitude) {
		super(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.period = Math.abs(period);
		this.amplitude = amplitude;
	}
	
	@Override
	public Vec3 eval(float x) 
	{
		Vec3 result = new Vec3((float) x, 0f, 0f);
		if (x >= 0)
			result.y = (float) (amplitude * (x % period)/ period);
		else
			result.y = (float) (amplitude * (1 - Math.abs(x % period)/ period));
		return result;
	}
	
	
	public String toString() {
		return "sawtooth(x)";
	}
	
	public static void main(String[] args) {
//		double period = Double.parseDouble(args[0]);
//		double amplitude = Double.parseDouble(args[1]);
//		SawTooth sf = new SawTooth(period,amplitude);
//		
//		canvas.setXscale(-3*period, 3*period);
//		canvas.setYscale(0,amplitude);
//		
//		int N = 1000;
//		double x0 = -3*period, y0 = sf.eval(x0);
//		double x1, y1;
//		double left = x0, step = 6*period / (N-1);
//		for (int i = 1; i < N; i++) {
//			x1 = left + i * step;
//			y1 = sf.eval(x1);
//			canvas.line(x0, y0, x1, y1);
//			x0 = x1;
//			y0 = y1;
//		}
	}
}
