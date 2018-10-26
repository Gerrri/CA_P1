package Aufgabenblatt_3_Asbach_Dustin;

import animation.AbstController;
import math.Vec3;
import scenegraph.Channel;

public class BallisticController extends AbstController{
	
	String name;
	Vec3 x0, v0, a; // Startposition, Startgeschwindigkeit, beschleunigung
	Channel channel;
	float time_old;
	
	
	public BallisticController(String name, Vec3 pos, Vec3 vel, Vec3 acc, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.
				POSITIVE_INFINITY);
		this.name = name;
		setName(this.name);
		
		this.x0 = pos;
		this.v0 = vel;
		this.a = acc;
		this.channel = channel;
	}
		
	
	@Override
	public boolean update(float time) {
		
		if (!super.update(time)) { //Prüfe ob neue Rechnung erfprderlich
			return false;
		}
		
		if(getLocalTime(time)-time_old > 0) {
			
			
			// x0 + vo * deltat;
			//x0 = x0.add(v0.mul(getLocalTime(time)-time_old));
			// v0 = vi
			//v0 = v0.add(a.mul(getLocalTime(time)-time_old));
			
			x0 = x0.sub(a);
			
			// set the channel object to calculated output value (postion).
			Vec3 ref_Pos = (Vec3) channel.getData();
			ref_Pos.set(x0);
			
		
			
		time_old = time;
		}
		return true; 
		
	}

}

