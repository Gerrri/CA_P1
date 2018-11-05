package Aufgabenblatt_4.Asbach_Dustin;

/**
*CA Winter 2018/19
*Name , Vorname : Asbach , Dustin
*Matrikelnummer : 11117108
*Aufgabenblatt : 4
*Aufgabe : 4.1
**/

import animation.AbstController;
import math.Vec3;
import math.Mat4;
import scenegraph.Channel;

public class ClockController extends AbstController {
	String name;
	int start_h, start_m;
	float time_old,ver_time;
	Mat4 t_h,t_m, h_mat4,m_mat4;
	private float pi = (float) Math.PI;
	

	public ClockController(String name, int start_h, int start_m, Channel ch_rot_h, Channel ch_rot_m) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.
				POSITIVE_INFINITY);
		this.start_h = start_h;
		this.start_m = start_m;
		this.name = name;
		setName(this.name);
		
		h_mat4 = (Mat4) ch_rot_h.getData();
		m_mat4 = (Mat4) ch_rot_m.getData();
	}
	

	@Override
	public boolean update(float time) {
		if (!super.update(time)) { //Pr�fe ob neue Rechnung erforderlich
			return false;
		}
		
		ver_time = getLocalTime(time)-time_old;
		
		
		if(ver_time > 0) { 
			// -pi/1800 							=> bewegung Pro Sekunde des Minutenzeigers
			// getLocalTime(time)+(start_m*60)		=> Aktuelle Zeit + voreingestellte Minuten (in Sekunden)
			// +pi 									=> verschiebung um 180 Grad
			
			t_m = Mat4.rotationY(-pi/1800*(getLocalTime(time)+(start_m*60))+pi); 
			m_mat4.set(t_m);
			
			// -pi/21600							=> bewegung Pro Sekunde des Stundenzeigers
			// getLocalTime(time)+(start_h*60*60) 	=> Akutell verstrichene Zeit + Start wert der Uhr (in Sekunden)
			// +pi-(start_m*pi/360) 				=> + verschiebung um 180 grad - eingestellte Minutenanzahl 
			t_h = Mat4.rotationY(-pi/21600*(getLocalTime(time)+(start_h*3600))+pi-(start_m*pi/360)); 
			h_mat4.set(t_h);
			
			time_old = time;
		}
		
		return true; 
		
	}
	
	

}
