package Aufgabenblatt_5.Asbach_Dustin.ufo;

import math.Vec3;
import math.function.FunctionR1Vec3;


public class Toruskurve extends FunctionR1Vec3 {

	
	float a,b,c,p,q;
	
	public Toruskurve(float a, float b, float c, float p, float q) {
		super(0f, (float)(2*Math.PI));
		
		this.a = a;
		this.b = b;
		this.c = c;
		this.p = p;
		this.q = q;
		
		
	}

	@Override
	public Vec3 eval(float t) {

		Vec3 temp= new Vec3(0);
		
		Vec3 toruskurve = new Vec3( (float) ( (a + b*Math.cos(q*t)) * Math.cos(p*t) ), 
				(float) ( (a + b*Math.cos(q*t)) * Math.sin(p*t)), 
				(float) (c*Math.sin(q*t)) );
		temp.set(toruskurve);
		
		return temp;
		
		
	}

}
