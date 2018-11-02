package Aufgabenblatt_4.Asbach_Dustin;

import animation.AbstController;
import math.Vec3;
import math.Mat4;
import scenegraph.Channel;

public class ClockController extends AbstController {
	String name;
	int start_h, start_m;
	float time_old,ver_time;
	Mat4 t_h,t_m, h_mat4,m_mat4;
	private float pi = 3.14159265359f;
	
	
	
	
	
	public ClockController(String name, int start_h, int start_m, Channel ch_rot_h, Channel ch_rot_m) {
		super(AbstController.RepeatType.CLAMP, Float.NEGATIVE_INFINITY, Float.
				POSITIVE_INFINITY);
		this.start_h = start_h;
		this.start_m = start_m;
		this.name = name;
		setName(this.name);
		
		h_mat4 = (Mat4) ch_rot_h.getData();
		m_mat4 = (Mat4) ch_rot_m.getData();
		
		init_vec(start_h, start_m);
	}
	

	@Override
	public boolean update(float time) {
		if (!super.update(time)) { //Prüfe ob neue Rechnung erforderlich
			return false;
		}
		
		ver_time = getLocalTime(time)-time_old;
		
		if(ver_time > 0) { //aktualisiere alle 60 (60000ms) sekunden
			

			t_m = m_mat4.rotationY(-pi/1800*getLocalTime(time)); //= h_mat4.add(new Vec3(0, start_h*pi/6+(start_m*pi/360), 0));
			m_mat4.set(t_m);
			
			
			t_h = h_mat4.rotationY(-pi/10800*getLocalTime(time)); //pi/6-pi/360
			h_mat4.set(t_h);
			
			
			time_old = time;
			
		}
		
		return true; 
		
	}
	
	private void init_vec(int start_h, int start_m){
		
		//m_mat4.rotationY(start_m*6f); //= m_mat4.add(new Vec3(0, start_m*pi/30, 0));
		t_m = m_mat4.rotationY(pi-(start_m*pi/30)); //= h_mat4.add(new Vec3(0, start_h*pi/6+(start_m*pi/360), 0));
		m_mat4.set(t_m);
		
		t_h = h_mat4.rotationY(pi-(start_h*pi/6)-(start_m*pi/360));
		h_mat4.set(t_h);
		
	
	}
	
	

}
