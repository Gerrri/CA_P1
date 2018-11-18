package Aufgabenblatt_6.Asbach_Dustin;

import math.function.FunctionR1R1;
import math.function.FunctionR1Vec3Util;

public class ParamTrans extends FunctionR1R1 {
	private float[] ti;
	private float[] si;
	
	
	public ParamTrans(float tmin, float tmax, float[] si, float[] ti) {
		super(tmin, tmax);
		this.ti = ti;
		this.si= si;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public float eval(float t) {
		float ret = 0;
		
		if(t<6){
			ret = ((ti[1]-ti[0])/(si[1]-si[0]))*(t-si[0])+ti[0];	
		}
		
		else if(t>6 && t<12){
			ret = ((ti[2]-ti[1])/(si[2]-si[1]))*(t-si[1])+ti[1];	
		}
		
		else if(t>12 && t<18){
			ret = ((ti[3]-ti[2])/(si[3]-si[2]))*(t-si[2])+ti[2];	
		}
		
		else if(t>18 && t<24){
			ret = ((ti[4]-ti[3])/(si[4]-si[3]))*(t-si[3])+ti[3];	
		}
		
		
		
		// TODO Auto-generated method stub
		return ret;
	}
	

}
