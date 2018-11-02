package Aufgabenblatt_4.Asbach_Dustin;

import animation.AbstController;
import math.Vec3;
import scenegraph.Channel;

public class ClockController extends AbstController {
	float start_time;
	Channel channel_rot;
	float time_old;
	Vec3 n_vec;
	
	
	
	public ClockController(float start_time, Channel channel_rot) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.
				POSITIVE_INFINITY);
		this.start_time = start_time;
		this.channel_rot = channel_rot;
	}
	
	@Override
	public boolean update(float time) {
		if (!super.update(time)) { //Prüfe ob neue Rechnung erfprderlich
			return false;
		}
		
		if(getLocalTime(time)-time_old > 0) {
			new
			
		}
		
		
		
		
		
		return true; 
		
	}
	
	

}
