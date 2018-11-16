package Aufgabenblatt_6.Asbach_Dustin;

import math.function.FunctionR1R1;
import math.function.FunctionR1Vec3Util;

public class ParamTrans extends FunctionR1R1 {

	public ParamTrans(float tmin, float tmax) {
		super(tmin, tmax);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float eval(float t) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public float trans (float s0,float s1, float t0, float t1, float t) {
		float x=0;
		//		6	/	6	* 0 + 0
		//x = ((s1-s0)/(t1-t0))*(t-t0)+s0;
		x = ((t1-t0)/(s1-s0))*(t-s0)+t0;
		
		//System.out.println(x);		
		
		//FunctionR1Vec3Util.compose(f, p);
		return x;
	}
}
