package Aufgabenblatt_4.Asbach_Dustin;

/**
*CA Winter 2018/19
*Name , Vorname : Asbach , Dustin
*Matrikelnummer : 11117108
*Aufgabenblatt : 4
*Aufgabe : 4.2
**/

import animation.AbstController;
import math.Vec3;
import scenegraph.Channel;
import scenegraph.util.VisualHelp;

public class BallisticController extends AbstController{
	
	String name;
	Vec3 x0, v0, a, x0_start; // Startposition, Startgeschwindigkeit, beschleunigung
	Channel channel;
	float time_old;
	
	
	public BallisticController(String name, Vec3 pos, Vec3 vel, Vec3 acc, Channel channel) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.
				POSITIVE_INFINITY);
		this.name = name;
		setName(this.name);
		
		this.x0 = pos;
		this.x0_start = pos;
		this.v0 = vel;
		this.a = acc;
		this.channel = channel;
	}
		
	
	@Override
	public boolean update(float time) {
		
		if (!super.update(time)) { //Prüfe ob neue Rechnung erfprderlich
			return false;
		}
		

		if(getLocalTime(time)-time_old > 0 && x0.y>1f) {
				
			// der Test hier funktioniert prima :D
			x0 = x0.add(new Vec3(v0).mul(getLocalTime(time)-time_old));
			v0 = v0.add(new Vec3(a).mul(getLocalTime(time)-time_old));
			
			// set the channel object to calculated output value (postion).
			Vec3 ref_Pos = (Vec3) channel.getData();
			ref_Pos.set(x0);
			
			
			VisualHelp.markTimeOnGrid(time);
		
			time_old = time;
		}
		
		
		
		
		
		return true; 
		
	}

}

