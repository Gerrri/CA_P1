package Aufgabenblatt_5.Asbach_Dustin.rocket;

import math.Vec3;
import math.function.FunctionR1Vec3;

public class TakeOff extends FunctionR1Vec3{
	Vec3 accPhase1;			//beschleunigung1
	float durationPhase1;	//dauer1
	Vec3 posStart;			//startPosition
	float durationPhase2;	//dauer2
	
	Vec3 velocityPhase2;	//geschwindigkeit 2
	Vec3 velocityPhase3;	//geschwindigkeit 3
	Vec3 posEndPhase1;		//pos1
	Vec3 posEndPhase2;		//pos2
	float phase1End;		//zeit1
	float phase2End;		//zeit2
	Vec3 position;			//aktuelle posisiton
	
	
	public TakeOff(Vec3 accPhase1, float durationPhase1, Vec3 posStart, float durationPhase2, float tmin, float tmax) {
		
		super(tmin, tmax);
		this.accPhase1 = accPhase1;
		this.durationPhase1 = durationPhase1;
		this.posStart = posStart;
		this.durationPhase2 = durationPhase2;
		
		this.position = posStart;
		this.phase1End = durationPhase1;
		this.phase2End = phase1End + durationPhase2;		
		this.velocityPhase2 = new Vec3(accPhase1).mul(durationPhase1);
		this.velocityPhase3 = new Vec3(velocityPhase2).mul(4);
		this.posEndPhase1 = new Vec3(accPhase1).mul(0.5f * durationPhase1 * durationPhase1).add(posStart); // 0.5 * a * t^2 + start
		this.posEndPhase2 = new Vec3(posEndPhase1).add((new Vec3(velocityPhase2).mul(durationPhase2)));
	}


	@Override
	public Vec3 eval(float t) {
		// TODO Auto-generated method stub
		
		
		if(t<phase1End) {						//Phase1
			position.add(new Vec3(accPhase1).mul(0.5f*t*t));
		}
		else if (t>phase1End && t<phase2End) {	//Phase2
			position.add(new Vec3(velocityPhase2).mul(t));
		}
		else if (t>phase2End) {					//Phase3
			
		}
		
		
		
		

		
		return position;
	}
	
	
	
	
	
}
