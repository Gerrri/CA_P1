package Aufgabenblatt_6.Asbach_Dustin;

import math.function.FunctionR1R1;
import math.function.FunctionR1Vec3Util;

public class ParamTrans extends FunctionR1R1 {
	private float[] si;
	private float[] ti;
	int i;
	
	
	public ParamTrans(float tmin, float tmax, float[] si, float[] ti) {
		super(tmin, tmax);
		this.si = si;
		this.ti= ti;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public float eval(float t) {
		float ret = 0;
		
		
		if(t>ti[0] && t<=ti[1])			{i=0;}
		else if(t>ti[1] && t<=ti[2])	{i=1;}
		else if(t>ti[2] && t<=ti[3])	{i=2;}
		else if(t>ti[3] && t<=ti[4])	{i=3;}
		
		ret = ((si[1+i]-si[0+i])/(ti[1+i]-ti[0+i]))*(t-ti[0+i])+si[0+i];	
		
		
		/*// ist da gleiche wie oben, nur in ausführlich
		if	(t>ti[0] && t<=ti[1]){
			ret = ((si[1]-si[0])/(ti[1]-ti[0]))*(t-ti[0])+si[0];	
		}
		
		else if(t>ti[1] && t<=ti[2]){
			ret = ((si[2]-si[1])/(ti[2]-ti[1]))*(t-ti[1])+si[1];	
		}
		
		else if(t>ti[2] && t<=ti[3]){
			ret = ((si[3]-si[2])/(ti[3]-ti[2]))*(t-ti[2])+si[2];	
		}
		
		else if(t>ti[3] && t<=ti[4]){
			ret = ((si[4]-si[3])/(ti[4]-ti[3]))*(t-ti[3])+si[3];	
		}
		*/
		
		
		return ret;
	}
	

}
